package net.frebib.sscdatabase;

import net.frebib.sscdatabase.DBConfig;
import net.frebib.sscdatabase.IOHelper;

import java.io.IOException;
import java.sql.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class InitDB {

    public static void createDB(Connection conn, DBConfig cfg) throws SQLException, IOException {
        try {
            executeSQLFile(conn, cfg.getProp("clearsql"));
        } catch (SQLException e) {
            // Ignore the error if it is trying to remove a table that doesn't exist.
            // It's not the end of the world
            if (!Pattern.matches("^/ERROR: table \".*\" does not exist$", e.getMessage()))
                throw e;
        }
        executeSQLFile(conn, cfg.getProp("createsql"));
        populateDB(conn, cfg);
    }

    public static boolean populateDB(Connection conn, DBConfig cfg) throws SQLException, IOException {
        String[] forenames = IOHelper.loadFileArray(cfg.getProp("forenames"));
        String[] surnames = IOHelper.loadFileArray(cfg.getProp("surnames"));
        int count = cfg.getInt("randnames");
        int initmin = cfg.getInt("initidmin");
        int initmax = cfg.getInt("initidmax");

        Random r = new Random();
        int nextid = initmin + r.nextInt(initmax - initmin);
        long oneyear = 31556952000L;

        ResultSet rs = conn.createStatement().executeQuery("SELECT count(*) FROM Title");
        rs.next();
        int titlecount = rs.getInt(1);

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Student (sid, titleid, forename, familyname, dob) VALUES (?, ?, ?, ?, ?)");
        for (int i = 0; i < count; i++) {
            ps.setInt(1, nextid++);
            ps.setInt(2, r.nextInt(titlecount) + 1);
            ps.setString(3, forenames[r.nextInt(forenames.length)]);
            ps.setString(4, surnames[r.nextInt(surnames.length)]);
            ps.setDate(5, new Date(631152000000L + (long)((oneyear * 6) * r.nextDouble())));
            ps.addBatch();
        }
        ps.executeBatch();

        return true;
    }

    public static boolean executeSQLFile(Connection conn, String path) throws SQLException, IOException {
        String query = IOHelper.loadFile(path);
        return conn.createStatement().execute(query);
    }
}
