package database;

import classes.User;

import java.sql.*;
import java.util.List;
import java.util.TimeZone;
/**
 * This class defines the methods that send query's to the EXTERNAL DB and receive the data back from the DB
 * and return it to the relevant controllers and classes that need to use that information
 */
public class ExternalDatabaseController {
    private static ExternalDatabaseController externalDatabaseController;
    private static Connection connection;

    private ExternalDatabaseController() {}

    public static ExternalDatabaseController getInstance() {
        if (externalDatabaseController == null)
            externalDatabaseController = new ExternalDatabaseController();
        return new ExternalDatabaseController();
    }

    public static Connection getConnection() {
        return connection;
    }

    public Connection connectToExternalDB() {
        if (connection != null)
            return connection;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            System.out.println("Driver definition succeed");
        } catch (Exception ex) {
            /* handle the error */
            System.out.println("Driver definition failed");
        }

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost/external_db?serverTimezone=" + TimeZone.getDefault().getID(),
                    "root",
                    "mysql"
            );
            return connection;
        } catch (SQLException ex) {/* handle any errors */
            System.out.println("ExternalDB - SQLException: " + ex.getMessage());
            System.out.println("ExternalDB - SQLState: " + ex.getSQLState());
            System.out.println("ExternalDB - VendorError: " + ex.getErrorCode());
        }
        return null;
    }

    /**
     * @return return null if no users fetched, a list of users otherwise.
     */
    public List<User> getAllUsers() {
        String query = "SELECT * FROM external_db.users";

        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs != null) {
            return User.createUserListFromResultSet(rs);
        }
        return null;
    }
}
