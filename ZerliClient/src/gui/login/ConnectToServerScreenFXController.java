package gui.login;

import client.ZerliClientController;
import client.ZerliClientUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static utils.Constants.DEFAULT_PORT;
/**
 * This class defines to Connect to Server screen confirmation screen
 */
public class ConnectToServerScreenFXController extends Application {
    @FXML
    private Button connectButton;
    @FXML
    private Button exitButton;
    @FXML
    private TextField ipTextField;
    @FXML
    private Button minimizeButton;
    @FXML
    private TextField portTextField;
    @FXML
    private Text ipErrorLabel;
    @FXML
    private Text portErrorLabel;
    @FXML
    private Text serverErrorLabel;

    private double xOffset, yOffset;

    public static Stage primaryStage;

    public static StringProperty updateStatus = new SimpleStringProperty("");

    /**
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        ConnectToServerScreenFXController.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);

        Parent root = FXMLLoader.load(getClass().getResource("/gui/login/ConnectToServerScreenFX.fxml"));

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * Close window and terminate app
     * @param event Button Press  on the 'X' button
     */
    @FXML
    void onButtonPressExit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Minimize window
     * @param event Button Press on the '-'
     */
    @FXML
    void onButtonPressMinimizeWindow(ActionEvent event) {
        ((Stage)((Button) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    /**
     * Connect client to server
     * @param event
     */
    @FXML
    void onButtonPressConnect(ActionEvent event) {
        // IP
        String ip = ipTextField.getText().toLowerCase();
        if (!isIPValid(ip)) {
            ipErrorLabel.setVisible(true);
            return;
        }
        else ipErrorLabel.setVisible(false);

        // Port
        int port;
        try {
            if (portTextField.getText().length() == 4) {
                port = Integer.parseInt(portTextField.getText());
                portErrorLabel.setVisible(false);
            } else throw new Exception();
        } catch (Exception e) {
            portErrorLabel.setVisible(true);
            return;
        }

        // Connect to server
        ZerliClientUI.zerliClientController = new ZerliClientController(ip, port);
        if (ZerliClientController.client == null || !ZerliClientController.client.isConnected())
            serverErrorLabel.setVisible(true);
        else openLoginScreen();
    }

    /**
     * Check if the IP address inserted is valid.
     * @param ip
     * @return True if valid, False otherwise
     */
    private boolean isIPValid(String ip) {
        if (ip.equalsIgnoreCase("localhost"))
            return true;
        String[] splitString = ip.split("[.]");
        if (splitString.length > 4 || ip.startsWith(".") || ip.endsWith(".")) {
            return false;
        }
        for (String string : splitString) {
            if (string.isEmpty()) {
                return false;
            }
            if (!string.matches("[0-9]{1,3}")) {
                return false;
            }
            int number = Integer.parseInt(string);
            if (!(number >= 0 && number <= 255)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Open the login screen once a successful connection is established
     */
    public void openLoginScreen() {
        try {
            LoginScreenFXController loginScreenFXController = new LoginScreenFXController();
            loginScreenFXController.start(primaryStage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        /*try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/gui/login/LoginScreenFX.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            primaryStage.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}