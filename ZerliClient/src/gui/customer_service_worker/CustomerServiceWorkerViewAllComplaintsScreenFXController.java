package gui.customer_service_worker;

import classes.Complaint;
import controllers.ComplaintController;
import gui.sidenavigation.SidenavigationCustomerServiceWorkerFXController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static utils.Constants.*;

public class CustomerServiceWorkerViewAllComplaintsScreenFXController extends Pane implements Initializable {

    // Complaints Table View
    @FXML
    private TableView<Complaint> complaintsTable;
        @FXML
        private TableColumn<Complaint, Integer> complaintIdCol;
        @FXML
        private TableColumn<Complaint, Integer> accountIdCol;
        @FXML
        private TableColumn<Complaint, String> statusCol;

        @FXML
        private TableColumn<Complaint, Date> dateCol;

    // Pane
    @FXML
    private Pane pane;

    @FXML
    private Text treatErrorText;
    // Buttons
    @FXML
    private Button viewComplaintButton;

    private final SidenavigationCustomerServiceWorkerFXController sidenavigationCustomerServiceWorkerFXController;

    public static ComplaintController complaintController;
    public static ObservableList<Complaint> complaints = FXCollections.observableArrayList();
    public static Complaint complaintToTreat;



    /**
     * Constructor to initialize the screen
     * @param sidenavigationCustomerServiceWorkerFXController sidenavigationCustomerServiceWorkerFXController screen to
     *                                                        initialize with
     */
    public CustomerServiceWorkerViewAllComplaintsScreenFXController(SidenavigationCustomerServiceWorkerFXController sidenavigationCustomerServiceWorkerFXController){
        this.sidenavigationCustomerServiceWorkerFXController = sidenavigationCustomerServiceWorkerFXController;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/customer_service_worker/CustomerServiceWorkerViewAllComplaintsScreenFX.fxml"));

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
        complaintIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        accountIdCol.setCellValueFactory(new PropertyValueFactory<>("accountId"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("complaintStatus"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(CustomerServiceWorkerViewAllComplaintsScreenFXController::setTableViewDateFormat);

        complaintsTable.setItems(complaints);
        ComplaintController.getAllComplaints();
    }


    /**
     * Convert data from the Tables Column and return a TableCell with that data
     * @param tc Table Colum containing the data to be converted to a table view cell object
     * @return TableCell object containing the table's column data
     */
    private static TableCell<Complaint, Date> setTableViewDateFormat(TableColumn<Complaint, Date> tc) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                if (empty)
                    setText(null);
                else
                    setText(SQL_DATE_FORMAT.format(date));
            }
        };
    }


    /**
     * Treat a complaint and save it to the DB
     * @param event
     */
    @FXML
    void onButtonPressTreatComplaint(ActionEvent event) {
        try {
            int complaintId = complaintsTable.getSelectionModel().getSelectedItem().getId();
            treatComplaint(complaintId);
        } catch (Exception ignored) {
            treatErrorText.setText("Please select a complaint to treat.");
            treatErrorText.setVisible(true);
        }
    }


    /**
     * Open treat complaint screen
     * @param complaintId
     */
    private void treatComplaint(int complaintId) {
        for (Complaint complaint:complaints)
            if (complaint.getId() == complaintId){
                complaintToTreat = complaint;
                break;
            }
        CustomerServiceWorkerTreatComplaintScreenFXController customerServiceWorkerTreatComplaintScreenFXController = new CustomerServiceWorkerTreatComplaintScreenFXController();
        sidenavigationCustomerServiceWorkerFXController.setMainPaneCenter(customerServiceWorkerTreatComplaintScreenFXController);
        sidenavigationCustomerServiceWorkerFXController.setTopBarText("Login -> User Portal -> View Complaint");
    }
}
