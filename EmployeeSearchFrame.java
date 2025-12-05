import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane; 
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.sql.*;
import java.util.List;

public class EmployeeSearchFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtDatabase;
    private JList<String> lstDepartment;
    private DefaultListModel<String> departmentModel = new DefaultListModel<String>();
    private JList<String> lstProject;
    private DefaultListModel<String> projectModel = new DefaultListModel<String>();
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
        btnDBFill.setFont(new Font("Times New Roman", Font.BOLD, 12));
        btnDBFill.setBounds(307, 19, 68, 23);
        contentPane.add(btnDBFill);
        
        // ---------------------------------------------------------
        // UPDATED FILL BUTTON ACTION
        // ---------------------------------------------------------
        btnDBFill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // 1. Clear existing data (Reset)
                    departmentModel.clear();
                    projectModel.clear();
                    textAreaEmployee.setText("");

                    // 2. Get the database name from the text field
                    String dbName = txtDatabase.getText();

                    // 3. Use DatabaseHelper to get data
                    List<String> depts = DatabaseHelper.getDepartments(dbName);
                    List<String> projs = DatabaseHelper.getProjects(dbName);

                    // 4. Fill the DefaultListModels
                    for (String d : depts) {
                        departmentModel.addElement(d);
                    }
                    for (String p : projs) {
                        projectModel.addElement(p);
                    }

                    textAreaEmployee.setText("Database connected. Lists filled.");

                } catch (Exception ex) {
                    // ERROR POP-UP as requested
                    JOptionPane.showMessageDialog(null, 
                        "The database could not be opened.\nError: " + ex.getMessage(), 
                        "Database Error", 
                        JOptionPane.ERROR_MESSAGE);
                    
                    ex.printStackTrace();
                }
            }
        });

        JLabel lblDepartment = new JLabel("Department");
        lblDepartment.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblDepartment.setBounds(52, 63, 89, 14);
        contentPane.add(lblDepartment);

        JLabel lblProject = new JLabel("Project");
        lblProject.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblProject.setBounds(255, 63, 47, 14);
        contentPane.add(lblProject);

        lstProject = new JList<String>();
        lstProject.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lstProject.setModel(projectModel);
        lstProject.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollProject = new JScrollPane(lstProject);
        scrollProject.setBounds(220, 84, 155, 42);
        contentPane.add(scrollProject);

        chckbxNotDept = new JCheckBox("Not");
        chckbxNotDept.setBounds(71, 133, 59, 23);
        contentPane.add(chckbxNotDept);

        chckbxNotProject = new JCheckBox("Not");
        chckbxNotProject.setBounds(270, 133, 59, 23);
        contentPane.add(chckbxNotProject);

        lstDepartment = new JList<String>();
        lstDepartment.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lstDepartment.setModel(departmentModel);
        lstDepartment.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JScrollPane scrollDept = new JScrollPane(lstDepartment);
        scrollDept.setBounds(36, 84, 150, 42);
        contentPane.add(scrollDept);

        JLabel lblEmployee = new JLabel("Employee");
        lblEmployee.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblEmployee.setBounds(52, 179, 89, 14);
        contentPane.add(lblEmployee);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(80, 276, 89, 23);
        contentPane.add(btnSearch);
        
        // Search Button Logic
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Connection con = null;
                PreparedStatement pstmt = null;
                ResultSet rs = null;
            
                try {
                    // Read database name entered by the user
                    String dbName = txtDatabase.getText();
            
                    // Establish connection using helper method
                    con = DatabaseHelper.getConnection(dbName);
            
                    // Begin building the SQL query dynamically
                    StringBuilder query = new StringBuilder();
                    query.append("SELECT DISTINCT E.Fname, E.Lname FROM EMPLOYEE E ");
                    query.append("JOIN DEPARTMENT D ON E.Dno = D.Dnumber ");
                    query.append("WHERE 1=1 ");   // Base condition to simplify appending ANDs
            
                    // Get all selected Departments and Projects from the GUI lists
                    java.util.List<String> selectedDepts = lstDepartment.getSelectedValuesList();
                    java.util.List<String> selectedProjs = lstProject.getSelectedValuesList();
            
                    // --------------------------
                    // Department Filtering Logic
                    // --------------------------
                    if (!selectedDepts.isEmpty()) {
                        // Choose IN or NOT IN depending on checkbox
                        query.append("AND D.Dname ")
                             .append(chckbxNotDept.isSelected() ? "NOT IN (" : "IN (");
            
                        // Add placeholders for prepared statement (?, ?, ?)
                        for (int i = 0; i < selectedDepts.size(); i++)
                            query.append(i == 0 ? "?" : ", ?");
            
                        query.append(") ");
                    }
            
                    // Project Filtering Logic
                    if (!selectedProjs.isEmpty()) {
                        // If NOT is selected → exclude employees who work on these projects
                        if (chckbxNotProject.isSelected()) {
                            query.append("AND E.Ssn NOT IN (");
                        } else {
                            // Otherwise include employees who work on the selected projects
                            query.append("AND E.Ssn IN (");
                        }
            
                        // Subquery that finds employees who work on selected projects
                        query.append("SELECT W.Essn FROM WORKS_ON W ");
                        query.append("JOIN PROJECT P ON W.Pno = P.Pnumber ");
                        query.append("WHERE P.Pname IN (");
            
                        for (int i = 0; i < selectedProjs.size(); i++) {
                            query.append(i == 0 ? "?" : ", ?");
                        }
            
                        query.append(")) "); // closes IN (...) and the subquery
                    }
            
                    // Prepare the final SQL command with all placeholders
                    pstmt = con.prepareStatement(query.toString());
            
                    int paramIndex = 1;
            
                    // First fill department names
                    for (String dept : selectedDepts)
                        pstmt.setString(paramIndex++, dept);
            
                    // Then fill project names (order is important)
                    for (String proj : selectedProjs)
                        pstmt.setString(paramIndex++, proj);
            
                    // Execute the SQL query
                    rs = pstmt.executeQuery();
            
                    // Build result text for display
                    StringBuilder results = new StringBuilder();
                    boolean found = false;
            
                    while (rs.next()) {
                        found = true;
                        results.append(rs.getString("Fname"))
                               .append(" ")
                               .append(rs.getString("Lname"))
                               .append("\n");
                    }
            
                    // If no results, notify user; otherwise show the list
                    if (!found)
                        textAreaEmployee.setText("No employees found matching criteria.");
                    else
                        textAreaEmployee.setText(results.toString());
            
                } catch (Exception ex) {
                    // Show any SQL or connection errors
                    JOptionPane.showMessageDialog(null, "Error Searching: " + ex.getMessage());
                } finally {
                    // Always close all SQL resources to avoid memory/database leaks
                    try { if (rs != null) rs.close(); } catch (SQLException se) {}
                    try { if (pstmt != null) pstmt.close(); } catch (SQLException se) {}
                    try { if (con != null) con.close(); } catch (SQLException se) {}
                }
            }
            });
            
            // ----------------------------------------
            // Clear button: resets all selections and output
            // ----------------------------------------
            JButton btnClear = new JButton("Clear");
            btnClear.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    textAreaEmployee.setText("");        // Clear output
                    lstDepartment.clearSelection();      // Clear department list selection
                    lstProject.clearSelection();         // Clear project list selection
                    chckbxNotDept.setSelected(false);    // Reset NOT dept checkbox
                    chckbxNotProject.setSelected(false); // Reset NOT project checkbox
                }
            });
            btnClear.setBounds(236, 276, 89, 23);
            contentPane.add(btnClear);
            
            // Text area (inside scroll pane) for showing results
            textAreaEmployee = new JTextArea();
            JScrollPane scrollEmployee = new JScrollPane(textAreaEmployee);
            scrollEmployee.setBounds(36, 197, 339, 68);
            contentPane.add(scrollEmployee);
            }
            }
