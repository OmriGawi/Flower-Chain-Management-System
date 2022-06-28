package gui.client;

import classes.Order;
import enums.OrderEnum;
import classes.Store;
import controllers.AccountController;
import controllers.*;
import gui.sidenavigation.SidenavigationClientFXController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import static utils.Constants.*;

public class ClientCheckoutScreenFXController extends Pane implements Initializable {

    @FXML
    private Pane mainPain;

    // Radio buttons
    @FXML
    private RadioButton deliveryRadioButton;
    @FXML
    private RadioButton selfPickupRadioButton;

    // Buttons
    @FXML
    private Button confirmOrderButton;
    @FXML
    private Button backButton;

    // ComboBox
    @FXML
    private ComboBox<String> storeComboBox;

    // Date Picker
    @FXML
    private DatePicker supplyDatePicker;

    // TextFields
    @FXML
    private TextField cityTextField;
    @FXML
    private TextField numberTextField;
    @FXML
    private TextField streetTextField;
    @FXML
    private TextField timeTextField;


    // Text - Errors
    @FXML
    private Text cityErrorLabel;
    @FXML
    private Text numberErrorLabel;
    @FXML
    private Text storeErrorLabel;
    @FXML
    private Text streetErrorLabel;
    @FXML
    private Text supplyDateErrorLabel;
    @FXML
    private Text timeErrorText;

    @FXML
    private Text successText;

    //Text Area
    @FXML
    private TextArea greetingTextArea;

    private SidenavigationClientFXController sidenavigationClientFXController;
    public static ObservableList<Store> stores = FXCollections.observableArrayList();
    public static StringProperty successTextProperty = new SimpleStringProperty("");
    private Order myOrder;
    private double totalPrice;
    private double balance;

