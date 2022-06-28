package client;

import classes.Store;
import communication.Answer;
import communication.Message;
import controllers.*;
import gui.company_marketing_worker.CompanyMarketingWorkerAddItemScreenFXController;
import gui.company_marketing_worker.CompanyMarketingWorkerEditCatalogScreenFXController;
import gui.company_marketing_worker.CompanyMarketingWorkerEnableSalesPromotionScreenFXController;
import javafx.application.Platform;

import javax.swing.*;
/**
 * The Class 'AnalyzeMessageFromServer' is responsible for analyzing and forwarding messages received from the server
 * and their content back to client and relevant controllers.
 */
public class AnalyzeMessageFromServer {

	/**
	 * The analyze method is responsible for decrypting a Message from the server and send it back to the relevant
	 * controllers
	 * @param message - The message containing the Task, Answer and data received from the server
	 * @return
	 */
	public static boolean analyze(Object message) {
		if (message instanceof Message) {
			Message msg = (Message) message;
			if (msg.getAnswer() == Answer.FAILED)
				return false;
			switch (msg.getTask()) {
				case CONFIRM_IP:
					ZerliClientUI.confirmIpMessage = msg;
					if (msg.getAnswer() == Answer.ALREADY_CONNECTED) {
						Platform.exit();
						JOptionPane.showMessageDialog(null, "You cannot login from the same computer twice!", "Multiple logins", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
					break;
				case LOGOUT:
					ZerliClientController.client.closeClientConnection();
					break;
				case LOGIN_USERNAME_PASSWORD:
					LoginController.updateLoginStatus(msg);
					break;
				case GET_CATALOG_BY_CLIENT:
					CatalogController.showCatalog(msg.getObject());
					break;
				case GET_CATALOG_BY_CMW:
					CatalogController.showItemsInCatalog_CMW(msg.getObject());
					break;
				case GET_SALES_BY_CMW:
					CatalogController.showItemsInSales_CMW(msg.getObject());
					break;
				case GET_SALES_BY_CLIENT:
					CatalogController.showSales(msg.getObject());
					break;
				case GET_ALL_ITEMS:
					CatalogController.showAllItems_CMW(msg.getObject());
					break;
				case GET_ALL_ITEMS_TO_REMOVE:
					CatalogController.showAllItemsToRemove_CMW(msg.getObject());
					break;
				case REMOVE_ITEM_FROM_SALES_CMW:
					CatalogController.showRemovedItemFromSalesInTable((Integer) msg.getObject());
					break;
				case REMOVE_ITEM_BY_CMW:
					CatalogController.showRemoveItem_CMW(msg);
					break;
				case UPDATE_ITEM_PRICE:
					CompanyMarketingWorkerEditCatalogScreenFXController.catalogController.showUpdateItemPrice_CMW((Integer) msg.getObject());
					break;
				case UPDATE_ITEM_DISCOUNT:
					CompanyMarketingWorkerEnableSalesPromotionScreenFXController.catalogController.showUpdateItemDiscount_CMW((Integer) msg.getObject());
					break;
				case GET_ORDERS_BY_CLIENT:
					OrderController.showClientOrders(msg.getObject());
					break;
				case CANCEL_ORDER_BY_CLIENT:
					OrderController.showCancelOrderMessage(msg);
					break;
				case GET_ITEMS_IN_ORDER_BY_CLIENT:
					OrderController.showOrderItems(msg.getObject());
					break;
				case NUMBER_OF_ORDERS_BY_ACCOUNT:
					AccountController.showNumberOfOrders(msg.getObject());
					break;
				case GET_ALL_STORES:
					OrderController.showAllStores(msg.getObject());
					break;
				case CREATE_NEW_ORDER:
					OrderController.showNewOrderWasAdded(msg.getAnswer());
					break;
				case CREATE_NEW_SINGLE_ITEM:
					CompanyMarketingWorkerAddItemScreenFXController.itemController.showCreateNewSingleItemMessage((Integer) msg.getObject());
					break;
				case GET_ALL_ITEMS_FOR_ADDING_NEW_PRODUCT_BY_CW:
					CatalogController.showAllItemsForAddingNewProductItem(msg.getObject());
					break;
				case CREATE_NEW_PRODUCT_ITEM:
					ItemController.showNewProductItem(msg.getAnswer());
					break;
				case GET_ALL_CLIENT_USERS_WITHOUT_REGISTERED_ACCOUNT_BY_SM:
					AccountController.showAllClientUsersWithoutRegisteredAccount(msg.getObject());
					break;
				case CREATE_NEW_ACCOUNT_BY_SM:
					AccountController.showCreateNewAccount(msg);
					break;
				case GET_ALL_CLIENT_USERS_WITH_REGISTERED_ACCOUNT_BY_SM:
					AccountController.showAllClientUsersWithRegisteredAccount(msg.getObject());
					break;
				case EDIT_ACCOUNT_STATUS_BY_SM:
					AccountController.updateAccount(msg);
					AccountController.showEditAccountStatus(msg);
					break;
				case GET_ORDERS_BY_MANAGER:
					OrderController.showPendingApprovalOrders(msg.getObject());
					break;
				case APPROVE_ORDER_BY_MANAGER:
					OrderController.showNewOrderApprovedMessage(msg.getAnswer());
					break;
				case GET_CANCELLATIONS_BY_MANAGER:
					OrderController.showPendingCancellationOrders(msg.getObject());
					break;
				case CANCEL_ORDER_BY_MANAGER:
					OrderController.showNewOrderCanceledMessage(msg.getAnswer());
					break;
				case REFUND_ORDER_BY_MANAGER:
					AccountController.showAccountRefundedMessage(msg);
					break;
				case ADD_COMPLAINT:
					ComplaintController.showAddComplaintResult(msg);
					break;
				case GET_ALL_COMPLAINTS:
					ComplaintController.showAllComplaints(msg.getObject());
					break;
				case CLOSE_COMPLAINT:
					ComplaintController.afterCloseComplaint(msg);
					break;
				case GET_STORE_BY_SM:
					AccountController.showStoreBySM((Store) msg.getObject());
					break;
				case GET_REPORT_BY_SM:
					ReportController.showSelectedReportBySM(msg);
					break;
				case GET_ALL_STORES_REPORT_BY_CEO:
				case GET_ONE_REPORT_BY_CEO:
				case GET_TWO_REPORTS_BY_CEO:
					ReportController.showSelectedReportByCEO(msg);
					break;
				case GET_ALL_SURVEYS:
					SurveyController.showAllSurveys(msg.getObject());
					break;
				case GET_SURVEY_QUESTIONS:
					SurveyController.showSurveyQuestions(msg.getObject());
					break;
				case UPDATE_ANSWERS:
					SurveyController.showUpdateSuccess(msg.getAnswer());
					break;
				case GET_ALL_SURVEYS_CUSTOMER_SERVICE:
					SurveyController.customerServiceShowAllSurveys(msg.getObject());
					break;
				case GET_SURVEY_QUESTIONS_CUSTOER_SERVICE:
					SurveyController.customerServiceShowSurveyQuestions(msg.getObject());
					break;
				case GET_WORKERS_IN_STORE:
					StoreController.showWorkersInStore(msg.getObject());
					break;
				case GET_WORKER_PERMISSION:
					StoreController.showWorkerPermission(msg.getObject());
					break;
				case GET_ALL_STORES_BY_CEO:
					ReportController.showAllStoresByCEO(msg.getObject());
					break;
				case GET_ALL_STORES_BY_CSW:
					StoreController.showAllStoresByCSW(msg.getObject());
					break;
				case GET_ALL_ORDERS_BY_DM:
					OrderController.showAllOrdersByDM(msg.getObject());
					break;
				case UPDATE_ORDER_TO_ARRIVED_BY_DM:
					OrderController.showUpdateStatusToArrived(msg);
					break;
			}
		}
		return true;
	}
}
