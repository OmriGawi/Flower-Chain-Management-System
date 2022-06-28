package controllers;

import classes.Item;
import client.ZerliClientController;
import communication.Answer;
import communication.Message;
import communication.Task;
import gui.client.ClientViewCatalogScreenFXController;
import gui.company_marketing_worker.*;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * This class handles the Catalog methods to be sent and received from the server for the CatalogFXController
 * */
public class CatalogController {

    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CLIENT METHODS @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

    public static void getItemsInCatalog() {
        Message msg;
        if (Utils.currUser.getAccount() != null) {
            msg = new Message(Task.GET_CATALOG_BY_CLIENT, Utils.currUser.getAccount().getId());
        } else {
            msg = new Message(Task.GET_CATALOG_BY_CLIENT, -1);
        }
        ZerliClientController.accept(msg);
    }

    public static void getItemsInSales() {
        Message msg = new Message(Task.GET_SALES_BY_CLIENT);
        ZerliClientController.accept(msg);
    }

    public static void showCatalog(Object catalog) {
        if (catalog instanceof ArrayList<?>) {
            if (((ArrayList<?>) catalog).get(0) instanceof Item) {
                ClientViewCatalogScreenFXController.catalog.clear();
                ClientViewCatalogScreenFXController.catalog.addAll((ArrayList<Item>) catalog);
            }
        }
    }

    public static void showSales(Object sales) {
        if (sales instanceof ArrayList<?>) {
            if (((ArrayList<?>) sales).get(0) instanceof Item) {
                ClientViewCatalogScreenFXController.sales.clear();
                ClientViewCatalogScreenFXController.sales.addAll((ArrayList<Item>) sales);
            }
        }
    }

    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@ COMPANY_MARKETING_WORKER METHODS @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

    /**
     * This method will create a new Message to get all items from Database.
     */
    public static void getAllItems_CMW(){
        Message msg = new Message(Task.GET_ALL_ITEMS);
        ZerliClientController.accept(msg);
    }

    /**
     * This method put the received Item list into
     * CompanyMarketingWorkerEditCatalogScreenFXController itemTableView
     * @param items ArrayList of items
     */
    public static void showAllItems_CMW(Object items){
        if (items instanceof ArrayList<?>) {
            if (((ArrayList<?>) items).get(0) instanceof Item) {
                CompanyMarketingWorkerEditCatalogScreenFXController.catalog.clear();
                CompanyMarketingWorkerEditCatalogScreenFXController.catalog.addAll((ArrayList<Item>) items);
            }
        }
    }

    /**
     * This method will create a new Message for updating an item price.
     * @param itemId String of the Item ID to be updated
     * @param newPrice String of the new price to be updated to
     */
    public static void updateItemPrice_CMW(String itemId, String newPrice) {
        HashMap<String, String> args = new HashMap<>();
        args.put("itemId", itemId);
        args.put("newPrice", newPrice);

        Message msg = new Message(Task.UPDATE_ITEM_PRICE, Answer.WAIT_RESPONSE, args);
        ZerliClientController.accept(msg);
    }

    /**
     * This method will show a message in CompanyMarketingWorkerEditCatalogScreenFXController
     * depends on the affected rows.
     * @param affectedRows number of rows affected from Database
     */
    public void showUpdateItemPrice_CMW(int affectedRows) {
        if(affectedRows == -1)
            CompanyMarketingWorkerEditCatalogScreenFXController.errorTextProperty.set("An error occurred. Please try again!");
        if(affectedRows == 0)
            CompanyMarketingWorkerEditCatalogScreenFXController.errorTextProperty.set("Item not exist! Please try again!");
        else{
            CompanyMarketingWorkerEditCatalogScreenFXController.successTextProperty.set("Updated price successfully!");
        }
    }

