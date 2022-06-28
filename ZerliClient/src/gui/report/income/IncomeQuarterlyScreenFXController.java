package gui.report.income;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import reports.IncomeReport;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
/**
 * This class defines the Income Quarterly reports screen
 */
public class IncomeQuarterlyScreenFXController extends Pane implements Initializable {

    // Bar Chart
    @FXML
    private BarChart<String, Double> barChart;

    // Labels
    @FXML
    private Label quarterLabel;
    @FXML
    private Label yearLabel;

    @FXML
    private Label storeIdLabel;

    // x,y Axis
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    // Variables
    public static IncomeReport quarterlyReport;

    /**
     * Constructor
     * @param report Report to be shown on screen
     */
    public IncomeQuarterlyScreenFXController(IncomeReport report){
        quarterlyReport = report;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/report/income/IncomeQuarterlyScreenFX.fxml"));

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
        quarterLabel.setText(String.valueOf(quarterlyReport.getPeriod()));
        yearLabel.setText(String.valueOf(quarterlyReport.getYear()));

        // If the report is for specific store
        if(quarterlyReport.getStoreId() != -1){
            xAxis.setLabel("Month");
            storeIdLabel.setText(String.valueOf(quarterlyReport.getStoreId()));
        }
        // If the report is for all stores
        else{
            xAxis.setLabel("Store");
            storeIdLabel.setText("All Stores");
        }

        HashMap<String, Double> dataMap = quarterlyReport.getObject();
        TreeMap<String, Double> treeMap = new TreeMap<>(dataMap);
        XYChart.Series<String , Double> dataSet = new XYChart.Series<>();
        dataSet.setName("Total Revenue");

        for (Map.Entry<String, Double> entry : treeMap.entrySet()) {
            String revenue = String.valueOf(entry.getKey());
            Double day = entry.getValue();
            dataSet.getData().add(new XYChart.Data(revenue, day));
        }
        barChart.getData().add(dataSet);
    }
}
