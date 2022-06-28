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
 * This class defines the Income Monthlu report screen
 */
public class IncomeMonthlyScreenFXController extends Pane implements Initializable {

    // Bar Chart
    @FXML
    private BarChart<String, Double> barChart;

    // x,y Axis
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    // Labels
    @FXML
    private Label yearLabel;
    @FXML
    private Label monthLabel;
    @FXML
    private Label storeIdLabel;


    // Variables
    public static IncomeReport incomeReport;

    /**
     * Constructor to initialize screen and the report to be shown in it
     * @param report Report to be shown on screen
     */
    public IncomeMonthlyScreenFXController(IncomeReport report){
        incomeReport = report;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/report/income/IncomeMonthlyScreenFX.fxml"));

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
        monthLabel.setText(String.valueOf(incomeReport.getPeriod()));
        yearLabel.setText(String.valueOf(incomeReport.getYear()));
        storeIdLabel.setText(String.valueOf(incomeReport.getStoreId()));
        barChart.setCategoryGap(0);

        HashMap<String, Double> tempMap = incomeReport.getObject();
        HashMap<Integer, Double> dataMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : tempMap.entrySet()) {
            Integer d = Integer.parseInt(entry.getKey());
            Double a = entry.getValue();
            dataMap.put(d, a);
        }

        TreeMap<Integer, Double> treeMap = new TreeMap<>(dataMap);
        XYChart.Series<String , Double> dataSet = new XYChart.Series<>();
        dataSet.setName("Total Revenue");

        for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
            String day_of_month = String.valueOf(entry.getKey());
            Double amount = entry.getValue();
            dataSet.getData().add(new XYChart.Data(day_of_month, amount));
        }
        barChart.getData().add(dataSet);
    }
}
