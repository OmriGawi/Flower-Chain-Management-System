package database;

import classes.Item;
import communication.Answer;
import communication.Message;

import java.sql.ResultSet;
import java.util.HashMap;
/**
 * This class defines the methods that send query's to the DB and receive the data back from the DB
 * and return it to the relevant controllers and classes that need to use that information
 */
public class CatalogDBController {

    /**
     * Get all the items in the catalog from the database and convert the data to an ArrayList
     * of Item objects.
     * @param msg
     */
    public static void getItemsInCatalog(Message msg) {
        String query;
        if (msg.getObject() instanceof Integer) {
            int accountID = (int) msg.getObject();
            query = "SELECT * FROM items WHERE discount_percentage = 0 AND isExist = '1' AND (account_id IS NULL OR account_id = " + accountID + ")";
        } else query = "SELECT * FROM items WHERE discount_percentage = 0 AND isExist = '1' AND account_id IS NULL";
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            msg.setAnswer(Answer.FAILED);
        else {
            msg.setAnswer(Answer.SUCCEED);
            msg.setObject(Item.createItemsListFromResultSet(rs));
        }
    }

    /**
     * Get all items that are in a sale (sales category in the GUI)
     * @param msg
     */
    public static void getItemsInSales(Message msg) {
        String query = "SELECT * FROM items WHERE discount_percentage <> 0 AND isExist = '1' AND account_id is null;";
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            msg.setAnswer(Answer.FAILED);
        else {
            msg.setAnswer(Answer.SUCCEED);
            msg.setObject(Item.createItemsListFromResultSet(rs));
        }
    }

    /**
     * This method creates a new query for selecting all items in database.<p></p>
     * * Costume items made by users are not included.
     * @param msg returns ResultSet in Object
     */
    public static void getAllItems_CMW(Message msg) {
        String query = "SELECT * FROM zerli.items WHERE isExist = '1' AND account_id is null";
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            msg.setAnswer(Answer.FAILED);
        else {
            msg.setAnswer(Answer.SUCCEED);
            msg.setObject(Item.createItemsListFromResultSet(rs));
        }
    }

    /**
     * This method creates a new query for selecting all items in catalog
     * that are not self-customized by customers.<p></p>
     * * Items in catalog are items without discount percentage.
     * @param msg msg
     */
    public static void getItemsInCatalog_CMW(Message msg) {
        String query = "SELECT * " +
                        "FROM zerli.items " +
                        "WHERE discount_percentage = '0' AND " +
                        "isExist = '1' AND " +
                        "account_id is null;";
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            msg.setAnswer(Answer.FAILED);
        else {
            msg.setAnswer(Answer.SUCCEED);
            msg.setObject(Item.createItemsListFromResultSet(rs));
        }
    }

    /**
     * This method creates a new query for updating item price by itemID.
     * @param message item ID and item newPrice.
     */
    public static void updateItemPrice(Message message){
        HashMap<String, String> args = (HashMap<String, String>) message.getObject();
        String itemId = args.get("itemId");
        String newPrice = args.get("newPrice");

        String query = String.format(
                "UPDATE zerli.items " +
                        "SET `price` = '%s' " +
                        "WHERE `id` = '%s'",
                newPrice, itemId);

        int result = DatabaseController.getInstance().executeUpdate(query);
        if (result == -1)
            message.setAnswer(Answer.NO_ROWS_AFFECTED);
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(result);
        }
    }

    /**
     * This method creates a new query for updating item discount by itemID.
     * @param message item ID and item newDiscount.
     */
    public static void updateItemDiscount(Message message){
        HashMap<String, String> args = (HashMap<String, String>) message.getObject();
        String itemId = args.get("itemId");
        String newDiscount = args.get("newDiscount");

        String query = String.format(
                "UPDATE zerli.items " +
                        "SET `discount_percentage` = '%s' " +
                        "WHERE `id` = '%s'",
                newDiscount, itemId);

        int result = DatabaseController.getInstance().executeUpdate(query);
        if (result == -1)
            message.setAnswer(Answer.NO_ROWS_AFFECTED);
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(result);
        }
    }

    /**
     * This method used to create a query for deleting an item in Database.<p>
     * Will set the message's Object to the number of rows affected by the query.
     * @param message msg
     */
    public static void removeItem_CMW(Message message){
        Item item = (Item) message.getObject();
        String itemID = String.valueOf(item.getId());

        String query = String.format("UPDATE `zerli`.`items` SET `isExist` = '0' WHERE (`id` = '%s')", itemID);

        int result = DatabaseController.getInstance().executeUpdate(query);
        if (result == -1)
            message.setAnswer(Answer.NO_ROWS_AFFECTED);
        if (result == 1){
            message.setAnswer(Answer.SUCCEED);
        }
    }
}
