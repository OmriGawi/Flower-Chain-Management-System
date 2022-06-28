package database;

import classes.Item;
import communication.Answer;
import communication.Message;

import java.util.HashMap;
/**
 * This class defines the methods that send query's to the DB and receive the data back from the DB
 * and return it to the relevant controllers and classes that need to use that information
 */
public class ItemDBController {

    /**
     * Create new product item. <p>
     * @param msg hashmap with keys: {"itemToAdd", "accountID"}
     * @implNote itemToAdd - Item object with all sub-items inside <p>
     * accountID - account id if it's a custom item, null otherwise <p>
     */
    public static void createNewProductItem(Message msg) {
        HashMap<String, Object> args = (HashMap<String, Object>) msg.getObject();
        Item itemToAdd = (Item) args.get("itemToAdd");
        Integer accountID = args.get("accountID") != null ? (Integer) args.get("accountID") : null;
        String query = "INSERT INTO zerli.items " +
                "(`name`, `price`, `color`, `discount_percentage`, `img`, `isProduct`, `account_id`) " +
                "VALUES ('" + itemToAdd.getName() + "', " + itemToAdd.getPriceWithoutDiscount() + ", '" + itemToAdd.getColor() + "', " + itemToAdd.getDiscountPercentage() + ", '" + itemToAdd.getImageSrc() + "', " + itemToAdd.isProduct() + ", " + accountID + ")";
        int affectedRows = DatabaseController.getInstance().executeUpdate(query);
        if (affectedRows == -1)
            msg.setAnswer(Answer.FAILED);
        else {
            boolean isSuccess = addItemsToProduct(-1, itemToAdd.getItemsMap());
            if (isSuccess) {
                msg.setObject(itemToAdd);
                msg.setAnswer(Answer.SUCCEED);
            }
            else msg.setAnswer(Answer.FAILED);
        }
    }

    /**
     * Add sub-items into product
     * @param product_id if it's a new product then assign -1
     * @param itemsMap {Item, Quantity}
     * @return True if the data was written to DB, False otherwise
     */
    public static boolean addItemsToProduct(int product_id, HashMap<Item, Integer> itemsMap) {
        String query;
        if (product_id == -1) {
            query = "INSERT INTO zerli.products (`product_id`, `item_id`, `amount`) " +
                    "SELECT MAX(items.id), ITEM_ID_TO_REPLACE, AMOUNT_TO_REPLACE FROM items";
        }
        else {
            query = "INSERT INTO zerli.products (`product_id`, `item_id`, `amount`) " +
                    "VALUES (" + product_id + ", ITEM_ID_TO_REPLACE, AMOUNT_TO_REPLACE)";
        }
        for (Item item : itemsMap.keySet()) {
            Integer amount = itemsMap.get(item);
            String queryForThisItem = query
                    .replace("ITEM_ID_TO_REPLACE", String.valueOf(item.getId()))
                    .replace("AMOUNT_TO_REPLACE", String.valueOf(amount));
            int affectedRows = DatabaseController.getInstance().executeUpdate(queryForThisItem);
            if (affectedRows == -1)
                return false;
        }
        return true;
    }

    /**
     * This method creates a new query for inserting a new single item to database.
     * @param msg HashMap of name,price,color, imagePath
     */
    public static void createNewSingleItem(Message msg){
        HashMap<String, String> args = (HashMap<String, String>) msg.getObject();
        String name = args.get("name");
        String price = args.get("price");
        String color = args.get("color");
        String imagePath = args.get("imagePath");

        String query = String.format(
                "INSERT INTO `zerli`.`items`" +
                        " (`name`, `price`, `color`, `img`, `isProduct`)" +
                        " VALUES ('%s', '%s', '%s', '%s', '0')"
                ,name, price, color, imagePath);

        int result = DatabaseController.getInstance().executeUpdate(query);
        if (result == -1)
            msg.setAnswer(Answer.NO_ROWS_AFFECTED);
        else {
            msg.setAnswer(Answer.SUCCEED);
            msg.setObject(result);
        }
    }
}