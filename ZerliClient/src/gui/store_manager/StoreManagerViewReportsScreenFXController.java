package gui.store_manager;

import classes.Store;
import controllers.ReportController;
import gui.report.complaints.ComplaintMonthlyScreenFXController;
import gui.report.complaints.ComplaintQuarterlyScreenFXController;
import gui.report.income.IncomeMonthlyScreenFXController;
import gui.report.income.IncomeQuarterlyScreenFXController;
import gui.report.orders.OrderMonthlyScreenFXController;
import gui.report.orders.OrderQuarterlyScreenFXController;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import reports.*;
import gui.sidenavigation.SidenavigationStoreManagerFXController;
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
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.Constants.*;
/**
 * This class defines the Store Manager View Reports screen
 */
public class StoreManagerViewReportsScreenFXController extends Pane implements Initializable {

    // ComboBox
    @FXML
    private ComboBox<ReportTypeEnum> reportTypeComboBox;
    @FXML
    private ComboBox<String> monthComboBox;
    @FXML
    private ComboBox<String> monthlyYearComboBox;
    @FXML
    private ComboBox<String> quarterComboBox;
    @FXML
    private ComboBox<String> quarterlyYearComboBox;

    // Radio Buttons
    @FXML
    private RadioButton monthlyReportRadioButton;
    @FXML
    private RadioButton quarterlyReportRadioButton;

    // Buttons
    @FXML
    private Button showReportButton;

    // Text
    @FXML
    private Text successText;
    @FXML
    private Text errorText;

    // Properties
    public static StringProperty errorTextProperty = new SimpleStringProperty("");
    public static StringProperty successTextProperty = new SimpleStringProperty("");

    // Variables
    private final SidenavigationStoreManagerFXController sidenavigationStoreManagerFXController;
    private RadioButton selectedRadioButton;
    int storeId;
    String year, month, quarter;
    private static Store store;
    ReportTypeEnum reportTypeEnum;

    public static SimpleObjectProperty<Report> report = new SimpleObjectProperty<>();

    /**
     * Constructor
     * @param sidenavigationStoreManagerFXController side-navigation
     */
    public StoreManagerViewReportsScreenFXController(SidenavigationStoreManagerFXController sidenavigationStoreManagerFXController){
        this.sidenavigationStoreManagerFXController = sidenavigationStoreManagerFXController;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/store_manager/StoreManagerViewReportsScreenFX.fxml"));

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
        ObservableList<ReportTypeEnum> reportTypeList = FXCollections.observableArrayList(ReportTypeEnum.values());
        ObservableList<String> monthList = FXCollections.observableArrayList(MONTHS);
        ObservableList<String> yearList = FXCollections.observableArrayList(YEARS);
        ObservableList<String> quarterList = FXCollections.observableArrayList(QUARTERS);
        reportTypeComboBox.setItems(reportTypeList);
        monthComboBox.setItems(monthList);
        monthlyYearComboBox.setItems(yearList);
        quarterlyYearComboBox.setItems(yearList);
        quarterComboBox.setItems(quarterList);

        // Initialize Variables
        selectedRadioButton = monthlyReportRadioButton;

        // Get Store
        store = StoreManagerMyAccountScreenFXController.store;

        // Listen for changes
        report.addListener(change -> Platform.runLater(this::openNewPane));
    }

    /**
     * Check if all values entered current.<p>
     *     if yes it will create a new report depends on the values and will proceed the task to the controller,
     *     else it will show an appropriate message to the user.
     * @param event Button press
     */
    @FXML
    void onButtonPressShowReport(ActionEvent event) {
        clearAllTexts();
        if(isAllValuesSelected()){
            storeId = store.getId();
            reportTypeEnum = reportTypeComboBox.getSelectionModel().getSelectedItem();

            // Monthly Report
            if(selectedRadioButton.equals(monthlyReportRadioButton)){
                month = monthComboBox.getSelectionModel().getSelectedItem();
                year = monthlyYearComboBox.getSelectionModel().getSelectedItem();
                switch (reportTypeEnum){
                    case INCOME:
                        ReportController.getSelectedReportBySM(new IncomeReport(storeId, year, month, ReportPeriodEnum.MONTHLY));
                        break;
                    case ORDERS:
                        ReportController.getSelectedReportBySM(new OrderReport(storeId, year, month, ReportPeriodEnum.MONTHLY));
                        break;
                    case COMPLAINTS:
                        ReportController.getSelectedReportBySM(new ComplaintReport(storeId, year, month, ReportPeriodEnum.MONTHLY));
                        break;
                }
            }
            // Quarterly Report
            else{
                quarter = quarterComboBox.getSelectionModel().getSelectedItem();
                year = quarterlyYearComboBox.getSelectionModel().getSelectedItem();
                switch (reportTypeEnum) {
                    case INCOME:
                        ReportController.getSelectedReportBySM(new IncomeReport(storeId, year, quarter, ReportPeriodEnum.QUARTERLY));
                        break;
                    case ORDERS:
                        ReportController.getSelectedReportBySM(new OrderReport(storeId, year, quarter, ReportPeriodEnum.QUARTERLY));
                        break;
                    case COMPLAINTS:
                        ReportController.getSelectedReportBySM(new ComplaintReport(storeId, year, quarter, ReportPeriodEnum.QUARTERLY));
                        break;
                }
            }
        }else errorTextProperty.set("Please select all required fields.");
    }

