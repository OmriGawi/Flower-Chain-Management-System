package controllers;

import classes.Item;
import classes.Order;
import classes.Store;
import client.ZerliClientController;
import communication.Answer;
import communication.Message;
import communication.Task;
import gui.client.ClientCheckoutScreenFXController;
import gui.client.ClientMyOrdersScreenFXController;
import gui.client.ClientViewOrderScreenFXController;
import gui.delivery_man.DeliveryManArrivalConfirmScreenFXController;
import gui.sidenavigation.SidenavigationClientFXController;
import gui.store_manager.StoreManagerApproveOrdersScreenFXController;
import gui.store_manager.StoreManagerCancelOrdersScreenFXController;
import javafx.util.Pair;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class creates Orders client Messages and
 * show the results on  the relevant GUI screens.
 */
public class OrderController {

    /**
     * Creates a new Message to get all orders by specific client username.
     */
    public static void getOrdersByClient() {
        Message msg = new Message(Task.GET_ORDERS_BY_CLIENT, Answer.WAIT_RESPONSE, Utils.currUser.getUsername());
        ZerliClientController.accept(msg);
    }

    /**
     * Create a new order
     * @param order order data
     */
    public static void createNewOrder(Order order, HashMap<Item, Integer> itemsInOrder) {
        HashMap<String, Object> args = new HashMap<>();
        args.put("order", order);
        args.put("itemsInOrder", itemsInOrder);
        Message msg = new Message(Task.CREATE_NEW_ORDER, args);
        ZerliClientController.accept(msg);
    }

    /**
     * This method shows the relevant text in the GUI
     * @param answer The answer received from the server regarding the creation of the order
     */
    public static void showNewOrderWasAdded(Answer answer) {
        SidenavigationClientFXController.cart.clear();
        SidenavigationClientFXController.cartQuantity.clear();
        if (answer == Answer.SUCCEED)
            ClientCheckoutScreenFXController.successTextProperty.set("Order was created successfully!");
        else if (answer == Answer.ACCOUNT_IS_FROZEN) {
            ClientCheckoutScreenFXController.successTextProperty.set("Account is frozen!");
        }
        else {
            ClientCheckoutScreenFXController.successTextProperty.set("Order was not created!");
        }
    }

    /**
     * Use this method to show the orders by specific client in
     * ClientMyOrdersScreenFXController
     * @param orders ArrayList of Orders.
     */
    public static void showClientOrders(Object orders) {
        if (orders instanceof ArrayList<?>) {
            if (((ArrayList<?>) orders).get(0) instanceof Order) {
                ((ArrayList<Order>) orders).sort((o1, o2) -> Integer.compare(o1.getId(), o2.getId())); // Sort table by order id
                ClientMyOrdersScreenFXController.orders.clear();
                ClientMyOrdersScreenFXController.orders.addAll((ArrayList<Order>) orders);
            }
        }
    }

    /**
     * Creates a new Message to ask for an order cancellation by client.
     */
    public static void cancelOrderByClient(Order order) {
        Message msg = new Message(Task.CANCEL_ORDER_BY_CLIENT, Answer.WAIT_RESPONSE, order);
        ZerliClientController.accept(msg);
    }


    /**
     * Use this method to show a message to ClientMyOrdersScreenFXController
     * after "Remove Button" is pressed.
     * @param msg getObject() has a Pair<Integer, Order> when {key=affectedRows | value=order}
     */
    public static void showCancelOrderMessage(Message msg){
        Order order = (Order) msg.getObject();
        ClientMyOrdersScreenFXController.orderObjectProperty.set(order);
    }

    /**
     * Creates a new Message to get items in order by specific order id.
     * @param orderID The order ID to get its items
     */
    public static void getOrderItems(int orderID) {
        Message msg = new Message(Task.GET_ITEMS_IN_ORDER_BY_CLIENT, Answer.WAIT_RESPONSE, orderID);
        ZerliClientController.accept(msg);
    }

    /**
     * Use this method to show the items in order by order id
     * ClientMyOrdersScreenFXController
     * @param items A list of items
     */
    public static void showOrderItems(Object items) {
        if (items instanceof HashMap<?, ?>) {
            HashMap<Item, Pair<Integer, String>> itemsInfo = (HashMap<Item, Pair<Integer, String>>) items;
            ClientViewOrderScreenFXController.itemsInfo = itemsInfo;
            ClientViewOrderScreenFXController.items.clear();
            ClientViewOrderScreenFXController.items.addAll(itemsInfo.keySet());
        }
    }

    /**
     * Creates a new Message to get all stores from the DB.
     */
    public static void getAllStores() {
        Message msg = new Message(Task.GET_ALL_STORES, Answer.WAIT_RESPONSE);
        ZerliClientController.accept(msg);
    }

    /**
     * Use this method to show all stores to the client when choosing a store to perform the purchase of the order
     * from
     * @param stores ArrayList of Stores
     */
    public static void showAllStores(Object stores) {
        if (stores instanceof ArrayList<?>) {
            if (((ArrayList<?>) stores).get(0) instanceof Store) {
                ClientCheckoutScreenFXController.stores.clear();
                ClientCheckoutScreenFXController.stores.addAll((ArrayList<Store>) stores);
            }
        }
    }

