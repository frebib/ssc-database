package net.frebib.sscdatabase.gui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class DBView {
    private Connection conn;
    protected DBTable table;

    private JFrame frame;

    public DBView(Connection conn, String title) {
        this.frame = new JFrame(title);
        this.conn = conn;
        this.table = new DBTable(conn);

        frame.setLayout(new BorderLayout());
        frame.add(table, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    public void setVisible(boolean b) {frame.setVisible(b);}
}
