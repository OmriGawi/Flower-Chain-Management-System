package gui.report.complaints;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import reports.ComplaintReport;
import reports.IncomeReport;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
/**
 * This class defines the complaints Two stores Quarterly reports screen
 */
public class ComplaintQuarterlyTwoStoresScreenFXController extends Pane implements Initializable {
    // Bar Chart
    @FXML
    private BarChart<String, Integer> barChart1;
    @FXML
    private BarChart<String, Integer> barChart2;

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
    public static ComplaintReport complaintReport1;
    public static ComplaintReport complaintReport2;


    /**
     * Constructor to show both reports on screen
     * @param report1 report1
     * @param report2 report2
     */
    public ComplaintQuarterlyTwoStoresScreenFXController(ComplaintReport report1, ComplaintReport report2){
        complaintReport1 = report1;
        complaintReport2 = report2;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/report/complaints/ComplaintQuarterlyTwoStoresScreenFX.fxml"));

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
        quarterLabel1.setText(String.valueOf(complaintReport1.getPeriod()));
        yearLabel1.setText(String.valueOf(complaintReport1.getYear()));
        storeIdLabel1.setText(String.valueOf(complaintReport1.getStoreId()));

        HashMap<String, Integer> tempMap1 = complaintReport1.getObject();
        HashMap<Integer, Integer> dataMap1 = new HashMap<>();

        for (Map.Entry<String, Integer> entry1 : tempMap1.entrySet()){
            Integer d1 = Integer.parseInt(entry1.getKey());
            Integer a1 = entry1.getValue();
            dataMap1.put(d1,a1);
        }

        TreeMap<Integer, Integer> treeMap1 = new TreeMap<>(dataMap1);
        XYChart.Series<String , Integer> dataSet1 = new XYChart.Series<>();

        for (Map.Entry<Integer, Integer> entry1 : treeMap1.entrySet()) {
            String month1 = String.valueOf(entry1.getKey());
            Integer amount1 = entry1.getValue();
            int nextMonth1 = entry1.getKey() + 1;
            if(nextMonth1 != 13){
                dataSet1.getData().add(new XYChart.Data(month1 + " - " + (entry1.getKey()+1), amount1));
            }
            else dataSet1.getData().add(new XYChart.Data(month1, amount1));
        }
        barChart1.getData().add(dataSet1);


        // Store 2
        quarterLabel2.setText(String.valueOf(complaintReport2.getPeriod()));
        yearLabel2.setText(String.valueOf(complaintReport2.getYear()));
        storeIdLabel2.setText(String.valueOf(complaintReport2.getStoreId()));

        HashMap<String, Integer> tempMap2 = complaintReport2.getObject();
        HashMap<Integer, Integer> dataMap2 = new HashMap<>();

        for (Map.Entry<String, Integer> entry2 : tempMap2.entrySet()){
            Integer d2 = Integer.parseInt(entry2.getKey());
            Integer a2 = entry2.getValue();
            dataMap2.put(d2,a2);
        }

        TreeMap<Integer, Integer> treeMap2 = new TreeMap<>(dataMap2);
        XYChart.Series<String , Integer> dataSet2 = new XYChart.Series<>();

        for (Map.Entry<Integer, Integer> entry2 : treeMap2.entrySet()) {
            String month2 = String.valueOf(entry2.getKey());
            Integer amount2 = entry2.getValue();
            int nextMonth2 = entry2.getKey() + 1;
            if(nextMonth2 != 13){
                dataSet2.getData().add(new XYChart.Data(month2 + " - " + (entry2.getKey()+1), amount2));
            }
            else dataSet2.getData().add(new XYChart.Data(month2, amount2));
        }
        barChart2.getData().add(dataSet2);
    }
}
