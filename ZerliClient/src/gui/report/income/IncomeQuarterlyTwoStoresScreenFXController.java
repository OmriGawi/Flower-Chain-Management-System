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
 * This class defines the Income Quarterly report screen for two reports
 */
public class IncomeQuarterlyTwoStoresScreenFXController extends Pane implements Initializable {

    // Bar Chart
    @FXML
    private BarChart<String, Double> barChart1;
    @FXML
    private BarChart<String, Double> barChart2;

    // x,y Axis
    @FXML
    private CategoryAxis xAxis1;
    @FXML
    private CategoryAxis xAxis2;
    @FXML
    private NumberAxis yAxis1;
    @FXML
    private NumberAxis yAxis2;

    // Labels
    @FXML
    private Label quarterLabel1;
    @FXML
    private Label quarterLabel2;
    @FXML
    private Label storeIdLabel1;
    @FXML
    private Label storeIdLabel2;
    @FXML
    private Label yearLabel1;
    @FXML
    private Label yearLabel2;

    // Variables
    public static IncomeReport quarterlyReport1;
    public static IncomeReport quarterlyReport2;


    /**
     * Constructor
     * @param report1 report1
     * @param report2 report2
     */
    public IncomeQuarterlyTwoStoresScreenFXController(IncomeReport report1, IncomeReport report2){
        quarterlyReport1 = report1;
        quarterlyReport2 = report2;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/report/income/IncomeQuarterlyTwoStoresScreenFX.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setControllerFactory(callback -> this);

        try {
            fxmlLoader.load();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Store 1
        quarterLabel1.setText(String.valueOf(quarterlyReport1.getPeriod()));
        yearLabel1.setText(String.valueOf(quarterlyReport1.getYear()));
        storeIdLabel1.setText(String.valueOf(quarterlyReport1.getStoreId()));

        HashMap<String, Double> dataMap1 = quarterlyReport1.getObject();
        TreeMap<String, Double> treeMap1 = new TreeMap<>(dataMap1);
        XYChart.Series<String , Double> dataSet1 = new XYChart.Series<>();

        for (Map.Entry<String, Double> entry : treeMap1.entrySet()) {
            String revenue = String.valueOf(entry.getKey());
            Double day = entry.getValue();
            dataSet1.getData().add(new XYChart.Data(revenue, day));
        }
        barChart1.getData().add(dataSet1);


        // Store 2
        quarterLabel2.setText(String.valueOf(quarterlyReport2.getPeriod()));
        yearLabel2.setText(String.valueOf(quarterlyReport2.getYear()));
        storeIdLabel2.setText(String.valueOf(quarterlyReport2.getStoreId()));

        HashMap<String, Double> dataMap2 = quarterlyReport2.getObject();
        TreeMap<String, Double> treeMap2 = new TreeMap<>(dataMap2);
        XYChart.Series<String , Double> dataSet2 = new XYChart.Series<>();

        for (Map.Entry<String, Double> entry : treeMap2.entrySet()) {
            String revenue = String.valueOf(entry.getKey());
            Double day = entry.getValue();
            dataSet2.getData().add(new XYChart.Data(revenue, day));
        }
        barChart2.getData().add(dataSet2);
    }
}
