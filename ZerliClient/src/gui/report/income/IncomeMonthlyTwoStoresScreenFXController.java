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
 * This class defines the Income Monthly report of two screens
 */
public class IncomeMonthlyTwoStoresScreenFXController extends Pane implements Initializable {

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
    private Label yearLabel1;
    @FXML
    private Label yearLabel2;
    @FXML
    private Label monthLabel1;
    @FXML
    private Label monthLabel2;
    @FXML
    private Label storeIdLabel1;
    @FXML
    private Label storeIdLabel2;

    // Variables
    public static IncomeReport incomeReport1;
    public static IncomeReport incomeReport2;


    /**
     * Constructor initialize screen and the two reports to be shown in it
     * @param report1
     * @param report2
     */
    public IncomeMonthlyTwoStoresScreenFXController(IncomeReport report1, IncomeReport report2){
        incomeReport1 = report1;
        incomeReport2 = report2;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/report/income/IncomeMonthlyTwoStoresScreenFX.fxml"));

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
        // Store 1
        monthLabel1.setText(String.valueOf(incomeReport1.getPeriod()));
        yearLabel1.setText(String.valueOf(incomeReport1.getYear()));
        storeIdLabel1.setText(String.valueOf(incomeReport1.getStoreId()));

        HashMap<String, Double> tempMap1 = incomeReport1.getObject();
        HashMap<Integer, Double> dataMap1 = new HashMap<>();

        for (Map.Entry<String, Double> entry1 : tempMap1.entrySet()) {
            Integer d1 = Integer.parseInt(entry1.getKey());
            Double a1 = entry1.getValue();
            dataMap1.put(d1, a1);
        }

        TreeMap<Integer, Double> treeMap1 = new TreeMap<>(dataMap1);
        XYChart.Series<String , Double> dataSet1 = new XYChart.Series<>();
        dataSet1.setName("Total Revenue");

        for (Map.Entry<Integer, Double> entry1 : treeMap1.entrySet()) {
            String day_of_month1 = String.valueOf(entry1.getKey());
            Double amount1 = entry1.getValue();
            dataSet1.getData().add(new XYChart.Data(day_of_month1, amount1));
        }
        barChart1.getData().add(dataSet1);

        // Store 2
        monthLabel2.setText(String.valueOf(incomeReport2.getPeriod()));
        yearLabel2.setText(String.valueOf(incomeReport2.getYear()));
        storeIdLabel2.setText(String.valueOf(incomeReport2.getStoreId()));

        HashMap<String, Double> tempMap2 = incomeReport2.getObject();
        HashMap<Integer, Double> dataMap2 = new HashMap<>();

        for (Map.Entry<String, Double> entry2 : tempMap2.entrySet()) {
            Integer d2 = Integer.parseInt(entry2.getKey());
            Double a2 = entry2.getValue();
            dataMap2.put(d2, a2);
        }

        TreeMap<Integer, Double> treeMap2 = new TreeMap<>(dataMap2);
        XYChart.Series<String , Double> dataSet2 = new XYChart.Series<>();
        dataSet2.setName("Total Revenue");

        for (Map.Entry<Integer, Double> entry2 : treeMap2.entrySet()) {
            String day_of_month2 = String.valueOf(entry2.getKey());
            Double amount2 = entry2.getValue();
            dataSet2.getData().add(new XYChart.Data(day_of_month2, amount2));
        }
        barChart2.getData().add(dataSet2);
    }
}
