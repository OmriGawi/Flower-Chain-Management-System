package gui.ceo;

import classes.Store;
import controllers.ReportController;
import gui.report.complaints.ComplaintMonthlyScreenFXController;
import gui.report.complaints.ComplaintMonthlyTwoStoresScreenFXController;
import gui.report.complaints.ComplaintQuarterlyScreenFXController;
import gui.report.complaints.ComplaintQuarterlyTwoStoresScreenFXController;
import gui.report.income.IncomeMonthlyScreenFXController;
import gui.report.income.IncomeMonthlyTwoStoresScreenFXController;
import gui.report.income.IncomeQuarterlyScreenFXController;
import gui.report.income.IncomeQuarterlyTwoStoresScreenFXController;
import gui.report.orders.OrderMonthlyScreenFXController;
import gui.report.orders.OrderMonthlyTwoStoresScreenFXController;
import gui.report.orders.OrderQuarterlyScreenFXController;
import gui.report.orders.OrderQuarterlyTwoStoresFXController;
import gui.sidenavigation.SidenavigationCEOFXController;
import javafx.application.Platform;
import javafx.beans.property.*;
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
import reports.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.Constants.*;

public class CeoViewSingleStoreReportScreenFXController extends Pane implements Initializable {

    // ComboBox
    @FXML
    private ComboBox<ReportTypeEnum> reportTypeComboBox;

    @FXML
    private ComboBox<String> monthComboBox1;
    @FXML
    private ComboBox<String> monthlyYearComboBox1;
    @FXML
    private ComboBox<String> quarterComboBox1;
    @FXML
    private ComboBox<String> quarterlyYearComboBox1;
    @FXML
    private ComboBox<Store> storeComboBox1;

    @FXML
    private ComboBox<String> monthComboBox2;
    @FXML
    private ComboBox<String> monthlyYearComboBox2;
    @FXML
    private ComboBox<String> quarterComboBox2;
    @FXML
    private ComboBox<String> quarterlyYearComboBox2;
    @FXML
    private ComboBox<Store> storeComboBox2;

    // Radio buttons
    @FXML
    private RadioButton quarterlyReportRadioButton1;
    @FXML
    private RadioButton monthlyReportRadioButton1;
    @FXML
    private RadioButton monthlyReportRadioButton2;
    @FXML
    private RadioButton quarterlyReportRadioButton2;
    @FXML
    private RadioButton radioButton2;

    // Pane
    @FXML
    private Pane pane2;

    // Buttons
    @FXML
    private Button backButton;
    @FXML
    private Button showReportButton;

    // Text
    @FXML
    private Text errorText;
    @FXML
    private Text successText;

    // Side-Navigation
    private final SidenavigationCEOFXController sidenavigationCEOFXController;

    // Properties
    public static StringProperty errorTextProperty = new SimpleStringProperty("");
    public static StringProperty successTextProperty = new SimpleStringProperty("");

    // Variables
    public static ObservableList<Store> stores = FXCollections.observableArrayList();
    private int storeId1, storeId2;
    private String year1, year2, month1, month2, quarter1, quarter2;
    public static SimpleObjectProperty<Report> showOneReport = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<Report> showTwoReports1 = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<Report> showTwoReports2 = new SimpleObjectProperty<>();

