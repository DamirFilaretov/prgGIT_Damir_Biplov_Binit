# Programming assignment for class CSCI4055

**Description:**
A Java GUI application that connects to a MySQL database to manage and search for employee records. This application allows users to filter employees based on their associated Departments and Projects using a dynamic search interface.

## 🚀 Features
* **GUI Interface:** User-friendly interface built with Java.
* **Dynamic Database Connection:** Connect to local MySQL databases dynamically.
* **Filtering:** Filter employees by selecting specific Departments and Projects.

## 🛠️ Prerequisites
Before running this project, ensure you have the following installed:
* **Java Development Kit (JDK):** Version 8 or higher.
* **MySQL Server:** Locally installed and running.
* **MySQL Connector/J:** The JDBC driver added to your project's classpath (referenced libraries).
* **Visual Studio Code or Any other IDEs**.

## ⚙️ Configuration (Important)

**Note:** For security reasons, the database configuration file ("database.prop.properties") is **not** included in this repository. You must create this file manually to connect to your local database.


## 🗄️ Step 1: Database Setup (First Time Only)

This project requires a specific database schema to work. You must run the included SQL script to create the database and tables.

1.  Locate the file **`company.sql`** in this repository.
2.  **Run the script** using one of the following methods:
    * **Method A (MySQL Workbench):** Open Workbench, go to *File -> Open SQL Script*, select `company.sql`, and click the Lightning Bolt icon to execute.
    * **Method B (VS Code):** Open the file in VS Code (with the MySQL extension installed), right-click inside the file, and select "Run MySQL Script".
    * **Method C (Terminal):** Run the command:
        ```bash
        mysql -u root -p < company.sql
        ```

### Step 2: Create the Properties File
1.  Navigate to the root folder of this project.
2.  Create a new file named: `database.prop.properties`
3.  Paste the following content into the file:
   ```properties
# Connection URL to local mysql (Keep 'COMPANY' as the placeholder)
db.url=jdbc:mysql://localhost:3306/COMPANY

# Your MySQL Username
db.user=root

# Your MySQL Password
db.password=YOUR_ACTUAL_PASSWORD

# The JDBC Driver class
db.driver=com.mysql.cj.jdbc.Driver
```

## 🚀 How to Run
Open the project in Visual Studio Code.

Ensure the mysql-connector-java-x.x.xx.jar file is in your "Referenced Libraries".

Open EmployeeSearchFrame.java.

Run the file (Click the "Run" button or press F5).

In the Application:

Enter your database name (default is COMPANY) in the text box.

Click Fill to load Departments and Projects.

Select items from the lists and click Search.

## 🐛 Troubleshooting
"Driver not found" error: Make sure you have downloaded the MySQL JDBC Connector jar file and added it to your VS Code "Referenced Libraries".

"Access denied for user" error: Check your database.prop.properties file. Ensure the password matches your local MySQL setup.

"Communication link failure": Ensure your MySQL server is actually running.
