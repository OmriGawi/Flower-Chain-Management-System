package gui.sidenavigation;

import client.ZerliClientUI;
import controllers.LoginController;
import gui.login.LoginScreenFXController;
import gui.store_manager.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.Functions;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * This class defines the Side Navigation bar for the Store Manager role in the system.
 * The side navigation bar will present only the relevant icons and screens that the Store Manager need to see
 * and interact with.
 */
public class SidenavigationStoreManagerFXController extends Application implements Initializable {

    @FXML
    private Parent root;
    @FXML
    private Stage stage;
    @FXML
    private BorderPane mainPane;

    // SideBar HBox
    @FXML
    private HBox myAccountHBox;
    @FXML
    private HBox approveOrdersHBox;
    @FXML
    private HBox cancelOrdersHBox;
    @FXML
    private HBox newAccountHBox;
    @FXML
    private HBox updateDetailsHBox;
    @FXML
    private HBox viewReportsHBox;
    @FXML
    private HBox permissionsHBox;

    // Buttons
    @FXML
    private Button exitButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button minimizeButton;

    // Text
    @FXML
    private Text topBarText;


    // Variables
    private double xOffset, yOffset;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            root = FXMLLoader.load(getClass().getResource("/gui/sidenavigation/sidenavigationStoreManager.fxml"));
            stage = primaryStage;

            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            root.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });

            Scene scene = new Scene(root);
            stage.setScene(scene);
            Functions.centerWindow(primaryStage);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StoreManagerMyAccountScreenFXController storeManagerMyAccountScreenFXController = new StoreManagerMyAccountScreenFXController();
        mainPane.setCenter(storeManagerMyAccountScreenFXController);
        topBarText.setText("Login -> User Portal -> My Account");
    }

    @FXML
    void onButtonPressExit(ActionEvent event) throws Exception{
        LoginController.logoutUser(LoginScreenFXController.currentUsername);
        ZerliClientUI.logoutFromServer();
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void onButtonPressMinimizeWindow(ActionEvent event) {
        ((Stage)((Button) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    @FXML
    void onButtonPressLogout(ActionEvent event) {
        Stage primaryStage = ((Stage)((Button) event.getSource()).getScene().getWindow());
        Functions.openNewWindowInsteadOfCurrent(primaryStage, getClass(), "/gui/login/LoginScreenFX.fxml");
        LoginController.logoutUser(LoginScreenFXController.currentUsername);
    }

    @FXML
    void onButtonPressMyAccount(MouseEvent event) {
        StoreManagerMyAccountScreenFXController storeManagerMyAccountScreenFXController = new StoreManagerMyAccountScreenFXController();
        mainPane.setCenter(storeManagerMyAccountScreenFXController);
        topBarText.setText("Login -> User Portal -> My Account");
    }

    @FXML
    void onButtonPressApproveOrders(MouseEvent event) {
        StoreManagerApproveOrdersScreenFXController storeManagerApproveOrdersScreenFXController = new StoreManagerApproveOrdersScreenFXController();
        mainPane.setCenter(storeManagerApproveOrdersScreenFXController);
        topBarText.setText("Login -> User Portal -> Approve Orders");
    }

    @FXML
    void onButtonPressCancelOrders(MouseEvent event) {
        StoreManagerCancelOrdersScreenFXController storeManagerCancelOrdersScreenFXController = new StoreManagerCancelOrdersScreenFXController();
        mainPane.setCenter(storeManagerCancelOrdersScreenFXController);
        topBarText.setText("Login -> User Portal -> Cancel Orders");
    }

    @FXML
    void onButtonPressNewAccount(MouseEvent event) {
        StoreManagerCreateAccountScreenFXController storeManagerCreateAccountScreenFXController = new StoreManagerCreateAccountScreenFXController();
        mainPane.setCenter(storeManagerCreateAccountScreenFXController);
        topBarText.setText("Login -> User Portal -> Create New Account");
    }

    @FXML
    void onButtonPressUpdateDetails(MouseEvent event) {
        StoreManagerUpdateAccountScreenFXController storeManagerUpdateAccountScreenFXController = new StoreManagerUpdateAccountScreenFXController();
        mainPane.setCenter(storeManagerUpdateAccountScreenFXController);
        topBarText.setText("Login -> User Portal -> Edit Account Details");
    }

    @FXML
    void onButtonPressViewReports(MouseEvent event) {
        StoreManagerViewReportsScreenFXController storeManagerViewReportsScreenFXController = new StoreManagerViewReportsScreenFXController(this);
        mainPane.setCenter(storeManagerViewReportsScreenFXController);
        topBarText.setText("Login -> User Portal -> View Reports");
    }

    @FXML
    void onButtonPressPermissions(MouseEvent event) {
        StoreManagerPermissionsScreenFXController storeManagerPermissionsScreenFXController = new StoreManagerPermissionsScreenFXController();
        mainPane.setCenter(storeManagerPermissionsScreenFXController);
        topBarText.setText("Login -> User Portal -> Permissions");
    }

    public void setMainPaneCenter(Pane pane) {
        mainPane.setCenter(pane);
    }

    public void setTopBarText(String text) {
        topBarText.setText(text);
    }

}