    /**
     * Constructor
     * @param sidenavigationCEOFXController side navigation
     */
    public CeoViewSingleStoreReportScreenFXController(SidenavigationCEOFXController sidenavigationCEOFXController){
        this.sidenavigationCEOFXController = sidenavigationCEOFXController;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/ceo/CeoViewSingleStoreReportScreenFX.fxml"));
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
        // Properties
        successTextProperty.addListener((observable, oldValue, newValue) -> {
            successText.setText(newValue);
            successText.setStyle(TEXT_VALID_STYLE);
        });
        errorTextProperty.addListener((observable, oldValue, newValue) -> {
            errorText.setText(newValue);
            errorText.setStyle(TEXT_NOT_VALID_STYLE);
        });

        // Get All Stores
        ReportController.getAllStoresByCEO();

        // Initialize ComboBox
        ObservableList<ReportTypeEnum> reportTypeList = FXCollections.observableArrayList(ReportTypeEnum.values());
        ObservableList<String> monthList = FXCollections.observableArrayList(MONTHS);
        ObservableList<String> yearList = FXCollections.observableArrayList(YEARS);
        ObservableList<String> quarterList = FXCollections.observableArrayList(QUARTERS);
        reportTypeComboBox.setItems(reportTypeList);
        monthComboBox1.setItems(monthList);
        monthComboBox2.setItems(monthList);
        monthlyYearComboBox1.setItems(yearList);
        storeComboBox1.setItems(stores);
        monthlyYearComboBox2.setItems(yearList);
        quarterlyYearComboBox1.setItems(yearList);
        quarterlyYearComboBox2.setItems(yearList);
        quarterComboBox1.setItems(quarterList);
        quarterComboBox2.setItems(quarterList);
        storeComboBox2.setItems(stores);

        // Listen for changes
        showOneReport.addListener(change -> Platform.runLater(this::openNewPaneOneReport));
        showTwoReports2.addListener(change -> Platform.runLater(this::openNewPaneTwoReports));
    }

    /**
     * This function used to create an instance of a reported depends on the report selected
     * by the user in GUI. It will proceed the task to the appropriate controller.
     * @param event Button Press
     */
    @FXML
    void onButtonPressShowReport(ActionEvent event) {
        clearAllTexts();
        if(isAllValuesSelected()) {
            storeId1 = storeComboBox1.getValue().getId();
            ReportTypeEnum reportTypeEnum = reportTypeComboBox.getSelectionModel().getSelectedItem();

            // Case only 1 Store has been selected
            if(!radioButton2.isSelected())
                getOneStoreReport(reportTypeEnum);
            // Case 2 Stores have been selected
            else{
                storeId2 = storeComboBox2.getValue().getId();
                getTwoStoresReport(reportTypeEnum);
            }
        }else errorTextProperty.set("Please select all required fields.");

    }

    /**
     * Creates a new instance of Report depends on the report type selected by the user.
     * Will proceed the instance to the controller.
     * @param reportTypeEnum reportTypeEnum
     */
    private void getOneStoreReport(ReportTypeEnum reportTypeEnum) {
        // Monthly report
        if(monthlyReportRadioButton1.isSelected()){
            month1 = monthComboBox1.getSelectionModel().getSelectedItem();
            year1 = monthlyYearComboBox1.getSelectionModel().getSelectedItem();
            switch (reportTypeEnum){
                case INCOME:
                    ReportController.getOneSelectedReportByCEO(new IncomeReport(storeId1, year1, month1, ReportPeriodEnum.MONTHLY));
                    break;
                case ORDERS:
                    ReportController.getOneSelectedReportByCEO(new OrderReport(storeId1, year1, month1, ReportPeriodEnum.MONTHLY));
                    break;
                case COMPLAINTS:
                    ReportController.getOneSelectedReportByCEO(new ComplaintReport(storeId1, year1, month1, ReportPeriodEnum.MONTHLY));
                    break;
            }
        }
        // Quarterly Report
        else {
            quarter1 = quarterComboBox1.getSelectionModel().getSelectedItem();
            year1 = quarterlyYearComboBox1.getSelectionModel().getSelectedItem();
            switch (reportTypeEnum) {
                case INCOME:
                    ReportController.getOneSelectedReportByCEO(new IncomeReport(storeId1, year1, quarter1, ReportPeriodEnum.QUARTERLY));
                    break;
                case ORDERS:
                    ReportController.getOneSelectedReportByCEO(new OrderReport(storeId1, year1, quarter1, ReportPeriodEnum.QUARTERLY));
                    break;
                case COMPLAINTS:
                    ReportController.getOneSelectedReportByCEO(new ComplaintReport(storeId1, year1, quarter1, ReportPeriodEnum.QUARTERLY));
                    break;
            }
        }
    }

