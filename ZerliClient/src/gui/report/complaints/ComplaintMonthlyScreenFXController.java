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
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * This screen defines the Complaint monthly report screen
 */
public class ComplaintMonthlyScreenFXController extends Pane implements Initializable {

    // BarChart
    @FXML
    private BarChart<String, Integer> barChart;

    // Labels
    @FXML
    private Label monthLabel;
    @FXML
    private Label storeIdLabel;
    @FXML
    private Label yearLabel;

    // x,y Axis
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    // Variables
    public static ComplaintReport complaintReport;

    /**
     * Constructor - initialize screen with the report of that month to show on screen chart
     * @param report
     */
    public ComplaintMonthlyScreenFXController(ComplaintReport report){
        complaintReport = report;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/report/complaints/ComplaintMonthlyScreenFX.fxml"));

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
        monthLabel.setText(String.valueOf(complaintReport.getPeriod()));
        yearLabel.setText(String.valueOf(complaintReport.getYear()));
        storeIdLabel.setText(String.valueOf(complaintReport.getStoreId()));
        barChart.setCategoryGap(0);

        HashMap<String, Integer> tempMap = complaintReport.getObject();
        HashMap<Integer, Integer> dataMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : tempMap.entrySet()){
            Integer d = Integer.parseInt(entry.getKey());
            Integer a = entry.getValue();
            dataMap.put(d,a);
        }

        TreeMap<Integer, Integer> treeMap = new TreeMap<>(dataMap);
        XYChart.Series<String , Integer> dataSet = new XYChart.Series<>();
        //dataSet.setName("Day in month");

        for (Map.Entry<Integer, Integer> entry : treeMap.entrySet()) {
            String day_of_month = String.valueOf(entry.getKey());
            Integer amount = entry.getValue();
            dataSet.getData().add(new XYChart.Data(day_of_month, amount));
        }
        barChart.getData().add(dataSet);
    }
}
