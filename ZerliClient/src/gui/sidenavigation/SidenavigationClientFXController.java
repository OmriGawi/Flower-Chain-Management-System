package gui.sidenavigation;

import classes.Item;
import client.ZerliClientUI;
import controllers.LoginController;
import gui.client.ClientCartScreenFXController;
import gui.client.ClientMyAccountScreenFXController;
import gui.client.ClientMyOrdersScreenFXController;
import gui.client.ClientViewCatalogScreenFXController;
import gui.login.LoginScreenFXController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.Functions;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
/**
 * This class defines the Side Navigation bar for the Client role in the system.
 * The side navigation bar will present only the relevant icons and screens that the Client need to see
 * and interact with.
 */
public class SidenavigationClientFXController extends Application implements Initializable {

    @FXML
    private Parent root;
    @FXML
    private Stage stage;
    @FXML
    private BorderPane mainPane;

    // Buttons
    @FXML
    private Button cartButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button minimizeButton;

    // Sidebar HBox
    @FXML
    private HBox myAccountHBox;
    @FXML
    private HBox myOrdersHBox;
    @FXML
    private HBox viewCatalogHBox;

    // Text
    @FXML
    private Text topBarText;
    @FXML
    private Text itemsInCartText;

    // Variables
    private double xOffset, yOffset;

    public static ObservableList<Item> cart = FXCollections.observableArrayList();
    public static HashMap<Item, Integer> cartQuantity = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            root = FXMLLoader.load(getClass().getResource("/gui/sidenavigation/sidenavigationClient.fxml"));
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
        ClientMyAccountScreenFXController clientMyAccountScreenFXController = new ClientMyAccountScreenFXController();
        mainPane.setCenter(clientMyAccountScreenFXController);
        topBarText.setText("Login -> User Portal -> My Account");

        cart.addListener((ListChangeListener<Item>) c -> {
            itemsInCartText.setVisible(cart.size() > 0);
            itemsInCartText.setText(String.valueOf(cart.size()));
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        cart.clear();
        cartQuantity.clear();
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
        cart.clear();
        cartQuantity.clear();
        Stage primaryStage = ((Stage)((Button) event.getSource()).getScene().getWindow());
        Functions.openNewWindowInsteadOfCurrent(primaryStage, getClass(), "/gui/login/LoginScreenFX.fxml");
        LoginController.logoutUser(LoginScreenFXController.currentUsername);
    }

    @FXML
    void onButtonPressMyAccount(MouseEvent event) {
        ClientMyAccountScreenFXController clientMyAccountScreenFXController = new ClientMyAccountScreenFXController();
        mainPane.setCenter(clientMyAccountScreenFXController);
        topBarText.setText("Login -> User Portal -> My Account");
    }

    @FXML
    void onButtonPressMyOrders(MouseEvent event) {
        ClientMyOrdersScreenFXController clientMyOrdersScreenFXController = new ClientMyOrdersScreenFXController(this);
        mainPane.setCenter(clientMyOrdersScreenFXController);
        topBarText.setText("Login -> User Portal -> My Orders");
    }

    @FXML
    void onButtonPressViewCatalog(MouseEvent event) {
        ClientViewCatalogScreenFXController clientViewCatalogScreenFXController = new ClientViewCatalogScreenFXController(this);
        mainPane.setCenter(clientViewCatalogScreenFXController);
        topBarText.setText("Login -> User Portal -> View Catalog");
    }

    @FXML
    void onButtonPressGoToCart(MouseEvent event) {
        ClientCartScreenFXController clientCartScreenFXController = new ClientCartScreenFXController(this);
        mainPane.setCenter(clientCartScreenFXController);
        topBarText.setText("Login -> User Portal -> My Cart");
    }

    public void setMainPaneCenter(Pane pane) {
        mainPane.setCenter(pane);
    }

    public void setTopBarText(String text) {
        topBarText.setText(text);
    }
}