    /**
     * Creates two new instances of Reports depends on the reports type selected by the user.
     * Will proceed the instances to the controller.
     * @param reportTypeEnum reportTypeEnum
     */
    private void getTwoStoresReport(ReportTypeEnum reportTypeEnum) {
        // Monthly Report
        if(monthlyReportRadioButton2.isSelected()){
            year1 = monthlyYearComboBox1.getSelectionModel().getSelectedItem();
            year2 = monthlyYearComboBox2.getSelectionModel().getSelectedItem();
            month1 = monthComboBox1.getSelectionModel().getSelectedItem();
            month2 = monthComboBox2.getSelectionModel().getSelectedItem();
            switch (reportTypeEnum){
                case INCOME:
                    ReportController.getTwoSelectedReportByCEO(
                            new IncomeReport(storeId1, year1, month1, ReportPeriodEnum.MONTHLY),
                            new IncomeReport(storeId2, year2, month2, ReportPeriodEnum.MONTHLY));
                    break;
                case ORDERS:
                    ReportController.getTwoSelectedReportByCEO(
                            new OrderReport(storeId1, year1, month1, ReportPeriodEnum.MONTHLY),
                            new OrderReport(storeId2, year2, month2, ReportPeriodEnum.MONTHLY));
                    break;
                case COMPLAINTS:
                    ReportController.getTwoSelectedReportByCEO(
                            new ComplaintReport(storeId1, year1, month1, ReportPeriodEnum.MONTHLY),
                            new ComplaintReport(storeId2, year2, month2, ReportPeriodEnum.MONTHLY));
                    break;
            }
        }
        // Quarterly Report
        else{
            year1 = quarterlyYearComboBox1.getSelectionModel().getSelectedItem();
            year2 = quarterlyYearComboBox2.getSelectionModel().getSelectedItem();
            quarter1 = quarterComboBox1.getSelectionModel().getSelectedItem();
            quarter2 = quarterComboBox2.getSelectionModel().getSelectedItem();
            switch (reportTypeEnum){
                case INCOME:
                    ReportController.getTwoSelectedReportByCEO(
                            new IncomeReport(storeId1, year1, quarter1, ReportPeriodEnum.QUARTERLY),
                            new IncomeReport(storeId2, year2, quarter2, ReportPeriodEnum.QUARTERLY));
                    break;
                case ORDERS:
                    ReportController.getTwoSelectedReportByCEO(
                            new OrderReport(storeId1, year1, quarter1, ReportPeriodEnum.QUARTERLY),
                            new OrderReport(storeId2, year2, quarter2, ReportPeriodEnum.QUARTERLY));
                    break;
                case COMPLAINTS:
                    ReportController.getTwoSelectedReportByCEO(
                            new ComplaintReport(storeId1, year1, quarter1, ReportPeriodEnum.QUARTERLY),
                            new ComplaintReport(storeId2, year2, quarter2, ReportPeriodEnum.QUARTERLY));
                    break;
            }
        }
    }

    /**
     * Changes the middle Pane depends on the selected report by the user.
     */
    private void openNewPaneOneReport() {
        Report selectedReport = showOneReport.get();
        if(selectedReport instanceof IncomeReport && selectedReport.isMonthly()){
            IncomeMonthlyScreenFXController incomeMonthlyScreenFXController = new IncomeMonthlyScreenFXController((IncomeReport) selectedReport);
            sidenavigationCEOFXController.setMainPaneCenter(incomeMonthlyScreenFXController);
            sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports -> Show Report");
        }
        else if(selectedReport instanceof IncomeReport && selectedReport.isQuarterly()){
            IncomeQuarterlyScreenFXController incomeQuarterlyScreenFXController = new IncomeQuarterlyScreenFXController((IncomeReport) selectedReport);
            sidenavigationCEOFXController.setMainPaneCenter(incomeQuarterlyScreenFXController);
            sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports -> Show Report");
        }
        else if(selectedReport instanceof OrderReport && selectedReport.isMonthly()){
            OrderMonthlyScreenFXController orderMonthlyScreenFXController = new OrderMonthlyScreenFXController((OrderReport) selectedReport);
            sidenavigationCEOFXController.setMainPaneCenter(orderMonthlyScreenFXController);
            sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports -> Show Report");
        }
        else if(selectedReport instanceof OrderReport && selectedReport.isQuarterly()){
            OrderQuarterlyScreenFXController orderQuarterlyScreenFXController = new OrderQuarterlyScreenFXController((OrderReport) selectedReport);
            sidenavigationCEOFXController.setMainPaneCenter(orderQuarterlyScreenFXController);
            sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports -> Show Report");
        }
        else if(selectedReport instanceof ComplaintReport && selectedReport.isMonthly()){
            ComplaintMonthlyScreenFXController complaintMonthlyScreenFXController = new ComplaintMonthlyScreenFXController((ComplaintReport) selectedReport);
            sidenavigationCEOFXController.setMainPaneCenter(complaintMonthlyScreenFXController);
            sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports -> Show Report");
        }
        else if(selectedReport instanceof ComplaintReport && selectedReport.isQuarterly()){
            ComplaintQuarterlyScreenFXController complaintQuarterlyScreenFXController = new ComplaintQuarterlyScreenFXController((ComplaintReport) selectedReport);
            sidenavigationCEOFXController.setMainPaneCenter(complaintQuarterlyScreenFXController);
            sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports -> Show Report");
        }
    }

