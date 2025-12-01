import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseHelper {

    // Helper method to establish connection
    public static Connection getConnection(String dbNameFromGui) throws Exception {
        Properties p = new Properties();
        // Load the properties file
        p.load(new FileInputStream("database.prop.properties"));

        String driver = p.getProperty("db.driver");
        String url = p.getProperty("db.url");
        String user = p.getProperty("db.user");
        String password = p.getProperty("db.password");

        // Load the driver class (Required by your properties file logic)
        Class.forName(driver);

        // If the user typed a specific database name in the GUI, we try to use that.
        // Otherwise, it uses the default "COMPANY" from the properties file.
        if (dbNameFromGui != null && !dbNameFromGui.trim().isEmpty()) {
            // This replaces 'COMPANY' in the url with the text from the text box
            // Example: jdbc:mysql://localhost:3307/COMPANY... becomes jdbc:mysql://localhost:3307/NewDB...
            url = url.replace("COMPANY", dbNameFromGui);
        }

        return DriverManager.getConnection(url, user, password);
    }

    public static List<String> getDepartments(String dbName) throws Exception {
        List<String> list = new ArrayList<>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection(dbName);
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT Dname FROM DEPARTMENT ORDER BY Dname");

            while (rs.next()) {
                list.add(rs.getString("Dname"));
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        }
        return list;
    }

    public static List<String> getProjects(String dbName) throws Exception {
        List<String> list = new ArrayList<>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection(dbName);
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT Pname FROM PROJECT ORDER BY Pname");

            while (rs.next()) {
                list.add(rs.getString("Pname"));
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        }
        return list;
    }
}