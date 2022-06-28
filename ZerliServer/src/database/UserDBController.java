package database;

import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * This class defines the methods that send query's to the DB and receive the data back from the DB
 * and return it to the relevant controllers and classes that need to use that information
 */
public class UserDBController {
    /**
     * Fetch user's email from DB
     * @param userId
     * @return User's email, if no user was found it returns null
     */
    public static String getEmailByUserId(int userId){
        String query = "SELECT email FROM zerli.users WHERE id = " + userId;
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        try {
            if (rs != null && rs.next())
                return rs.getString("email");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
