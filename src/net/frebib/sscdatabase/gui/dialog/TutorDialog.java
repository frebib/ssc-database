package net.frebib.sscdatabase.gui.dialog;

import net.frebib.sscdatabase.data.ComboBoxPair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TutorDialog extends JDialog {
    private JPanel panel;
    protected JComboBox<Object> tutors;
    protected JButton ok, cancel;
    private Connection conn;

    public TutorDialog(Connection conn, JFrame owner) {
        super(owner, "Edit Tutor");
        this.conn = conn;

        init();
    }

    private void init() {
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        ArrayList<Object> elements = new ArrayList<Object>();
        elements.add(new ComboBoxPair("Choose a tutor", -1));
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT lid, title, forename, familyname FROM Lecturer" +
                    " LEFT JOIN Title ON Lecturer.titleid=Title.titleid");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                elements.add(new ComboBoxPair(rs.getString(2) + " " + rs.getString(3) + " "
                        + rs.getString(4), rs.getInt(1)));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        tutors = new JComboBox<Object>(elements.toArray());

        c.gridwidth = 2;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(tutors, c);

        ok = new JButton("Ok");
        ok.addActionListener((ActionEvent e) -> okClicked(e));
        c.gridy = 1;
        c.gridwidth = 1;
        panel.add(ok, c);

        cancel = new JButton("Cancel");
        cancel.addActionListener((ActionEvent e) -> cancelClicked(e));
        c.gridx = 1;
        c.gridwidth = 1;
        panel.add(cancel, c);

        this.add(panel);
        this.pack();
    }

    private void okClicked(ActionEvent e) {
        this.setVisible(false);
    }
    private void cancelClicked(ActionEvent e) {
        tutors.setSelectedIndex(0);
        this.setVisible(false);
    }
}
