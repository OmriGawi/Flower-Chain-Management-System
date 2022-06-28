package controllers;

import enums.RoleEnum;
import classes.User;
import client.ZerliClientController;
import communication.Message;
import communication.Task;
import gui.login.LoginScreenFXController;
import utils.Utils;

import java.util.HashMap;

/**
 * This class handles the Item methods to be sent and received from the server for the relevant controllers
 */
public class LoginController {
    /**
     * This method creates a new message for logging in a user with it username and password provided
     * @param username The username of the client
     * @param password The password of the client
     */
    public static void loginByUsernameAndPassword(String username, String password) {
        HashMap<String, String> args = new HashMap<>();
        args.put("username", username);
        args.put("password", password);
        Message msg = new Message(Task.LOGIN_USERNAME_PASSWORD, args);
        ZerliClientController.accept(msg);
    }

    /**
     * This method creates a new message used to update the login status of an account in the GUI and the Current User
     * @param msg The message containing the answer regarding the log in status of the account from the server
     */
    public static void updateLoginStatus(Message msg) {
        String loginStatus = "";
        switch (msg.getAnswer()) {
            case SUCCEED:
                loginStatus = "Login successfully";
                break;
            case INCORRECT_VALUES:
                loginStatus = "Username or password is incorrect";
                break;
            case ALREADY_LOGGED_IN:
                loginStatus = "User already logged-in";
                break;
        }
        RoleEnum currUserRole = null;
        if (msg.getObject() instanceof User) {
            Utils.currUser = (User) msg.getObject();
            currUserRole = Utils.currUser.getRole();
        }
        LoginScreenFXController.loginStatusUpdated(
                loginStatus,
                currUserRole
        );
    }

    /**
     * Set 'is_logged_in' column in DB to 0
     * @param username to logout
     */
    public static void logoutUser(String username) {
        Utils.currUser = null;
        HashMap<String, String> args = new HashMap<>();
        args.put("username", username);
        Message msg = new Message(Task.LOGOUT_USERNAME, args);
        ZerliClientController.accept(msg);
    }
}
