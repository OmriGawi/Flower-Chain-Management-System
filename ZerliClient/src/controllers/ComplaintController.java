package controllers;

import classes.Complaint;
import client.ZerliClientController;
import communication.Answer;
import communication.Message;
import communication.Task;
import gui.customer_service_worker.CustomerServiceWorkerAddComplaintScreenFXController;
import gui.customer_service_worker.CustomerServiceWorkerTreatComplaintScreenFXController;
import gui.customer_service_worker.CustomerServiceWorkerViewAllComplaintsScreenFXController;
import utils.Utils;

import java.util.ArrayList;

/**
 * This class handles the complaint methods to be sent and received from the server for the relevant complaints FX
 * controllers
 * */
public class ComplaintController {
    /**
     * This method creates a message for adding a new complaint
     * @param complaint Complaint to be added
     */
    public static void addComplaint(Complaint complaint) {
        Message msg = new Message(Task.ADD_COMPLAINT, Answer.WAIT_RESPONSE, complaint);
        ZerliClientController.accept(msg);
    }

    /**
     * This method shows the relevant text in the GUI
     * @param msg Message containing the answer from the server
     */
    public static void showAddComplaintResult(Message msg) {
        if (msg.getAnswer() == Answer.NO_ROWS_AFFECTED)
            CustomerServiceWorkerAddComplaintScreenFXController.successTextProperty.set("fail");
        if (msg.getAnswer() == Answer.SUCCEED)
            CustomerServiceWorkerAddComplaintScreenFXController.successTextProperty.set("Complaint was added successfully !");
    }

    /**
     * This method creates a new message for getting all the complaints
     */
    public static void getAllComplaints() {
        Message msg = new Message(Task.GET_ALL_COMPLAINTS, Answer.WAIT_RESPONSE, Utils.currUser.getId());
        ZerliClientController.accept(msg);
    }

    /**
     * This method sets all the Complaints in the FXController to be shown to the user in the GUI
     * @param complaintsList ArrayList of Complaints
     */
    public static void showAllComplaints(Object complaintsList) {
        if (complaintsList instanceof ArrayList<?>) {
            if(((ArrayList<?>) complaintsList).isEmpty())
                CustomerServiceWorkerViewAllComplaintsScreenFXController.complaints.clear();
            else{
                if (((ArrayList<?>) complaintsList).get(0) instanceof Complaint) {
                    CustomerServiceWorkerViewAllComplaintsScreenFXController.complaints.clear();
                    CustomerServiceWorkerViewAllComplaintsScreenFXController.complaints.addAll((ArrayList<Complaint>) complaintsList);
                }
            }
        }
    }

    /**
     * This method creates a new message for closing a complaint
     * @param complaint Complaint to be closed
     * @param refund The refund to be given to the Account
     */
    public static void closeComplaint(Complaint complaint, Double refund){
        complaint.setRefund(refund);
        Message msg = new Message(Task.CLOSE_COMPLAINT, Answer.WAIT_RESPONSE, complaint);
        ZerliClientController.accept(msg);
    }

    /**
     * This method shows the relevant text in the GUI
     * @param msg Message containing the answer from the server
     */
    public static void afterCloseComplaint(Message msg) {
        if (msg.getAnswer() == Answer.SUCCEED)
            CustomerServiceWorkerTreatComplaintScreenFXController.successTextProperty.set("success");
        else
            CustomerServiceWorkerTreatComplaintScreenFXController.successTextProperty.set("fail");
    }
}
