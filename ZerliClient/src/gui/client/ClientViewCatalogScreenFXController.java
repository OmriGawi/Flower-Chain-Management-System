package gui.client;

import classes.Item;
import controllers.CatalogController;
import controllers.ItemController;
import gui.sidenavigation.SidenavigationClientFXController;
import interfaces.IItemCatalog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import utils.Utils;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

public class ClientViewCatalogScreenFXController extends Pane implements Initializable {

    // Items Sales HBox
    @FXML
    private HBox itemSalesLayout;

    // Items Catalog GridPane
    @FXML
    private GridPane itemCatalogContainer;

    @FXML
    private ComboBox<String> cmbColor;

    @FXML
    private ComboBox<String> cmbItemType;

    @FXML
    private ComboBox<String> cmbOrderBy;

    // Buttons
    @FXML
    private Button addToCartButton;

    @FXML
    private Button saveAsCustomItemButton;


    // Pane
    @FXML
    private Pane infoDetailsPane;


    private final SidenavigationClientFXController sidenavigationClientFXController;
    public static ObservableList<Item> sales = FXCollections.observableArrayList();
    public static ObservableList<Item> catalog = FXCollections.observableArrayList();
    private final FilteredList<Item> filteredCatalog = new FilteredList<>(catalog);
    private HashMap<Item, IItemCatalog> itemsControllers;
    private Predicate<Item> predicateColor;
    private Predicate<Item> predicateType;

    private HashMap<Item, Integer> selectedItemsMap;