    /**
     * Constructor
     * @param sidenavigationClientFXController sidenavigationClientFXController instance
     * @param totalPrice Total Price of the entire order to present in the checkout screen
     * @param balance Account's balance
     */
    public ClientCheckoutScreenFXController(SidenavigationClientFXController sidenavigationClientFXController, double totalPrice, double balance){
        this.myOrder = new Order();
        this.sidenavigationClientFXController = sidenavigationClientFXController;
        this.totalPrice = totalPrice;
        this.balance = balance;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/client/ClientCheckoutScreenFX.fxml"));

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
        storeComboBox.getEditor().setEditable(false);
        supplyDatePicker.getEditor().setEditable(false);
        supplyDatePicker.getEditor().setDisable(false);

        supplyDatePicker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) < 0 );
            }
        });

        successTextProperty.addListener((observable, oldValue, newValue) -> {
            successText.setText(newValue);
            if (newValue.contains("success")) {
                successText.setStyle(TEXT_VALID_STYLE);
                mainPain.setDisable(true);
            }
            else
                successText.setStyle(TEXT_NOT_VALID_STYLE);
            successText.setVisible(true);
        });
        successTextProperty.setValue("");

        getAllStores();
        ObservableList<String> comboBoxList = storeComboBox.getItems();
        for (Store store : stores)
            comboBoxList.add(store.getName());
    }

    @FXML
    void onButtonPressConfirmOrder(ActionEvent event) {
        if(isOrderDataValid()) {
            myOrder.setTotalPrice(totalPrice);
            myOrder.setGreetingCard(greetingTextArea.getText().isBlank() ? null : greetingTextArea.getText());
            myOrder.setAccount(Utils.currUser.getAccount());
            myOrder.setDeliveryAddress(selfPickupRadioButton.isSelected() ? null : cityTextField.getText() + " - " + streetTextField.getText() + " - " + numberTextField.getText());
            myOrder.setStatus(OrderEnum.PENDING_APPROVAL);
            myOrder.setSupplyDate(calSupplyDateAndTime());
            OrderController.createNewOrder(myOrder, SidenavigationClientFXController.cartQuantity);
            Utils.currUser.getAccount().setBalance(balance);
            AccountController.setAccountBalance(Utils.currUser.getAccount().getBalance());
            SidenavigationClientFXController.cart.clear();
            SidenavigationClientFXController.cartQuantity.clear();
        }
    }

    /**
     * This method checks of the order's data is valid and the input from the user is correct
     * @return True if the data is valid
     */
    public boolean isOrderDataValid(){
        storeErrorLabel.setVisible(myOrder.getStore().getName() == null || myOrder.getStore().getName().isBlank());
        supplyDateErrorLabel.setVisible(supplyDatePicker.getValue() == null || myOrder.getCreationDate() == null);

        if (deliveryRadioButton.isSelected()) {
            cityErrorLabel.setVisible(cityTextField.getText().isBlank());
            streetErrorLabel.setVisible(streetTextField.getText().isBlank());
            try {
                int number = Integer.parseInt(numberTextField.getText());
                if (number < 0) {
                    numberErrorLabel.setVisible(true);
                    return false;
                }
                numberErrorLabel.setVisible(false);
            } catch (NumberFormatException e) {
                numberErrorLabel.setVisible(true);
                return false;
            }
        }

        try {
            TIME_FORMAT.parse(timeTextField.getText()).getTime();
            int hours = Integer.parseInt(timeTextField.getText().split(":")[0]);
            int minutes = Integer.parseInt(timeTextField.getText().split(":")[1]);
            if (hours > 23 || minutes > 59) {
                timeErrorText.setText("Max time can be 23:59");
                timeErrorText.setVisible(true);
                return false;
            }

            // Create a temp time for 3 hours from now
            // in order to check that supply time is 3 hours ahead
            Calendar time3HoursAhead = Calendar.getInstance();
            time3HoursAhead.add(Calendar.HOUR, 3);

            Calendar desiredSupplyDate = Calendar.getInstance();
            desiredSupplyDate.set(
                    supplyDatePicker.getValue().getYear(),
                    supplyDatePicker.getValue().getMonthValue() - 1,
                    supplyDatePicker.getValue().getDayOfMonth(),
                    hours,
                    minutes,
                    0
            );

            if (desiredSupplyDate.after(time3HoursAhead)) {
                timeErrorText.setVisible(false);
            } else {
                timeErrorText.setText("Time muse be at least " + time3HoursAhead.get(Calendar.HOUR_OF_DAY) + ":" + time3HoursAhead.get(Calendar.MINUTE));
                timeErrorText.setVisible(true);
                return false;
            }
        } catch (ParseException e) {
            timeErrorText.setText("Time should be like 13:30");
            timeErrorText.setVisible(true);
            return false;
        }

        return !((cityTextField.getText().isBlank() && deliveryRadioButton.isSelected())
                || (numberTextField.getText().isBlank() && deliveryRadioButton.isSelected())
                || (streetTextField.getText().isBlank() && deliveryRadioButton.isSelected())
                || myOrder.getStore().getName() == null || myOrder.getStore().getName().isBlank()
                || supplyDatePicker.getValue() == null || myOrder.getCreationDate() == null);
    }

    /**
     * Get all stores from the DB - used in the initialize method
     */
    private void getAllStores(){
        OrderController.getAllStores();
    }
    @FXML
    void onButtonPressChooseStore(ActionEvent event) {
        myOrder.getStore().setName(storeComboBox.getValue());
        for (Store store : stores) {
            if (store.getName().equals(storeComboBox.getValue())) {
                myOrder.setStore(store);
                return;
            }
        }
    }

    @FXML
    void onRadioButtonPressDelivery(ActionEvent event) {
        cityTextField.setDisable(false);
        streetTextField.setDisable(false);
        numberTextField.setDisable(false);
    }

    @FXML
    void onRadioButtonPressSelfPickup(ActionEvent event) {
        cityTextField.setDisable(true);
        streetTextField.setDisable(true);
        numberTextField.setDisable(true);
    }

    @FXML
    void onButtonPressBack(ActionEvent event) {
        ClientCartScreenFXController clientCartScreenFXController = new ClientCartScreenFXController(sidenavigationClientFXController);
        sidenavigationClientFXController.setMainPaneCenter(clientCartScreenFXController);
        sidenavigationClientFXController.setTopBarText("Login -> User Portal -> My Cart");
    }

    /**
     * This method converts and returns a Date object representing the supply date and time chosen by the client
     * @return Date format of the supply date and time chosen by the client
     */
    private Date calSupplyDateAndTime() {
        // Date
        LocalDate localDate = supplyDatePicker.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date date = Date.from(instant);

        // Time
        int hour = Integer.parseInt(timeTextField.getText().split(":")[0]);
        int minutes = Integer.parseInt(timeTextField.getText().split(":")[1]);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minutes);
        c.set(Calendar.SECOND, 0);

        // Merge date & time to one object
        return c.getTime();
    }
}
