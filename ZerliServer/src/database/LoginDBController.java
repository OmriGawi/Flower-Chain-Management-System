package database;

import classes.Account;
import classes.User;
import communication.Answer;
import communication.Message;
import utils.Functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
/**
 * This class defines the methods that send query's to the DB and receive the data back from the DB
 * and return it to the relevant controllers and classes that need to use that information
 */
public class LoginDBController {
    /**
     * Check username and password with the server,
     * if they are valid it updates 'is_logged_in' column to 1 in DB
     * @param msg contains HashMap with 'username' and 'password'
     */
    public static void loginByUsernameAndPassword(Message msg) {
        HashMap<String, String> args = (HashMap<String, String>) msg.getObject();
        String username = args.get("username");
        String password = args.get("password");

        String queryFetchInfoByUsernameAndPassword = "SELECT * FROM zerli.users where username='" + username + "' and password='" + password + "'";
        ResultSet rs = DatabaseController.getInstance().executeQuery(queryFetchInfoByUsernameAndPassword);
        if (rs == null)
            msg.setAnswer(Answer.FAILED);
        else {
            if (updateIsLoggedInColumn(username, password, 1)) {
                User currUser = Functions.getFirstElementFromList(User.createUserListFromResultSet(rs));
                currUser.setAccount(getAccountInfoByUserID(currUser.getId()));
                //TODO Remove these comments to check if user is already logged in (and acts accordingly)
                if (currUser.isLoggedIn()) {
                    msg.setAnswer(Answer.ALREADY_LOGGED_IN);
                } else {
                    msg.setObject(currUser);
                    msg.setAnswer(Answer.SUCCEED);
                }
            } else {
                msg.setAnswer(Answer.INCORRECT_VALUES);
            }
        }
    }

    /**
     * Set 'is_logged_in' column in DB to 0
     * @param msg contains HashMap with 'username'
     */
    public static void logoutUsername(Message msg) {
        HashMap<String, String> args = (HashMap<String, String>) msg.getObject();
        String username = args.get("username");
        updateIsLoggedInColumn(username, null, 0);
    }

    /**
     * Check if a user is already flagged as 'logged in', in the DB
     * @param rs
     * @return True if logged in, False otherwise
     */
    private static boolean isAlreadyLoggedIn(ResultSet rs) {
        try {
            if (rs.first()) {
                return rs.getBoolean("is_logged_in");
            }
            rs.beforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update 'is_logged_in' column in DB to 'value' parameter
     *
     * @param username to update his 'is_logged_in' column
     * @param value    0 / 1
     * @param password
     * @return
     */
    private static boolean updateIsLoggedInColumn(String username, String password, int value) {
        if (value != 0 && value != 1)
            throw new IllegalArgumentException("'is_logged_in' column can be 0 or 1!");
        String query;
        if (password == null)
            query = "UPDATE zerli.users SET is_logged_in = " + value + " WHERE username = '" + username + "'";
        else query = "UPDATE zerli.users SET is_logged_in = " + value + " WHERE username = '" + username + "' AND password = '" + password + "'";
        int affectedRows = DatabaseController.getInstance().executeUpdate(query);
        return affectedRows == 1;
    }

    /**
     * Get account and credit card info (if exist)
     * @param userID to retrieve his account info
     * @return Account if exist, otherwise null
     */
    private static Account getAccountInfoByUserID(int userID) {
        String queryFetchAccountInfoByUserID = "SELECT id, balance, status, card_number, cvv, expiration_date \n" +
                "FROM zerli.account\n" +
                "JOIN creditcards ON creditcards.number=account.card_number\n" +
                "AND account.user_id=" + userID;
        ResultSet rs = DatabaseController.getInstance().executeQuery(queryFetchAccountInfoByUserID);
        if (rs != null) {
            return Functions.getFirstElementFromList(Account.createAccountListFromResultSet(rs));
        }
        return null;
    }
}
