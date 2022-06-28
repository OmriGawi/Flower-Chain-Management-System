package communication;

/**
 * Enum that defines the tasks to be performed in the Messages sent to and from the server and client
 */
public enum Task {
    CONNECT_TO_SERVER,
    UPDATE_ORDER,
    GET_ORDERS_BY_CLIENT,
    GET_ORDERS_BY_MANAGER,
    GET_CANCELLATIONS_BY_MANAGER,
    APPROVE_ORDER_BY_MANAGER,
    CONFIRM_IP,
    LOGOUT,
    LOGIN_USERNAME_PASSWORD,
    LOGOUT_USERNAME,
    GET_CATALOG_BY_CLIENT,
    GET_CATALOG_BY_CMW,
    REMOVE_ITEM_BY_CMW,
    GET_SALES_BY_CMW,
    REMOVE_ITEM_FROM_SALES_CMW,
    GET_SALES_BY_CLIENT,
    GET_USER_INFO,
    CANCEL_ORDER_BY_CLIENT,
    GET_ACCOUNT_INFO,
    GET_ALL_ITEMS,
    GET_ALL_ITEMS_TO_REMOVE,
    UPDATE_ITEM_PRICE,
    UPDATE_ITEM_DISCOUNT,
    CREATE_NEW_CUSTOM_ITEM,
    CREATE_NEW_PRODUCT_ITEM,
    SHOW_NEW_CUSTOM_ITEM,
    GET_ITEMS_IN_ORDER_BY_CLIENT,
    NUMBER_OF_ORDERS_BY_ACCOUNT,
    CREATE_NEW_ORDER,
    GET_ALL_STORES,
    SET_ACCOUNT_BALANCE,
    CREATE_NEW_SINGLE_ITEM,
    GET_ALL_ITEMS_FOR_ADDING_NEW_PRODUCT_BY_CW,
    GET_ALL_CLIENT_USERS_WITHOUT_REGISTERED_ACCOUNT_BY_SM,
    CREATE_NEW_ACCOUNT_BY_SM,
    GET_ALL_CLIENT_USERS_WITH_REGISTERED_ACCOUNT_BY_SM,
    EDIT_ACCOUNT_STATUS_BY_SM,
    CANCEL_ORDER_BY_MANAGER,
    REFUND_ORDER_BY_MANAGER,
    ADD_COMPLAINT,
    GET_ALL_COMPLAINTS,
    CLOSE_COMPLAINT,
    GET_STORE_BY_SM,
    GET_MONTHLY_REPORT_BY_SM,
    GET_QUARTERLY_REPORT_BY_SM,
    GET_REPORT_BY_SM,
    GET_SURVEY_QUESTIONS,
    UPDATE_ANSWERS,
    GET_ALL_SURVEYS_CUSTOMER_SERVICE,
    GET_SURVEY_QUESTIONS_CUSTOER_SERVICE,
    UPDATE_CONCLUSION,
    GET_WORKERS_IN_STORE,
    UPDATE_WORKER_PERMISSION,
    GET_WORKER_PERMISSION,
    GET_ALL_SURVEYS,
    GET_ALL_STORES_REPORT_BY_CEO,
    GET_ALL_STORES_BY_CEO,
    GET_ONE_REPORT_BY_CEO,
    GET_TWO_REPORTS_BY_CEO,
    GET_ALL_STORES_BY_CSW,
    GET_ALL_ORDERS_BY_DM,
    UPDATE_ORDER_TO_ARRIVED_BY_DM
}
