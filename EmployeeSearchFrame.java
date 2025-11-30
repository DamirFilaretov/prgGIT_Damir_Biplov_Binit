
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
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;

public class EmployeeSearchFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtDatabase;
    private JList<String> lstDepartment;
    private DefaultListModel<String> department = new DefaultListModel<String>();
    private JList<String> lstProject;
    private DefaultListModel<String> project = new DefaultListModel<String>();
    private JTextArea textAreaEmployee;

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
        /**
         * The btnDBFill should fill the department and project JList with the
         * departments and projects from your entered database name.
         */
        btnDBFill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String dbName = txtDatabase.getText().trim();
                department.clear();
                project.clear();
                try {
                    List<String> depts = DatabaseHelper.loadDepartments(dbName);
                    for (String d : depts) {
                        department.addElement(d);
                    }
                    List<String> projs = DatabaseHelper.loadProjects(dbName);
                    for (String p : projs) {
                        project.addElement(p);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(EmployeeSearchFrame.this,
                            "The database could not be opened or data could not be loaded.",
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
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
        lstProject.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane projectScrollPane = new JScrollPane(lstProject);
        projectScrollPane.setBounds(225, 84, 150, 42);
        contentPane.add(projectScrollPane);

        JCheckBox chckbxNotDept = new JCheckBox("Not");
        chckbxNotDept.setBounds(71, 133, 59, 23);
        contentPane.add(chckbxNotDept);

        JCheckBox chckbxNotProject = new JCheckBox("Not");
        chckbxNotProject.setBounds(270, 133, 59, 23);
        contentPane.add(chckbxNotProject);

        lstDepartment = new JList<String>(new DefaultListModel<String>());
        lstDepartment.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lstDepartment.setModel(department);
        lstDepartment.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane departmentScrollPane = new JScrollPane(lstDepartment);
        departmentScrollPane.setBounds(36, 84, 172, 42);
        contentPane.add(departmentScrollPane);

        JLabel lblEmployee = new JLabel("Employee");
        lblEmployee.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblEmployee.setBounds(52, 179, 89, 14);
        contentPane.add(lblEmployee);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String dbName = txtDatabase.getText().trim();
                List<String> selectedDepts = lstDepartment.getSelectedValuesList();
                List<String> selectedProjects = lstProject.getSelectedValuesList();
                boolean notDept = chckbxNotDept.isSelected();
                boolean notProject = chckbxNotProject.isSelected();
                btnSearch.setEnabled(false);
                textAreaEmployee.setText("Searching...");
                SwingWorker<List<String>, Void> worker = new SwingWorker<List<String>, Void>() {
                    @Override
                    protected List<String> doInBackground() throws Exception {
                        return DatabaseHelper.searchEmployees(dbName, selectedDepts, selectedProjects, notDept, notProject);
                    }

                    @Override
                    protected void done() {
                        btnSearch.setEnabled(true);
                        try {
                            List<String> employees = get();
                            if (employees.isEmpty()) {
                                textAreaEmployee.setText("(no employees found)");
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (String emp : employees) {
                                    sb.append(emp).append("\n");
                                }
                                textAreaEmployee.setText(sb.toString().trim());
                            }
                        } catch (Exception ex) {
                            textAreaEmployee.setText("");
                            JOptionPane.showMessageDialog(EmployeeSearchFrame.this,
                                    "Error searching employees: " + ex.getMessage(),
                                    "Database Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();
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
        JScrollPane employeeScrollPane = new JScrollPane(textAreaEmployee);
        employeeScrollPane.setBounds(36, 197, 339, 68);
        contentPane.add(employeeScrollPane);
    }
}
