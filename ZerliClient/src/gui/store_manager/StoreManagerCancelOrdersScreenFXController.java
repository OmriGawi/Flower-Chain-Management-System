package gui.store_manager;

import classes.Order;
import enums.OrderEnum;
import controllers.AccountController;
import controllers.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Pair;
import utils.Functions;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static utils.Constants.*;
/**
 * This class defines the Store Manager Cancel order screen
 */
public class StoreManagerCancelOrdersScreenFXController extends Pane implements Initializable {

    // Approve Orders Table
    @FXML
    private TableView<Order> cancelOrdersTable;
    @FXML
    private TableColumn<Order, Integer> orderNumberCol;
    @FXML
    private TableColumn<Order, Date> dateCol;
    @FXML
    private TableColumn<Order, Double> totalPriceCol;
    @FXML
    private TableColumn<Order, String> statusCol;

    // Buttons
    @FXML
    private Button approveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Text successText;
    @FXML
    private Text successRefundText;

    // Controllers
    public static OrderController orderController;

    // Variables
    public static ObservableList<Order> cancelOrders = FXCollections.observableArrayList();

    public static StringProperty successTextProperty = new SimpleStringProperty("");
    public static StringProperty successRefundTextProperty = new SimpleStringProperty("");

    /**
     * Constructor
     */
    public StoreManagerCancelOrdersScreenFXController(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/store_manager/StoreManagerCancelOrdersScreenFX.fxml"));

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
        // Initialize orderController
        orderController = new OrderController();

        // Set values factory
        orderNumberCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        totalPriceCol.setCellValueFactory(param -> new SimpleObjectProperty<>(Functions.round(param.getValue().getTotalPrice(), 2)));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set TableView date format
        dateCol.setCellFactory(StoreManagerCancelOrdersScreenFXController::setTableViewDateFormat);

        // Set observable list into orderTableView
        cancelOrdersTable.setItems(cancelOrders);

        successTextProperty.addListener((observable, oldValue, newValue) -> {
            successText.setText(newValue);
            if (newValue.contains("success")) {
                successText.setStyle(TEXT_VALID_STYLE);
            }
            else
                successText.setStyle(TEXT_NOT_VALID_STYLE);
            successText.setVisible(true);
            new Timer().schedule(new TimerTask(){

                @Override
                public void run() {
                    successText.setVisible(false);
                    successTextProperty.setValue("");
                }
            },3000L);
        });
        successRefundTextProperty.addListener((observable, oldValue, newValue) -> {
            successRefundText.setText(newValue);
            if (newValue.contains("success")) {
                successRefundText.setStyle(TEXT_VALID_STYLE);
            }
            else
                successRefundText.setStyle(TEXT_NOT_VALID_STYLE);
            successRefundText.setVisible(true);
            new Timer().schedule(new TimerTask(){

                @Override
                public void run() {
                    successRefundText.setVisible(false);
                    successRefundTextProperty.setValue("");
                }
            },3000L);
        });
        successRefundTextProperty.setValue("");
        successTextProperty.setValue("");

        // Get Client Orders from Database
        OrderController.getWaitingCancellationOrders();
    }

    /**
     * This method represent a click on "Cancel Order" button.
     * It will check the type of the selected order status and send a query to change the status of the order in the DB
     *
     * @param event Button click
     */
    @FXML
    void onButtonPressApproveCancellationOrder(ActionEvent event) {
        try {
            Order order = cancelOrdersTable.getSelectionModel().getSelectedItem();
            OrderEnum orderEnum = cancelOrdersTable.getSelectionModel().getSelectedItem().getStatus();
            int orderId = cancelOrdersTable.getSelectionModel().getSelectedItem().getId();
            int accountId = order.getAccount().getId();
            double refund = calcRefund(order);
            if(orderEnum.equals(OrderEnum.PENDING_CANCEL)) {
                if (refund != 0)
                    AccountController.refundOrderByManager(new Pair<>(accountId, refund));
                OrderController.cancelOrderByManager(orderId, refund);
            }
        }catch(Exception ignored){
        }
        OrderController.getWaitingCancellationOrders();
    }


    /**
     * Convert a TableColumn data to a TableCell object
     * @param tc TableColumn object
     * @return TableCell object with the date
     */
    private static TableCell<Order, Date> setTableViewDateFormat(TableColumn<Order, Date> tc) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                if (empty)
                    setText(null);
                else
                    setText(SQL_DATE_FORMAT.format(date));
            }
        };
    }

    /**
     * Calculate the refund for an order
     * @param order The order to calculate the refund for
     * @return The refund for the account
     */
    private static double calcRefund(Order order) {
        Date cancelDate = order.getCancelDate();
        Date supplyDate = order.getSupplyDate();
        double refund = order.getTotalPrice();
        long cancelDateMilli = cancelDate.getTime();
        long supplyDateMilli = supplyDate.getTime();
        double timeDiffInHours = (supplyDateMilli - cancelDateMilli) / (60 * 60 * 1000.0);

        if(timeDiffInHours > 3.0)
            return refund; //100% refund of order total price

        if(timeDiffInHours >= 1)
            return refund/2.0; // 50% refund of order total price

        return 0;
    }
}

