package classes;

import enums.StatusEnum;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * A class that represents an Account of a user, saves all the data from the DB relevant to an account in this
 * class and its data is used in various controllers
 */
public class Account implements Serializable {
    private int id;
    private double balance;
    private StatusEnum status;
    private CreditCard creditCard;

    /**
     * Constructor
     * @param id id
     * @param balance balance
     * @param status account status
     * @param creditCard credit card
     */
    public Account(int id, double balance, StatusEnum status, CreditCard creditCard) {
        this.id = id;
        this.balance = balance;
        this.status = status;
        this.creditCard = creditCard;
    }

    /**
     * Constructor
     * @param id id
     * @param balance balance
     * @param status account status
     */
    public Account(int id, double balance, StatusEnum status) {
        this.id = id;
        this.balance = balance;
        this.status = status;
    }

    /**
     * Constructor
     * @param balance balance
     * @param status account status
     * @param creditCard credit card
     */
    public Account (double balance, StatusEnum status, CreditCard creditCard){
        this.balance = balance;
        this.status = status;
        this.creditCard = creditCard;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    /**
     * Creates a new List of Account with a given ResultSet
     * @param rs Result set
     * @return List of Account
     */
    public static List<Account> createAccountListFromResultSet(ResultSet rs) {
        List<Account> accounts = new ArrayList<>();
        try{
            while(rs.next()) {
                CreditCard creditCard = new CreditCard(
                        rs.getString("card_number"),
                        rs.getString("cvv"),
                        rs.getString("expiration_date"));
                Account account = new Account(
                        rs.getInt("id"),
                        rs.getDouble("balance"),
                        StatusEnum.valueOf(rs.getString("status")),
                        creditCard);
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

}