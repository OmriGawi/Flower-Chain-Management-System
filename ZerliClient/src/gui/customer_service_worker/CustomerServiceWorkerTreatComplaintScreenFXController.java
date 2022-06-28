package gui.customer_service_worker;

import classes.Complaint;
import enums.ComplaintStatusEnum;
import gui.sidenavigation.SidenavigationCustomerServiceWorkerFXController;
import controllers.ComplaintController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CustomerServiceWorkerTreatComplaintScreenFXController extends Pane implements Initializable {

    @FXML
    private Text clientNumberText;

    @FXML
    public Text refundErrorText;

    @FXML
    public Text complaintStatusText;

    @FXML
    public Text amountOfRefundText;


    @FXML
    private Button closeComplaintButton;

    @FXML
    private TextArea complaintContentTextField;


    @FXML
    private TextField refundAmountTextField;


    private SidenavigationCustomerServiceWorkerFXController sidenavigationCustomerServiceWorkerFXController;
    public Complaint complaint ;
    public static ComplaintController complaintController;
    public static StringProperty successTextProperty = new SimpleStringProperty("");



    /**
     *
     */
    public CustomerServiceWorkerTreatComplaintScreenFXController(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/customer_service_worker/CustomerServiceWorkerTreatComplaintScreenFX.fxml"));

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
        complaint = CustomerServiceWorkerViewAllComplaintsScreenFXController.complaintToTreat;
        clientNumberText.setText(Integer.toString(complaint.getAccountId()));
        complaintContentTextField.setText(complaint.getContent());
        successTextProperty.setValue("");
        successTextProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("success")) {
                complaintStatusText.setText("Complaint was closed successfully!");
                closeComplaintButton.setDisable(true);
            } else {
                complaintStatusText.setText("Error to close the compliant");
            }
            complaintStatusText.setVisible(true);
            refundErrorText.setVisible(false);
        });
        if (complaint.getComplaintStatus()== ComplaintStatusEnum.CLOSED){
            closeComplaintButton.setVisible(false);
            refundAmountTextField.setVisible(false);
            amountOfRefundText.setVisible(false);
        }

    }


    /**
     * Close complaint in the DB, and refund a user the amount entered at the 'RefundAmountTextField'
     * @param event Button Press on 'Close complaint'
     */
    @FXML
    void onButtonPressCloseComplaint(ActionEvent event) {
        double refund;
        try {
            refund = Double.parseDouble(refundAmountTextField.getText());
        }
        catch(Exception e) {
            refundErrorText.setText("Refund must be a number");
            refundErrorText.setVisible(true);
            return;
        }
        if(refund<0){
            refundErrorText.setText("Refund must be greater than zero");
            refundErrorText.setVisible(true);
            return;
        }
        ComplaintController.closeComplaint(complaint,refund);
    }

}
