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
 * This class defines the Order quarterly report screen
 */
public class OrderQuarterlyScreenFXController extends Pane implements Initializable {

    // Pie Chart
    @FXML
    private PieChart pieChart;

    // Labels
    @FXML
    private Label quarterLabel;
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
    public static OrderReport quarterlyReport;
    private final ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

    /**
     * Constructor
     * @param report Report to be shown on screen
     */
    public OrderQuarterlyScreenFXController (OrderReport report){
        quarterlyReport = report;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/report/orders/OrderQuarterlyScreenFX.fxml"));

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
        quarterLabel.setText(String.valueOf(quarterlyReport.getPeriod()));
        yearLabel.setText(String.valueOf(quarterlyReport.getYear()));
        storeIdLabel.setText(String.valueOf(quarterlyReport.getStoreId()));

        // Get Data
        HashMap<String, Integer> dataMap = quarterlyReport.getObject().getKey();
        Item bestSellerItem = quarterlyReport.getObject().getValue();

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
