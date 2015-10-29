package net.frebib.sscdatabase;

import net.frebib.sscdatabase.util.DateHelper;
import net.frebib.sscdatabase.util.IOHelper;

import java.io.IOException;
import java.sql.*;
import java.util.Random;
import java.util.regex.Pattern;

public class InitDB {

    public static void createDB(Connection conn, DBConfig cfg) throws SQLException, IOException {
        try {
            executeSQLFile(conn, cfg.getProp("clearsql"));
        } catch (Exception e) {
            // Ignore the error if it is trying to remove a table that doesn't exist.
            // It's not the end of the world
            if (!Pattern.matches("^ERROR: table \".*\" does not exist$", e.getMessage()))
                throw e;
        }
        executeSQLFile(conn, cfg.getProp("createsql"));
        populateDB(conn, cfg);
    }

    public static void populateDB(Connection conn, DBConfig cfg) throws SQLException, IOException {
        String[] boys = IOHelper.loadFileArray(cfg.getProp("boys"));
        String[] girls = IOHelper.loadFileArray(cfg.getProp("girls"));
        String[] surnames = IOHelper.loadFileArray(cfg.getProp("surnames"));
        int scount = cfg.getInt("scount");
        int sidmin = cfg.getInt("sidmin");
        int sidmax = cfg.getInt("sidmax");
        int lcount = cfg.getInt("lcount");
        int lidmin = cfg.getInt("lidmin");
        int lidmax = cfg.getInt("lidmax");
        PreparedStatement ps, ps1, ps2;

        Random r = new Random();

        ResultSet rs = conn.createStatement().executeQuery("SELECT count(*) FROM Title");
        rs.next();
        int titlecount = rs.getInt(1);
        int nextid = sidmin + r.nextInt(sidmax - sidmin);

        // ## Populate Students ## \\
        ps = conn.prepareStatement("INSERT INTO Student (sid, titleid, forename, familyname, dob) VALUES (?,?,?,?,?)");
        ps1 = conn.prepareStatement("INSERT INTO StudentContact (sid, email, address) VALUES (?,?,?)");
        for (int i = 0; i < scount; i++) {
            boolean isMale = r.nextBoolean();
            String fname = isMale ? boys[r.nextInt(boys.length)] : girls[r.nextInt(girls.length)],
                   sname = surnames[r.nextInt(surnames.length)];

            // TODO: Clean up title calculation with random chance for Dr otherwise just choose randomly
            int titleid = isMale ? Math.max(r.nextInt(3) + 2, 3) + 1 :
                    Math.min(r.nextInt(3) + (r.nextInt(4) == 0 ? 10 : 1), 5);
            int id = nextid++;
            ps.setInt(1, id);
            ps.setInt(2, titleid);
            ps.setString(3, fname);
            ps.setString(4, sname);
            ps.setDate(5, new Date(DateHelper.dateOf(1992, 1, 1) +
                    DateHelper.timespan(r.nextInt(5), r.nextInt(12), r.nextInt(31))));
            ps.addBatch();

            ps1.setInt(1, id);
            ps1.setString(2, emailGenerator(fname, sname, id, r));
            ps1.setString(3, "");
            // TODO: Add addresses!
            ps1.addBatch();
        }
        ps.executeBatch();
        ps1.executeBatch();

        // ## Populate Lecturers ## \\
        ps = conn.prepareStatement("INSERT INTO Lecturer (lid, titleid, forename, familyname) VALUES (?,?,?,?)");

        nextid = lidmin + r.nextInt(lidmax - lidmin);
        for (int i = 0; i < lcount; i++) {
            // TODO: Remove redundant code
            boolean isMale = r.nextBoolean();
            String fname = isMale ? boys[r.nextInt(boys.length)] : girls[r.nextInt(girls.length)],
                   sname = surnames[r.nextInt(surnames.length)];
            int titleid = isMale ? Math.max(r.nextInt(3) + 2, 3) + 1 :
                    Math.min(r.nextInt(3) + (r.nextInt(4) == 0 ? 10 : 1), 5);

            ps.setInt(1, nextid++);
            ps.setInt(2, titleid); // Higher chance to be Dr
            ps.setString(3, fname);
            ps.setString(4, sname);
            ps.addBatch();
        }
        ps.executeBatch();
    }

    private static String emailGenerator(String forename, String surname, int idnum, Random r) {
        String[] emailseps = {"", "-", "_", "."};
        String[] domains = { "gmail.com", "hotmail.com", "live.co.uk", "outlook.com",
                "bham.ac.uk", "student.bham.ac.uk", "cs.bham.ac.uk" };

        if (r.nextInt(4) == 0) {
            if (r.nextInt(3) == 0)
                forename = forename.substring(0, 2);
            else
                forename = forename.substring(0, 1);
        }
        if (r.nextInt(2) == 0) {
            String temp = forename;
            forename = surname;
            surname = temp;
        }
        if(idnum != 0 && r.nextInt(4) == 0)
            surname = idnum + "";

        return forename + emailseps[r.nextInt(emailseps.length)] + surname +
                '@' + domains[r.nextInt(domains.length)];
    }

    public static boolean executeSQLFile(Connection conn, String path) throws SQLException, IOException {
        String query = IOHelper.loadFile(path);
        return conn.createStatement().execute(query);
    }
}
