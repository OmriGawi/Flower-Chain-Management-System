package gui.sidenavigation;

import client.ZerliClientUI;
import controllers.LoginController;
import gui.ceo.CeoMyAccountScreenFXController;
import gui.ceo.CeoTypeOfReportScreenFXController;
import gui.login.LoginScreenFXController;
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
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.Functions;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class defines the Side Navigation bar for the CEO role in the system.
 * The side navigation bar will present only the relevant icons and screens that the CEO need to see
 * and interact with.
 */
public class SidenavigationCEOFXController extends Application implements Initializable {

    @FXML
    private Parent root;
    @FXML
    private Stage stage;
    @FXML
    private BorderPane mainPane;

    // Buttons
    @FXML
    private Button btnExit;
    @FXML
    private Button btnMinimize;

    // Text
    @FXML
    private Text topBarText;

    // Variables
    private double xOffset, yOffset;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            root = FXMLLoader.load(getClass().getResource("/gui/sidenavigation/sidenavigationCEO.fxml"));
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

    /**
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CeoMyAccountScreenFXController ceoMyAccountScreenFXController = new CeoMyAccountScreenFXController();
        mainPane.setCenter(ceoMyAccountScreenFXController);
        topBarText.setText("Login -> User Portal -> My Account");
    }

    @FXML
    void onButtonPressExit(ActionEvent event) {
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
        CeoMyAccountScreenFXController ceoMyAccountScreenFXController = new CeoMyAccountScreenFXController();
        mainPane.setCenter(ceoMyAccountScreenFXController);
        topBarText.setText("Login -> User Portal -> My Account");
    }

    @FXML
    void onButtonPressViewReports(MouseEvent event) {
        CeoTypeOfReportScreenFXController ceoTypeOfReportScreenFXController = new CeoTypeOfReportScreenFXController(this);
        mainPane.setCenter(ceoTypeOfReportScreenFXController);
        topBarText.setText("Login -> User Portal -> Choose Report Type");
    }

    public void setMainPaneCenter(Pane pane) {
        mainPane.setCenter(pane);
    }

    public void setTopBarText(String text) {
        topBarText.setText(text);
    }
}