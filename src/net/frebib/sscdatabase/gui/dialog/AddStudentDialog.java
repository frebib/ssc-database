package net.frebib.sscdatabase.gui.dialog;

import java.awt.event.ActionEvent;
import java.sql.Connection;

public class AddStudentDialog extends StudentDialog {
    private TutorDialog tutorDialog;

    public AddStudentDialog(Connection conn) {
        super(conn);

        tutorDialog = new TutorDialog(conn, this);
    }

    @Override
    protected void acceptClicked(ActionEvent e) {

    }

    @Override
    protected void cancelClicked(ActionEvent e) {

    }

    @Override
    protected void addTutorClicked(ActionEvent e) {
        tutorDialog.setLocationRelativeTo(null);
        tutorDialog.setVisible(true);
    }
}
