package gui.service_expert;

import classes.Survey;
import controllers.SurveyController;
import gui.sidenavigation.SidenavigationCustomerServiceWorkerFXController;
import gui.sidenavigation.SidenavigationServiceExpertFXController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class defines the service expert view all surveys screen
 */
public class ServiceExpertViewAllSurveysFXController extends Pane implements Initializable {

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

    private SidenavigationServiceExpertFXController sidenavigationServiceExpertFXController;

    public static ObservableList<Survey> surveys = FXCollections.observableArrayList();;
    public static Survey chosenSurvey = null;

    /**
     * Constructor
     * @param sidenavigationServiceExpertFXController
     */
    public ServiceExpertViewAllSurveysFXController(SidenavigationServiceExpertFXController sidenavigationServiceExpertFXController) {
        this.sidenavigationServiceExpertFXController = sidenavigationServiceExpertFXController;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/service_expert/ServiceExpertViewAllSurveysFX.fxml"));

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
        fillSurveyErrorText.setVisible(false);
        surveyIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        surveysTableView.setItems(surveys);
        SurveyController.customerServiceGetAllSurveys();
    }

    /**
     * Open the fill survey conclusions screen for the survey chosen, also prompt relevant message text
     * to user on screen
     * @param event
     */
    @FXML
    void onButtonPressFillSurvey(ActionEvent event) {
        chosenSurvey = surveysTableView.getSelectionModel().getSelectedItem();
        fillSurveyErrorText.setVisible(false);
        if(chosenSurvey!=null){
            fillSurveyErrorText.setVisible(false);
            fillSurvey();
        }
        else {
            fillSurveyErrorText.setText("Please select survey");
            fillSurveyErrorText.setVisible(true);
        }
    }

    /**
     * Open the fill survey conclusions screen to the user
     */
    private void fillSurvey() {
        ServiceExpertSurveyConclusionsScreenFXController serviceExpertSurveyConclusionsScreenFXController = new ServiceExpertSurveyConclusionsScreenFXController();
        sidenavigationServiceExpertFXController.setMainPaneCenter(serviceExpertSurveyConclusionsScreenFXController);
        sidenavigationServiceExpertFXController.setTopBarText("Login -> User Portal -> View Surveys -> Conclusions");
    }
}