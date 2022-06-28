package database;

import classes.*;
import communication.Answer;
import communication.Message;
import emails.EmailManager;
import enums.RoleEnum;
import enums.StatusEnum;
import javafx.util.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * This class defines the methods that send query's to the DB and receive the data back from the DB
 * and return it to the relevant controllers and classes that need to use that information
 */
public class AccountDBController {
    /**
     * Get account status from the DB for a specific account using its ID
     * @param accountId
     * @return Enum status or null
     */
    public static StatusEnum getAccountStatusByAccountId(int accountId){
        String query = "SELECT status FROM zerli.account WHERE id = " + accountId;

        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        try {
            if (rs != null && rs.next()) {
                return StatusEnum.valueOf(rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Update account balance in the DB
     * @param msg a given Pair<String accountId, String refund>
     */
    public static void refundAccount(Message msg) {
        Pair<Integer, Double> pair = (Pair) msg.getObject();
        String query = "UPDATE zerli.account\n" +
                "SET balance = balance + " + pair.getValue() + "\n" +
                "WHERE id = " + pair.getKey() + ";";
        int affectedRows = DatabaseController.getInstance().executeUpdate(query);
        if (affectedRows == -1)
            msg.setAnswer(Answer.NO_ROWS_AFFECTED);
        else {
            msg.setAnswer(Answer.SUCCEED);
            msg.setSendToAll(true);
        }
    }

    /**
     * Get the amount of orders an account has performed
     * @param message
     * @throws SQLException
     */
    public static void getNumberOfOrdersByAccount(Message message) throws SQLException {
        int accountId = (int) message.getObject();
        String query = String.format(
                "SELECT COUNT(id) as amountOfOrders " +
                        "FROM zerli.orders " +
                        "WHERE account_id = "+accountId+";");
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null || !rs.next())
            message.setAnswer(Answer.FAILED);
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(rs.getInt("amountOfOrders"));
        }
    }

    /**
     * This method will create a query for Selecting all client without registered account
     * and will create a Users list with the resultset
     * @param message will put all selected client users into message's Object.
     */
    public static void getAllClientUsersWithoutRegisteredAccount(Message message){
        String query = "SELECT * FROM zerli.users WHERE role ='CLIENT' AND account_id is null;";

        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            message.setAnswer(Answer.FAILED);
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(User.createUserListFromResultSet(rs));
        }
    }

    /**
     * This method will create a new account for a given user:<p></p>
     * 1. Insert the user's credit card into credit cards table.<p>
     * 2. Insert account details into accounts table. <p>
     * 3. Selecting the latest account_id created from accounts table.<p>
     * 4. Update user's account_id in users table.<p>
     * @param message User to create account for
     */
    public static void createNewAccount(Message message){
        User user = (User) message.getObject();
        String cardNumber = user.getAccount().getCreditCard().getCardNumber();
        String expirationDate = user.getAccount().getCreditCard().getExpirationDate();
        String cvv = user.getAccount().getCreditCard().getCvv();
        String userID = String.valueOf(user.getId());
        String query;
        int affectedRows;

        // New card number in database
        query = String.format("INSERT INTO `zerli`.`creditcards` (`number`, `cvv`, `expiration_date`) VALUES ('%s', '%s', '%s')", cardNumber, cvv, expirationDate);
        affectedRows = DatabaseController.getInstance().executeUpdate(query);
        if(affectedRows != -1) {
            // New account in database
            query = String.format("INSERT INTO `zerli`.`account` (`balance`, `card_number`, `user_id`) VALUES ('0', '%s', '%s')",cardNumber, userID);
            affectedRows = DatabaseController.getInstance().executeUpdate(query);
            if(affectedRows == -1)
                message.setAnswer(Answer.NO_ROWS_AFFECTED);
            else{
                // Selecting the latest account id
                query = "SELECT max(id) as lastAccount FROM account";
                ResultSet rs = DatabaseController.getInstance().executeQuery(query);
                if(rs == null)
                    message.setAnswer(Answer.FAILED);
                else{
                    try {
                        while(rs.next())
                            user.getAccount().setId(rs.getInt("lastAccount"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    // Updating user account id in database
                    query = String.format("UPDATE `zerli`.`users` SET `account_id` = '%s' WHERE (`id` = '%s')",
                            user.getAccount().getId(), user.getId());
                    affectedRows = DatabaseController.getInstance().executeUpdate(query);
                    if(affectedRows == -1)
                        message.setAnswer(Answer.NO_ROWS_AFFECTED);
                    else{
                        message.setAnswer(Answer.CREATE_ACCOUNT_SUCCESSFULLY);
                        message.setObject(user);
                        new Thread(() -> EmailManager.sendEmail(
                                user.getEmail(),
                                "🌷 Welcome to Zerli store! 🌷",
                                "🥳 !Welcome to Zerli store",
                                ".You're now part of a community that love nature<br>" + ":We want to offer you a special discount",
                                "mail_with_banner_template.html"
                        )).start();
                    }
                }
            }
        }else{
            message.setAnswer(Answer.CARD_NUMBER_ALREADY_EXIST);
        }

    }

    /**
     * This method will create a query for Selecting all client users with registered account
     * and will create a Users list with the resultset
     * @param message will put all selected client users into message's Object.
     */
    public static void getAllClientUsersWithRegisteredAccount(Message message){
        String query = "SELECT users.*, account.balance, account.status FROM zerli.users JOIN zerli.account ON users.id = account.user_id";

        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            message.setAnswer(Answer.FAILED);
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(createClientUsersWithAccountListFromResultSet(rs));
        }
    }

    /**
     * This method will create a List of users with account.<p>
     * account id column has to be named "account_id" <p>
     * @param rs ResultSet
     * @return List of users with account
     */
    private static List<User> createClientUsersWithAccountListFromResultSet(ResultSet rs){
        List<User> users = new ArrayList<>();
        try{
            while(rs.next()){
                Account account = new Account(
                        rs.getInt("account_id"),
                        rs.getDouble("balance"),
                        StatusEnum.valueOf(rs.getString("status"))
                );
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("is_logged_in") == 1,
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        RoleEnum.valueOf(rs.getString("role")),
                        account
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * This method will create a query for updating status of a given user.
     * @param message User
     */
    public static void editAccountStatus(Message message) {
        User user = (User) message.getObject();
        StatusEnum status = user.getAccount().getStatus();
        int accountId = user.getAccount().getId();

        String query = String.format("UPDATE `zerli`.`account` SET `status` = '%s' WHERE (`id` = '%s')", status, accountId);
        int affectedRows = DatabaseController.getInstance().executeUpdate(query);
        if(affectedRows == -1)
            message.setAnswer(Answer.NO_ROWS_AFFECTED);
        else{
            message.setAnswer(Answer.SUCCEED);
            message.setSendToAll(true);
        }
    }

    /**
     * Set a new account balance in the DB
     * @param message
     */
    public static void setAccountBalance(Message message) {
        HashMap<String, Object> args = (HashMap<String, Object>) message.getObject();
        double accountNewBalance = (double) args.get("accountNewBalance");
        int accountId = (int) args.get("accountId");
        String query = "UPDATE `zerli`.`account` " +
                "SET `balance` = '" + accountNewBalance + "' " +
                "WHERE (`id` = '" + accountId + "')";
        int result = DatabaseController.getInstance().executeUpdate(query);
        if (result == -1)
            message.setAnswer(Answer.FAILED);
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(result);
        }
    }

    /**
     * Create a new query for selecting the store associated for a given store manager user id.
     * @param message Object - (int) userId
     */
    public static void getStoreBySM(Message message){
        String manager_id = String.valueOf(message.getObject());
        String query = String.format("SELECT * FROM zerli.stores WHERE manager_id = %s", manager_id);
        Store store = null;

        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            message.setAnswer(Answer.FAILED);
        else {
            message.setAnswer(Answer.SUCCEED);
            try {
                while (rs.next()) {
                    store = new Store(
                            rs.getInt("id"),
                            rs.getInt("manager_id"),
                            rs.getString("name"),
                            rs.getString("address")
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            message.setObject(store);
        }
    }

    /**
     * Get a user's email from the DB using its ID
     * @param accountId
     * @return null if ID does not exist, the email otherwise.
     */
    public static String getEmailByAccountId(int accountId){
        String query = "SELECT email FROM zerli.users WHERE account_id = " + accountId;

        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            return null;
        try {
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
}
