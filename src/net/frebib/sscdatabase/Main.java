package net.frebib.sscdatabase;

import net.frebib.sscdatabase.gui.DBView;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

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
            JOptionPane.showMessageDialog(null, "There was an error loading the config file...\n" +
                    e.toString(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void run() {
        // TODO: Create a connection GUI and pretty it up
        try {
            dbname = "jdbc:postgresql://" + cfg.getProp("host") + '/' + cfg.getProp("dbname");
            System.setProperty("jdbc.drivers", "org.postgresql.Driver");
            conn = DriverManager.getConnection(dbname, cfg.getProp("username"), cfg.getProp("password"));

            InitDB.createDB(conn, cfg);

            UIManager.setLookAndFeel(UIManager.getInstalledLookAndFeels()[3].getClassName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not connect to the database...\n" +
                            e.toString(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }

        DBView view = new DBView(conn, "Joe");
        view.setVisible(true);
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
