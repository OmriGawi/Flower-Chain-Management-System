package gui.store_manager;

import classes.User;
import controllers.AccountController;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.Constants.*;
/**
 * This class defines the Store Manager Create Account screen
 */
public class StoreManagerCreateAccountScreenFXController extends Pane implements Initializable {

    private static final int LENGTH_OF_CREDIT_CARD = 16;
    private static final int LENGTH_OF_CVV = 3;
    private static final int ALL_VALUES_CORRECT = 3;

    // Users Table View
    @FXML
    private TableView<User> usersTableView;
        @FXML
        private TableColumn<User, Integer> idCol;
        @FXML
        private TableColumn<User, String> firstNameCol;
        @FXML
        private TableColumn<User, String> lastNameCol;
        @FXML
        private TableColumn<User, String> emailCol;
        @FXML
        private TableColumn<User, String> telephoneCol;


    // TextFields
    @FXML
    private TextField cardNumberTextField;
    @FXML
    private TextField expirationDateTextField;
    @FXML
    private TextField cvvTextField;

    // Text - Errors
    @FXML
    private Text cardNumberErrorText;
    @FXML
    private Text expirationDateErrorText;
    @FXML
    private Text cvvErrorText;
    @FXML
    private Text errorText;
    @FXML
    private Text successText;

    // Labels
    @FXML
    private Label selectedUserFirstNameLabel;
    @FXML
    private Label selectedUserIdLabel;
    @FXML
    private Label selectedUserLastNameLabel;

    // Buttons
    @FXML
    private Button createAccountButton;

    // Properties
    public static StringProperty successTextProperty = new SimpleStringProperty("");
    public static StringProperty errorTextProperty = new SimpleStringProperty("");

    // Controllers
    public static AccountController accountController;

    // Observable List
    public static ObservableList<User> users = FXCollections.observableArrayList();

    // Variables
    private User selectedUser;

    /**
     * Constructor
     */
    public StoreManagerCreateAccountScreenFXController(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/store_manager/StoreManagerCreateAccountScreenFX.fxml"));

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
        // Initialize controllers
        accountController = new AccountController();

        // Set values factory
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        // Properties
        successTextProperty.addListener((observable, oldValue, newValue) -> {
            successText.setText(newValue);
            successText.setStyle(TEXT_VALID_STYLE);
        });
        errorTextProperty.addListener((observable, oldValue, newValue) -> {
            errorText.setText(newValue);
            errorText.setStyle(TEXT_NOT_VALID_STYLE);
        });

        // Set observable list into usersTableView
        usersTableView.setItems(users);

        // Get all client users without registered account.
        AccountController.getAllClientUsersWithoutRegisteredAccount();
    }

    /**
     * This method used to run when the user want to create a new account.
     * @param event Button press
     */
    @FXML
    void onButtonPressCreateAccount(ActionEvent event) {
        int correctValues = 0;
        if (isCardNumberValid())
            correctValues++;
        if(isExpirationDateValid())
            correctValues++;
        if(isCvvValid())
            correctValues++;

        if(correctValues == ALL_VALUES_CORRECT){
            String cardNumber = cardNumberTextField.getText();
            String expirationDate = expirationDateTextField.getText();
            String cvv = cvvTextField.getText();
            successTextProperty.set("");
            errorTextProperty.set("");
            AccountController.createNewAccount(cardNumber, expirationDate, cvv, selectedUser);
            this.setDisable(true);
        }
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
                selectedUserIdLabel.setText(String.valueOf(user.getId()));
                selectedUserFirstNameLabel.setText(user.getFirstName());
                selectedUserLastNameLabel.setText(user.getLastName());
                selectedUser = user;
                createAccountButton.setDisable(false);
            }
        }catch (Exception ignored){}
    }

    /**
     * This method validate the card number entered from the user in GUI
     * @return boolean
     */
    private boolean isCardNumberValid(){
        try{
             String cardNumber = cardNumberTextField.getText();
            // validate 16 length card number
            if(cardNumber.length() != LENGTH_OF_CREDIT_CARD || !cardNumber.matches("\\d+")) {
                cardNumberErrorText.setText("Please enter 16 length number");
                cardNumberErrorText.setStyle(TEXT_NOT_VALID_STYLE);
                cardNumberTextField.setStyle(TEXT_FIELD_NOT_VALID_STYLE);
                return false;
            }
        }catch (Exception ignored){
        }
        cardNumberErrorText.setText("");
        cardNumberTextField.setStyle(TEXT_FIELD_VALID_STYLE);
        return true;
    }

    /**
     * This method validates the expiration date of a card entered by the user in the GUI
     * @return boolean
     */
    private boolean isExpirationDateValid(){
        String expirationDate = expirationDateTextField.getText();
        if(expirationDate.matches("(?:0[1-9]|1[0-2])/\\d{2}")){
            expirationDateErrorText.setText("");
            expirationDateTextField.setStyle(TEXT_FIELD_VALID_STYLE);
            return true;
        }else{
            expirationDateErrorText.setText("Please enter MM/yy format");
            expirationDateErrorText.setStyle(TEXT_NOT_VALID_STYLE);
            expirationDateTextField.setStyle(TEXT_FIELD_NOT_VALID_STYLE);
            return false;
        }
    }

    /**
     * This method validates the cvv entered by the user in the GUI
     * @return boolean
     */
    private boolean isCvvValid(){
        try{
            String cvv = cvvTextField.getText();
            // validate 3 length card number
            if(cvv.length() != LENGTH_OF_CVV || !cvv.matches("\\d+")) {
                cvvErrorText.setText("Please enter 3 length cvv");
                cvvErrorText.setStyle(TEXT_NOT_VALID_STYLE);
                cvvTextField.setStyle(TEXT_FIELD_NOT_VALID_STYLE);
                return false;
            }
        }catch (Exception ignored){
        }
        cvvErrorText.setText("");
        cvvTextField.setStyle(TEXT_FIELD_VALID_STYLE);
        return true;
    }
}