    /**
     * Creates a new Message to get all orders awaiting approval for specific store manager.
     */
    public static void getWaitingApprovalOrders() {
        Message msg = new Message(Task.GET_ORDERS_BY_MANAGER, Answer.WAIT_RESPONSE, Utils.currUser.getId());
        ZerliClientController.accept(msg);
    }

    /**
     * Creates a new Message to get all orders awaiting approval for specific store manager.
     */
    public static void getWaitingCancellationOrders() {
        Message msg = new Message(Task.GET_CANCELLATIONS_BY_MANAGER, Answer.WAIT_RESPONSE, Utils.currUser.getId());
        ZerliClientController.accept(msg);
    }

    /**
     * Use this method to show the pending orders by specific store manager in
     * StoreManagerApproveOrderScreenFXController
     * @param pendingOrders A list of orders.
     */
    public static void showPendingApprovalOrders(Object pendingOrders) {
        StoreManagerApproveOrdersScreenFXController.approveOrders.clear();
        StoreManagerApproveOrdersScreenFXController.approveOrders.addAll((ArrayList<Order>) pendingOrders);
    }

    /**
     * Use this method to show the pending orders by specific store manager in
     * StoreManagerApproveOrderScreenFXController
     * @param pendingOrders A list of orders.
     */
    public static void showPendingCancellationOrders(Object pendingOrders) {
        StoreManagerCancelOrdersScreenFXController.cancelOrders.clear();
        StoreManagerCancelOrdersScreenFXController.cancelOrders.addAll((ArrayList<Order>) pendingOrders);
    }


    /**
     * Creates a new Message to ask for an order approval by store manager.
     * @param orderId The order ID to be approved by the manager
     */
    public static void approveOrderByManager(int orderId) {
        Message msg = new Message(Task.APPROVE_ORDER_BY_MANAGER, Answer.WAIT_RESPONSE, orderId);
        ZerliClientController.accept(msg);
    }


    /**
     * Creates a new Message to ask for an order cancellation by store manager.
     * @param orderId The order ID to be canceled
     * @param refund The refund amount to be given to the account
     */
    public static void cancelOrderByManager(int orderId, double refund) {
        HashMap<String, Object> args = new HashMap<>();
        args.put("orderId", orderId);
        args.put("refund", refund);
        Message msg = new Message(Task.CANCEL_ORDER_BY_MANAGER, Answer.WAIT_RESPONSE, args);
        ZerliClientController.accept(msg);
    }

    /**
     * This method shows the relevant text in the GUI
     * @param answer The answer from the server
     */
    public static void showNewOrderApprovedMessage(Answer answer) {
        if (answer == Answer.SUCCEED)
            StoreManagerApproveOrdersScreenFXController.successTextProperty.set("The order was successfully confirmed!");
        else StoreManagerApproveOrdersScreenFXController.successTextProperty.set("Order was not approved!");
    }

    /**
     * This method shows the relevant text in the GUI
     * @param answer The answer from the server
     */
    public static void showNewOrderCanceledMessage(Answer answer) {
        if (answer == Answer.SUCCEED)
            StoreManagerCancelOrdersScreenFXController.successTextProperty.set("The order was successfully canceled!");
        else StoreManagerCancelOrdersScreenFXController.successTextProperty.set("Order was not canceled!");
    }

    /**
     * Creates new task for selecting all orders that wait for delivery.
     */
    public static void getAllOrdersWaitForDeliveryByDM() {
        Message msg = new Message(Task.GET_ALL_ORDERS_BY_DM);
        ZerliClientController.accept(msg);
    }

    /**
     * Will put list of orders into DeliveryManArrivalConfirmScreenFXController OrdersTableView
     * @param object List of orders
     */
    public static void showAllOrdersByDM(Object object) {
        if (object instanceof ArrayList<?>) {
            if(((ArrayList<?>) object).isEmpty())
                DeliveryManArrivalConfirmScreenFXController.orders.clear();
            else{
                if (((ArrayList<?>) object).get(0) instanceof Order) {
                    DeliveryManArrivalConfirmScreenFXController.orders.clear();
                    DeliveryManArrivalConfirmScreenFXController.orders.addAll((ArrayList<Order>) object);
                }
            }
        }
    }

    /**
     * Creates a new task for updating an order status to arrive
     * @param order order
     */
    public static void updateOrderToArrived(Order order){
        Message msg = new Message(Task.UPDATE_ORDER_TO_ARRIVED_BY_DM, order);
        ZerliClientController.accept(msg);
    }

    /**
     * set an appropriate text on DeliveryManArrivalConfirmScreenFXController depends on the updating status answer
     * @param message message
     */
    public static void showUpdateStatusToArrived(Message message) {
        Answer answer = message.getAnswer();
        if(answer == Answer.ORDER_STATUS_UPDATED_TO_ARRIVED_NO_REFUND)
            DeliveryManArrivalConfirmScreenFXController.successTextProperty.set("Updated successfully! No refund");
        else if(answer == Answer.ORDER_STATUS_UPDATED_TO_ARRIVED_WITH_REFUND){
            DeliveryManArrivalConfirmScreenFXController.successTextProperty.set("Updated successfully! Order has been refunded");
        }
        else DeliveryManArrivalConfirmScreenFXController.errorTextProperty.set("An error occurred. Please try again!");
    }
}
