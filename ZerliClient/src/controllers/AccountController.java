package controllers;

import classes.*;
import client.ZerliClientController;
import communication.Answer;
import communication.Message;
import communication.Task;
import enums.StatusEnum;
import gui.client.ClientCartScreenFXController;
import gui.store_manager.StoreManagerCreateAccountScreenFXController;
import gui.store_manager.StoreManagerMyAccountScreenFXController;
import gui.store_manager.StoreManagerUpdateAccountScreenFXController;
import gui.store_manager.StoreManagerCancelOrdersScreenFXController;
import javafx.util.Pair;
import utils.Utils;

import java.util.HashMap;

import java.util.ArrayList;

/**
 * This class handles all methods used to send data and requests to the server related to Accounts
 * it also handles received data from the analyzed messages from the server and returns them to the relevant
 * FX controllers.
 * */
public class AccountController {
    /**
     * This method creates a message for the server to get the amount of  orders an account has
     */
    public static void getNumberOfOrdersByAccount() {
        Message msg = new Message(Task.NUMBER_OF_ORDERS_BY_ACCOUNT, Answer.WAIT_RESPONSE, Utils.currUser.getAccount().getId());
        ZerliClientController.accept(msg);
    }

    /**
     * This method will set the amount of orders an account has in the Cart screen FX 'numberOfOrders' property
     *
     * @param number An Integer which is the amount of order an account has
     */
    public static void showNumberOfOrders(Object number){
        ClientCartScreenFXController.numberOfOrders.set((Integer)number);
    }

    /**
     * This method creates a new message for setting a new balance to an account.
     *
     * @param accountNewBalance An account's new balance needed to be set in the DB.
     */
    public static void setAccountBalance(double accountNewBalance) {
        HashMap<String, Object> args = new HashMap<>();
        args.put("accountNewBalance", accountNewBalance);
        args.put("accountId", Utils.currUser.getAccount().getId());
        Message msg = new Message(Task.SET_ACCOUNT_BALANCE, Answer.WAIT_RESPONSE, args);
        ZerliClientController.accept(msg);
    }

    /**
     * This method creates a new message for selecting all client users without a registered
     * account
     */
    public static void getAllClientUsersWithoutRegisteredAccount(){
        Message msg = new Message(Task.GET_ALL_CLIENT_USERS_WITHOUT_REGISTERED_ACCOUNT_BY_SM);
        ZerliClientController.accept(msg);
    }

    /**
     * This method put the received User list into
     * StoreManagerCreateAccountScreenFXController usersTableView
     * @param users  - List of users
     */
    public static void showAllClientUsersWithoutRegisteredAccount(Object users) {
        if (users instanceof ArrayList<?>) {
            if(((ArrayList<?>) users).isEmpty())
                StoreManagerCreateAccountScreenFXController.users.clear();
            else{
                if (((ArrayList<?>) users).get(0) instanceof User) {
                    StoreManagerCreateAccountScreenFXController.users.clear();
                    StoreManagerCreateAccountScreenFXController.users.addAll((ArrayList<User>) users);
                }
            }
        }
    }

    /**
     * This method creates a new message for creating new account
     */
    public static void createNewAccount(String cardNumber, String expirationDate, String cvv, User user){
        CreditCard creditCard = new CreditCard(cardNumber, cvv, expirationDate);
        Account account = new Account(0, StatusEnum.CONFIRMED, creditCard);
        user.setAccount(account);
        Message msg = new Message(Task.CREATE_NEW_ACCOUNT_BY_SM, user);
        ZerliClientController.accept(msg);
    }

    /**
     * This method will show a message on StoreManagerCreateAccountScreenFXController
     * with the appropriate result came from database.
     * @param message Object: The user<p>
     *                Answer: The answer from the server
     */
    public static void showCreateNewAccount(Message message){
        Answer answer = message.getAnswer();
        switch (answer){
            case CARD_NUMBER_ALREADY_EXIST:
                StoreManagerCreateAccountScreenFXController.errorTextProperty.set("Card number already exist!");
                break;
            case CREATE_ACCOUNT_SUCCESSFULLY:
                StoreManagerCreateAccountScreenFXController.successTextProperty.set("Created Account successfully!");
                break;
            case NO_ROWS_AFFECTED:
                StoreManagerCreateAccountScreenFXController.errorTextProperty.set("An error occurred. Please try again!");
                break;
        }
    }