    /**
     * This method will create a new message to get all items in Catalog. <p></p>
     * * Items in catalog are items without discount in their price. <p>
     * * Will get only items that are not self-customized by customers. <p><
     */
    public static void getItemsInCatalog_CMW() {
        Message msg = new Message(Task.GET_CATALOG_BY_CMW);
        ZerliClientController.accept(msg);
    }

    /**
     * This method put the received Item list into
     * CompanyMarketingWorkerEnableSalesPromotionScreenFXController itemsTableView
     * @param items ArrayList of items
     */
    public static void showItemsInCatalog_CMW(Object items) {
        if (items instanceof ArrayList<?>) {
            if(((ArrayList<?>) items).isEmpty())
                CompanyMarketingWorkerEnableSalesPromotionScreenFXController.catalog.clear();
            if (((ArrayList<?>) items).get(0) instanceof Item) {
                CompanyMarketingWorkerEnableSalesPromotionScreenFXController.catalog.clear();
                CompanyMarketingWorkerEnableSalesPromotionScreenFXController.catalog.addAll((ArrayList<Item>) items);
            }
        }
    }

    /**
     * This method will create a new message for set discount in price for an item.
     * @param itemId String of the Item ID to be updated
     * @param newDiscount String of the new discount to be updated
     */
    public static void setItemDiscount_CMW(String itemId, String newDiscount) {
        HashMap<String, String> args = new HashMap<>();
        args.put("itemId", itemId);
        args.put("newDiscount", newDiscount);

        Message msg = new Message(Task.UPDATE_ITEM_DISCOUNT, Answer.WAIT_RESPONSE, args);
        ZerliClientController.accept(msg);
    }

    /**
     * This method will show a message in CompanyMarketingWorkerEnableSalesScreenFXController
     * depends on the affected rows from updating item discount
     * @param affectedRows number of rows affected from Database
     */
    public void showUpdateItemDiscount_CMW(int affectedRows) {
        if(affectedRows == -1)
            CompanyMarketingWorkerEnableSalesPromotionScreenFXController.errorTextProperty.set("An error occurred. Please try again!");
        if(affectedRows == 0)
            CompanyMarketingWorkerEnableSalesPromotionScreenFXController.errorTextProperty.set("Item not exist! Please try again!");
        else{
            CompanyMarketingWorkerEnableSalesPromotionScreenFXController.successTextProperty.set("Updated price discount successfully!");
        }
    }

    /**
     * This method will create a new Message to get all items to remove from Database.
     */
    public static void getAllItemsToRemove_CMW(){
        Message msg = new Message(Task.GET_ALL_ITEMS_TO_REMOVE);
        ZerliClientController.accept(msg);
    }

    /**
     * This method put the received Item list into
     * CompanyMarketingWorkerEditCatalogScreenFXController itemTableView
     * @param items ArrayList of items
     */
    public static void showAllItemsToRemove_CMW(Object items){
        if (items instanceof ArrayList<?>) {
            if (((ArrayList<?>) items).get(0) instanceof Item) {
                CompanyMarketingWorkerRemoveItemScreenFXController.catalog.clear();
                CompanyMarketingWorkerRemoveItemScreenFXController.catalog.addAll((ArrayList<Item>) items);
            }
        }
    }

    /**
     * This method creates a new Message for delete an Item from catalog.
     * @param item Item to remove from Catalog.
     */
    public static void removeItem_CMW(Item item){
        Message msg = new Message(Task.REMOVE_ITEM_BY_CMW, item);
        ZerliClientController.accept(msg);
    }

    /**
     * This method will remove the item from itemsTableView in GUI depends on the answer received.
     * @param message The message containing the item to remove and the answer from the server
     */
    public static void showRemoveItem_CMW(Message message){
        Item item = (Item) message.getObject();
        if(message.getAnswer() == Answer.SUCCEED) {
            CompanyMarketingWorkerRemoveItemScreenFXController.catalog.remove(item);
            CompanyMarketingWorkerRemoveItemScreenFXController.successTextProperty.set("Removed item successfully!");
        }
        if(message.getAnswer() == Answer.NO_ROWS_AFFECTED) {
            CompanyMarketingWorkerRemoveItemScreenFXController.errorTextProperty.set("Could not find item. Please refresh table!");
        }
    }

