package net.frebib.sscdatabase;

import net.frebib.sscdatabase.util.DateHelper;
import net.frebib.sscdatabase.util.IOHelper;

import java.io.IOException;
import java.sql.*;
import java.util.Random;
import java.util.regex.Pattern;

public class InitDB {
    private static String[] boys, girls, surnames;

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
        boys = IOHelper.loadFileArray(cfg.getProp("boys"));
        girls = IOHelper.loadFileArray(cfg.getProp("girls"));
        surnames = IOHelper.loadFileArray(cfg.getProp("surnames"));

        Random r = new Random();
        PreparedStatement[] ps;

        int scount = cfg.getInt("scount");
        int sidmin = cfg.getInt("sidmin");
        int sidmax = cfg.getInt("sidmax");
        int lcount = cfg.getInt("lcount");
        int lidmin = cfg.getInt("lidmin");
        int lidmax = cfg.getInt("lidmax");
        int sstart = sidmin + r.nextInt(sidmax - sidmin);
        int lstart = lidmin + r.nextInt(lidmax - lidmin);

        ResultSet rs = conn.createStatement().executeQuery("SELECT count(*) FROM Title");
        rs.next();
        int titlecount = rs.getInt(1);


        // ## Populate Lecturers ## \\
        ps    = new PreparedStatement[2];
        ps[0] = conn.prepareStatement("INSERT INTO Lecturer (lid, titleid, forename, familyname) VALUES (?,?,?,?)");
        ps[1] = conn.prepareStatement("INSERT INTO LecturerContact (lid, office, email) VALUES (?,?,?)");

        int nextid = lstart;
        for (int i = 0; i < lcount; i++) {
            boolean isMale = r.nextBoolean();
            String forename = forenameGenerator(r, isMale);
            String surname  = surnames[r.nextInt(surnames.length)];
            int id = nextid++;
            ps[0].setInt(1, id);
            ps[0].setInt(2, titleIDGenerator(r, isMale, true));
            ps[0].setString(3, forename);
            ps[0].setString(4, surname);

            ps[1].setInt(1, id);
            ps[1].setString(2, String.format("%03d", r.nextInt(3) * 100 + r.nextInt(40) + 11));
            ps[1].setString(3, emailGenerator(forename, surname, id, r));

            for (PreparedStatement psx : ps)
                if (psx != null) psx.addBatch();
        }
        for (PreparedStatement psx : ps)
            if (psx != null) psx.executeBatch();


        // ## Populate Students ## \\
        ps    = new PreparedStatement[5];
        ps[0] = conn.prepareStatement("INSERT INTO Student (sid,titleid,forename,familyname,dob) VALUES (?,?,?,?,?)");
        ps[1] = conn.prepareStatement("INSERT INTO StudentContact (sid, email, address) VALUES (?,?,?)");
        ps[2] = conn.prepareStatement("INSERT INTO StudentRegistration (sid, yearofstudy, regtypeid) VALUES (?,?,?)");
        ps[3] = conn.prepareStatement("INSERT INTO NextOfKinContact (sid, name, email, address) VALUES (?,?,?,?)");
        ps[4] = conn.prepareStatement("INSERT INTO Tutor (sid, lid) VALUES (?,?)");

        nextid = sstart;
        for (int i = 0; i < scount; i++) {
            boolean isMale = r.nextBoolean();
            String forename = forenameGenerator(r, isMale);
            String surname  = surnames[r.nextInt(surnames.length)];
            int id = nextid++;
            ps[0].setInt(1, id);
            ps[0].setInt(2, titleIDGenerator(r, isMale, false));
            ps[0].setString(3, forename);
            ps[0].setString(4, surname);
            ps[0].setDate(5, new Date(DateHelper.dateOf(1992, 1, 1) +
                    DateHelper.timespan(r.nextInt(5), r.nextInt(12), r.nextInt(31))));

            ps[1].setInt(1, id);
            ps[1].setString(2, emailGenerator(forename, surname, id, r));
            ps[1].setString(3, "");                                       // TODO: Add addresses!

            ps[2].setInt(1, id);
            ps[2].setInt(2, r.nextInt(5) + 1);
            ps[2].setInt(3, r.nextInt(5) == 0 ? 1 : r.nextInt(2) + 2);

            String nokforename = forenameGenerator(r, r.nextBoolean());
            ps[3].setInt(1, id);
            ps[3].setString(2, nokforename + " " + surname);
            ps[3].setString(3, emailGenerator(nokforename, surname, 0, r));
            ps[3].setString(4, "");                                       // TODO: Add addresses!

            ps[4].setInt(1, id);
            ps[4].setInt(2, r.nextInt(lcount) + lstart);

            for (PreparedStatement psx : ps)
                if (psx != null) psx.addBatch();
        }
        for (PreparedStatement psx : ps)
            if (psx != null) psx.executeBatch();
    }

    private static String forenameGenerator(Random r, boolean isMale) {
        return isMale ? boys[r.nextInt(boys.length)] : girls[r.nextInt(girls.length)];
    }
    private static int titleIDGenerator(Random r, boolean isMale, boolean isLecturer) {
        if (r.nextInt(4) == 0 || isLecturer && r.nextBoolean())
            return 5;               // Dr is titleid=5

        if (isMale) return 4;        // If male & !Dr then must be Mr
        return r.nextInt(3) + 1;    // If female then should be Miss,Ms or Mrs
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
