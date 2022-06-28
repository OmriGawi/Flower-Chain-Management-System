package gui.client;

import classes.Item;
import classes.Order;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ClientViewOrderScreenFXController extends Pane implements Initializable {


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
    @FXML
    private TableColumn<Item, String> createdByCol;
    @FXML
    private TableColumn<Item, Boolean> isProductCol;

    // Labels
    @FXML
    private Label orderIDLabel;

    // Buttons
    @FXML
    private Button backButton;

    // Controllers
    public static OrderController orderController;

    // Variables
    private final SidenavigationClientFXController sidenavigationClientFXController;
    public static ObservableList<Item> items = FXCollections.observableArrayList();
    public static HashMap<Item, Pair<Integer, String>> itemsInfo;
    private Order selectedOrder;



    /**
     * Constructor
     *
     * @param sidenavigationClientFXController side navigation
     * @param selectedOrder
     */
    public ClientViewOrderScreenFXController(SidenavigationClientFXController sidenavigationClientFXController, Order selectedOrder){
        this.sidenavigationClientFXController = sidenavigationClientFXController;
        this.selectedOrder = selectedOrder;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/client/ClientViewOrderScreenFX.fxml"));

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
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityCol.setCellValueFactory(param -> new SimpleObjectProperty<>(itemsInfo.get(param.getValue()).getKey()));
        totalPriceCol.setCellValueFactory(param -> new SimpleObjectProperty<>(itemsInfo.get(param.getValue()).getKey() * param.getValue().getPrice()));
        createdByCol.setCellValueFactory(param -> new SimpleObjectProperty<>(itemsInfo.get(param.getValue()).getValue()));
        isProductCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().isProduct()));

        // Set observable list into orderTableView
        itemsTableView.setItems(items);

        // Get Client Orders from Database
        OrderController.getOrderItems(selectedOrder.getId());

        orderIDLabel.setText(String.valueOf(selectedOrder.getId()));
    }

    /**
     * Show the 'My Orders' screen to the client
     * @param event Button Press on the Back button
     */
    @FXML
    void onButtonPressBack(ActionEvent event) {
        ClientMyOrdersScreenFXController clientMyOrdersScreenFXController = new ClientMyOrdersScreenFXController(sidenavigationClientFXController);
        sidenavigationClientFXController.setMainPaneCenter(clientMyOrdersScreenFXController);
        sidenavigationClientFXController.setTopBarText("Login -> User Portal -> My Orders");
    }
}
