package net.frebib.sscdatabase.gui.dialog;

import com.toedter.calendar.JDateChooser;
import net.frebib.sscdatabase.data.ComboBoxPair;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public abstract class StudentDialog extends JFrame {
    private JPanel studpanel, contactpanel, btnpanel;
    protected JTextField sid, forename, surname;
    protected JCheckBox chksid;
    protected JComboBox<Object> titles, regtypeid;
    protected JComboBox<Integer> yearofstudy;
    protected JDateChooser dobchooser;

    protected JTextField email;
    protected JTextArea address;

    private JButton accept, cancel, addtutor;

    protected Connection conn;

    public StudentDialog(Connection conn) {
        super("Add a Student");
        this.conn = conn;
        init(conn);
    }

    private void init(Connection conn) {
        studpanel = new JPanel(new GridBagLayout());
        Border title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "StudentForm Details");
        Border gap = BorderFactory.createEmptyBorder(6,6,6,6);
        Border brgap = BorderFactory.createEmptyBorder(0,0,6,0);
        Border lbrgap = BorderFactory.createEmptyBorder(0,6,6,0);
        studpanel.setBorder(BorderFactory.createCompoundBorder(gap, title));

        GridBagConstraints c = new GridBagConstraints();

        JLabel lblid = new JLabel("Student ID");
        lblid.setBorder(gap);
        lblid.setLabelFor(sid);
        studpanel.add(lblid, createGBC(0, 0));

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT sid FROM STUDENT ORDER BY sid DESC LIMIT 1");
            ResultSet rs = ps.executeQuery();
            rs.next();

            sid = new JTextField();
            sid.setText((rs.getInt(1) + 1) + "");
            sid.setEnabled(false);
            sid.setPreferredSize(new Dimension(120, sid.getMinimumSize().height));
            c = createGBC(1, 0);
            c.gridwidth = 1;
            studpanel.add(sid, c);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        chksid = new JCheckBox();
        chksid.setSelected(true);
        chksid.addActionListener(e -> { sid.setEnabled(!((JCheckBox)e.getSource()).isSelected()); });
        c = createGBC(2, 0);
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 0;
        studpanel.add(chksid, c);

        JLabel lbltitle = new JLabel("Title");
        lbltitle.setBorder(lbrgap);
        lbltitle.setLabelFor(titles);
        studpanel.add(lbltitle, createGBC(0, 1));

        ArrayList<ComboBoxPair> elements = new ArrayList<ComboBoxPair>();
        elements.add(new ComboBoxPair("Choose a title", -1));
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT titleid, title FROM Title");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                elements.add(new ComboBoxPair(rs.getString(2), rs.getInt(1)));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        titles = new JComboBox<Object>(elements.toArray());
        titles.setBorder(brgap);
        titles.setPreferredSize(new Dimension(180, titles.getMinimumSize().height));
        studpanel.add(titles, createGBC(1, 1));

        JLabel lblforename = new JLabel("Forename");
        lblforename.setBorder(lbrgap);
        lblforename.setLabelFor(forename);
        studpanel.add(lblforename, createGBC(0, 2));

        forename = new JTextField();
        forename.setPreferredSize(new Dimension(180, forename.getMinimumSize().height));
        studpanel.add(forename, createGBC(1, 2));

        JLabel lblsurname = new JLabel("Surname");
        lblsurname.setBorder(lbrgap);
        lblsurname.setLabelFor(surname);
        studpanel.add(lblsurname, createGBC(0, 3));

        surname = new JTextField();
        surname.setPreferredSize(new Dimension(180, surname.getMinimumSize().height));
        studpanel.add(surname, createGBC(1, 3));

        JLabel lbldob = new JLabel("Birthday");
        lbldob.setBorder(lbrgap);
        lbldob.setLabelFor(dobchooser);
        studpanel.add(lbldob, createGBC(0, 4));

        dobchooser = new JDateChooser();
        dobchooser.setPreferredSize(new Dimension(180, dobchooser.getMinimumSize().height));
        studpanel.add(dobchooser, createGBC(1, 4));

        JLabel lblyos = new JLabel("Study Year");
        lblyos.setBorder(lbrgap);
        lblyos.setLabelFor(yearofstudy);
        studpanel.add(lblyos, createGBC(0, 5));

        yearofstudy = new JComboBox<Integer>(new Integer[] { 1, 2, 3, 4, 5 });
        yearofstudy.setBorder(brgap);
        yearofstudy.setPreferredSize(new Dimension(180, yearofstudy.getMinimumSize().height));
        studpanel.add(yearofstudy, createGBC(1, 5));

        JLabel lblreg = new JLabel("Reg Type");
        lblreg.setBorder(lbrgap);
        lblreg.setLabelFor(regtypeid);
        studpanel.add(lblreg, createGBC(0, 6));

        elements = new ArrayList<ComboBoxPair>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT regtypeid, descr FROM RegistrationType");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                elements.add(new ComboBoxPair(rs.getString(2), rs.getInt(1)));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        regtypeid = new JComboBox<Object>(elements.toArray());
        regtypeid.setBorder(brgap);
        regtypeid.setPreferredSize(new Dimension(180, regtypeid.getMinimumSize().height));
        studpanel.add(regtypeid, createGBC(1, 6));


        /////////////////////////////

        contactpanel = new JPanel(new GridBagLayout());
        title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Contact");
        contactpanel.setBorder(BorderFactory.createCompoundBorder(gap, title));

        JLabel lblemail = new JLabel("Email");
        lblemail.setBorder(lbrgap);
        lblemail.setLabelFor(email);
        contactpanel.add(lblemail, createGBC(0, 0));

        email = new JTextField();
        email.setPreferredSize(new Dimension(180, email.getMinimumSize().height));
        contactpanel.add(email, createGBC(1, 0));

        JLabel lbladdr = new JLabel("Address");
        lbladdr.setBorder(lbrgap);
        lbladdr.setLabelFor(address);
        contactpanel.add(lbladdr, createGBC(0, 1));

        address = new JTextArea();
        address.setRows(6);
        address.setPreferredSize(new Dimension(180, address.getSize().height));
        contactpanel.add(address, createGBC(1, 1));

        addtutor = new JButton("Edit Tutor");
        addtutor.addActionListener((ActionEvent e) -> addTutorClicked(e));
        c = createGBC(0, 2);
        c.gridwidth = 2;
        contactpanel.add(addtutor, c);

        //////////////////////////

        GridLayout gl = new GridLayout(1, 2);
        gl.setHgap(12);
        btnpanel = new JPanel(gl);

        accept = new JButton("Ok");
        accept.addActionListener((ActionEvent e) -> acceptClicked(e));
        cancel = new JButton("Cancel");
        cancel.addActionListener((ActionEvent e) -> cancelClicked(e));
        btnpanel.add(accept);
        btnpanel.add(cancel);

        JPanel window = new JPanel(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 2;
        c.anchor = GridBagConstraints.NORTHWEST;
        window.add(studpanel, c);

        c.gridx = 1;
        c.gridheight = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTHEAST;
        window.add(contactpanel, c);

        c.gridx = 1;
        c.gridy = 1;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.EAST;
        window.add(btnpanel, c);

        this.add(window);
        this.pack();

        contactpanel.setPreferredSize(new Dimension(studpanel.getSize().width, studpanel.getSize().height));
        this.setMinimumSize(this.getSize());
        this.pack();
        this.setMaximumSize(this.getSize());
    }

    private GridBagConstraints createGBC(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = y == 0 ? 1 : 2;
        c.gridheight = 1;

        c.anchor = x == 0 ? GridBagConstraints.WEST : GridBagConstraints.EAST;
        c.fill = x == 0 ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;


        final Insets WEST_INSETS = new Insets(6, 0, 6, 6);
        final Insets EAST_INSETS = new Insets(6, 6, 6, 6);
        c.insets = (x == 0) ? WEST_INSETS : EAST_INSETS;
        c.weightx = (x == 0) ? 0.1 : 1.0;
        c.weighty = 1.0;
        return c;
    }

    public String[] validateFields() {
        ArrayList<String> errors = new ArrayList<>(4);
        if (sid.getText().isEmpty())
            errors.add("The student ID must not be empty. Please fill it in or allow" +
                    " the system to auto-generate an id");
        if (((ComboBoxPair)titles.getSelectedItem()).id < 1)
            errors.add("Please choose a title");

        if (forename.getText().isEmpty())
            errors.add("Please enter a first name/forename");
        if (surname.getText().isEmpty())
            errors.add("Please enter a family name/surname");
        if (dobchooser.getDate().equals(Date.from(Instant.EPOCH)))
            errors.add("Pleas enter your date of birth. You could not have been born today!");
        if (email.getText().isEmpty())
            errors.add("Please enter a valid email address");
        if (address.getText().isEmpty())
            errors.add("Please enter your home address");

        return errors.toArray(new String[errors.size()]);
    }

    protected abstract void acceptClicked(ActionEvent e);
    protected abstract void cancelClicked(ActionEvent e);
    protected abstract void addTutorClicked(ActionEvent e);
}
