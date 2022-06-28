package gui.client;

import classes.Item;
import controllers.*;
import gui.sidenavigation.SidenavigationClientFXController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import utils.Utils;
import utils.Functions;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.Constants.TEXT_NOT_VALID_STYLE;

public class ClientCartScreenFXController extends Pane implements Initializable {

    // Items Table
    @FXML
    private TableView<Item> itemsTableView;
    @FXML
    private TableColumn<Item, String> itemNameCol;
    @FXML
    private TableColumn<Item, Double> priceCol;
    @FXML
    private TableColumn<Item, Integer> quantityCol;
    @FXML
    private TableColumn<Item, Double> totalPriceCol;

    // Buttons
    @FXML
    private Button checkoutButton;
    @FXML
    private Button removeButton;

    // Text
    @FXML
    private Text errorText;


    // Labels
    @FXML
    private Label discountLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label accountBalanceLabel;
    @FXML
    private Label totalLabel;

    private SidenavigationClientFXController sidenavigationClientFXController;
    public static SimpleIntegerProperty numberOfOrders = new SimpleIntegerProperty(Integer.MIN_VALUE);
    private double subtotal;
    private double totalPrice = 0;
    private double balance = 0;


    /**
     * Initialize the Cart Screen
     * @param sidenavigationClientFXController
     */
    public ClientCartScreenFXController(SidenavigationClientFXController sidenavigationClientFXController){
        this.sidenavigationClientFXController = sidenavigationClientFXController;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/client/ClientCartScreenFX.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setControllerFactory(callback -> this);

        try {
            fxmlLoader.load();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityCol.setCellValueFactory(param -> new SimpleObjectProperty<>(SidenavigationClientFXController.cartQuantity.get(param.getValue())));
        totalPriceCol.setCellValueFactory(param -> new SimpleObjectProperty<>(Functions.round(SidenavigationClientFXController.cartQuantity.get(param.getValue()) * param.getValue().getPrice(), 2)));

        numberOfOrders.addListener((observable, oldValue, newValue) -> {
            updateClientDiscountAndBalance((int)newValue);
        });

        itemsTableView.setItems(SidenavigationClientFXController.cart);

        getClientDiscount();
        updateOrderSummary();
    }

    @FXML
    void onButtonPressRemoveItem(ActionEvent event) {
        try{
            Item item = itemsTableView.getSelectionModel().getSelectedItem();
            int newQuantity = SidenavigationClientFXController.cartQuantity.get(item) - 1;
            if (newQuantity == 0) {
                SidenavigationClientFXController.cartQuantity.remove(item);
                SidenavigationClientFXController.cart.remove(item);
            } else {
                SidenavigationClientFXController.cartQuantity.put(item, newQuantity);
                itemsTableView.refresh();
            }
            updateOrderSummary();
        }catch (Exception ignored){}
    }
    @FXML
    void updateOrderSummary(){
        subtotal = 0;
        for (Item item : SidenavigationClientFXController.cart) {
            subtotal += item.getPrice() * SidenavigationClientFXController.cartQuantity.get(item) ;
        }
        subtotalLabel.setText(String.valueOf(Functions.round(subtotal, 2)));
        updateClientDiscountAndBalance(numberOfOrders.getValue());
    }
    @FXML
    void onButtonPressProceedToCheckout(ActionEvent event) {
        if (!SidenavigationClientFXController.cart.isEmpty()) {
            ClientCheckoutScreenFXController clientCheckoutScreenFXController = new ClientCheckoutScreenFXController(sidenavigationClientFXController, totalPrice, balance);
            sidenavigationClientFXController.setMainPaneCenter(clientCheckoutScreenFXController);
            sidenavigationClientFXController.setTopBarText("Login -> User Portal -> My Cart -> Checkout");
        } else {
            errorText.setStyle(TEXT_NOT_VALID_STYLE);
            errorText.setText("You cannot proceed to checkout without items");
        }
    }

    /**
     * Get the discount for the client
     */
    private void getClientDiscount(){
        if (Utils.currUser.hasAccount())
            AccountController.getNumberOfOrdersByAccount();
    }

    /**
     * Update the account's Balance and Discount
     * @param numberOfOrders Number of orders the account has
     */
    public void updateClientDiscountAndBalance(int numberOfOrders){
        double discount = 0;
        if (numberOfOrders == 0) {
            discount = subtotal * 0.20;
            totalPrice = subtotal - discount;
        }
        else {
            totalPrice = subtotal;
        }
        if (Utils.currUser.hasAccount() && Utils.currUser.getAccount().getBalance() > 0) {
            totalPrice -= Utils.currUser.getAccount().getBalance();
            if (totalPrice < 0) {
                balance = Math.abs(totalPrice);
                totalPrice = 0;
                accountBalanceLabel.setText("-" + Functions.round(Utils.currUser.getAccount().getBalance() - balance, 2));
            } else {
                accountBalanceLabel.setText("-" + Functions.round(Utils.currUser.getAccount().getBalance(), 2));
                balance = 0;
            }
        } else {
            accountBalanceLabel.setText("-0.0");
        }
        discountLabel.setText("-" + Functions.round(discount, 2));
        totalLabel.setText(String.valueOf(Functions.round(totalPrice, 2)));
    }
}

