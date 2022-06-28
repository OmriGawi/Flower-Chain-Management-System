package gui.store_manager;

import classes.Order;
import enums.OrderEnum;
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
import utils.Functions;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static utils.Constants.*;
import static utils.Constants.TEXT_NOT_VALID_STYLE;
/**
 * This class defines the Store Manager Approve order screen
 */
public class StoreManagerApproveOrdersScreenFXController extends Pane implements Initializable {

        // Approve Orders Table
        @FXML
        private TableView<Order> approveOrdersTableView;
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

    // Controllers
    public static OrderController orderController;

    // Variables
    public static ObservableList<Order> approveOrders = FXCollections.observableArrayList();

    public static StringProperty successTextProperty = new SimpleStringProperty("");


    /**
     * Constructor
     */
    public StoreManagerApproveOrdersScreenFXController(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/store_manager/StoreManagerApproveOrdersScreenFX.fxml"));

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
        dateCol.setCellFactory(StoreManagerApproveOrdersScreenFXController::setTableViewDateFormat);

        // Set observable list into orderTableView
        approveOrdersTableView.setItems(approveOrders);

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
        successTextProperty.setValue("");

        // Get Client Orders from Database
        OrderController.getWaitingApprovalOrders();
    }

    /**
     * This method represent a click on "Approve Order" button.
     * It will check the type of the selected order status and send a query to change the status of the order in the DB
     *
     * @param event Button click
     */
    @FXML
    void onButtonPressApproveOrder(ActionEvent event) {
        try {
            OrderEnum orderEnum = approveOrdersTableView.getSelectionModel().getSelectedItem().getStatus();
            int orderId = approveOrdersTableView.getSelectionModel().getSelectedItem().getId();
            if (orderEnum.equals(OrderEnum.PENDING_APPROVAL))
                OrderController.approveOrderByManager(orderId);
        }catch(Exception ignored){}
        OrderController.getWaitingApprovalOrders();
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
}
