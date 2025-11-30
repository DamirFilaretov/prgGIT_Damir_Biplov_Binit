import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseHelper {

    public static Connection getConnection(String dbName) throws SQLException {
        // Ensure MySQL driver is available on the classpath when running (e.g., java -cp .:mysql-connector-j-<version>.jar EmployeeSearchFrame on macOS/Linux or java -cp .;mysql-connector-j-<version>.jar EmployeeSearchFrame on Windows).
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found. Make sure mysql-connector-j is on the classpath.", e);
        }
        DriverManager.setLoginTimeout(5);

        Properties props = new Properties();
        File file = new File("database.prop.properties");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
            } catch (IOException e) {
                throw new SQLException("Failed to load database properties", e);
            }
        } else {
            throw new SQLException("database.prop.properties not found in working directory");
        }

        String host = props.getProperty("db.host");
        String port = props.getProperty("db.port", "3306");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        if (isNullOrEmpty(host) || isNullOrEmpty(user) || isNullOrEmpty(password)) {
            throw new SQLException("Database configuration missing in database.prop.properties (db.host/db.user/db.password)");
        }

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?connectTimeout=5000&socketTimeout=5000";
        System.out.println("Connecting to: " + url);
        return DriverManager.getConnection(url, user, password);
    }

    public static List<String> loadDepartments(String dbName) throws SQLException {
        List<String> departments = new ArrayList<>();
        String sql = "SELECT DNAME FROM DEPARTMENT";
        try (Connection conn = getConnection(dbName);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                departments.add(rs.getString(1));
            }
        }
        return departments;
    }

    public static List<String> loadProjects(String dbName) throws SQLException {
        List<String> projects = new ArrayList<>();
        String sql = "SELECT PNAME FROM PROJECT";
        try (Connection conn = getConnection(dbName);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                projects.add(rs.getString(1));
            }
        }
        return projects;
    }

    public static List<String> searchEmployees(
            String dbName,
            List<String> selectedDepts,
            List<String> selectedProjects,
            boolean notDept,
            boolean notProject) throws SQLException {

        List<String> employees = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT e.FNAME, e.LNAME ")
           .append("FROM EMPLOYEE e ")
           .append("JOIN DEPARTMENT d ON e.DNO = d.DNUMBER ")
           .append("JOIN WORKS_ON w ON e.SSN = w.ESSN ")
           .append("JOIN PROJECT p ON w.PNO = p.PNUMBER ")
           .append("WHERE 1=1 ");

        if (selectedDepts != null && !selectedDepts.isEmpty()) {
            sql.append("AND d.DNAME ");
            sql.append(notDept ? "NOT IN (" : "IN (");
            sql.append(makePlaceholders(selectedDepts.size()));
            sql.append(") ");
        }

        if (selectedProjects != null && !selectedProjects.isEmpty()) {
            sql.append("AND p.PNAME ");
            sql.append(notProject ? "NOT IN (" : "IN (");
            sql.append(makePlaceholders(selectedProjects.size()));
            sql.append(") ");
        }

        try (Connection conn = getConnection(dbName);
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (selectedDepts != null) {
                for (String dept : selectedDepts) {
                    stmt.setString(index++, dept);
                }
            }
            if (selectedProjects != null) {
                for (String project : selectedProjects) {
                    stmt.setString(index++, project);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String fname = rs.getString(1);
                    String lname = rs.getString(2);
                    employees.add(fname + " " + lname);
                }
            }
        }

        return employees;
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    private static String makePlaceholders(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("?");
            if (i < count - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