    /**
     * This method will create a new message to get all items in Sales. <p></p>
     * * Items in Sales are items with discount in their price. <p>
     * * Will get only items that are not self-customized by customers. <p><
     */
    public static void getItemsInSales_CMW() {
        Message msg = new Message(Task.GET_SALES_BY_CMW);
        ZerliClientController.accept(msg);
    }

    /**
     * This method put the received Item list into
     * CompanyMarketingWorkerDisableSalesPromotionScreenFXController itemsTableView
     * @param items ArrayList of items
     */
    public static void showItemsInSales_CMW(Object items) {
        if (items instanceof ArrayList<?>) {
            if(((ArrayList<?>) items).isEmpty())
                CompanyMarketingWorkerDisableSalesPromotionScreenFXController.sales.clear();
            if (((ArrayList<?>) items).get(0) instanceof Item) {
                CompanyMarketingWorkerDisableSalesPromotionScreenFXController.sales.clear();
                CompanyMarketingWorkerDisableSalesPromotionScreenFXController.sales.addAll((ArrayList<Item>) items);
            }
        }
    }

    /**
     * This method will create a new message for updating discount to 0 for a given Item
     * @param itemToRemove Item to remove from Sales.
     * @param newDiscount String of "0" - that is the new discount value to update
     */
    public static void removeItemFromSales_CMW(Item itemToRemove, String newDiscount) {
        String itemId = String.valueOf(itemToRemove.getId());

        HashMap<String, String> args = new HashMap<>();
        args.put("itemId", itemId);
        args.put("newDiscount", newDiscount);

        Message msg = new Message(Task.REMOVE_ITEM_FROM_SALES_CMW, Answer.WAIT_RESPONSE, args);
        ZerliClientController.accept(msg);
    }

    /**
     * This method will remove the item from itemsTableView in GUI depends on the answer received,
     * and show an appropriate message in GUI.
     * @param affectedRows rows affected from database
     */
    public static void showRemovedItemFromSalesInTable(int affectedRows){
        if(affectedRows == 0)
            CompanyMarketingWorkerDisableSalesPromotionScreenFXController.errorTextProperty.set("Could not find item. Please refresh table!");
        if(affectedRows == 1){
            // Remove item from list
            CompanyMarketingWorkerDisableSalesPromotionScreenFXController.sales.remove(CompanyMarketingWorkerDisableSalesPromotionScreenFXController.itemToRemoveFromSales);
            // Set appropriate text message
            CompanyMarketingWorkerDisableSalesPromotionScreenFXController.successTextProperty.set("Item removed from sales! discount is now 0.");
        }
    }


    /**
     * This method will create a new message to get all items in Catalog. <p></p>
     * * Items in catalog are items without discount in their price. <p>
     * * Will get only items that are not self-customized by customers. <p><
     */
    public static void getAllItemsForAddingNewProductItem() {
        Message msg = new Message(Task.GET_ALL_ITEMS_FOR_ADDING_NEW_PRODUCT_BY_CW);
        ZerliClientController.accept(msg);
    }

    /**
     * This method put the received Item list into
     * CompanyMarketingWorkerAddProductScreenFXController itemsTableView
     * @param items ArrayList of items
     */
    public static void showAllItemsForAddingNewProductItem(Object items) {
        if (items instanceof ArrayList<?>) {
            if(((ArrayList<?>) items).isEmpty())
                CompanyMarketingWorkerAddProductScreenFXController.catalog.clear();
            if (((ArrayList<?>) items).get(0) instanceof Item) {
                CompanyMarketingWorkerAddProductScreenFXController.catalog.clear();
                CompanyMarketingWorkerAddProductScreenFXController.catalog.addAll((ArrayList<Item>) items);
            }
        }
    }
}
