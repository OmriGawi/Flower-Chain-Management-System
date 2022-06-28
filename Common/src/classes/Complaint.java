package classes;

import enums.ComplaintStatusEnum;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * A class that represents a Complaint of a user, saves all the data from the DB relevant to a complaint in this
 * class and its data is used in various controllers
 */
public class Complaint implements Serializable {
    private int complaintId = -1;
    private String content;
    private int customerServiceWorkerId;
    private int accountId;
    private Date date;
    private ComplaintStatusEnum complaintStatus;
    private boolean notificationStatus;
    private int storeId;


    private double refund = 0;

    /**
     * constructor
     * @param complaintId complaint id
     * @param content content
     * @param customerServiceWorkerId customer service worker id
     * @param accountId account id
     * @param date date
     * @param complaintStatus complaint status
     * @param notificationStatus notification status
     */
    public Complaint(int complaintId, String content, int customerServiceWorkerId, int accountId, Date date, ComplaintStatusEnum complaintStatus, boolean notificationStatus) {
        this.complaintId = complaintId;
        this.content = content;
        this.customerServiceWorkerId = customerServiceWorkerId;
        this.accountId = accountId;
        this.date = date;
        this.complaintStatus = complaintStatus;
        this.notificationStatus = notificationStatus;
    }

    /**
     * Constructor
     * @param content content
     * @param customerServiceWorkerId customer service worker id
     * @param storeId store id
     * @param accountId account id
     * @param date date
     * @param complaintStatus
     */
    public Complaint(String content, int customerServiceWorkerId, int storeId, int accountId, Date date, ComplaintStatusEnum complaintStatus) {
        this.content = content;
        this.customerServiceWorkerId = customerServiceWorkerId;
        this.storeId = storeId;
        this.accountId = accountId;
        this.date = date;
        this.complaintStatus = complaintStatus;
        this.notificationStatus = false;
    }

    public int getStoreId(){
        return storeId;
    }

    public void setStoreId(int storeId){
        this.storeId = storeId;
    }

    public int getId() {
        return complaintId;
    }

    public void setId(int complaintId) {
        this.complaintId = complaintId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCustomerServiceWorkerId() {
        return customerServiceWorkerId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ComplaintStatusEnum getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(ComplaintStatusEnum complaintStatus) {
        this.complaintStatus = complaintStatus;
    }

    public boolean isNotificationWasSent() {
        return notificationStatus;
    }

    /**
     * Creates a new list of Complaint with a given ResultSet
     * @param rs ResultSet
     * @return List of Complaint
     */
    public static List<Complaint> createComplaintsListFromResultSet(ResultSet rs) {
        List<Complaint> complaints = new ArrayList<>();
        try{
            while(rs.next()) {
                Complaint complaint = new Complaint(
                    rs.getInt("complaint_id"),
                    rs.getString("content"),
                    rs.getInt("customer_service_worker_id"),
                    rs.getInt("account_id"),
                    rs.getTimestamp("date"),
                    ComplaintStatusEnum.valueOf(rs.getString("status")),
                    rs.getBoolean("notification_was_sent")
                );
                complaints.add(complaint);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complaints;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "complaintId=" + complaintId +
                ", content='" + content + '\'' +
                ", clientNumber=" + accountId +
                ", date=" + date +
                ", complaintStatus=" + complaintStatus +
                '}';
    }

    public double getRefund() {
        return refund;
    }

    public void setRefund(double refund) {
        this.refund = refund;
    }
}