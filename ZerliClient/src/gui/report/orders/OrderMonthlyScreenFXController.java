package gui.report.orders;

import classes.Item;
import interfaces.IItemCatalog;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import reports.IncomeReport;
import reports.OrderReport;

import javax.naming.Binding;
import java.io.IOException;
import java.net.URL;
import java.util.*;
/**
 * This class defines the Orders Monthly report screen
 */
public class OrderMonthlyScreenFXController extends Pane implements Initializable {

    // Pie Chart
    @FXML
    private PieChart pieChart;

    // Labels
    @FXML
    private Label monthLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label itemNameLabel;
    @FXML
    private Label itemPriceLabel;
    @FXML
    private Label storeIdLabel;

    // Image
    @FXML
    private ImageView itemImage;

    // Variables
    public static OrderReport monthlyReport;
    private final ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();


    /**
     * Constructor
     * @param report
     */
    public OrderMonthlyScreenFXController (OrderReport report){
        monthlyReport = report;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/report/orders/OrderMonthlyScreenFX.fxml"));

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
        // Set Labels
        monthLabel.setText(String.valueOf(monthlyReport.getPeriod()));
        yearLabel.setText(String.valueOf(monthlyReport.getYear()));
        storeIdLabel.setText(String.valueOf(monthlyReport.getStoreId()));

        // Get Data
        HashMap<String, Integer> dataMap = monthlyReport.getObject().getKey();
        Item bestSellerItem = monthlyReport.getObject().getValue();

        // Add data to list
        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            String itemType = String.valueOf(entry.getKey());
            Integer value = entry.getValue();
            pieChartData.add(new PieChart.Data(itemType, value));
        }
        pieChartData.forEach(data -> data.nameProperty().bind(Bindings.concat(data.getName(), " amount: ", data.pieValueProperty())));

        // Put all data slices into pieChart
        pieChart.getData().addAll(pieChartData);

        // Put Item data in gui
        itemNameLabel.setText(bestSellerItem.getName());
        itemPriceLabel.setText(bestSellerItem.getPriceWithoutDiscount() + " ILS");
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(bestSellerItem.getImageSrc())));
        itemImage.setImage(image);
    }
}
