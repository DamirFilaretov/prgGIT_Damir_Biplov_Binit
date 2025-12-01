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

### Step 1: Create the Properties File
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
