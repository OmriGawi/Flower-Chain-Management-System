package classes;

import enums.RoleEnum;
import enums.StatusEnum;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * A class that represents a User, saves all the data from the DB relevant to a user in this class
 * And its data is used in various controllers
 */
public class User implements Serializable {
    private int id;
    private String username;
    private String password;
    private boolean isLoggedIn;
    private String firstName;
    private String lastName;
    private String email;
    private String telephone;
    private RoleEnum role;
    private Account account;

    /**
     * Constructor
     * @param id
     * @param username
     * @param password
     * @param isLoggedIn
     * @param firstName
     * @param lastName
     * @param email
     * @param telephone
     * @param role
     * @param account
     */
    public User(int id, String username, String password, boolean isLoggedIn, String firstName, String lastName, String email, String telephone, RoleEnum role, Account account) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.telephone = telephone;
        this.role = role;
        this.account = account;
    }

    /**
     * Constructor
     * @param id
     * @param username
     * @param password
     * @param isLoggedIn
     * @param firstName
     * @param lastName
     * @param email
     * @param telephone
     * @param role
     */
    public User(int id, String username, String password, boolean isLoggedIn, String firstName, String lastName, String email, String telephone, RoleEnum role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.telephone = telephone;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public RoleEnum getRole() {
        return role;
    }

    public Account getAccount() {
        return account;
    }
    public int getAccountId(){ return account.getId();}
    public double getAccountBalance(){ return account.getBalance();}
    public StatusEnum getAccountStatus(){ return account.getStatus();}

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean hasAccount() {
        return account != null && getAccountStatus() != StatusEnum.FROZEN;
    }

    /**
     * Creates a new list of User with a given ResultSet
     * @param rs ResultSet
     * @return List of User
     */
    public static List<User> createUserListFromResultSet(ResultSet rs){
        List<User> users = new ArrayList<>();
        try{
            while(rs.next()){
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("is_logged_in") == 1,
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        RoleEnum.valueOf(rs.getString("role"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
