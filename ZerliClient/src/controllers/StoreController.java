package controllers;

import classes.Store;
import classes.Worker;
import client.ZerliClientController;
import communication.Answer;
import communication.Message;
import communication.Task;
import gui.client.ClientCheckoutScreenFXController;
import gui.customer_service_worker.CustomerServiceWorkerAddComplaintScreenFXController;
import gui.store_manager.StoreManagerPermissionsScreenFXController;
import gui.store_worker.StoreWorkerViewAllSurveysFXController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * This class creates Store Messages and
 * show the results on  the relevant GUI screens.
 */
public class StoreController {
    /**
     * Get all orders by specific client username.
     * @param managerId The Manger's ID managing the store and workers
     */
    public static void getWorkersInStore(int managerId) {
        Message msg = new Message(Task.GET_WORKERS_IN_STORE, Answer.WAIT_RESPONSE, managerId);
        ZerliClientController.accept(msg);
    }

    /**
     * Will show the all workers in the store in the StoreManager GUI
     * @param workersList ArrayList of workers
     */
    public static void showWorkersInStore(Object workersList) {
        if (workersList instanceof ArrayList<?>) {
            if (((ArrayList<?>) workersList).get(0) instanceof Worker) {
                StoreManagerPermissionsScreenFXController.workersList.clear();
                StoreManagerPermissionsScreenFXController.workersList.addAll((List<Worker>)workersList);
            }
        }
    }

    /**
     * This method creates a new message for updating a worker's permission to fill surveys for the store
     * @param workerId The worker's id
     * @param storeId The store ID
     * @param isAllowedToFillSurveys Is worker allowed to fill forms boolean value
     */
    public static void updateWorkerPermission(int workerId, int storeId, boolean isAllowedToFillSurveys) {
        HashMap<String, Object> args = new HashMap<>();
        args.put("workerId", workerId);
        args.put("storeId", storeId);
        args.put("isAllowedToFillSurveys", isAllowedToFillSurveys);
        Message msg = new Message(Task.UPDATE_WORKER_PERMISSION, Answer.WAIT_RESPONSE, args);
        ZerliClientController.accept(msg);
    }

    /**\
     * Get the worker's permission from the DB
     * @param workerId The worker's id
     */
    public static void getWorkerPermission(int workerId) {
        Message msg = new Message(Task.GET_WORKER_PERMISSION, Answer.WAIT_RESPONSE, workerId);
        ZerliClientController.accept(msg);
    }

    /**
     * Show the worker's permission in the view all surveys screen GUI
     * @param isAllowedToFillSurveys The boolean indicating if the worker is allowed or not
     */
    public static void showWorkerPermission(Object isAllowedToFillSurveys) {
        StoreWorkerViewAllSurveysFXController.isAllowedToFillSurveysProperty.set((Boolean) isAllowedToFillSurveys);
    }

    /**
     * This method creates a message for getting all stores by a customer service worker
     */
    public static void getAllStoresByCSW(){
        Message msg = new Message(Task.GET_ALL_STORES_BY_CSW);
        ZerliClientController.accept(msg);
    }

    /**
     * Show all stores in the add complaint screen of the customer service workers
     * @param stores ArrayList of Stores
     */
    public static void showAllStoresByCSW(Object stores) {
        if (stores instanceof ArrayList<?>) {
            try{
                if (((ArrayList<?>) stores).get(0) instanceof Store) {
                    CustomerServiceWorkerAddComplaintScreenFXController.stores.clear();
                    CustomerServiceWorkerAddComplaintScreenFXController.stores.addAll((ArrayList<Store>) stores);
                }
            }catch (NullPointerException | IndexOutOfBoundsException ignored){
            }
        }
    }
}
