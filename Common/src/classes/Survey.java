package classes;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * A class that represents a Survey, saves all the data from the DB relevant to a Survey in this class
 * And its data is used in various controllers
 */
public class Survey implements Serializable {
    private int id;
    private String description;
    private String conclusion;
    private List<SurveyQuestion> questions;

    public Survey() {
    }

    /**
     * Constructor
     * @param id survey id
     * @param description description
     * @param conclusion conclusion
     */
    public Survey(int id, String description, String conclusion) {
        this.id = id;
        this.description = description;
        this.conclusion = conclusion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public List<SurveyQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestion> questions) {
        this.questions = questions;
    }

    /**
     * Creates a new list of Survey with a given ResultSet
     * @param rs ResultSet
     * @return List of Survey
     */
    public static List<Survey> createSurveyListFromResultSet(ResultSet rs) {
        List<Survey> surveys = new ArrayList<>();
        try{
            while(rs.next()) {
                Survey survey = new Survey(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getString("conclusions")
                        );
                surveys.add(survey);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return surveys;
    }


    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", conclusion='" + conclusion + '\'' +
                '}';
    }
}