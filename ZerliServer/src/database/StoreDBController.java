package database;

import classes.Store;
import classes.Worker;
import communication.Answer;
import communication.Message;
import communication.Task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
/**
 * This class defines the methods that send query's to the DB and receive the data back from the DB
 * and return it to the relevant controllers and classes that need to use that information
 */
public class StoreDBController {
    /**
     * Get all stores from the DB using the getAllStores overloaded method below
     * @return List of all Stores
     */
    public static List<Store> getAllStores() {
        Message msg = new Message(Task.GET_ALL_STORES);
        getAllStores(msg);
        return (List<Store>) msg.getObject();
    }

    /**
     * Get all stores from the DB
     * @param message
     */
    public static void getAllStores(Message message) {
        String query = String.format("SELECT * FROM zerli.stores;");
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            message.setAnswer(Answer.FAILED);
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(Store.createStoreListFromResultSet(rs));
        }
    }

    /**
     * Get all workers associated to the store from the dB and return a list of Workers
     * @param msg
     */
    public static void getWorkersInStore(Message msg) {
        int managerId = (int) msg.getObject();
        String query = "SELECT worker_id, store_id, first_name, last_name, is_allowed_to_fill_surveys FROM zerli.workers_in_stores " +
                "JOIN zerli.users ON id=worker_id " +
                "JOIN zerli.stores ON zerli.stores.id = zerli.workers_in_stores.store_id " +
                "WHERE zerli.stores.manager_id = " + managerId;
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            msg.setAnswer(Answer.FAILED);
        else {
            msg.setAnswer(Answer.SUCCEED);
            msg.setObject(Worker.createWorkerListFromResultSet(rs));
        }
    }

    /**
     * Update a worker's permission to fill surveys
     * @param msg
     */
    public static void updateWorkerPermission(Message msg) {
        HashMap<String, Object> args = (HashMap<String, Object>) msg.getObject();
        int workerId = (int) args.get("workerId");
        int storeId = (int) args.get("storeId");
        boolean isAllowedToFillSurveys = (boolean) args.get("isAllowedToFillSurveys");
        String query = "UPDATE `zerli`.`workers_in_stores` SET `is_allowed_to_fill_surveys` = " + isAllowedToFillSurveys +
                " WHERE `worker_id` = " + workerId +
                " AND `store_id` = " + storeId;
        int affectedRows = DatabaseController.getInstance().executeUpdate(query);
        if(affectedRows == -1)
            msg.setAnswer(Answer.NO_ROWS_AFFECTED);
        else{
            msg.setAnswer(Answer.SUCCEED);
        }
    }

    /**
     * Get a worker's permission from the DB
     * @param msg
     */
    public static void getWorkerPermission(Message msg) {
        int workerId = (int) msg.getObject();
        String query = "SELECT is_allowed_to_fill_surveys " +
                "FROM zerli.workers_in_stores " +
                "WHERE worker_id = " + workerId;
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            msg.setAnswer(Answer.FAILED);
        else {
            try {
                if (rs.next()) {
                    msg.setObject(rs.getBoolean("is_allowed_to_fill_surveys"));
                } else {
                    msg.setAnswer(Answer.FAILED);
                    return;
                }
            } catch (SQLException e) {
                msg.setAnswer(Answer.FAILED);
                throw new RuntimeException(e);
            }
            msg.setAnswer(Answer.SUCCEED);
        }
    }


}
