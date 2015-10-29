package net.frebib.sscdatabase.gui.dialog;

import com.sun.xml.internal.ws.util.StringUtils;
import net.frebib.sscdatabase.data.ComboBoxPair;
import net.frebib.sscdatabase.util.DateHelper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class AddStudentDialog extends StudentDialog {
    private TutorDialog tutorDialog;

    public AddStudentDialog(Connection conn) {
        super(conn);

        tutorDialog = new TutorDialog(conn, this);
    }

    @Override
    protected void acceptClicked(ActionEvent e) {
        String[] errors = validateFields();

        if (errors.length > 0) {
            JOptionPane.showMessageDialog(this, String.join("\n", errors),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        String stmt = "INSERT INTO Student (sid, titleid, forename, familyname, dob) " +
                "VALUES (?, ?, ?, ?, ?);" +
                "INSERT INTO StudentContact (sid, email, address) " +
                "VALUES (?, ?, ?);" +
                "INSERT INTO StudentRegistration (sid, yearofstudy, regtypeid) " +
                "VALUES (?, ?, ?);";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(stmt);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "There was a critical exception and we " +
                            "need to close...\n" + ex.toString(),
                    "SQL Exception", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        int id = Integer.parseInt(sid.getText());
        String fname = forename.getText().trim();
        fname = fname.substring(0, 1).toUpperCase() + fname.substring(1);
        String sname = surname.getText().trim();
        sname = sname.substring(0, 1).toUpperCase() + sname.substring(1);

        try {
            ps.setInt(1, id);
            ps.setInt(6, id);
            ps.setInt(9, id);
            ps.setInt(2, ((ComboBoxPair)titles.getSelectedItem()).id);
            ps.setString(3, fname);
            ps.setString(4, sname);
            ps.setDate(5, new Date(dobchooser.getDate().getTime()));
            ps.setString(7, email.getText());
            ps.setString(8, address.getText());
            ps.setInt(10, Integer.parseInt(yearofstudy.getSelectedItem().toString()));
            ps.setInt(11, ((ComboBoxPair)regtypeid.getSelectedItem()).id);

            ps.execute();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "SQL Exception", JOptionPane.ERROR_MESSAGE);
            //ex.printStackTrace();
            //ex.getNextException().printStackTrace();
        }

        JOptionPane.showMessageDialog(this, "Database entry added successfully",
                "No problems here!", JOptionPane.INFORMATION_MESSAGE);
        this.setVisible(false);
    }

    @Override
    protected void cancelClicked(ActionEvent e) {
        this.setVisible(false);
        this.dispose();
    }

    @Override
    protected void addTutorClicked(ActionEvent e) {
        tutorDialog.setLocationRelativeTo(null);
        tutorDialog.setVisible(true);
    }

}
