package gui.service_expert;

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
/**
 * This class defines the My Account screen for the service expert worker
 */
public class ServiceExpertMyAccountScreenFXController extends Pane implements Initializable {

    // Labels
    @FXML
    private Label accountRoleLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label phoneNumberLabel;

    /**
     * Constructor
     */
    public ServiceExpertMyAccountScreenFXController(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/service_expert/ServiceExpertMyAccountScreenFX.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setControllerFactory(callback -> this);

        try {
            fxmlLoader.load();
        } catch (
                IOException e) {
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

        firstNameLabel.setText(Utils.currUser.getFirstName());
        lastNameLabel.setText(Utils.currUser.getLastName());
        emailLabel.setText(Utils.currUser.getEmail());
        phoneNumberLabel.setText(Utils.currUser.getTelephone());
        accountRoleLabel.setText(String.valueOf(user.getRole()).replace("_", " "));
    }
}