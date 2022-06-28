package controllers;

import classes.Item;
import client.ZerliClientController;
import communication.Answer;
import communication.Message;
import communication.Task;
import gui.company_marketing_worker.CompanyMarketingWorkerAddItemScreenFXController;
import gui.company_marketing_worker.CompanyMarketingWorkerAddProductScreenFXController;

import java.util.HashMap;
/**
 * This class handles the Item methods to be sent and received from the server for the relevant controllers
 * */
public class ItemController {
    /**
     * This method creates a new Task to create a new single item
     *
     * @param name Name of the item
     * @param price Price of the item
     * @param color Color of the item
     * @param imagePath The default single item image path
     */
    public static void createNewSingleItem(String name, String price, String color, String imagePath){
        HashMap<String, String> args = new HashMap<>();
        args.put("name", name);
        args.put("price", price);
        args.put("color", color);
        args.put("imagePath", imagePath);
        Message msg = new Message(Task.CREATE_NEW_SINGLE_ITEM, args);
        ZerliClientController.accept(msg);
    }

    /**
     * This method creates a new Custom Item and sends a message to server to add it to the user
     * @param itemToAdd The item to add to the custom item
     * @param accountID the account that the item needs to be added to
     */
    public static void createNewCustomItem(Item itemToAdd, int accountID) {
        HashMap<String, Object> args = new HashMap<>();
        args.put("itemToAdd", itemToAdd);
        args.put("accountID", accountID);
        Message msg = new Message(Task.CREATE_NEW_CUSTOM_ITEM, args);
        ZerliClientController.accept(msg);
    }

    /**
     * This method creates a new message for creating a new Product Item
     * @param productToAdd The new product item to add
     */
    public static void createNewProductItem(Item productToAdd) {
        HashMap<String, Object> args = new HashMap<>();
        args.put("itemToAdd", productToAdd);
        Message msg = new Message(Task.CREATE_NEW_PRODUCT_ITEM, args);
        ZerliClientController.accept(msg);
    }

    /**
     * This method shows the relevant text in the GUI
     * @param answer Message containing the answer from the server
     */
    public static void showNewProductItem(Answer answer) {
        if (answer == Answer.SUCCEED) {
            CompanyMarketingWorkerAddProductScreenFXController.successTextProperty.set("Product was added successfully");
        } else {
            CompanyMarketingWorkerAddProductScreenFXController.errorTextProperty.set("Product was not added");
        }
    }

    /**
     * This method will show a message in CompanyMarketingWorkerAddItemScreenFXController
     * depends on the affected rows from creating new single item
     * @param affectedRows number of rows affected from Database
     */
    public void showCreateNewSingleItemMessage(int affectedRows) {
        if(affectedRows == -1)
            CompanyMarketingWorkerAddItemScreenFXController.errorTextProperty.set("An error occurred. Please try again!");
        else{
            CompanyMarketingWorkerAddItemScreenFXController.successTextProperty.set("Added new single item successfully!");
        }
    }
}