    /**
     * Changes the middle Pane depends on the selected report by the user.
     */
    private void openNewPane(){
        Report selectedReport = report.get();
        if(selectedReport instanceof IncomeReport && selectedReport.isMonthly()){
            IncomeMonthlyScreenFXController incomeMonthlyScreenFXController = new IncomeMonthlyScreenFXController((IncomeReport) selectedReport);
            sidenavigationStoreManagerFXController.setMainPaneCenter(incomeMonthlyScreenFXController);
            sidenavigationStoreManagerFXController.setTopBarText("Login -> User Portal -> View Reports -> Show Report");
        }
        else if(selectedReport instanceof IncomeReport && selectedReport.isQuarterly()){
            IncomeQuarterlyScreenFXController incomeQuarterlyScreenFXController = new IncomeQuarterlyScreenFXController((IncomeReport) selectedReport);
            sidenavigationStoreManagerFXController.setMainPaneCenter(incomeQuarterlyScreenFXController);
            sidenavigationStoreManagerFXController.setTopBarText("Login -> User Portal -> View Reports -> Show Report");
        }
        else if(selectedReport instanceof OrderReport && selectedReport.isMonthly()){
            OrderMonthlyScreenFXController orderMonthlyScreenFXController = new OrderMonthlyScreenFXController((OrderReport) selectedReport);
            sidenavigationStoreManagerFXController.setMainPaneCenter(orderMonthlyScreenFXController);
            sidenavigationStoreManagerFXController.setTopBarText("Login -> User Portal -> View Reports -> Show Report");
        }
        else if(selectedReport instanceof OrderReport && selectedReport.isQuarterly()){
            OrderQuarterlyScreenFXController orderQuarterlyScreenFXController = new OrderQuarterlyScreenFXController((OrderReport) selectedReport);
            sidenavigationStoreManagerFXController.setMainPaneCenter(orderQuarterlyScreenFXController);
            sidenavigationStoreManagerFXController.setTopBarText("Login -> User Portal -> View Reports -> Show Report");
        }
        else if(selectedReport instanceof ComplaintReport && selectedReport.isMonthly()){
            ComplaintMonthlyScreenFXController complaintMonthlyScreenFXController = new ComplaintMonthlyScreenFXController((ComplaintReport) selectedReport);
            sidenavigationStoreManagerFXController.setMainPaneCenter(complaintMonthlyScreenFXController);
            sidenavigationStoreManagerFXController.setTopBarText("Login -> User Portal -> View Reports -> Show Report");
        }
        else if(selectedReport instanceof ComplaintReport && selectedReport.isQuarterly()){
            ComplaintQuarterlyScreenFXController complaintQuarterlyScreenFXController = new ComplaintQuarterlyScreenFXController((ComplaintReport) selectedReport);
            sidenavigationStoreManagerFXController.setMainPaneCenter(complaintQuarterlyScreenFXController);
            sidenavigationStoreManagerFXController.setTopBarText("Login -> User Portal -> View Reports -> Show Report");
        }
    }

    /**
     * set quarterly report fields to be disabled.
     * @param event mouse click
     */
    @FXML
    void onMouseClickMonthlyReportRadioButton(MouseEvent event) {
        // Set quarterly report options un-visible
        quarterlyReportRadioButton.setSelected(false);
        quarterComboBox.setDisable(true);
        quarterlyYearComboBox.setDisable(true);

        // Set monthly report options visible
        monthlyReportRadioButton.setSelected(true);
        monthComboBox.setDisable(false);
        monthlyYearComboBox.setDisable(false);

        selectedRadioButton = monthlyReportRadioButton;
    }

    /**
     * Set monthly report fields to be disabled
     * @param event mouse click
     */
    @FXML
    void onMouseClickQuarterlyReportRadioButton(MouseEvent event) {
        // Set quarterly report options visible
        quarterlyReportRadioButton.setSelected(true);
        quarterComboBox.setDisable(false);
        quarterlyYearComboBox.setDisable(false);

        // Set monthly report options un-visible
        monthlyReportRadioButton.setSelected(false);
        monthComboBox.setDisable(true);
        monthlyYearComboBox.setDisable(true);

        selectedRadioButton = quarterlyReportRadioButton;
    }

    /**
     * Validates if all required values are selected.
     * @return boolean
     */
    private boolean isAllValuesSelected(){
        return isReportTypeSelected() && isReportDurationSelected();
    }

    /**
     * Validates if report type ComboBox has selected in GUI.
     * @return boolean
     */
    private boolean isReportTypeSelected() {
        return reportTypeComboBox.getSelectionModel().getSelectedItem() != null;
    }

    /**
     * Validates if report duration ComboBox has selected in GUI.
     * @return boolean
     */
    private boolean isReportDurationSelected() {
        if(monthlyReportRadioButton.isSelected()){
            return (monthComboBox.getSelectionModel().getSelectedItem() != null &
                    monthlyYearComboBox.getSelectionModel().getSelectedItem() != null);
        }else{
            return (quarterComboBox.getSelectionModel().getSelectedItem() != null &
                    quarterlyYearComboBox.getSelectionModel().getSelectedItem() != null);
        }
    }

    /**
     * Clear all texts
     */
    private void clearAllTexts(){
        successTextProperty.set("");
        errorTextProperty.set("");
    }

}
