package classes;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * A class that represents a Survey Question, saves all the data from the DB relevant to a Survey Question in this class
 * And its data is used in various controllers
 */
public class SurveyQuestion implements Serializable {
    private int id;
    private int survey_id;
    private String question;
    private int amuontOfParticipants;
    private double avgRating;

    public SurveyQuestion() {
    }

    /**
     * @param id id
     * @param survey_id survey id
     * @param question question
     * @param amuntOfParticipants amount of participants
     * @param avgRating rating avg
     */
    public SurveyQuestion(int id, int survey_id, String question, int amuntOfParticipants, double avgRating) {
        this.id = id;
        this.survey_id = survey_id;
        this.question = question;
        this.amuontOfParticipants = amuntOfParticipants;
        this.avgRating = avgRating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSurvey_id() {
        return survey_id;
    }

    public void setSurvey_id(int survey_id) {
        this.survey_id = survey_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getAmuntOfParticipants() {
        return amuontOfParticipants;
    }

    public void setAmuntOfParticipants(int amuntOfParticipants) {
        this.amuontOfParticipants = amuntOfParticipants;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    /**
     * Creates a new list of SurveyQuestions with a given ResultSet
     * @param rs ResultSet
     * @return List of SurveyQuestion
     */
    public static List<SurveyQuestion> createSurveyQuestionFromResultSet(ResultSet rs) {
        List<SurveyQuestion> questions = new ArrayList<>();
        try{
            while(rs.next()) {
                SurveyQuestion question = new SurveyQuestion(
                        rs.getInt("question_id"),
                        rs.getInt("survey_id"),
                        rs.getString("question"),
                        rs.getInt("amount_participants"),
                        rs.getDouble("rating_avg")
                        );
                questions.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }


    @Override
    public String toString() {
        return "SurveyQuestion{" +
                "id=" + id +
                ", survey_id=" + survey_id +
                ", question='" + question + '\'' +
                ", amuntOfParticipants=" + amuontOfParticipants +
                ", avgRating=" + avgRating +
                '}';
    }
}