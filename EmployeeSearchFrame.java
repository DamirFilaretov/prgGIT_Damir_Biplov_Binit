
/** Git Progamming assignment
 * Course: Database systems
 *
 */
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.sql.*;
import java.io.FileReader;
import java.util.Properties;

public class EmployeeSearchFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtDatabase;
    private JList<String> lstDepartment;
    private DefaultListModel<String> department = new DefaultListModel<String>();
    private JList<String> lstProject;
    private DefaultListModel<String> project = new DefaultListModel<String>();
    private JTextArea textAreaEmployee;

    private JCheckBox chckbxNotDept;
    private JCheckBox chckbxNotProject;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    EmployeeSearchFrame frame = new EmployeeSearchFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //method to connect to the database
    private Connection getConnection() throws Exception {
        FileReader reader = new FileReader("database.prop.properties");
        Properties p = new Properties();
        p.load(reader);
        String dbuser = p.getProperty("db.user");
        String dbpassword = p.getProperty("db.password");
        String dburl = p.getProperty("db.url");
        
        return DriverManager.getConnection(dburl, dbuser, dbpassword);
    }

    /**
     * Create the frame.
     */
    public EmployeeSearchFrame() {
        setTitle("Employee Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 347);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Database:");
        lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblNewLabel.setBounds(21, 23, 59, 14);
        contentPane.add(lblNewLabel);

        txtDatabase = new JTextField();
        txtDatabase.setBounds(90, 20, 193, 20);
        contentPane.add(txtDatabase);
        txtDatabase.setColumns(10);

        JButton btnDBFill = new JButton("Fill");
        /**
         * The btnDBFill should fill the department and project JList with the
         * departments and projects from your entered database name.
         */
        btnDBFill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Connection con = null;
                Statement stmt = null;
                ResultSet rs = null;
                
                try {
                    department.clear();
                    project.clear();
                    
                    con = getConnection();
                    stmt = con.createStatement();
                    
                    // 1. Get Departments
                    rs = stmt.executeQuery("SELECT Dname FROM DEPARTMENT ORDER BY Dname");
                    while (rs.next()) {
                        department.addElement(rs.getString("Dname"));
                    }
                    rs.close();
                    
                    // 2. Get Projects
                    rs = stmt.executeQuery("SELECT Pname FROM PROJECT ORDER BY Pname");
                    while (rs.next()) {
                        project.addElement(rs.getString("Pname"));
                    }
                    
                    textAreaEmployee.setText("Database connected. Lists filled.");
                    
                } catch (Exception ex) {
                    textAreaEmployee.setText("Error filling lists:\n" + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    // Clean up resources
                    try { if (rs != null) rs.close(); } catch (SQLException se) {}
                    try { if (stmt != null) stmt.close(); } catch (SQLException se) {}
                    try { if (con != null) con.close(); } catch (SQLException se) {}
                }
            }
        });
        
        btnDBFill.setFont(new Font("Times New Roman", Font.BOLD, 12));
        btnDBFill.setBounds(307, 19, 68, 23);
        contentPane.add(btnDBFill);
        
        JLabel lblDepartment = new JLabel("Department");
        lblDepartment.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblDepartment.setBounds(52, 63, 89, 14);
        contentPane.add(lblDepartment);
        
        JLabel lblProject = new JLabel("Project");
        lblProject.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblProject.setBounds(255, 63, 47, 14);
        contentPane.add(lblProject);
        
        lstProject = new JList<String>(new DefaultListModel<String>());
        lstProject.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lstProject.setModel(project);
        lstProject.setBounds(225, 84, 150, 42);
        contentPane.add(lstProject);
        
        chckbxNotDept = new JCheckBox("Not");
        chckbxNotDept.setBounds(71, 133, 59, 23);
        contentPane.add(chckbxNotDept);
        
        chckbxNotProject = new JCheckBox("Not");
        chckbxNotProject.setBounds(270, 133, 59, 23);
        contentPane.add(chckbxNotProject);
        
        lstDepartment = new JList<String>(new DefaultListModel<String>());
        lstDepartment.setBounds(36, 84, 172, 40);
        contentPane.add(lstDepartment);
        lstDepartment.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lstDepartment.setModel(department);
        
        JLabel lblEmployee = new JLabel("Employee");
        lblEmployee.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblEmployee.setBounds(52, 179, 89, 14);
        contentPane.add(lblEmployee);
        
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Connection con = null;
                PreparedStatement pstmt = null;
                ResultSet rs = null;

                try {
                    con = getConnection();
                    
                    // Base Query
                    StringBuilder query = new StringBuilder();
                    query.append("SELECT DISTINCT E.Fname, E.Lname FROM EMPLOYEE E ");
                    query.append("JOIN DEPARTMENT D ON E.Dno = D.Dnumber ");
                    query.append("JOIN WORKS_ON W ON E.Ssn = W.Essn ");
                    query.append("JOIN PROJECT P ON W.Pno = P.Pnumber ");
                    query.append("WHERE 1=1 ");

                    String selectedDept = lstDepartment.getSelectedValue();
                    String selectedProj = lstProject.getSelectedValue();

                    // Logic for Department Selection
                    if (selectedDept != null) {
                        String operator = chckbxNotDept.isSelected() ? "!=" : "=";
                        query.append("AND D.Dname ").append(operator).append(" ? ");
                    }

                    // Logic for Project Selection
                    if (selectedProj != null) {
                        String operator = chckbxNotProject.isSelected() ? "!=" : "=";
                        query.append("AND P.Pname ").append(operator).append(" ? ");
                    }

                    pstmt = con.prepareStatement(query.toString());

                
                    int paramIndex = 1;
                    if (selectedDept != null) {
                        pstmt.setString(paramIndex++, selectedDept);
                    }
                    if (selectedProj != null) {
                        pstmt.setString(paramIndex++, selectedProj);
                    }

                    rs = pstmt.executeQuery();
                    
                    // Display Results
                    StringBuilder results = new StringBuilder();
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        results.append(rs.getString("Fname"))
                               .append(" ")
                               .append(rs.getString("Lname"))
                               .append("\n");
                    }
                    
                    if (!found) {
                        textAreaEmployee.setText("No employees found matching criteria.");
                    } else {
                        textAreaEmployee.setText(results.toString());
                    }

                } catch (Exception ex) {
                    textAreaEmployee.setText("Error searching:\n" + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    try { if (rs != null) rs.close(); } catch (SQLException se) {}
                    try { if (pstmt != null) pstmt.close(); } catch (SQLException se) {}
                    try { if (con != null) con.close(); } catch (SQLException se) {}
                }
            }
        });
        btnSearch.setBounds(80, 276, 89, 23);
        contentPane.add(btnSearch);
        
        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textAreaEmployee.setText("");
                lstDepartment.clearSelection();
                lstProject.clearSelection();
                chckbxNotDept.setSelected(false);
                chckbxNotProject.setSelected(false);
            }
        });
        btnClear.setBounds(236, 276, 89, 23);
        contentPane.add(btnClear);
        
        textAreaEmployee = new JTextArea();
        textAreaEmployee.setBounds(36, 197, 339, 68);
        contentPane.add(textAreaEmployee);
    }
}
