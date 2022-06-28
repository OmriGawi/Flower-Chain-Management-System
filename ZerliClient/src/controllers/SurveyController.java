package controllers;

import classes.*;
import client.ZerliClientController;
import communication.Answer;
import communication.Message;
import communication.Task;
import gui.service_expert.ServiceExpertSurveyConclusionsScreenFXController;
import gui.service_expert.ServiceExpertViewAllSurveysFXController;
import gui.store_worker.StoreWorkerFillSurveyFXController;
import gui.store_worker.StoreWorkerViewAllSurveysFXController;

import java.util.ArrayList;
import java.util.List;

/**
 * This class creates Survey Messages and
 * show the results on screens.
 */
public class SurveyController {

    /**
     * Get all orders by specific client username.
     */
    public static void getAllSurveys() {
        Message msg = new Message(Task.GET_ALL_SURVEYS, Answer.WAIT_RESPONSE);
        ZerliClientController.accept(msg);
    }

    /**
     * Will show the all surveys in the store in the Store Worker GUI
     * @param surveysList ArrayList of Survey
     */
    public static void showAllSurveys(Object surveysList) {
        if (surveysList instanceof ArrayList<?>) {
            if (((ArrayList<?>) surveysList).get(0) instanceof Survey) {
                StoreWorkerViewAllSurveysFXController.surveys.clear();
                StoreWorkerViewAllSurveysFXController.surveys.addAll((ArrayList<Survey>) surveysList);
            }
        }
    }

    /**
     * Get all the questions in the survey from the DB
     * @param id The ID of the Survey
     */
    public static void getSurveyQuestion(int id) {
        Message msg = new Message(Task.GET_SURVEY_QUESTIONS, Answer.WAIT_RESPONSE,id);
        ZerliClientController.accept(msg);
    }

    /**
     * Will show all survey questions for a particular survey in the Store Worker GUI
     * @param questionsLists ArrayList of SurveyQuestion
     */
    public static void showSurveyQuestions(Object questionsLists) {
        if (questionsLists instanceof ArrayList<?>) {
            if (((ArrayList<?>) questionsLists).get(0) instanceof SurveyQuestion) {
                StoreWorkerFillSurveyFXController.surveyQuestions.clear();
                StoreWorkerFillSurveyFXController.surveyQuestions.addAll((ArrayList<SurveyQuestion>) questionsLists);
                StoreWorkerFillSurveyFXController.questionLabelTextProperty.set(StoreWorkerFillSurveyFXController.surveyQuestions.get(0).getQuestion());
            }
        }
    }

    /**
     * This method creates a message for updating the answers of an account for a survey by a store worker
     * @param surveyQuestions The questions whose answers are filled
     */
    public static void updateAnswers(List<SurveyQuestion> surveyQuestions) {
        Message msg = new Message(Task.UPDATE_ANSWERS, Answer.WAIT_RESPONSE,surveyQuestions);
        ZerliClientController.accept(msg);
    }

    /**
     * Use this method to show a message to StoreWorkerFillSurveyFXController screen
     * @param answer The answer received from the server
     */
    public static void showUpdateSuccess(Answer answer) {
        if(answer.equals(Answer.SUCCEED)){
            StoreWorkerFillSurveyFXController.successTextProperty.set("success");
        }
    }

    /**
     *This method creates a message to get all Surveys for a customer service worker from the DB
     */
    public static void customerServiceGetAllSurveys() {
        Message msg = new Message(Task.GET_ALL_SURVEYS_CUSTOMER_SERVICE, Answer.WAIT_RESPONSE);
        ZerliClientController.accept(msg);
    }

    /**
     * Show all Surveys in the view all surveys screen
     * @param surveysList ArrayList of Surveys
     */
    public static void customerServiceShowAllSurveys(Object surveysList) {
        if (surveysList instanceof ArrayList<?>) {
            if (((ArrayList<?>) surveysList).get(0) instanceof Survey) {
                ServiceExpertViewAllSurveysFXController.surveys.clear();
                ServiceExpertViewAllSurveysFXController.surveys.addAll((ArrayList<Survey>) surveysList);
            }
        }
    }

    /**
     * Get all the questions in the survey from the DB for a customer service worker
     * @param id The ID of the Survey
     */
    public static void customerServiceGetSurveysQuestions(int id) {
        Message msg = new Message(Task.GET_SURVEY_QUESTIONS_CUSTOER_SERVICE, Answer.WAIT_RESPONSE,id);
        ZerliClientController.accept(msg);
    }

    /**
     * Show all the questions in the survey from the DB for a customer service worker
     * @param questionsLists
     */
    public static void customerServiceShowSurveyQuestions(Object questionsLists) {
        if (questionsLists instanceof ArrayList<?>) {
            if (((ArrayList<?>) questionsLists).get(0) instanceof SurveyQuestion) {
                ServiceExpertSurveyConclusionsScreenFXController.surveyQuestion.clear();
                ServiceExpertSurveyConclusionsScreenFXController.surveyQuestion.addAll((ArrayList<SurveyQuestion>) questionsLists);
            }
        }
    }

    /**
     * This method creates a new message for updating a survey conclusion in the DB
     * @param survey The Survey to update the conclusion for it
     */
    public static void updateConclusion(Survey survey) {
        Message msg = new Message(Task.UPDATE_CONCLUSION, Answer.WAIT_RESPONSE,survey);
        ZerliClientController.accept(msg);
    }
}
