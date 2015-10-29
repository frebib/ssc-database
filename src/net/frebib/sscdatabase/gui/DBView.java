package net.frebib.sscdatabase.gui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class DBView extends JFrame {
    private Connection conn;
    private JPanel panel;
    protected DBTable table;

    public DBView(Connection conn, String title) {
        super(title);
        this.conn = conn;

        //this.setLayout(new GridLayout(1,1));
        this.table = new DBTable(conn);
        panel = new JPanel();
        panel.add(table);
        this.add(panel);

        this.pack();
    }
}
