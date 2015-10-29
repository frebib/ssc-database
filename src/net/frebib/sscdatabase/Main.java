package net.frebib.sscdatabase;

import javax.swing.*;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.util.Random;

public class Main {
    private final static String PROPFILE = "database.properties";

    private DBConfig cfg;
    private Connection conn;
    private String dbname;

    public Main() {
        this(PROPFILE);
    }
    public Main(String propFile) {
        try {
            cfg = DBConfig.load(propFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run() {
        // TODO: Create a connection GUI and pretty it up
        dbname = "jdbc:postgresql://" + cfg.getProp("host") + '/' + cfg.getProp("dbname");
        try {
            System.setProperty("jdbc.drivers", "org.postgresql.Driver");
            conn = DriverManager.getConnection(dbname, cfg.getProp("username"), cfg.getProp("password"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            InitDB.createDB(conn, cfg);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        new JFrame().setVisible(true);
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
