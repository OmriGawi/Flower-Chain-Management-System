package gui.delivery_man;

import classes.Order;
import enums.OrderEnum;
import controllers.OrderController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import utils.Functions;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static utils.Constants.*;

/**
 * This class defines the screen of delivery man arrival confirmation screen
 */
public class DeliveryManArrivalConfirmScreenFXController extends Pane implements Initializable {

    // Table View
    @FXML
    private TableView<Order> ordersTableView;
        @FXML
        private TableColumn<Order, Integer> orderNumberCol;
        @FXML
        private TableColumn<Order, Date> creationDateCol;
        @FXML
        private TableColumn<Order, OrderEnum> statusCol;
        @FXML
        private TableColumn<Order, Date> supplyDateCol;
        @FXML
        private TableColumn<Order, Double> totalPriceCol;

    @FXML
    private Label arriveStatusLabel;
    @FXML
    private Label supplyDateLabel;
    @FXML
    private Label creationDateLabel;
    @FXML
    private Label orderIdLabel;

    // Text
    @FXML
    private Text errorText;
    @FXML
    private Text successText;

    // Buttons
    @FXML
    private Button saveButton;

    // Variables
    private Order selectedOrder;
    public static ObservableList<Order> orders = FXCollections.observableArrayList();

    public static StringProperty successTextProperty = new SimpleStringProperty("");
    public static StringProperty errorTextProperty = new SimpleStringProperty("");

    /**
     * Constructor
     */
    public DeliveryManArrivalConfirmScreenFXController(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/delivery_man/DeliveryManArrivalConfirmScreenFX.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setControllerFactory(callback -> this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
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
        creationDateCol.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        supplyDateCol.setCellValueFactory(new PropertyValueFactory<>("supplyDate"));
        totalPriceCol.setCellValueFactory(param -> new SimpleObjectProperty<>(Functions.round(param.getValue().getTotalPrice(), 2)));

        // Set TableView date format
        creationDateCol.setCellFactory(DeliveryManArrivalConfirmScreenFXController::setTableViewDateFormat);
        supplyDateCol.setCellFactory(DeliveryManArrivalConfirmScreenFXController::setTableViewDateFormat);

        // Properties
        successTextProperty.addListener((observable, oldValue, newValue) -> {
            successText.setText(newValue);
            successText.setStyle(TEXT_VALID_STYLE);
            if(newValue.equals("Updated successfully! No refund") || newValue.equals("Updated successfully! Order has been refunded")){
                selectedOrder.setStatus(OrderEnum.ARRIVED);
                orders.remove(selectedOrder);
                ordersTableView.refresh();
            }
        });
        errorTextProperty.addListener((observable, oldValue, newValue) -> {
            errorText.setText(newValue);
            errorText.setStyle(TEXT_NOT_VALID_STYLE);
        });

        // Set observable list into orderTableView
        ordersTableView.setItems(orders);

        // Get all orders that wait for delivery
        OrderController.getAllOrdersWaitForDeliveryByDM();
    }

    /**
     * call the order controller with the selected order
     * @param event button press
     */
    @FXML
    void onButtonPressUpdate(ActionEvent event) {
        errorTextProperty.set("");
        successTextProperty.set("");
        if(selectedOrder != null){
            OrderController.updateOrderToArrived(selectedOrder);
            clearAllLabels();
        }

    }

    /**
     * Clear all labels
     */
    private void clearAllLabels(){
        orderIdLabel.setText("");
        creationDateLabel.setText("");
        supplyDateLabel.setText("");
        arriveStatusLabel.setText("");
    }

    /**
     * This method will put selected item info in appropriate labels
     * @param event TableRow click
     */
    @FXML
    void onMouseClickTableRow(MouseEvent event) {
        try{
            selectedOrder = ordersTableView.getSelectionModel().getSelectedItem();
            // Get data from Table row into text fields
            Date supplyDate = ordersTableView.getSelectionModel().getSelectedItem().getSupplyDate();
            orderIdLabel.setText(String.valueOf(ordersTableView.getSelectionModel().getSelectedItem().getId()));
            creationDateLabel.setText(String.valueOf(ordersTableView.getSelectionModel().getSelectedItem().getCreationDate()));
            supplyDateLabel.setText(String.valueOf(supplyDate));

            long longSupplyDate = supplyDate.getTime();
            if(System.currentTimeMillis() > longSupplyDate){
                arriveStatusLabel.setStyle(TEXT_NOT_VALID_STYLE);
                arriveStatusLabel.setText("Too late! Order will be refunded!");
            }else{
                arriveStatusLabel.setStyle(TEXT_VALID_STYLE);
                arriveStatusLabel.setText("In time");
            }

            saveButton.setDisable(false);
            successTextProperty.set("");
            errorTextProperty.set("");
        }catch (Exception ignored){
        }
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
