package gui.report.orders;

import classes.Item;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import reports.OrderReport;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
/**
 * This class defines the Order's Monthly report screen for two stores
 */
public class OrderMonthlyTwoStoresScreenFXController extends Pane implements Initializable {

    // Pie Chart
    @FXML
    private PieChart pieChart1;
    @FXML
    private PieChart pieChart2;

    // Image
    @FXML
    private ImageView itemImage1;
    @FXML
    private ImageView itemImage2;

    // Labels
    @FXML
    private Label itemNameLabel1;
    @FXML
    private Label itemNameLabel2;
    @FXML
    private Label itemPriceLabel1;
    @FXML
    private Label itemPriceLabel2;
    @FXML
    private Label monthLabel1;
    @FXML
    private Label monthLabel2;
    @FXML
    private Label storeIdLabel1;
    @FXML
    private Label storeIdLabel2;
    @FXML
    private Label yearLabel2;
    @FXML
    private Label yearLabel1;

    // Variables
    public static OrderReport monthlyReport1;
    public static OrderReport monthlyReport2;
    private final ObservableList<PieChart.Data> pieChartData1 = FXCollections.observableArrayList();
    private final ObservableList<PieChart.Data> pieChartData2 = FXCollections.observableArrayList();

    /**
     * Constructor
     * @param report1 report
     * @param report2 report
     */
    public OrderMonthlyTwoStoresScreenFXController(OrderReport report1, OrderReport report2){
        monthlyReport1 = report1;
        monthlyReport2 = report2;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/report/orders/OrderMonthlyTwoStoresScreenFX.fxml"));

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
        /*    Store 1    */
        // Set Labels
        monthLabel1.setText(String.valueOf(monthlyReport1.getPeriod()));
        yearLabel1.setText(String.valueOf(monthlyReport1.getYear()));
        storeIdLabel1.setText(String.valueOf(monthlyReport1.getStoreId()));

        // Get Data
        HashMap<String, Integer> dataMap1 = monthlyReport1.getObject().getKey();
        Item bestSellerItem1 = monthlyReport1.getObject().getValue();

        // Add data to list
        for (Map.Entry<String, Integer> entry : dataMap1.entrySet()) {
            String itemType1 = String.valueOf(entry.getKey());
            Integer value1 = entry.getValue();
            pieChartData1.add(new PieChart.Data(itemType1, value1));
        }
        pieChartData1.forEach(data -> data.nameProperty().bind(Bindings.concat(data.getName(), " amount: ", data.pieValueProperty())));

        // Put all data slices into pieChart
        pieChart1.getData().addAll(pieChartData1);

        // Put Item data in gui
        itemNameLabel1.setText(bestSellerItem1.getName());
        itemPriceLabel1.setText(bestSellerItem1.getPriceWithoutDiscount() + " ILS");
        Image image1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream(bestSellerItem1.getImageSrc())));
        itemImage1.setImage(image1);


        /*    Store 2    */
        // Set Labels
        monthLabel2.setText(String.valueOf(monthlyReport2.getPeriod()));
        yearLabel2.setText(String.valueOf(monthlyReport2.getYear()));
        storeIdLabel2.setText(String.valueOf(monthlyReport2.getStoreId()));

        // Get Data
        HashMap<String, Integer> dataMap2 = monthlyReport2.getObject().getKey();
        Item bestSellerItem2 = monthlyReport2.getObject().getValue();

        // Add data to list
        for (Map.Entry<String, Integer> entry : dataMap2.entrySet()) {
            String itemType2 = String.valueOf(entry.getKey());
            Integer value2 = entry.getValue();
            pieChartData2.add(new PieChart.Data(itemType2, value2));
        }
        pieChartData2.forEach(data -> data.nameProperty().bind(Bindings.concat(data.getName(), " amount: ", data.pieValueProperty())));

        // Put all data slices into pieChart
        pieChart2.getData().addAll(pieChartData2);

        // Put Item data in gui
        itemNameLabel2.setText(bestSellerItem1.getName());
        itemPriceLabel2.setText(bestSellerItem1.getPriceWithoutDiscount() + " ILS");
        Image image2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream(bestSellerItem2.getImageSrc())));
        itemImage2.setImage(image2);
    }
}
