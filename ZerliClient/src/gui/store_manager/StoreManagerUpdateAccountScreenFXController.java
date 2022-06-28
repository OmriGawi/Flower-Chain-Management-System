package gui.store_manager;

import classes.Account;
import enums.StatusEnum;
import classes.User;
import controllers.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.Constants.*;
/**
 * This class defines the Store Manager Update Account screen
 */
public class StoreManagerUpdateAccountScreenFXController extends Pane implements Initializable {

    // Accounts TableView
    @FXML
    private TableView<User> usersTableView;
        @FXML
        private TableColumn<Account, Integer> accountIdCol;
        @FXML
        private TableColumn<User, String> firstNameCol;
        @FXML
        private TableColumn<User, String> lastNameCol;
        @FXML
        private TableColumn<User, String> emailCol;
        @FXML
        private TableColumn<User, String> telephoneCol;
        @FXML
        private TableColumn<Account, Double> balanceCol;
        @FXML
        private TableColumn<Account, StatusEnum> statusCol;

    // Labels
    @FXML
    private Label selectedAccountIdLabel;
    @FXML
    private Label selectedFirstNameLabel;
    @FXML
    private Label selectedLastNameLabel;

    // Text
    @FXML
    private Text errorText;
    @FXML
    private Text successText;

    // ComboBox
    @FXML
    private ComboBox<StatusEnum> editStatusComboBox;

    // Buttons
    @FXML
    private Button updateDetailsButton;

    // Properties
    public static StringProperty errorTextProperty = new SimpleStringProperty("");
    public static StringProperty successTextProperty = new SimpleStringProperty("");

    // Observable List
    public static ObservableList<User> users = FXCollections.observableArrayList();

    // Variables
    private User selectedUser;

    /**
     * Constructor
     */
    public StoreManagerUpdateAccountScreenFXController(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/store_manager/StoreManagerUpdateAccountScreenFX.fxml"));

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
        // Set values factory
        accountIdCol.setCellValueFactory(new PropertyValueFactory<>("accountId"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("accountBalance"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("accountStatus"));

        // Properties
        successTextProperty.addListener((observable, oldValue, newValue) -> {
            successText.setText(newValue);
            successText.setStyle(TEXT_VALID_STYLE);
        });
        errorTextProperty.addListener((observable, oldValue, newValue) -> {
            errorText.setText(newValue);
            errorText.setStyle(TEXT_NOT_VALID_STYLE);
        });

        // Initialize ComboBox
        ObservableList<StatusEnum> list = FXCollections.observableArrayList(StatusEnum.values());
        editStatusComboBox.setItems(list);

        // Set observable list into usersTableView
        usersTableView.setItems(users);

        // Get all client users without registered account.
        AccountController.getAllClientUsersWithRegisteredAccount();
    }

    /**
     * This method used to run when the user will click on update button
     * @param event Button press
     */
    @FXML
    void onButtonPressUpdateDetails(ActionEvent event) {
        successTextProperty.set("");
        errorTextProperty.set("");
        selectedUser.getAccount().setStatus(editStatusComboBox.getSelectionModel().getSelectedItem());
        AccountController.editAccountStatus(selectedUser);
        usersTableView.setDisable(true);
        updateDetailsButton.setDisable(false);
    }

    /**
     * This method will put the selected row into a variable "selectedUser"
     * @param event table row click
     */
    @FXML
    void onMouseClickTableRow(MouseEvent event) {
        try{
            User user = usersTableView.getSelectionModel().getSelectedItem();
            if(user != null){
                selectedAccountIdLabel.setText(String.valueOf(user.getAccount().getId()));
                selectedFirstNameLabel.setText(user.getFirstName());
                selectedLastNameLabel.setText(user.getLastName());
                selectedUser = user;
                editStatusComboBox.setDisable(false);
            }
        }catch (Exception ignored){}
    }

    /**
     * This method will set "Update Details" button to false when status has been selected
     * @param event select status in ComboBox
     */
    @FXML
    void onStatusSelected(ActionEvent event) {
        if(selectedUser != null)
            updateDetailsButton.setDisable(false);
    }
}
