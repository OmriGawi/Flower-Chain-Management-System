package gui;

import communication.Answer;
import communication.Message;
import connected_clients.ClientConnection;
import connected_clients.ClientConnectionController;
import database.DatabaseController;
import database.ExternalDatabaseController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.stage.StageStyle;
import server.ZerliServerCommunication;
import server.ZerliServerUI;
import utils.Constants;

import javax.swing.*;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class ServerScreenFXController {
	private ZerliServerCommunication zerliCommunincation;

	@FXML
	private TextField IpTextField;
	@FXML
	private TextField dbTextField;

	@FXML
	private Button disconnectButton;
	@FXML
	private Button connectButton;
	@FXML
	private Button exitButton;
	@FXML
	private Button minimizeButton;
	@FXML
	private Button importUsersButton;

	@FXML
	private TextField portTextField;
	@FXML
	private TextField userNameTextField;

	@FXML
	private TextArea consoleTextArea;

	@FXML
	private TableView<ClientConnection> connectedClientsTableView;

	@FXML
	private PasswordField passwordTextField;

	@FXML
	private ScrollPane consoleScrollPane;

	@FXML
	public Text portErrorText;
	@FXML
	private Text passwordErrorText;
	@FXML
	private Text dbUserErrorText;
	@FXML
	private Text dbNameErrorText;

	@FXML
	private TableColumn<ClientConnection, String> ipCol;
	@FXML
	private TableColumn<ClientConnection, String> hostCol;
	@FXML
	private TableColumn<ClientConnection, String> statusCol;

	private double xOffset, yOffset;
	private static boolean isImportButtonClicked = false;

	public static ObservableList<ClientConnection> connectedClients = FXCollections.observableArrayList();

	public static BooleanProperty isServerCrash = new SimpleBooleanProperty(false);

	/**
	 * Start a primary stage screen
	 * @param primaryStage
	 * @throws Exception
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ServerScreenFX.fxml"));

		primaryStage.initStyle(StageStyle.UNDECORATED);

		root.setOnMousePressed(event -> {
			xOffset = event.getSceneX();
			yOffset = event.getSceneY();
		});

		root.setOnMouseDragged(event -> {
			primaryStage.setX(event.getScreenX() - xOffset);
			primaryStage.setY(event.getScreenY() - yOffset);
		});

		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Initialize the cells for the table view
	 */
	@FXML
	private void initialize() {
		ipCol.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
		hostCol.setCellValueFactory(new PropertyValueFactory<>("hostName"));
		statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
		connectedClientsTableView.setItems(connectedClients);

		// Change default timezone
		dbTextField.setText(dbTextField.getText().replace("IST", TimeZone.getDefault().getID()));

		isServerCrash.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				disconnectFromServer();
				isServerCrash.set(false);
			}
		});
	}

	/**
	 * This method runs when Exit button is pressed in UI.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onButtonPressExit(ActionEvent event) throws Exception {
		onButtonPressDisconnect(event);
		Platform.exit();
		System.exit(0);
	}

	/**
	 * This method runs when Minimize button is pressed in UI.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onButtonPressMinimizeWindow(ActionEvent event) throws Exception {
		minimizeButton.setOnAction(e -> {
			((Stage) ((Button) e.getSource()).getScene().getWindow()).setIconified(true);
		});
	}

	/**
	 * This method runs when Connect button is pressed in UI. Used to connect to the
	 * database.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onButtonPressConnect(ActionEvent event) throws Exception {
		if (!isFieldsValid())
			return;
		Message msg = DatabaseController.getInstance().connectToDB(IpTextField.getText(), portTextField.getText(),
				dbTextField.getText(), userNameTextField.getText(), passwordTextField.getText());
		if (msg.getAnswer() == Answer.SUCCEED) {
			connectButton.setDisable(true);
			disconnectButton.setDisable(false);
			//TODO: Fix import external users will include also account_id
			//importUsersButton.setDisable(isImportButtonClicked);

			if (zerliCommunincation == null)
				zerliCommunincation = new ZerliServerCommunication(Integer.parseInt(portTextField.getText()));
			ZerliServerUI.connectToServer(portTextField.getText());
		}
		writeToConsole(msg.getObject().toString());
	}

	/**
	 * This method runs when Disconnect button is pressed in UI.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onButtonPressDisconnect(ActionEvent event) throws Exception {
		disconnectFromServer();
	}

	private void disconnectFromServer() {
		if (zerliCommunincation != null) {
			ClientConnectionController.disconnectAllClients();
			zerliCommunincation.stopListening();
			writeToConsole("Disconnected from Database");
			connectButton.setDisable(false);
			disconnectButton.setDisable(true);
			importUsersButton.setDisable(true);
			passwordTextField.clear();
		}
	}

	/**
	 * Import the data from the external server
	 * @param event Button press
	 * @throws Exception
	 */
	public void onButtonPressImport(ActionEvent event) throws Exception {
		if (!isImportButtonClicked) {
			isImportButtonClicked = true;
			ExternalDatabaseController externalDatabaseController = ExternalDatabaseController.getInstance();
			Connection conn = externalDatabaseController.connectToExternalDB();
			if (conn != null && conn.isValid(1000)) {
				String resultString = DatabaseController.getInstance().updateUsersTable(externalDatabaseController.getAllUsers());
				importUsersButton.setDisable(true);
				writeToConsole(resultString);
			}
			else {
				JOptionPane.showMessageDialog(null, "Couldn't connect to external DB", "Connection Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * This method write to the console in the server GUI.
	 * 
	 * @param msg The message to be written.
	 */
	private void writeToConsole(String msg) {
		if (consoleTextArea.getText().isEmpty())
			consoleTextArea.setText(currentTime() + "\n" + msg);
		else
			consoleTextArea.setText(currentTime() + "\n" + msg + "\n\n" + consoleTextArea.getText());
	}

	/**
	 * This method returns the current time.
	 */
	private String currentTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		return formatter.format(LocalDateTime.now());
	}

	/**
	 * This method used to validate the values entered when trying to connect the
	 * database from the server GUI.
	 */
	private boolean isFieldsValid() {
		// Revert to default style
		portTextField.setStyle(Constants.TEXT_FIELD_VALID_STYLE);
		portErrorText.setVisible(false);
		dbTextField.setStyle(Constants.TEXT_FIELD_VALID_STYLE);
		dbNameErrorText.setVisible(false);
		userNameTextField.setStyle(Constants.TEXT_FIELD_VALID_STYLE);
		dbUserErrorText.setVisible(false);
		passwordTextField.setStyle(Constants.TEXT_FIELD_VALID_STYLE);
		passwordErrorText.setVisible(false);

		// Check validation and change style accordingly
		boolean isValid = true;
		if (portTextField.getText().isEmpty() || portTextField.getText().length() != 4) {
			portErrorText.setVisible(true);
			portTextField.setStyle(Constants.TEXT_FIELD_NOT_VALID_STYLE);
			isValid = false;
		}
		if (!dbTextField.getText().startsWith("jdbc:mysql://")) {
			dbTextField.setStyle(Constants.TEXT_FIELD_NOT_VALID_STYLE);
			dbNameErrorText.setVisible(true);
			isValid = false;
		}
		if (userNameTextField.getText().isEmpty()) {
			dbUserErrorText.setVisible(true);
			userNameTextField.setStyle(Constants.TEXT_FIELD_NOT_VALID_STYLE);
			isValid = false;
		}
		if (passwordTextField.getText().isEmpty()) {
			passwordErrorText.setVisible(true);
			passwordTextField.setStyle(Constants.TEXT_FIELD_NOT_VALID_STYLE);
			isValid = false;
		}
		return isValid;
	}
}