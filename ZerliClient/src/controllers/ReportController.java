package controllers;

import classes.Order;
import classes.Store;
import client.ZerliClientController;
import communication.Answer;
import communication.Message;
import communication.Task;
import gui.ceo.CeoViewAllStoresReportScreenFXController;
import gui.ceo.CeoViewSingleStoreReportScreenFXController;
import gui.store_manager.StoreManagerViewReportsScreenFXController;
import reports.ComplaintReport;
import reports.IncomeReport;
import reports.OrderReport;
import reports.Report;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * This class creates Reports Messages and
 * show the results on  the relevant GUI screens.
 */
public class ReportController {

    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Store Manager @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */

    /**
     * Creates a new task for selecting a report from database
     * @param report  report
     * @param <T> The object used to save the report data
     */
    public static <T extends Serializable> void getSelectedReportBySM(Report<T> report){
        Message msg = new Message(Task.GET_REPORT_BY_SM, Answer.WAIT_RESPONSE, report);
        ZerliClientController.accept(msg);
    }

    /**
     * Will show the selected report in StoreManager GUI <p>
     * If selected report not exist, will show an appropriate message.
     * @param message contains object with the report
     * @param <T> The object used to save the report data
     */
    public static <T extends Serializable> void showSelectedReportBySM(Message message){
        Answer answer = message.getAnswer();
        Report<T> report = (Report<T>) message.getObject();
        if(answer == Answer.SELECTED_REPORT_NOT_EXIST)
            StoreManagerViewReportsScreenFXController.errorTextProperty.set("Selected report does not exist!");
        else{
            StoreManagerViewReportsScreenFXController.report.set(report);
        }
    }


    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CEO @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */

    /**
     * Creates a new task for selecting a report from database by CEO
     * @param report  report
     * @param <T> The object used to save the report data
     */
    public static <T extends Serializable> void getOneSelectedReportByCEO(Report<T> report){
        Message msg = new Message(Task.GET_ONE_REPORT_BY_CEO, Answer.WAIT_RESPONSE, report);
        ZerliClientController.accept(msg);
    }
    /**
     *
     * Creates a new task for selecting two reports from database by CEO
     * @param report1  report
     * @param report2  report
     * @param <T> The object used to save the report data
     */
    public static <T extends Serializable> void getTwoSelectedReportByCEO(Report<T> report1, Report<T> report2){
        Report[] list = new Report[]{report1, report2};
        Message msg = new Message(Task.GET_TWO_REPORTS_BY_CEO, Answer.WAIT_RESPONSE, list);
        ZerliClientController.accept(msg);
    }

    /**
     * Creates a new task for selecting a report from database
     * @param report  report
     * @param <T> The object used to save the report data
     */
    public static <T extends Serializable> void getAllStoresSelectedReportByCEO(Report<T> report){
        Message msg = new Message(Task.GET_ALL_STORES_REPORT_BY_CEO, Answer.WAIT_RESPONSE, report);
        ZerliClientController.accept(msg);
    }

    /**
     * Will show the selected report in CEO GUI <p>
     * If selected report not exist, will show an appropriate message.
     * @param message contains object with the report
     * @param <T> The object used to save the report data
     */
    public static <T extends Serializable> void showSelectedReportByCEO(Message message){
        Task task = message.getTask();
        Answer answer = message.getAnswer();
        Report[] reports = new Report[0];
        Report<T> report = null;
        if(message.getObject() instanceof Report[]){
            reports = (Report[]) message.getObject();
        }else{
            report = (Report<T>) message.getObject();
        }

        switch (task){
            case GET_ALL_STORES_REPORT_BY_CEO:
                if(answer == Answer.SELECTED_REPORT_NOT_EXIST)
                    CeoViewAllStoresReportScreenFXController.errorTextProperty.set("Selected report does not exist!");
                else CeoViewAllStoresReportScreenFXController.report.set(report);
                break;
            case GET_ONE_REPORT_BY_CEO:
                if(answer == Answer.SELECTED_REPORT_NOT_EXIST)
                    CeoViewSingleStoreReportScreenFXController.errorTextProperty.set("Selected report does not exist!");
                else CeoViewSingleStoreReportScreenFXController.showOneReport.set(report);
                break;
            case GET_TWO_REPORTS_BY_CEO:
                if(answer == Answer.SELECTED_REPORT_NOT_EXIST)
                    CeoViewSingleStoreReportScreenFXController.errorTextProperty.set("Selected report does not exist!");
                else{
                    CeoViewSingleStoreReportScreenFXController.showTwoReports1.set(reports[0]);
                    CeoViewSingleStoreReportScreenFXController.showTwoReports2.set(reports[1]);
                }
        }
    }

    /**
     * Create a new message for selecting all Stores from DB
     */
    public static void getAllStoresByCEO(){
        Message msg = new Message(Task.GET_ALL_STORES_BY_CEO);
        ZerliClientController.accept(msg);
    }

    /**
     * Put received stores into stores list
     * @param stores ArrayList of Stores
     */
    public static void showAllStoresByCEO(Object stores){
        if (stores instanceof ArrayList<?>) {
            if (((ArrayList<?>) stores).isEmpty())
                CeoViewSingleStoreReportScreenFXController.stores.clear();
            else {
                if (((ArrayList<?>) stores).get(0) instanceof Store) {
                    CeoViewSingleStoreReportScreenFXController.stores.clear();
                    CeoViewSingleStoreReportScreenFXController.stores.addAll((ArrayList<Store>) stores);
                }
            }
        }
    }
}
