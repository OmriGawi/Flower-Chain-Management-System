package gui.sidenavigation;

import client.ZerliClientUI;
import controllers.LoginController;
import gui.login.LoginScreenFXController;
import gui.service_expert.ServiceExpertViewAllSurveysFXController;
import gui.service_expert.ServiceExpertMyAccountScreenFXController;
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
import utils.Functions;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
/**
 * This class defines the Side Navigation bar for the Service Expert role in the system.
 * The side navigation bar will present only the relevant icons and screens that the Service Expert need to see
 * and interact with.
 */
public class SidenavigationServiceExpertFXController extends Application implements Initializable {

    @FXML
    private Parent root;
    @FXML
    private Stage stage;
    @FXML
    private BorderPane mainPane;

    // Sidebar HBox
    @FXML
    private HBox myAccountHBox;
    @FXML
    private HBox addConclusionsHBox;

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
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/gui/sidenavigation/sidenavigationServiceExpert.fxml")));
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
        ServiceExpertMyAccountScreenFXController serviceExpertMyAccountScreenFXController = new ServiceExpertMyAccountScreenFXController();
        mainPane.setCenter(serviceExpertMyAccountScreenFXController);
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
        ServiceExpertMyAccountScreenFXController serviceExpertMyAccountScreenFXController = new ServiceExpertMyAccountScreenFXController();
        mainPane.setCenter(serviceExpertMyAccountScreenFXController);
        topBarText.setText("Login -> User Portal -> My Account");
    }

    @FXML
    void onButtonPressAddConclusions(MouseEvent event) {
        ServiceExpertViewAllSurveysFXController serviceExpertViewAllSurveysFXController = new ServiceExpertViewAllSurveysFXController(this);
        mainPane.setCenter(serviceExpertViewAllSurveysFXController);
        topBarText.setText("Login -> User Portal -> View Surveys");
    }


    public void setMainPaneCenter(Pane pane) {
        mainPane.setCenter(pane);
    }
    public void setTopBarText(String text) {
        topBarText.setText(text);
    }

}