    public ClientViewCatalogScreenFXController(SidenavigationClientFXController sidenavigationClientFXController){
        this.sidenavigationClientFXController = sidenavigationClientFXController;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/client/ClientViewCatalogScreenFX.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setControllerFactory(callback -> this);

        try {
            fxmlLoader.load();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filteredCatalog.addListener((ListChangeListener<Item>) c -> populateCatalog());
        sales.addListener((ListChangeListener<Item>) c -> populateSales());

        predicateColor = Objects::nonNull;
        predicateType = Objects::nonNull;

        CatalogController.getItemsInCatalog();
        CatalogController.getItemsInSales();
        selectedItemsMap = new HashMap<>();

        itemsControllers = new HashMap<>();

        addToCartButton.setDisable(!Utils.currUser.hasAccount());
    }

    /**
     * Shows items in catalog
     */
    private void populateCatalog() {
        Platform.runLater(() -> {
            int column = 0;
            int row = 1;
            itemCatalogContainer.getChildren().clear();
            for (Item item : filteredCatalog) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("ItemCatalogFX.fxml"));
                Pane itemBoxCatalog = null;
                try {   itemBoxCatalog = fxmlLoader.load(); }
                catch (IOException e) {throw new RuntimeException(e);}
                IItemCatalog itemCatalogFXController = fxmlLoader.getController();
                itemCatalogFXController.setData(item);
                itemsControllers.put(item, itemCatalogFXController);

                if (column == 4) {
                    column = 0;
                    row++;
                }
                itemBoxCatalog.setOnMousePressed(this::onCatalogItemPressed);
                itemCatalogContainer.add(itemBoxCatalog, column++, row);
                GridPane.setMargin(itemBoxCatalog, new Insets(10));
                selectItemAfterFilter(item, itemBoxCatalog);
            }
        });
    }

    /**
     * Shows items in sales
     */
    private void populateSales() {
        Platform.runLater(() -> {
            itemSalesLayout.getChildren().clear();
            for (Item item : sales) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("ItemSaleFX.fxml"));
                HBox itemBox = null;
                try {   itemBox = fxmlLoader.load(); }
                catch (IOException e) {throw new RuntimeException(e);}
                IItemCatalog itemSaleFXController = fxmlLoader.getController();
                itemSaleFXController.setData(item);
                itemsControllers.put(item, itemSaleFXController);

                itemBox.setOnMousePressed(this::onSalesItemPressed);
                itemSalesLayout.getChildren().add(itemBox);
            }
        });
    }

    private void onCatalogItemPressed(MouseEvent event) {
        int column = GridPane.getColumnIndex((Node) event.getSource());
        int row = GridPane.getRowIndex((Node) event.getSource());

        Item item = filteredCatalog.get(column + (row - 1) * 4);
        selectItem(item, event);
    }

    private void onSalesItemPressed(MouseEvent event) {
        int index = -1;
        for (int i = 0; i < itemSalesLayout.getChildren().size(); i++) {
            if (itemSalesLayout.getChildren().get(i).equals(event.getSource())) {
                index = i;
                Item item = sales.get(index);
                selectItem(item, event);
                return;
            }
        }
    }

    /**
     * Select an item to add to cart
     * @param item to select
     * @param event
     */
    private void selectItem(Item item, MouseEvent event) {
        if (Utils.currUser.hasAccount()) {
            if (event.getButton() == MouseButton.PRIMARY) {
                selectedItemsMap.merge(item, 1, Integer::sum);
                ((Pane) event.getSource()).setOpacity(0.5);
            } else if (event.getButton() == MouseButton.SECONDARY && selectedItemsMap.containsKey(item)) {
                selectedItemsMap.merge(item, -1, Integer::sum);
                if (selectedItemsMap.get(item) == 0) {
                    selectedItemsMap.remove(item);
                    ((Pane) event.getSource()).setOpacity(1);
                }
            }
            saveAsCustomItemButton.setDisable(selectedItemsMap.isEmpty());
            itemsControllers.get(item).setAmountText(selectedItemsMap.get(item));
        }
    }

    /**
     * Deselect items from catalog and sales
     */
    private void deselectItems() {
        selectedItemsMap.clear();
        for (Node item : itemCatalogContainer.getChildren()) {
            item.setOpacity(1);
        }
        for (Node item : itemSalesLayout.getChildren()) {
            item.setOpacity(1);
        }
        for (Item item : itemsControllers.keySet()) {
            itemsControllers.get(item).setAmountText(null);
        }
        saveAsCustomItemButton.setDisable(selectedItemsMap.isEmpty());
    }

    /**
     * Select items from catalog and sales after filtering
     * @param item The Item to select after filter
     * @param pane The Pane of the Catalog in the background
     */
    private void selectItemAfterFilter(Item item, Pane pane) {
        if (selectedItemsMap.containsKey(item)) {
            pane.setOpacity(0.5);
            itemsControllers.get(item).setAmountText(selectedItemsMap.get(item));
        }
    }

    @FXML
    void onButtonPressedAddToCart(ActionEvent event) {
        for (Item item : selectedItemsMap.keySet()) {
            if (!SidenavigationClientFXController.cart.contains(item)) {
                SidenavigationClientFXController.cart.add(item);
            }
            SidenavigationClientFXController.cartQuantity.merge(item, selectedItemsMap.get(item), Integer::sum);
        }
        deselectItems();
    }

    private void filterItems() {
        itemsControllers.entrySet().removeIf(item -> item instanceof ItemCatalogFXController);
        if (predicateColor != null && predicateType != null) {
            filteredCatalog.setPredicate(predicateColor.and(predicateType));
        } else if (predicateColor != null) {
            filteredCatalog.setPredicate(predicateColor);
        } else {
            filteredCatalog.setPredicate(predicateType);
        }
    }

    /**
     * Filter catalog list by color
     */
    @FXML
    void onButtonPressColor(ActionEvent event) {
        if (cmbColor.getValue().equals("All colors"))
            predicateColor = Objects::nonNull;
        else
            predicateColor = item -> item.getColor().equalsIgnoreCase(cmbColor.getValue());

        filterItems();
    }

    /**
     * Filter catalog list by item type (Product / Single item / Custom item / ALL)
     */
    @FXML
    void onButtonPressItemType(ActionEvent event) {
        switch (cmbItemType.getValue()) {
            case "Product":
                predicateType = Item::isProduct;
                break;
            case "Single item":
                predicateType = item -> !item.isProduct();
                break;
            case "Custom item":
                predicateType = Item::isCustom;
                break;
            default:
                predicateType = Objects::nonNull;
                break;
        }
        filterItems();
    }

    /**
     * Order catalog list by price (High to low / Low to high)
     */
    @FXML
    void onButtonPressOrderBy(ActionEvent event) {
        Comparator<Item> comparator = Comparator.comparing(Item::getPrice);
        if (cmbOrderBy.getValue().equals("High to low"))
            comparator = comparator.reversed();
        FXCollections.sort(catalog, comparator);
    }

    @FXML
    void onButtonPressedSaveAsCustomItem(ActionEvent event) {
        double totalPrice = 0;
        for (Item item : selectedItemsMap.keySet()) {
            totalPrice += item.getPrice();
        }
        TextInputDialog td = new TextInputDialog();
        td.setTitle("Set Name");
        td.setHeaderText("Enter you custom item name:");
        Optional<String> itemName = td.showAndWait();
        if (itemName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You must enter custom item's name!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            Item customItem = new Item(itemName.get(), totalPrice, selectedItemsMap, Utils.currUser.getAccount().getId());
            ItemController.createNewCustomItem(customItem, Utils.currUser.getAccount().getId());
            deselectItems();
            CatalogController.getItemsInCatalog();
        }
    }

    @FXML
    void onMouseEnteredInfo(MouseEvent event) {
        infoDetailsPane.setVisible(true);
    }

    @FXML
    void onMouseExitInfo(MouseEvent event) {
        infoDetailsPane.setVisible(false);
    }
}
