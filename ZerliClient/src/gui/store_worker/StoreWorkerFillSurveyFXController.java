package gui.store_worker;

import classes.Survey;
import classes.SurveyQuestion;
import controllers.SurveyController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import utils.Constants;
import java.io.IOException;
import java.net.URL;
import java.util.*;
/**
 * This class defines the Store Worker fill survey screen
 */
public class StoreWorkerFillSurveyFXController extends Pane implements Initializable {

    // Buttons
    @FXML
    private Button addSurveyButton;
    @FXML
    private Button backButton;
    @FXML
    private Button nextButton;

    // Text
    @FXML
    private Text clientNumberLabel;
    @FXML
    private Text clientNumberErrorLabel;
    @FXML
    private Text questionLabel;
    @FXML
    private Text successText;

    // Labels
    @FXML
    private Label maximumScaleLabel;
    @FXML
    private Label minimumScaleLabel;

    // Pane
    @FXML
    private Pane addSurveyPane;
    @FXML
    private Pane pane;

    // Text Fields
    @FXML
    private TextField clientNumberTextField;

    // Variables
    private double xOffset, yOffset;
    public static Survey choosenSurvey = StoreWorkerViewAllSurveysFXController.chosenSurvey;
    public static List<SurveyQuestion> surveyQuestions = new ArrayList();
    public static StringProperty questionLabelTextProperty = new SimpleStringProperty("");
    public static StringProperty successTextProperty = new SimpleStringProperty("");


    private int[] answers = new int[6];
    private static int currQuestion;
    private ToggleButton[] ratingPressedBtn;

    /**
     * Constructor
     */
    public StoreWorkerFillSurveyFXController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/store_worker/StoreWorkerFillSurveyFX.fxml"));

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
        this.ratingPressedBtn = new ToggleButton[6];
        successText.setVisible(false);
        this.currQuestion = 0;
        choosenSurvey = StoreWorkerViewAllSurveysFXController.chosenSurvey;
        questionLabelTextProperty.set("");
        questionLabelTextProperty.addListener((observable, oldValue, newValue) -> {
            showQuestions();
        });
        successTextProperty.addListener((observable, oldValue, newValue) -> {
            showSuccess();
        });
        SurveyController.getSurveyQuestion(choosenSurvey.getId());
    }

    /**
     * Show the questions on screen
     */
    public void showQuestions(){
        questionLabel.setText((currQuestion+1) + "/6 : " + surveyQuestions.get(0).getQuestion());
        backButton.setVisible(false);
    }

    /**
     * Press and save the rating for a particular question
     * @param event
     * @throws Exception
     */
    public void onButtonPressRating(ActionEvent event) throws Exception {
        this.ratingPressedBtn[currQuestion] = (ToggleButton) event.getSource();
        answers[currQuestion] = Integer.parseInt(this.ratingPressedBtn[currQuestion].getText());

        boolean isAllQuestionsHasAnswers = Arrays.stream(ratingPressedBtn).allMatch(Objects::nonNull);
        if (isAllQuestionsHasAnswers) {
            addSurveyPane.setDisable(false);
        }
    }

    /**
     * Show the next question
     * @param event
     * @throws Exception
     */
    public void onButtonPressNext(ActionEvent event) throws Exception {
        currQuestion++;

        // Set question text
        questionLabel.setText((currQuestion+1) + "/6 : " + surveyQuestions.get(currQuestion).getQuestion());

        // Deselect prev answer & Select current answer
        if (ratingPressedBtn[currQuestion - 1] != null)
            ratingPressedBtn[currQuestion - 1].setSelected(false);
        if (this.ratingPressedBtn[currQuestion] != null)
            this.ratingPressedBtn[currQuestion].setSelected(true);

        // Make sure 'back' button is visible
        backButton.setVisible(true);

        // Don't show 'next' button on the last question
        if (currQuestion == 5) {
            nextButton.setVisible(false);
        }
    }

    /**
     * Show the previous question
     * @param event
     * @throws Exception
     */
    public void onButtonPressBack(ActionEvent event) throws Exception {
        currQuestion--;

        // Set question text
        questionLabel.setText((currQuestion+1) + "/6 : " + surveyQuestions.get(currQuestion).getQuestion());

        // Deselect prev answer & Select current answer
        if (ratingPressedBtn[currQuestion + 1] != null)
            ratingPressedBtn[currQuestion + 1].setSelected(false);
        if (this.ratingPressedBtn[currQuestion] != null)
            this.ratingPressedBtn[currQuestion].setSelected(true);

        // Make sure 'next' button is visible
        nextButton.setVisible(true);

        // Don't show 'back' button on the first question
        if (currQuestion == 0) {
            backButton.setVisible(false);
        }
    }

    /**
     * Add the survey and rating for the questions in the DB
     * @param event
     * @throws Exception
     */
    public void onButtonPressAddSurvey(ActionEvent event) throws Exception {
        successTextProperty.set("");
        int  numberOfParticipants = surveyQuestions.get(0).getAmuntOfParticipants();
        int answer;
        double cururentAvg ;
        for (int i=0; i < 6 ; i++)
        {
            answer = answers[i];
            cururentAvg=surveyQuestions.get(i).getAvgRating();
            surveyQuestions.get(i).setAvgRating((numberOfParticipants*cururentAvg + answer)/(numberOfParticipants+1));
            surveyQuestions.get(i).setAmuntOfParticipants(numberOfParticipants+1);
        }
        SurveyController.updateAnswers(surveyQuestions);
    }

    /**
     * Show success text on screen
     */
    private void showSuccess(){
        pane.setDisable(true);
        successText.setVisible(true);
    }
}