package classes;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a Store Worker, saves all the data from the DB relevant to a worker in this class
 * And its data is used in various controllers
 */
public class Worker implements Serializable {
    private int id;
    private int storeId;
    private String firstName;
    private String lastName;
    private boolean isAllowedToFillSurveys;

    /**
     * Constructor
     * @param id
     * @param storeId
     * @param firstName
     * @param lastName
     * @param isAllowedToFillSurveys
     */
    public Worker(int id, int storeId, String firstName, String lastName, boolean isAllowedToFillSurveys) {
        this.id = id;
        this.storeId = storeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAllowedToFillSurveys = isAllowedToFillSurveys;
    }

    public int getId() {
        return id;
    }

    public int getStoreId() {
        return storeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isAllowedToFillSurveys() {
        return isAllowedToFillSurveys;
    }

    public void setIsAllowedToFillSurveys(boolean isAllowedToFillSurveys) {
        this.isAllowedToFillSurveys = isAllowedToFillSurveys;
    }

    /**
     * Creates a new list of Worker with a given ResultSet
     * @param rs ResultSet
     * @return List of Worker
     */
    public static List<Worker> createWorkerListFromResultSet(ResultSet rs) {
        List<Worker> workers = new ArrayList<>();
        try {
            while (rs.next()) {
                workers.add(new Worker(
                        rs.getInt("worker_id"),
                        rs.getInt("store_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getBoolean("is_allowed_to_fill_surveys")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workers;
    }
}