    /**
     * Changes the middle Pane depends on the selected 2 reports by the user.
     */
    private void openNewPaneTwoReports(){
        Report selectedReport1 = showTwoReports1.getValue();
        Report selectedReport2 = showTwoReports2.getValue();
        if(selectedReport1 instanceof IncomeReport && selectedReport1.isMonthly()){
            IncomeMonthlyTwoStoresScreenFXController incomeMonthlyTwoStoresScreenFXController = new IncomeMonthlyTwoStoresScreenFXController((IncomeReport) selectedReport1, (IncomeReport) selectedReport2);
            sidenavigationCEOFXController.setMainPaneCenter(incomeMonthlyTwoStoresScreenFXController);
            sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports -> Show Report");
        }
        else if(selectedReport1 instanceof IncomeReport && selectedReport1.isQuarterly()){
            IncomeQuarterlyTwoStoresScreenFXController incomeQuarterlyTwoStoresScreenFXController = new IncomeQuarterlyTwoStoresScreenFXController((IncomeReport) selectedReport1, (IncomeReport) selectedReport2);
            sidenavigationCEOFXController.setMainPaneCenter(incomeQuarterlyTwoStoresScreenFXController);
            sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports -> Show Report");
        }
        else if(selectedReport1 instanceof OrderReport && selectedReport1.isMonthly()){
            OrderMonthlyTwoStoresScreenFXController orderMonthlyTwoStoresScreenFXController = new OrderMonthlyTwoStoresScreenFXController((OrderReport) selectedReport1, (OrderReport) selectedReport2);
            sidenavigationCEOFXController.setMainPaneCenter(orderMonthlyTwoStoresScreenFXController);
            sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports -> Show Report");
        }
        else if(selectedReport1 instanceof OrderReport && selectedReport1.isQuarterly()){
            OrderQuarterlyTwoStoresFXController orderQuarterlyTwoStoresFXController = new OrderQuarterlyTwoStoresFXController((OrderReport) selectedReport1, (OrderReport) selectedReport2);
            sidenavigationCEOFXController.setMainPaneCenter(orderQuarterlyTwoStoresFXController);
            sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports -> Show Report");
        }
        else if(selectedReport1 instanceof ComplaintReport && selectedReport1.isMonthly()){
            ComplaintMonthlyTwoStoresScreenFXController complaintMonthlyTwoStoresScreenFXController = new ComplaintMonthlyTwoStoresScreenFXController((ComplaintReport) selectedReport1, (ComplaintReport) selectedReport2);
            sidenavigationCEOFXController.setMainPaneCenter(complaintMonthlyTwoStoresScreenFXController);
            sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports -> Show Report");
        }
        else if(selectedReport1 instanceof ComplaintReport && selectedReport1.isQuarterly()){
            ComplaintQuarterlyTwoStoresScreenFXController complaintQuarterlyTwoStoresScreenFXController = new ComplaintQuarterlyTwoStoresScreenFXController((ComplaintReport) selectedReport1, (ComplaintReport) selectedReport2);
            sidenavigationCEOFXController.setMainPaneCenter(complaintQuarterlyTwoStoresScreenFXController);
            sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports -> Show Report");
        }
    }

