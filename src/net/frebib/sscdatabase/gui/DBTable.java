package net.frebib.sscdatabase.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBTable extends JPanel {
    private Connection conn;
    private JTable table;

    public DBTable(Connection conn) {
        super();
        this.conn = conn;

        this.table = new JTable();
        setLayout(new BorderLayout());
		add(new JScrollPane(table), BorderLayout.CENTER);

        try {
            fetchData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchData() throws SQLException {
        String stmt =
                "SELECT Student.sid, title, Student.forename, Student.familyname, dob, StudentContact.email, " +
                        "address, Lecturer.titleid, Lecturer.forename, Lecturer.familyname " +
                        "FROM Student " +
                        "LEFT JOIN StudentContact ON Student.sid=StudentContact.sid " +
                        "LEFT JOIN Title ON Student.titleid=Title.titleid " +
                        "LEFT JOIN Tutor ON Student.sid=Tutor.sid " +
                        "LEFT JOIN Lecturer ON Tutor.lid=Lecturer.lid";
        PreparedStatement ps = conn.prepareStatement(stmt);
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsm = rs.getMetaData();

        String[] colnames = new String[rsm.getColumnCount()];
        for (int i = 1; i <= rsm.getColumnCount(); i++)
            colnames[i-1] = rsm.getColumnName(i);

        List<String[]> tableData = new ArrayList<String[]>();
        while (rs.next()) {
            String[] row = new String[rsm.getColumnCount()];
			row[0] = Integer.toString(rs.getInt(1));
			row[1] = rs.getString(2);
            row[2] = rs.getString(3);
            row[3] = rs.getString(4);
            row[4] = rs.getDate(5).toString();
            row[5] = rs.getString(6);
			row[6] = rs.getString(7);
            row[7] = rs.getInt(8) + "";
            row[8] = rs.getString(9);
            row[9] = rs.getString(10);
            tableData.add(row);
		}

        TableModel tm = new DefaultTableModel(tableData.toArray(new String[tableData.size()][]), colnames);
        this.table.setModel(tm);
    }
}
