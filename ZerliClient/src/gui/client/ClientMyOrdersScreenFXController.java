package gui.client;

import classes.Order;
import enums.OrderEnum;
import controllers.*;
import gui.sidenavigation.SidenavigationClientFXController;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import utils.Functions;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static utils.Constants.*;

public class ClientMyOrdersScreenFXController extends Pane implements Initializable {

    // Orders Table
    @FXML
    private TableView<Order> orderTableView;
        @FXML
        private TableColumn<Order, Integer> orderNumberCol;
        @FXML
        private TableColumn<Order, Date> dateCol;
        @FXML
         private TableColumn<Order, Double> totalPriceCol;
        @FXML
        private TableColumn<Order, String> storeCol;
        @FXML
        private TableColumn<Order, String> statusCol;

    // Buttons
    @FXML
    private Button removeOrderButton;

    // Text
    @FXML
    private Text removeOrderText;

    // Properties
    public static SimpleObjectProperty<Order> orderObjectProperty = new SimpleObjectProperty<>();

    // Variables
    public static ObservableList<Order> orders = FXCollections.observableArrayList();
    private final SidenavigationClientFXController sidenavigationClientFXController;
    /**
     * Constructor
     */
    public ClientMyOrdersScreenFXController(SidenavigationClientFXController sidenavigationClientFXController){
        this.sidenavigationClientFXController = sidenavigationClientFXController;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/client/ClientMyOrdersScreenFX.fxml"));

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
        // Set values factory
        orderNumberCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        totalPriceCol.setCellValueFactory(param -> new SimpleObjectProperty<>(Functions.round(param.getValue().getTotalPrice(), 2)));
        storeCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getStore().getName()));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Properties
        orderObjectProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                removeOrderText.setText("Your request has been submitted for administrator approval");
                removeOrderText.setStyle(TEXT_VALID_STYLE);
                orders.stream().filter(order -> order.getId() == newValue.getId()).findFirst().get().setStatus(OrderEnum.PENDING_CANCEL);
                orderTableView.refresh();
            } else {
                removeOrderText.setText("An error occurred. Please try again!");
                removeOrderText.setStyle(TEXT_NOT_VALID_STYLE);
            }
            removeOrderText.setVisible(true);
            new Timer().schedule(new TimerTask(){
                @Override
                public void run() {
                    removeOrderText.setVisible(false);
                }
            },3000L);
        });

        // Set TableView date format
        dateCol.setCellFactory(ClientMyOrdersScreenFXController::setTableViewDateFormat);

        // Set observable list into orderTableView
        orderTableView.setItems(orders);

        // Get Client Orders from Database
        OrderController.getOrdersByClient();
    }

    /**
     * This method represent a click on "Cancel Order" button.
     * It will check the type of the selected order status and will present an appropriate message to client.
     *
     * @param event Button click
     */
    @FXML
    void onButtonPressRemoveOrder(ActionEvent event){
        try{
            OrderEnum orderEnum = orderTableView.getSelectionModel().getSelectedItem().getStatus();
            Order order = orderTableView.getSelectionModel().getSelectedItem();

            switch (orderEnum){
                case PENDING_APPROVAL:
                case CONFIRMED:
                    order.setCancelDate(new Timestamp(System.currentTimeMillis()));
                    OrderController.cancelOrderByClient(order);
                    removeOrderText.setStyle(TEXT_VALID_STYLE);
                    break;
                case CANCELED:
                    removeOrderText.setText("This order is already canceled");
                    removeOrderText.setStyle(TEXT_NOT_VALID_STYLE);
                    break;
                case REFUNDED:
                    removeOrderText.setText("This order is already refunded");
                    removeOrderText.setStyle(TEXT_NOT_VALID_STYLE);
                    break;
                case PENDING_CANCEL:
                    removeOrderText.setText("This order is already waiting for cancellation approval");
                    removeOrderText.setStyle(TEXT_NOT_VALID_STYLE);
                    break;
                case ARRIVED:
                    removeOrderText.setText("This order arrived already!");
                    removeOrderText.setStyle(TEXT_NOT_VALID_STYLE);
                    break;
                default:
                    throw new Exception("orderEnum does not exist!");
            }
        } catch (Exception ignored){
            removeOrderText.setText("Please select an order to cancel.");
            removeOrderText.setStyle(TEXT_NOT_VALID_STYLE);
        }
    }

    @FXML
    void onMouseDoubleClickTableRow(MouseEvent event) {
        Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if(selectedOrder != null && event.getClickCount() == 2) {
            ClientViewOrderScreenFXController clientViewOrderScreenFXController = new ClientViewOrderScreenFXController(sidenavigationClientFXController, selectedOrder);
            sidenavigationClientFXController.setMainPaneCenter(clientViewOrderScreenFXController);
            sidenavigationClientFXController.setTopBarText("Login -> User Portal -> My Orders -> View Order");
        }
    }

    /**
     * Convert data from the Tables Column and return a TableCell with that data
     * @param tc Table Colum containing the data to be converted to a table view cell object
     * @return TableCell object containing the table's column data
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
