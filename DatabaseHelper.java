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
    // Create a list to store department names
    List<String> list = new ArrayList<>();
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
        // Establish connection to the database
        con = getConnection(dbName);

        // Create a simple SQL statement
        stmt = con.createStatement();

        // Execute query to retrieve all department names in alphabetical order
        rs = stmt.executeQuery("SELECT Dname FROM DEPARTMENT ORDER BY Dname");

        // Loop through returned rows and add each department name to the list
        while (rs.next()) {
            list.add(rs.getString("Dname"));
        }
    } finally {
        // Close ResultSet, Statement, and Connection to prevent memory leaks
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (con != null) con.close();
    }

    // Return list of department names
    return list;
}

public static List<String> getProjects(String dbName) throws Exception {
    // Create a list to store project names
    List<String> list = new ArrayList<>();
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
        // Establish connection to the database
        con = getConnection(dbName);

        // Create a simple SQL statement
        stmt = con.createStatement();

        // Execute query to retrieve all project names in alphabetical order
        rs = stmt.executeQuery("SELECT Pname FROM PROJECT ORDER BY Pname");

        // Loop through returned rows and add each project name to the list
        while (rs.next()) {
            list.add(rs.getString("Pname"));
        }
    } finally {
        // Close ResultSet, Statement, and Connection to prevent memory leaks
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (con != null) con.close();
    }

    // Return list of project names
    return list;
}
