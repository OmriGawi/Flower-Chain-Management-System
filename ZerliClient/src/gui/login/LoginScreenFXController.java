package gui.login;

import enums.RoleEnum;
import client.ZerliClientUI;
import controllers.LoginController;
import gui.sidenavigation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import utils.Constants;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
/**
 * This class defines User Login screen
 */
public class LoginScreenFXController extends Application implements Initializable {

    public static Stage primaryStage;
    @FXML
    private Button exitButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button loginButton;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Text passwordErrorLabel;
    @FXML
    private TextField userNameField;
    @FXML
    private Text usernameErrorLabel;
    @FXML
    private Text loginErrorLabel;

    public static String currentUsername;

    private double xOffset, yOffset;
    public static StringProperty loginStatus = new SimpleStringProperty("");

    /**
     * Start screen and initialize the Stage and scene for the screen
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        LoginScreenFXController.primaryStage = primaryStage;

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/gui/login/LoginScreenFX.fxml")));

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
    public void onButtonPressExit(ActionEvent event) throws Exception {
        ZerliClientUI.logoutFromServer();
        Platform.exit();
        System.exit(0);
    }

    /**
     * Minimize window
     * @param event Button Press on the '-'
     */
    public void onButtonPressMinimizeWindow(ActionEvent event) throws Exception {
        minimizeButton.setOnAction(e -> ((Stage) ((Button) e.getSource()).getScene().getWindow()).setIconified(true));
    }

    /**
     * Connect to server once pressed on the Login button.
     * Prompt error text on screen if input not valid
     * @param event
     */
    @FXML
    void onButtonPressLogin(ActionEvent event) {
        String username = userNameField.getText();
        String password = passwordField.getText();
        usernameErrorLabel.setVisible(username.isEmpty());
        passwordErrorLabel.setVisible(password.isEmpty());

        if (username.isEmpty()) {
            userNameField.setStyle(Constants.TEXT_FIELD_NOT_VALID_STYLE);
        } else userNameField.setStyle(Constants.TEXT_FIELD_VALID_STYLE);
        if (password.isEmpty()) {
            passwordField.setStyle(Constants.TEXT_FIELD_NOT_VALID_STYLE);
        } else passwordField.setStyle(Constants.TEXT_FIELD_VALID_STYLE);

        if (!username.isEmpty() && !password.isEmpty()) {
            currentUsername = username;
            primaryStage = ((Stage) ((Button) event.getSource()).getScene().getWindow());
            LoginController.loginByUsernameAndPassword(username, password);
        }
        if (usernameErrorLabel.isVisible() || passwordErrorLabel.isVisible()) {
            loginErrorLabel.setVisible(false);
        }
    }

    /**
     * Update login status of user, and show the relevant side navigation bar that corresponds
     * to the user's role
     * @param loginStatusStr Login status of the user
     * @param roleEnum The role of the user connected
     */
    public static void loginStatusUpdated(String loginStatusStr, RoleEnum roleEnum) {
        loginStatus.set(loginStatusStr);
        if (loginStatusStr.contains("success")) {
            openSideNavigationByRole(roleEnum);
        }
    }

    /**
     * Show the relevant side navigation for each role
     * @param roleEnum The user's role
     */
    public static void openSideNavigationByRole(RoleEnum roleEnum) {
        switch (roleEnum) {
            case CEO:
                SidenavigationCEOFXController sidenavigationCEOFXController = new SidenavigationCEOFXController();
                Platform.runLater(() -> sidenavigationCEOFXController.start(primaryStage));
                break;
            case CLIENT:
                SidenavigationClientFXController sidenavigationClientFXController = new SidenavigationClientFXController();
                Platform.runLater(() -> sidenavigationClientFXController.start(primaryStage));
                break;
            case COMPANY_MARKETING_WORKER:
                SidenavigationCompanyMarketingWorkerFXController sidenavigationCompanyMarketingWorkerFXController = new SidenavigationCompanyMarketingWorkerFXController();
                Platform.runLater(() -> sidenavigationCompanyMarketingWorkerFXController.start(primaryStage));
                break;
            case CUSTOMER_SERVICE_WORKER:
                SidenavigationCustomerServiceWorkerFXController sidenavigationCustomerServiceWorkerFXController = new SidenavigationCustomerServiceWorkerFXController();
                Platform.runLater(() -> sidenavigationCustomerServiceWorkerFXController.start(primaryStage));
                break;
            case STORE_MANAGER:
                SidenavigationStoreManagerFXController sidenavigationStoreManagerFXController = new SidenavigationStoreManagerFXController();
                Platform.runLater(() -> sidenavigationStoreManagerFXController.start(primaryStage));
                break;
            case STORE_WORKER:
                SidenavigationStoreWorkerFXController sidenavigationStoreWorkerFXController = new SidenavigationStoreWorkerFXController();
                Platform.runLater(() -> sidenavigationStoreWorkerFXController.start(primaryStage));
                break;
            case SERVICE_EXPERT:
                SidenavigationServiceExpertFXController sidenavigationServiceExpertFXController = new SidenavigationServiceExpertFXController();
                Platform.runLater(() -> sidenavigationServiceExpertFXController.start(primaryStage));
                break;
            case DELIVERY_MAN:
                SidenavigationDeliverymanFXController sidenavigationDeliveryManFXController = new SidenavigationDeliverymanFXController();
                Platform.runLater(() -> sidenavigationDeliveryManFXController.start(primaryStage));
                break;
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
        loginStatus.addListener((observable, oldValue, newValue) -> {
            loginErrorLabel.setText(newValue);
            loginErrorLabel.setTextAlignment(TextAlignment.CENTER);
            loginErrorLabel.setVisible(newValue.contains("incorrect") || newValue.contains("already logged-in"));
        });
    }
}
