package enums;
/**
 * Enum that defines the Order approval status in the Messages sent to and from the server and client and in the DB
 */
public enum OrderEnum {
    PENDING_APPROVAL, CONFIRMED, PENDING_CANCEL, CANCELED, REFUNDED, ARRIVED
}
