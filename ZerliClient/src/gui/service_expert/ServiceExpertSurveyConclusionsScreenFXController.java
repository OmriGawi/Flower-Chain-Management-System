package gui.service_expert;

import classes.Survey;
import classes.SurveyQuestion;
import controllers.SurveyController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import utils.Functions;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class defines the service expert conclusions for surveys screen
 */
public class ServiceExpertSurveyConclusionsScreenFXController extends Pane implements Initializable {

    // Questions TableView
    @FXML
    private TableView<SurveyQuestion> questionsTableView;
        @FXML
        private TableColumn<SurveyQuestion, String> questionCol;
        @FXML
        private TableColumn<SurveyQuestion, Double> ratingCol;

    // Buttons
    @FXML
    private Button submitConclusionsButton;

    // Text Area
    @FXML
    private TextArea conclusionsTextArea;

    // Text
    @FXML
    private Text successText;

    // Variables
    private int surveyID;
    public static ObservableList<SurveyQuestion> surveyQuestion = FXCollections.observableArrayList();
    public static StringProperty conclusionsTextAreaProperty = new SimpleStringProperty("Enter conclusion here");
    private String currentConclusion;
    public Survey chosenSurvey;

    /**
     * Constructor to initialize the screen
     */
    public ServiceExpertSurveyConclusionsScreenFXController(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/service_expert/ServiceExpertSurveyConclusionsScreenFX.fxml"));

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
        successText.setVisible(false);
        submitConclusionsButton.setDisable(true);
        questionCol.setCellValueFactory(new PropertyValueFactory<>("question"));
        ratingCol.setCellValueFactory(param -> new SimpleObjectProperty<>(Functions.round(param.getValue().getAvgRating(), 2)));

        questionsTableView.setItems(surveyQuestion);
        chosenSurvey = ServiceExpertViewAllSurveysFXController.chosenSurvey;
        SurveyController.customerServiceGetSurveysQuestions(ServiceExpertViewAllSurveysFXController.chosenSurvey.getId());
        if(chosenSurvey.getConclusion() != null && !chosenSurvey.getConclusion().isEmpty()){
            currentConclusion = chosenSurvey.getConclusion();
            conclusionsTextArea.setText(currentConclusion);
        }
        else
            conclusionsTextArea.setPromptText("Enter conclusion here");
    }

    /**
     * Check if user entered any text to the Text Area, if not then the 'Submit Conclusions' button is disabled
     * and enabled otherwise
     * @param event Key typed / pressed while cursor is in the Text Area field on screen
     */
    @FXML
    void onTextChangedConclusions(KeyEvent event) {
        submitConclusionsButton.setDisable(conclusionsTextArea.getText().equals("Enter conclusion here")||
                conclusionsTextArea.getText().equals(currentConclusion));
        successText.setVisible(false);
    }

    /**
     * Update conclusions for a survey in the DB, and prompt success text to the user
     * @param event Button Press on 'Submit Conclusions'
     */
    @FXML
    void onButtonPressSubmitConclusions(ActionEvent event) {
        successText.setVisible(true);
        currentConclusion=conclusionsTextArea.getText();
        submitConclusionsButton.setDisable(true);
        chosenSurvey.setConclusion(currentConclusion);
        List<SurveyQuestion> questionsList = new ArrayList<>();
        for (SurveyQuestion question : surveyQuestion)
            questionsList.add(question);
        chosenSurvey.setQuestions(questionsList);
        SurveyController.updateConclusion(chosenSurvey);
    }
}