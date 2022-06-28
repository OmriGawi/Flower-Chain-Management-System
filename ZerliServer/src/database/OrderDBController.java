package database;

import classes.*;
import communication.Answer;
import communication.Message;
import emails.EmailManager;
import enums.OrderEnum;
import enums.StatusEnum;
import javafx.util.Pair;
import utils.Functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static utils.Constants.*;


/**
 * This class used to create Database queries for Order usages.
 */
public class OrderDBController {

    /**
     * This method creates a query for selecting all orders by client username
     * @param message the client username
     */
    public static void getOrdersByClient(Message message){
        String currentUsername = (String) message.getObject();
        String query = String.format(
                "SELECT orders.*, stores.name " +
                "FROM zerli.users " +
                "JOIN zerli.orders " +
                        "ON users.account_id = orders.account_id AND " +
                        "username = '%s' " +
                "JOIN zerli.stores " +
                        "ON orders.store_id = stores.id",
                currentUsername);

        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            message.setAnswer(Answer.FAILED);
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(Order.createOrderListFromResultSet(rs));
        }
    }

    /**
     * This method creates a query for selecting all orders pending approval by store manager ID
     * @param message the store manager id
     */
    public static void getOrdersPendingApprovalByManager(Message message){
        String currentId = String.valueOf(message.getObject());
        String query = String.format(
                "SELECT orders.*, stores.name " +
                        "FROM zerli.orders, zerli.stores " +
                        "WHERE orders.status = \"PENDING_APPROVAL\" AND stores.id = orders.store_id AND stores.manager_id = '%s'"
                ,currentId);

        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            message.setAnswer(Answer.FAILED);
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(Order.createOrderListFromResultSet(rs));
        }
    }

    /**
     * This method creates a query for selecting all orders pending cancellation by store manager ID
     * @param message the store manager id
     */
    public static void getOrdersPendingCancellationByManager(Message message){
        String currentId = String.valueOf(message.getObject());
        String query = String.format(
                "SELECT orders.*, stores.name " +
                        "FROM zerli.orders, zerli.stores " +
                        "WHERE orders.status = \"PENDING_CANCEL\" AND stores.id = orders.store_id AND stores.manager_id = '%s'"
                ,currentId);

        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            message.setAnswer(Answer.FAILED);
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(Order.createOrderListFromResultSet(rs));
        }
    }

    /**
     * Update an order status to 'CANCELED' by orderID
     * Send email to orderer
     * @param message orderId, refund
     */
    public static void cancelOrderByManager(Message message){
        HashMap<String, Object> args = (HashMap<String, Object>) message.getObject();
        int orderId = (int) args.get("orderId");
        double refund = (double) args.get("refund");
        String query = String.format(
                "UPDATE zerli.orders " +
                        "SET `status` = 'CANCELED' " +
                        "WHERE `id` = '%s'",
                orderId);

        int result = DatabaseController.getInstance().executeUpdate(query);
        if (result == -1)
            message.setAnswer(Answer.NO_ROWS_AFFECTED);
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(result);
            new Thread(() -> {
                String ordererEmail = getOrdererEmail(orderId);
                EmailManager.sendEmail(
                    ordererEmail,
                    "🌷 Your Order Was Cancelled #" + orderId + " 🌷",
                    "Order #" + orderId + " was cancelled",
                    "₪You've got a refund of " + Functions.round(refund, 2),
                    "mail_template.html"
                );
            }).start();
        }
    }


    /**
     * This method creates a query for update an order status to PENDING_CANCEL by orderID
     * @param message the client username
     */
    public static void cancelOrderByClient(Message message){
        Order order = (Order) message.getObject();
        String orderId = String.valueOf(order.getId());
        Timestamp cancelDate = (Timestamp)order.getCancelDate();
        String query = String.format(
                "UPDATE zerli.orders " +
                "SET `status` = 'PENDING_CANCEL', `cancel_date` = " + "'" + cancelDate + "' " +
                "WHERE `id` = '%s'",
                orderId);

        int result = DatabaseController.getInstance().executeUpdate(query);
        if (result == -1) {
            message.setAnswer(Answer.NO_ROWS_AFFECTED);
            message.setObject(null);
        }
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(order);
        }
    }

    /**
     * This method creates a query for update an order status to CONFIRMED by orderID
     * @param message the order id
     */
    public static void approveOrderByManager(Message message){
        int orderId = (int) message.getObject();
        String query = String.format(
                "UPDATE zerli.orders " +
                        "SET `status` = 'CONFIRMED' " +
                        "WHERE `id` = '%s'",
                orderId);

        int result = DatabaseController.getInstance().executeUpdate(query);
        if (result == -1)
            message.setAnswer(Answer.NO_ROWS_AFFECTED);
        else {
            message.setAnswer(Answer.SUCCEED);
            new Thread(() -> {
                String ordererEmail = getOrdererEmail(orderId);
                EmailManager.sendEmail(
                    ordererEmail,
                    "🌷 Your Order Was Approved #" + orderId + " 🌷",
                    "Order #" + orderId + " was approved",
                    "",
                    "mail_template.html"
                );
            }).start();
        }
    }

    /**
     * This method creates a query for selecting all items in order by order id
     * @param msg orderID
     */
    public static void getOrderItems(Message msg) {
        int orderID = (int) msg.getObject();
        String query = "SELECT zerli.items.*, items.name, price, items_in_orders.amount, isProduct,\n" +
                "\tCASE\n" +
                "\t\tWHEN items.account_id is NOT null THEN 'Customized by me'\n" +
                "        ELSE 'Zerli'\n" +
                "\tEND AS 'made_by'\n" +
                "FROM zerli.items_in_orders\n" +
                "JOIN zerli.items\n" +
                "\tON items_in_orders.item_id = items.id AND\n" +
                "    items_in_orders.order_id = " + orderID;

        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            msg.setAnswer(Answer.FAILED);
        else {
            try {
                msg.setAnswer(Answer.SUCCEED);
                List<Item> items = Item.createItemsListFromResultSet(rs);
                HashMap<Item, Pair<Integer, String>> itemsAmount = new HashMap<>();
                rs.first();
                for (Item item : items) {
                    itemsAmount.put(item, new Pair<>(rs.getInt("amount"), rs.getString("made_by")));
                    rs.next();
                }
                msg.setObject(itemsAmount);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Create and add a new order
     * @param msg The order to be created and added to the DB
     */
    public static void createNewOrder(Message msg) {
        HashMap<String, Object> args = (HashMap<String, Object>) msg.getObject();
        Order order = (Order) args.get("order");
        HashMap<Item, Integer> itemsInOrder = (HashMap<Item, Integer>) args.get("itemsInOrder");

        StatusEnum accountStatus = AccountDBController.getAccountStatusByAccountId(order.getAccount().getId());
        if (accountStatus == StatusEnum.CONFIRMED) {
            String query;
            if (order.getDeliveryAddress() == null) {
                query = "INSERT INTO `zerli`.`orders` " +
                        "(`account_id`, `store_id`, `supply_date`, `total_price`, `greeting`) " +
                        "VALUES ('" + order.getAccount().getId() + "', '" + order.getStore().getId() + "', '" + SQL_DATE_FORMAT.format(order.getSupplyDate()) + "', '" + order.getTotalPrice() + "', '" + order.getGreetingCard() + "')";
            } else {
                query = "INSERT INTO `zerli`.`orders` " +
                        "(`account_id`, `store_id`, `supply_date`, `delivery_address`, `total_price`, `greeting`) " +
                        "VALUES ('" + order.getAccount().getId() + "', '" + order.getStore().getId() + "', '" + SQL_DATE_FORMAT.format(order.getSupplyDate()) + "', '" + order.getDeliveryAddress() + "', '" + order.getTotalPrice() + "', '" + order.getGreetingCard() + "')";
            }
            if (order.getGreetingCard() == null || order.getGreetingCard().isBlank()) {
                query = query.replace(", `greeting`", "");
                query = query.substring(0, query.lastIndexOf("', '") + 1) + ")";
            }
            int affectedRows = DatabaseController.getInstance().executeUpdate(query);
            if (affectedRows == -1) {
                msg.setAnswer(Answer.FAILED);
            } else {
                boolean isSuccess = addItemsToOrder(itemsInOrder);
                if (isSuccess) {
                    msg.setAnswer(Answer.SUCCEED);
                } else {
                    msg.setAnswer(Answer.FAILED);
                }
            }
        }
        else {
            msg.setAnswer(Answer.ACCOUNT_IS_FROZEN);
        }
    }

    /**
     * Add new items to an existing order
     * @param itemsInOrder
     * @return False if no items added, true otherwise.
     */
    private static boolean addItemsToOrder(HashMap<Item, Integer> itemsInOrder) {
        for (Item item : itemsInOrder.keySet()) {
            String query = "INSERT INTO `zerli`.`items_in_orders` (`order_id`, `item_id`, `amount`) " +
                    "SELECT MAX(orders.id), '" + item.getId() + "', '" + itemsInOrder.get(item) + "' FROM orders";
            int affectedRows = DatabaseController.getInstance().executeUpdate(query);
            if (affectedRows == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param orderId order id
     * @return Email of the user who ordered this order
     */
    private static String getOrdererEmail(int orderId) {
        String query = "SELECT zerli.users.email FROM zerli.orders\n" +
                "JOIN zerli.account ON account_id=zerli.account.id\n" +
                "JOIN zerli.users ON user_id=zerli.users.id\n" +
                "WHERE zerli.orders.id = " + orderId;

        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        try {
            if (rs != null && rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Creates new query for selecting all orders that wait for delivery.
     * @param message message
     */
    public static void getAllOrdersWaitForDeliveryByDM(Message message) {
        String query = "SELECT *\n" +
                "FROM zerli.orders\n" +
                "WHERE delivery_address IS NOT NULL AND status = 'CONFIRMED';";
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            message.setAnswer(Answer.FAILED);
        else {
            message.setAnswer(Answer.SUCCEED);
            List<Order> orders = new ArrayList<>();
            try {
                while (rs.next()) {
                    OrderEnum orderEnum = OrderEnum.valueOf(rs.getString("status"));

                    orders.add(new Order(
                            rs.getInt("id"),
                            new Account(rs.getInt("account_id")),
                            new Store(rs.getInt("store_id")),
                            rs.getTimestamp("supply_date"),
                            rs.getString("delivery_address"),
                            rs.getDouble("total_price"),
                            rs.getString("greeting"),
                            rs.getTimestamp("creation_date"),
                            rs.getTimestamp("cancel_date"),
                            orderEnum
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            message.setObject(orders);
        }
    }

    /**
     * Update the status of delivery to arrived in the DB for the order sent in the message to this method
     * @param message Order object
     */
    public static void updateOrderToArrived(Message message) {
        Order order = (Order) message.getObject();
        int orderId = order.getId();
        int accountId = order.getAccount().getId();
        double totalPrice = order.getTotalPrice();
        long longSupplyDate = order.getSupplyDate().getTime();
        String query;
        int result;

        // Updating status to Arrived with refund
        if(System.currentTimeMillis() > longSupplyDate){
            query = String.format("UPDATE `zerli`.`orders` SET `status` = 'ARRIVED' WHERE `id` = '%s'", orderId);
            result = DatabaseController.getInstance().executeUpdate(query);
            if (result == -1) {
                message.setAnswer(Answer.NO_ROWS_AFFECTED);
            } else {
                // Updating balance
                query = String.format("UPDATE zerli.account SET balance = balance + %s WHERE id = %s;", totalPrice, accountId);
                result = DatabaseController.getInstance().executeUpdate(query);
                if (result == -1) {
                    message.setAnswer(Answer.NO_ROWS_AFFECTED);
                }else{
                    message.setAnswer(Answer.ORDER_STATUS_UPDATED_TO_ARRIVED_WITH_REFUND);
                    String accountEmail = AccountDBController.getEmailByAccountId(accountId);
                    if (accountEmail != null) {
                        new Thread(() -> EmailManager.sendEmail(
                                accountEmail,
                                "🌷 Your Order Was Arrived #" + orderId + " 🌷",
                                "Order #" + orderId + " was arrived",
                                "₪As a result of our supply delay you've got a refund of " + Functions.round(totalPrice, 2),
                                "mail_template.html"
                        )).start();
                    }
                }
            }

        // Updating status to Arrived with no refund
        }else{
            query = String.format("UPDATE `zerli`.`orders` SET `status` = 'ARRIVED' WHERE `id` = '%s'", orderId);
            result = DatabaseController.getInstance().executeUpdate(query);
            if (result == -1) {
                message.setAnswer(Answer.NO_ROWS_AFFECTED);
            } else {
                message.setAnswer(Answer.ORDER_STATUS_UPDATED_TO_ARRIVED_NO_REFUND);
                String accountEmail = AccountDBController.getEmailByAccountId(accountId);
                if (accountEmail != null) {
                    new Thread(() -> EmailManager.sendEmail(
                            accountEmail,
                            "🌷 Your Order Was Arrived #" + orderId + " 🌷",
                            "Order #" + orderId + " was arrived",
                            "Your order has arrived we hope you like it!",
                            "mail_template.html"
                    )).start();
                }
            }
        }
    }
}