    /**
     * set quarterly report fields to be disabled.
     * @param event mouse click
     */
    @FXML
    void onMouseClickMonthlyReportRadioButton(MouseEvent event) {
        // Set quarterly report options un-visible
        quarterlyReportRadioButton1.setSelected(false);
        quarterComboBox1.setDisable(true);
        quarterlyYearComboBox1.setDisable(true);
        quarterlyReportRadioButton2.setSelected(false);
        quarterComboBox2.setDisable(true);
        quarterlyYearComboBox2.setDisable(true);

        // Set monthly report options visible
        monthlyReportRadioButton1.setSelected(true);
        monthComboBox1.setDisable(false);
        monthlyYearComboBox1.setDisable(false);
        monthlyReportRadioButton2.setSelected(true);
        monthComboBox2.setDisable(false);
        monthlyYearComboBox2.setDisable(false);
    }

    /**
     * Set monthly report fields to be disabled
     * @param event mouse click
     */
    @FXML
    void onMouseClickQuarterlyReportRadioButton(MouseEvent event) {
        // Set quarterly report options visible
        quarterlyReportRadioButton1.setSelected(true);
        quarterComboBox1.setDisable(false);
        quarterlyYearComboBox1.setDisable(false);
        quarterlyReportRadioButton2.setSelected(true);
        quarterComboBox2.setDisable(false);
        quarterlyYearComboBox2.setDisable(false);

        // Set monthly report options un-visible
        monthlyReportRadioButton1.setSelected(false);
        monthComboBox1.setDisable(true);
        monthlyYearComboBox1.setDisable(true);
        monthlyReportRadioButton2.setSelected(false);
        monthComboBox2.setDisable(true);
        monthlyYearComboBox2.setDisable(true);
    }

    @FXML
    void onButtonPressBack(ActionEvent event) {
        CeoTypeOfReportScreenFXController ceoTypeOfReportScreenFXController = new CeoTypeOfReportScreenFXController(sidenavigationCEOFXController);
        sidenavigationCEOFXController.setMainPaneCenter(ceoTypeOfReportScreenFXController);
        sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type");
    }

    @FXML
    void onMouseClickRadioButton2(MouseEvent event) {
        pane2.setDisable(!pane2.isDisable());
    }

    /**
     * Validates if all required values are selected.
     * @return boolean
     */
    private boolean isAllValuesSelected(){
        return isReportTypeSelected() && isStore1Selected() && isStore2Selected();
    }

    /**
     * Validates if report type ComboBox has selected in GUI.
     * @return boolean
     */
    private boolean isReportTypeSelected() {
        return reportTypeComboBox.getSelectionModel().getSelectedItem() != null;
    }

    /**
     * Validates if all values for store 1 has been selected in GUI.
     * @return boolean
     */
    private boolean isStore1Selected() {
        if(monthlyReportRadioButton1.isSelected()){
            return (monthComboBox1.getSelectionModel().getSelectedItem() != null &
                    monthlyYearComboBox1.getSelectionModel().getSelectedItem() != null &
                    storeComboBox1.getSelectionModel().getSelectedItem() != null);
        }else{
            return (quarterComboBox1.getSelectionModel().getSelectedItem() != null &
                    quarterlyYearComboBox1.getSelectionModel().getSelectedItem() != null &
                    storeComboBox1.getSelectionModel().getSelectedItem() != null);
        }
    }

    /**
     * Validates if all values for store 2 has been selected in GUI.
     * @return boolean
     */
    private boolean isStore2Selected() {
        if(radioButton2.isSelected()){
            if(monthlyReportRadioButton2.isSelected()){
                return (monthComboBox2.getSelectionModel().getSelectedItem() != null &
                        monthlyYearComboBox2.getSelectionModel().getSelectedItem() != null &
                        storeComboBox2.getSelectionModel().getSelectedItem() != null &
                        storeComboBox2.getSelectionModel().getSelectedItem() != null);
            }else{
                return (quarterComboBox2.getSelectionModel().getSelectedItem() != null &
                        quarterlyYearComboBox2.getSelectionModel().getSelectedItem() != null &
                        storeComboBox2.getSelectionModel().getSelectedItem() != null &
                        storeComboBox2.getSelectionModel().getSelectedItem() != null);
            }
        }else return true;
    }

    /**
     * Clear all texts
     */
    private void clearAllTexts(){
        successTextProperty.set("");
        errorTextProperty.set("");
    }
}
