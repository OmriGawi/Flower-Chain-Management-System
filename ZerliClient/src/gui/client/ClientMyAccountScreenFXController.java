package gui.client;

import classes.Account;
import classes.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientMyAccountScreenFXController extends Pane implements Initializable {

    // User Labels
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneNumberLabel;

    // Account Labels
    @FXML
    private Label accountIdLabel;
    @FXML
    private Label accountStatusLabel;
    @FXML
    private Label accountRoleLabel;
    @FXML
    private Label accountBalanceLabel;


    /**
     * Constructor initialize the fxml loader to screen path
     */
    public ClientMyAccountScreenFXController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/client/ClientMyAccountScreenFX.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setControllerFactory(callback -> this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = Utils.currUser;
        Account account = user.getAccount();

        firstNameLabel.setText(Utils.currUser.getFirstName());
        lastNameLabel.setText(Utils.currUser.getLastName());
        emailLabel.setText(Utils.currUser.getEmail());
        phoneNumberLabel.setText(Utils.currUser.getTelephone());
        accountRoleLabel.setText(String.valueOf(user.getRole()));
        if (account != null) {
            accountIdLabel.setText(String.valueOf(account.getId()));
            accountStatusLabel.setText(String.valueOf(account.getStatus()));
            accountBalanceLabel.setText(String.valueOf(account.getBalance()));
        }
    }
}
