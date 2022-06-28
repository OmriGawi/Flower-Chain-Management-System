package server;

import communication.Message;
import communication.Task;
import connected_clients.ClientConnectionController;
import database.*;
import ocsf.server.ConnectionToClient;

/**
 * This class used to analyze Messages from Client
 * and then proceed the Message to the appropriate Database Controller.
 */
public class AnalyzeMessageFromClient {

	/**
	 * Analyze the message from the client sent to the server
	 * @param msg The message (Task,Answer,Data) sent to the server
	 * @param client The Client that sent that message
	 * @throws Exception
	 */
	public static void analyze(Message msg, ConnectionToClient client) throws Exception {
		if (msg == null)
			throw new NullPointerException("Message cannot be null");

		Task task = msg.getTask();
		
		if (task == null)
			throw new NullPointerException("Task cannot be null");

		switch (task) {
			case CONFIRM_IP:
				ClientConnectionController.connectClientToServer(msg, client);
				break;
			case LOGOUT:
				ClientConnectionController.disconnectClientFromServer(msg, client);
				break;
			case LOGIN_USERNAME_PASSWORD:
				LoginDBController.loginByUsernameAndPassword(msg);
				break;
			case LOGOUT_USERNAME:
				LoginDBController.logoutUsername(msg);
				break;
			case GET_CATALOG_BY_CLIENT:
				CatalogDBController.getItemsInCatalog(msg);
				break;
			case GET_CATALOG_BY_CMW:
				CatalogDBController.getItemsInCatalog_CMW(msg);
				break;
			case GET_SALES_BY_CLIENT:
			case GET_SALES_BY_CMW:
				CatalogDBController.getItemsInSales(msg);
				break;
			case GET_ALL_ITEMS:
			case GET_ALL_ITEMS_TO_REMOVE:
			case GET_ALL_ITEMS_FOR_ADDING_NEW_PRODUCT_BY_CW:
				CatalogDBController.getAllItems_CMW(msg);
				break;
			case REMOVE_ITEM_BY_CMW:
				CatalogDBController.removeItem_CMW(msg);
				break;
			case REMOVE_ITEM_FROM_SALES_CMW:
			case UPDATE_ITEM_DISCOUNT:
				CatalogDBController.updateItemDiscount(msg);
				break;
			case UPDATE_ITEM_PRICE:
				CatalogDBController.updateItemPrice(msg);
				break;
			case GET_ORDERS_BY_CLIENT:
				OrderDBController.getOrdersByClient(msg);
				break;
			case CANCEL_ORDER_BY_CLIENT:
				OrderDBController.cancelOrderByClient(msg);
				break;
			case CREATE_NEW_CUSTOM_ITEM:
			case CREATE_NEW_PRODUCT_ITEM:
				ItemDBController.createNewProductItem(msg);
				break;
			case CREATE_NEW_SINGLE_ITEM:
				ItemDBController.createNewSingleItem(msg);
				break;
			case GET_ITEMS_IN_ORDER_BY_CLIENT:
				OrderDBController.getOrderItems(msg);
				break;
			case NUMBER_OF_ORDERS_BY_ACCOUNT:
				AccountDBController.getNumberOfOrdersByAccount(msg);
				break;
			case GET_ALL_STORES:
			case GET_ALL_STORES_BY_CEO:
			case GET_ALL_STORES_BY_CSW:
				StoreDBController.getAllStores(msg);
				break;
			case CREATE_NEW_ORDER:
				OrderDBController.createNewOrder(msg);
				break;
			case GET_ALL_CLIENT_USERS_WITHOUT_REGISTERED_ACCOUNT_BY_SM:
				AccountDBController.getAllClientUsersWithoutRegisteredAccount(msg);
				break;
			case CREATE_NEW_ACCOUNT_BY_SM:
				AccountDBController.createNewAccount(msg);
				break;
			case GET_ALL_CLIENT_USERS_WITH_REGISTERED_ACCOUNT_BY_SM:
				AccountDBController.getAllClientUsersWithRegisteredAccount(msg);
				break;
			case EDIT_ACCOUNT_STATUS_BY_SM:
				AccountDBController.editAccountStatus(msg);
				break;
			case SET_ACCOUNT_BALANCE:
				AccountDBController.setAccountBalance(msg);
				break;
			case GET_ORDERS_BY_MANAGER:
				OrderDBController.getOrdersPendingApprovalByManager(msg);
				break;
			case APPROVE_ORDER_BY_MANAGER:
				OrderDBController.approveOrderByManager(msg);
				break;
			case GET_CANCELLATIONS_BY_MANAGER:
				OrderDBController.getOrdersPendingCancellationByManager(msg);
				break;
			case CANCEL_ORDER_BY_MANAGER:
				OrderDBController.cancelOrderByManager(msg);
				break;
			case REFUND_ORDER_BY_MANAGER:
				AccountDBController.refundAccount(msg);
				break;
			case ADD_COMPLAINT:
				ComplaintDBController.addComplaint(msg);
				break;
			case GET_ALL_COMPLAINTS:
				ComplaintDBController.getAllComplaints(msg);
				break;
			case CLOSE_COMPLAINT:
				ComplaintDBController.closeComplaint(msg);
				break;
			case  GET_STORE_BY_SM:
				AccountDBController.getStoreBySM(msg);
				break;
			case GET_REPORT_BY_SM:
			case GET_ALL_STORES_REPORT_BY_CEO:
			case GET_ONE_REPORT_BY_CEO:
				ReportDBController.getSelectedReport(msg);
				break;
			case GET_TWO_REPORTS_BY_CEO:
				ReportDBController.getTwoSelectedReport(msg);
				break;
			case GET_ALL_SURVEYS:
			case GET_ALL_SURVEYS_CUSTOMER_SERVICE:
				SurveyDBController.getAllSurveys(msg);
				break;
			case GET_SURVEY_QUESTIONS:
			case GET_SURVEY_QUESTIONS_CUSTOER_SERVICE:
				SurveyDBController.getSurveyQuestion(msg);
				break;
			case UPDATE_ANSWERS:
				SurveyDBController.updateAnswers(msg);
				break;
			case UPDATE_CONCLUSION:
				SurveyDBController.updateConclusion(msg);
				break;
			case GET_WORKERS_IN_STORE:
				StoreDBController.getWorkersInStore(msg);
				break;
			case UPDATE_WORKER_PERMISSION:
				StoreDBController.updateWorkerPermission(msg);
				break;
			case GET_WORKER_PERMISSION:
				StoreDBController.getWorkerPermission(msg);
				break;
			case GET_ALL_ORDERS_BY_DM:
				OrderDBController.getAllOrdersWaitForDeliveryByDM(msg);
				break;
			case UPDATE_ORDER_TO_ARRIVED_BY_DM:
				OrderDBController.updateOrderToArrived(msg);
				break;
			default:
				throw new Exception("Task does not exist");
		}
	}

}
