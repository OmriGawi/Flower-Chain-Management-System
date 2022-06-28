package gui.customer_service_worker;

import classes.Complaint;
import enums.ComplaintStatusEnum;
import classes.Store;
import controllers.ComplaintController;
import controllers.StoreController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static utils.Constants.TEXT_NOT_VALID_STYLE;

public class CustomerServiceWorkerAddComplaintScreenFXController extends Pane implements Initializable {

    // Pane
    @FXML
    private Pane pane;

    // Buttons
    @FXML
    private Button addComplaintButton;

    // Text
    @FXML
    public Text clientNumberErrorLabel;
    @FXML
    private Text successText;
    @FXML
    private Text complaintContentErrorLabel;
    @FXML
    private Text storeComboBoxErrorText;

    // ComboBox
    @FXML
    private ComboBox<Store> selectStoreComboBox;

    // Text Fields
    @FXML
    private TextField clientNumberTextField;

    // Text Area
    @FXML
    private TextArea complaintTextArea;

    private String complaintContent;
    private int clientNumber;
    public static Complaint complaint;
    public static StringProperty successTextProperty = new SimpleStringProperty("");
    public static ComplaintController complaintController;
    public static ObservableList<Store> stores = FXCollections.observableArrayList();


    /**
     * Constructor to initialize the screen
     */
    public CustomerServiceWorkerAddComplaintScreenFXController(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/customer_service_worker/CustomerServiceWorkerAddComplaintScreenFX.fxml"));

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
        complaintController = new ComplaintController();

        // Get All Stores
        StoreController.getAllStoresByCSW();
        selectStoreComboBox.setItems(stores);

        clientNumberErrorLabel.setVisible(false);
        complaintContentErrorLabel.setVisible(false);

        successTextProperty.addListener((observable, oldValue, newValue) -> {
            successText.setText(newValue);
            if (newValue.contains("success")) {
                successText.setVisible(true);
                clientNumberErrorLabel.setVisible(false);
                clientNumberTextField.clear();
                complaintTextArea.clear();
            }
            else {
                successText.setVisible(false);
                clientNumberErrorLabel.setVisible(true);
            }
        });

        successTextProperty.setValue("");
    }

    /**
     * Adds a new complaint, and it's content to the DB
     * @param event Button Press on 'Add Complaint'
     */
    @FXML
    void onButtonPressAddComplaint(ActionEvent event) {
        successTextProperty.set("");
        if(updateComplainContent() && updateClientNumber() && isStoreSelected()) {
            complaint = new Complaint(
                    complaintContent,
                    Utils.currUser.getId(),
                    selectStoreComboBox.getValue().getId(),
                    clientNumber,
                    new Date(),
                    ComplaintStatusEnum.IN_PROGRESS
            );
            ComplaintController.addComplaint(complaint);
        }
    }

    /**
     * Method to check if the content of the complaint content is not empty (the Text Area is not empty)
     * @return True if TextArea is not empty, False otherwise.
     */
    private Boolean updateComplainContent(){
        complaintContent = complaintTextArea.getText();
        if (complaintContent.isEmpty()) {
            complaintContentErrorLabel.setVisible(true);
            successText.setVisible(false);
            return false;
        }
        complaintContentErrorLabel.setVisible(false);
        return true;
    }

    /**
     * Check if Customer Service Worker inserted the client number to update in the 'Account Number' field
     * @return True if client number field has text, False if empty
     */
    private Boolean updateClientNumber(){
        try {
            clientNumber = Integer.parseInt(clientNumberTextField.getText());
        }
        catch (Exception e) {
            clientNumberErrorLabel.setVisible(true);
            return false;
        }
        return true;
    }

    /**
     * Check if Customer Service Worker chose a store from the ComboBox in the screen
     * @return True if a store is selected from the ComboBox, False otherwise.
     */
    private Boolean isStoreSelected(){
        if(selectStoreComboBox.getValue() == null){
            storeComboBoxErrorText.setText("Please select store");
            storeComboBoxErrorText.setStyle(TEXT_NOT_VALID_STYLE);
            return false;
        }else{
            storeComboBoxErrorText.setText("");
            return true;
        }
    }
}
