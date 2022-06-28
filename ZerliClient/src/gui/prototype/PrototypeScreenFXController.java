package gui.prototype;

import client.ZerliClientUI;
import controllers.OrderController;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import classes.Order;
import utils.Constants;

import java.util.Date;

import static utils.Constants.SQL_DATE_FORMAT;

public class PrototypeScreenFXController {
    @FXML
    private TextField colorTextField;

    @FXML
    private TextField dateTextField;

    @FXML
    private TextField orderNumberTextField;

    @FXML
    private TableView<Order> ordersTableView;

    @FXML
    private Button refreshOrdersTableButton;

    @FXML
    private Button updateOrderButton;

	@FXML
	private TableColumn<Order, Integer> orderNumberCol;

	@FXML
	private TableColumn<Order, Double> priceCol;

	@FXML
	private TableColumn<Order, String> greetingCardCol;

	@FXML
	private TableColumn<Order, String> colorCol;

	@FXML
	private TableColumn<Order, String> dOrderCol;

	@FXML
	private TableColumn<Order, String> shopCol;

	@FXML
	private TableColumn<Order, Date> dateCol;

	@FXML
	private TableColumn<Order, Date> orderDateCol;

	@FXML
	private Text updateOrderStatusLabel;


	//Variables
    private double xOffset, yOffset;

	public static Stage primaryStage;

	public static OrderController orderController;

	public static ObservableList<Order> orders = FXCollections.observableArrayList();

	public static StringProperty updateStatus = new SimpleStringProperty("");



	@FXML
	public void initialize() {
		orderNumberCol.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		greetingCardCol.setCellValueFactory(new PropertyValueFactory<>("greetingCard"));
		colorCol.setCellValueFactory(new PropertyValueFactory<>("color"));
		dOrderCol.setCellValueFactory(new PropertyValueFactory<>("dOrder"));
		shopCol.setCellValueFactory(new PropertyValueFactory<>("shop"));
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
		orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

		// Set TableView date format
		dateCol.setCellFactory(PrototypeScreenFXController::setTableViewDateFormat);
		orderDateCol.setCellFactory(PrototypeScreenFXController::setTableViewDateFormat);

		ordersTableView.setItems(orders);

		orderController = new OrderController();

		dateTextField.setText(SQL_DATE_FORMAT.format(System.currentTimeMillis()));

		updateStatus.addListener((observable, oldValue, newValue) -> {
			updateOrderStatusLabel.setText(newValue);
		});

	}
    
	public void start(Stage primaryStage) throws Exception {
		PrototypeScreenFXController.primaryStage = primaryStage;

		Parent root = FXMLLoader.load(getClass().getResource("/gui/prototype/PrototypeScreenFX.fxml"));
		
		root.setOnMousePressed(event -> {
			xOffset = event.getSceneX();
			yOffset = event.getSceneY();
		});

		root.setOnMouseDragged(event -> {
			primaryStage.setX(event.getScreenX() - xOffset);
			primaryStage.setY(event.getScreenY() - yOffset);
		});

		primaryStage.setScene(new Scene(root));
	}

	public static void showScreen() {
		if (primaryStage != null)
			Platform.runLater(() -> primaryStage.show());
	}

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

	public void onButtonPressExit(ActionEvent event) throws Exception {
		ZerliClientUI.zerliClientUI.stop();
	}

	public void onButtonPressRefreshOrdersTable(ActionEvent event) throws Exception {
		OrderController.getOrdersByClient();
	}
	
	public void onButtonPressUpdateOrder(ActionEvent event) throws Exception {
		/*if (isUpdateValuesValid())
			OrderController.updateOrder(orderNumberTextField.getText(), dateTextField.getText(), colorTextField.getText().toUpperCase());
		else updateStatus.set("Please insert valid values");*/
	}

	private boolean isUpdateValuesValid() {
		boolean isColorValid = Constants.ITEMS_COLORS.contains(colorTextField.getText().toUpperCase());
		try {
			int orderNumber = Integer.parseInt(orderNumberTextField.getText());
			if (orderNumber < 0)
				return false;
			SQL_DATE_FORMAT.setLenient(false);
			Date date = SQL_DATE_FORMAT.parse(dateTextField.getText());
		} catch (Exception e) {
			return false;
		}
		return isColorValid;
	}
}