package Utilities;

import java.sql.*;

public class DBConnection {

    public static String getEmail;
    public static String getPassword;

    public static void DBConnection() throws SQLException {
        // Get DB credentials from environment variables
        String dbURL = System.getenv("DB_URL");
        String dbUsername = System.getenv("DB_USERNAME");
        String dbPassword = System.getenv("DB_PASSWORD");

        // Fallback to default if not set (for local development)
        if (dbURL == null) {
            dbURL = "jdbc:mysql://102.222.124.22:3306/ndosian6b8b7_teaching";
        }
        if (dbUsername == null) {
            dbUsername = "ndosian6b8b7_teaching";
        }
        if (dbPassword == null) {
            dbPassword = "^{SF0a=#~[~p)@l1";
        }

        try (Connection connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword)) {

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM loginUser WHERE id = 5")) {

                while (resultSet.next()) {
                    getEmail = resultSet.getString("email");
                    getPassword = resultSet.getString("password");
                    System.out.println("Email: " + getEmail + ", Password: " + getPassword);
                }
            } catch (SQLException e) {
                System.out.println("Error executing query: " + e.getMessage());
            }
        }

    }

}
