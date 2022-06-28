package gui.ceo;

import controllers.ReportController;
import gui.report.income.IncomeQuarterlyScreenFXController;
import gui.sidenavigation.SidenavigationCEOFXController;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import reports.IncomeReport;
import reports.Report;
import reports.ReportPeriodEnum;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.Constants.*;

public class CeoViewAllStoresReportScreenFXController extends Pane implements Initializable {

    // ComboBox
    @FXML
    private ComboBox<String> quarterlyYearComboBox;
    @FXML
    private ComboBox<String> quarterComboBox;

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

    // Properties
    public static StringProperty errorTextProperty = new SimpleStringProperty("");
    public static StringProperty successTextProperty = new SimpleStringProperty("");

    // Side-Navigation
    private SidenavigationCEOFXController sidenavigationCEOFXController;

    // Variables
    String year, quarter;
    public static SimpleObjectProperty<Report> report = new SimpleObjectProperty<>();

    public CeoViewAllStoresReportScreenFXController(SidenavigationCEOFXController sidenavigationCEOFXController){
        this.sidenavigationCEOFXController = sidenavigationCEOFXController;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/ceo/CeoViewAllStoresReportScreenFX.fxml"));

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

        // Initialize ComboBox
        ObservableList<String> yearList = FXCollections.observableArrayList(YEARS);
        ObservableList<String> quarterList = FXCollections.observableArrayList(QUARTERS);
        quarterlyYearComboBox.setItems(yearList);
        quarterComboBox.setItems(quarterList);

        // Listen for changes
        report.addListener(change -> Platform.runLater(this::openNewPane));

    }

    /**
     * This method will initialize a specific Pane and set it as a center
     * @param event Button press
     */
    @FXML
    void onButtonPressBack(ActionEvent event) {
        CeoTypeOfReportScreenFXController ceoTypeOfReportScreenFXController = new CeoTypeOfReportScreenFXController(sidenavigationCEOFXController);
        sidenavigationCEOFXController.setMainPaneCenter(ceoTypeOfReportScreenFXController);
        sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type");
    }

    /**
     * Show the Report and get the relevant data for it from the DB in the GUI
     * @param event Button Press on Show Report button in the GUI
     */
    @FXML
    void onButtonPressShowReport(ActionEvent event) {
        clearAllTexts();
        if(isReportDurationSelected()){
            quarter = quarterComboBox.getSelectionModel().getSelectedItem();
            year = quarterlyYearComboBox.getSelectionModel().getSelectedItem();
            ReportController.getAllStoresSelectedReportByCEO(new IncomeReport(-1, year, quarter, ReportPeriodEnum.QUARTERLY));
        }else errorTextProperty.set("Please select all required fields.");
    }

    /**
     * Changes the middle Pane depends on the selected report by the user.
     */
    private void openNewPane(){
        Report selectedReport = report.get();
        IncomeQuarterlyScreenFXController incomeQuarterlyScreenFXController = new IncomeQuarterlyScreenFXController((IncomeReport) selectedReport);
        sidenavigationCEOFXController.setMainPaneCenter(incomeQuarterlyScreenFXController);
        sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports -> Show Report");
    }

    /**
     * Clear all texts
     */
    private void clearAllTexts(){
        successTextProperty.set("");
        errorTextProperty.set("");
    }

    /**
     * Validates if report duration ComboBox is selected in GUI.
     * @return boolean
     */
    private boolean isReportDurationSelected() {
        return (quarterComboBox.getSelectionModel().getSelectedItem() != null &
                quarterlyYearComboBox.getSelectionModel().getSelectedItem() != null);
    }
}
