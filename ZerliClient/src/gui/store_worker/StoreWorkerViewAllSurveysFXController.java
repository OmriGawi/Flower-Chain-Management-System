package gui.store_worker;

import classes.Survey;
import controllers.StoreController;
import controllers.SurveyController;
import gui.sidenavigation.SidenavigationStoreWorkerFXController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
/**
 * This class defines the Store Worker View All Surveys screen
 */
public class StoreWorkerViewAllSurveysFXController extends Pane implements Initializable {

    // Pane
    @FXML
    private Pane addSurveyPane;
    @FXML
    private Pane pane;


    @FXML
    private TableColumn<Survey, String> descriptionCol;

    @FXML
    private TableColumn<Survey, Integer> surveyIdCol;

    @FXML
    private Button fillSurveyButton;

    @FXML
    private Text fillSurveyErrorText;

    @FXML
    private TableView<Survey> surveysTableView;

    // Variables
    private double xOffset, yOffset;
    private String[] questions;
    private int[] answers;
    private int currQuestion;
    private ToggleButton[] ratingPressedBtn;

    private SidenavigationStoreWorkerFXController sidenavigationStoreWorkerFXController;

    public static ObservableList<Survey> surveys = FXCollections.observableArrayList();
    public static BooleanProperty isAllowedToFillSurveysProperty = new SimpleBooleanProperty(false);
    public static Survey chosenSurvey = null;

    /**
     * Constructor
     * @param sidenavigationStoreWorkerFXController
     */
    public StoreWorkerViewAllSurveysFXController(SidenavigationStoreWorkerFXController sidenavigationStoreWorkerFXController) {
        this.sidenavigationStoreWorkerFXController = sidenavigationStoreWorkerFXController;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/store_worker/StoreWorkerViewAllSurveysFX.fxml"));

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
        surveyIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        surveysTableView.setItems(surveys);
        SurveyController.getAllSurveys();

        fillSurveyButton.setDisable(!isAllowedToFillSurveysProperty.get());
        fillSurveyErrorText.setVisible(!isAllowedToFillSurveysProperty.get());
        isAllowedToFillSurveysProperty.addListener((observable, oldValue, newValue) -> {
            fillSurveyButton.setDisable(!newValue);
            fillSurveyErrorText.setVisible(!newValue);
            if (newValue) {
                fillSurveyErrorText.setText("Please select survey");
            } else {
                fillSurveyErrorText.setText("You don't have permission!");
            }
        });
        StoreController.getWorkerPermission(Utils.currUser.getId());
    }

    /**
     * Open the add survey screen once a survey has been selected and fill survey button is pressed
     * @param event Button Press
     */
    @FXML
    void onButtonPressFillSurvey(ActionEvent event) {
        chosenSurvey = surveysTableView.getSelectionModel().getSelectedItem();
        fillSurveyErrorText.setVisible(false);
        if (chosenSurvey != null) {
            fillSurveyErrorText.setVisible(false);
            fillSurvey();
        }
        else {
            fillSurveyErrorText.setText("Please select survey");
            fillSurveyErrorText.setVisible(true);
        }
    }

    /**
     * Open the add survey screen for the store worker
     */
    private void fillSurvey() {
        StoreWorkerFillSurveyFXController storeWorkerAddSurveyFXController = new StoreWorkerFillSurveyFXController();
        sidenavigationStoreWorkerFXController.setMainPaneCenter(storeWorkerAddSurveyFXController);
        sidenavigationStoreWorkerFXController.setTopBarText("Login -> User Portal -> Fill Survey");
    }
}