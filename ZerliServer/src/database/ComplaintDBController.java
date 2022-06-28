package database;

import classes.Complaint;
import communication.Answer;
import communication.Message;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
/**
 * This class defines the methods that send query's to the DB and receive the data back from the DB
 * and return it to the relevant controllers and classes that need to use that information
 */
public class ComplaintDBController {
    /**
     * Add a new complaint to the DB
     * @param msg Complaint object
     * @throws SQLException
     */
    public static void addComplaint(Message msg) throws SQLException {
        Complaint complaint = (Complaint)msg.getObject();
        if (!isAccountNumberValid(complaint.getAccountId())){
            msg.setAnswer(Answer.NO_ROWS_AFFECTED);
            return;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String query = "INSERT INTO zerli.complaints "+
                "(`customer_service_worker_id`, `account_id`, `content`, `status`, `date`, `store_id`)"+
                "VALUES ("+complaint.getCustomerServiceWorkerId()+", '"+complaint.getAccountId()+"','"+complaint.getContent()+"','"+complaint.getComplaintStatus()+"','"+dateFormat.format(complaint.getDate())+"',"+complaint.getStoreId()+");";
        int affectedRows = DatabaseController.getInstance().executeUpdate(query);
        if (affectedRows == -1)
            msg.setAnswer(Answer.NO_ROWS_AFFECTED);
        else {
            msg.setAnswer(Answer.SUCCEED);
            msg.setObject(affectedRows);
        }
    }

    /**
     * Check if the account number (ID) is valid
     * @param accountNumber
     * @return True if valid, False otherwise.
     * @throws SQLException
     */
    public static boolean isAccountNumberValid(int accountNumber) throws SQLException {
        String queryClientNumberValid;
        queryClientNumberValid = "SELECT id " +
                "FROM zerli.account " +
                "WHERE id = " + accountNumber;
        ResultSet rs = DatabaseController.getInstance().executeQuery(queryClientNumberValid);
        return rs != null && rs.first();
    }

    /**
     * Get all complaints for a customer service worker
     * @param message
     */
    public static void getAllComplaints(Message message) {
        int customerServiceWorkerId = (int) message.getObject();
        String query = "SELECT * FROM zerli.complaints WHERE customer_service_worker_id=" + customerServiceWorkerId;
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            message.setAnswer(Answer.FAILED);
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(Complaint.createComplaintsListFromResultSet(rs));
        }
    }

    /**
     * Get all the complaints that no notifications to the user has been sent to yet
     * @return List of all the Complaints
     */
    public static List<Complaint> getComplaintsThatNeedsToBeNotified() {
        String query = "SELECT * FROM zerli.complaints " +
                "WHERE status <> 'CLOSED' " +
                "AND notification_was_sent = false " +
                "AND TIMESTAMPDIFF(HOUR, complaints.date, NOW()) >= 24;";
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            return null;
        return Complaint.createComplaintsListFromResultSet(rs);
    }

    /**
     * Set that the a notification was sent to the user in the DB
     * @param complaintId
     * @return The number of parameters that has been changed
     */
    public static int setNotificationWasSent(int complaintId) {
        String query = "UPDATE `zerli`.`complaints` " +
                "SET `notification_was_sent` = 1 " +
                "WHERE `complaint_id` = " + complaintId;
        return DatabaseController.getInstance().executeUpdate(query);
    }


    /**
     * Close complaint and set its status to 'CLOSED' in the DB
     * @param msg Complaint object
     */
    public static void closeComplaint(Message msg) {
        Complaint complaint = (Complaint)msg.getObject();
        String query = "UPDATE zerli.account\n" +
                "SET balance = balance + "+complaint.getRefund()+"\n" +
                "WHERE id = "+complaint.getAccountId()+";";
        int affectedRows = DatabaseController.getInstance().executeUpdate(query);
        if (affectedRows == -1)
            msg.setAnswer(Answer.NO_ROWS_AFFECTED);
        else {
            query = "UPDATE zerli.complaints\n" +
                    "SET status = 'CLOSED'\n" +
                    "WHERE complaint_id = "+complaint.getId()+";";
            affectedRows = DatabaseController.getInstance().executeUpdate(query);
            if (affectedRows == -1)
                msg.setAnswer(Answer.NO_ROWS_AFFECTED);
            else {
                msg.setAnswer(Answer.SUCCEED);
                msg.setObject(affectedRows);
            }
        }

    }
}
