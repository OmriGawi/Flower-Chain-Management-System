package classes;

import java.io.Serializable;
/**
 * A class that represents a Credit Card of a user, saves all the data from the DB relevant to an credit card in this
 * class and its data is used in various controllers
 */
public class CreditCard implements Serializable {
    private String cardNumber;
    private String cvv;
    private String expirationDate;

    /**
     * Constructor
     * @param cardNumber card number
     * @param cvv cvv
     * @param expirationDate expiration date
     */
    public CreditCard(String cardNumber, String cvv, String expirationDate) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expirationDate = expirationDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