    /**
     * This method creates a new message for selecting all client users with registered
     * account
     */
    public static void getAllClientUsersWithRegisteredAccount(){
        Message msg = new Message(Task.GET_ALL_CLIENT_USERS_WITH_REGISTERED_ACCOUNT_BY_SM);
        ZerliClientController.accept(msg);
    }

    /**
     * This method put the received User list into
     * StoreManagerUpdateAccountScreenFXController usersTableView
     * @param users List of users
     */
    public static void showAllClientUsersWithRegisteredAccount(Object users) {
        if (users instanceof ArrayList<?>) {
            if(((ArrayList<?>) users).isEmpty())
                StoreManagerUpdateAccountScreenFXController.users.clear();
            else{
                if (((ArrayList<?>) users).get(0) instanceof User) {
                    StoreManagerUpdateAccountScreenFXController.users.clear();
                    StoreManagerUpdateAccountScreenFXController.users.addAll((ArrayList<User>) users);
                }
            }
        }
    }

    /**
     * This method creates a new message for update an account status.
     */
    public static void editAccountStatus(User user){
        Message msg = new Message(Task.EDIT_ACCOUNT_STATUS_BY_SM, user);
        msg.setSenderUserId(Utils.currUser.getId());
        ZerliClientController.accept(msg);
    }

    /**
     * This method will show a message on StoreManagerUpdateAccountScreenFXController
     * with the appropriate result came from database.
     * @param message Object: The user<p>
     *                Answer: The answer
     */
    public static void showEditAccountStatus(Message message){
        if (Utils.currUser.getId() == message.getSenderUserId()) {
            Answer answer = message.getAnswer();
            switch (answer) {
                case NO_ROWS_AFFECTED:
                    StoreManagerUpdateAccountScreenFXController.errorTextProperty.set("Not found account, please try again!");
                    break;
                case SUCCEED:
                    StoreManagerUpdateAccountScreenFXController.successTextProperty.set("Updated account status successfully!");
                    break;
            }
        }
    }

    /**
     * This method updates the current user using the client.
     * @param msg - Message object containing a User object that needs to be updated as the current user using the
     *            client
     */
    public static void updateAccount(Message msg) {
        User freshUser = ((User) msg.getObject());
        if (Utils.currUser.getId() == freshUser.getId()) {
            Utils.currUser.setAccount(freshUser.getAccount());
        }
    }

    /**
     * This method creates a new message for refunding an order
     * @param refundDetailsNeeded Pair object containing the details needed in order to perform a refund to an account
     */
    public static void refundOrderByManager(Pair<Integer, Double> refundDetailsNeeded) {
        Message msg = new Message(Task.REFUND_ORDER_BY_MANAGER, Answer.WAIT_RESPONSE, refundDetailsNeeded);
        ZerliClientController.accept(msg);
    }

    /**
     * This method sets a message to show to the Store Manager regarding the success/failure of refunding an account
     * @param message Message object containing the response from the server
     */
    public static void showAccountRefundedMessage(Message message) {
        Pair<Integer, Double> accountIdAndBalance = (Pair<Integer, Double>) message.getObject();
        int accountId = accountIdAndBalance.getKey();
        double newBalance = accountIdAndBalance.getValue();
        if (Utils.currUser.getAccount() != null && Utils.currUser.getAccountId() == accountId) {
            Utils.currUser.getAccount().setBalance(newBalance);
            if (message.getAnswer() == Answer.SUCCEED)
                StoreManagerCancelOrdersScreenFXController.successRefundTextProperty.set("Account received a refund successfully!");
            else StoreManagerCancelOrdersScreenFXController.successRefundTextProperty.set("Error refunding account!");
        }
    }

    /**
     * Create a new task for getting for a given store manager user id his store.
     *
     * @param userId A userId object
     */
    public static void getStoreBySM(int userId){
        Message msg = new Message(Task.GET_STORE_BY_SM, userId);
        ZerliClientController.accept(msg);
    }

    /**
     * Initialize StoreManager's store
     *
     * @param store A Store object
     */
    public static void showStoreBySM(Store store){
        StoreManagerMyAccountScreenFXController.store = store;
    }
}