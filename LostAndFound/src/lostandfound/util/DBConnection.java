package lostandfound.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection singleton for XAMPP MySQL.
 * XAMPP default: host=localhost, port=3306, user=root, password=(empty)
 */
public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/bisu_lost_found_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Manila";
    private static final String USER     = "root";
    private static final String PASSWORD = "";   // XAMPP default has no password

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        } catch (ClassNotFoundException e) {
            String msg = "[DB] MySQL Driver not found: " + e.getMessage()
                    + "\n[DB] Make sure mysql-connector-j JAR is added to Libraries.";
            System.err.println(msg);
            throw new IllegalStateException(msg, e);
        } catch (SQLException e) {
            String msg = "[DB] Connection error: " + e.getMessage()
                    + "\n[DB] Make sure XAMPP MySQL is running on port 3306 and the database exists.";
            System.err.println(msg);
            throw new IllegalStateException(msg, e);
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            System.err.println("[DB] Close error: " + e.getMessage());
        }
    }
}