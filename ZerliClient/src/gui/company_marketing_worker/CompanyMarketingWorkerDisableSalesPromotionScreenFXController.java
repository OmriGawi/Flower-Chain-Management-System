package gui.company_marketing_worker;

import classes.Item;
import controllers.CatalogController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.Constants.*;

public class CompanyMarketingWorkerDisableSalesPromotionScreenFXController extends Pane implements Initializable {

    // Items Table
    @FXML
    private TableView<Item> itemsTableView;
        @FXML
        private TableColumn<Item, Integer> itemIdCol;
        @FXML
        private TableColumn<Item, String> nameCol;
        @FXML
        private TableColumn<Item, Double> priceCol;
        @FXML
        private TableColumn<Item, Double> priceAfterDiscountCol;

    // Labels
    @FXML
    private Label idLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label priceAfterDiscountLabel;

    // Buttons
    @FXML
    private Button removeButton;

    // Text
    @FXML
    private Text successText;
    @FXML
    private Text errorText;

    // Properties
    public static StringProperty successTextProperty = new SimpleStringProperty("");
    public static StringProperty errorTextProperty = new SimpleStringProperty("");

    // Controllers
    public static CatalogController catalogController;

    // Observable List
    public static ObservableList<Item> sales = FXCollections.observableArrayList();

    // Variables
    public static Item itemToRemoveFromSales;

    /**
     * Constructor
     */
    public CompanyMarketingWorkerDisableSalesPromotionScreenFXController(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/company_marketing_worker/CompanyMarketingWorkerDisableSalesPromotionScreenFX.fxml"));

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
        // Initialize Controllers
        catalogController = new CatalogController();

        // Set values factory
        itemIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("priceWithoutDiscount"));
        priceAfterDiscountCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Properties
        successTextProperty.addListener((observable, oldValue, newValue) -> {
            successText.setText(newValue);
            successText.setStyle(TEXT_VALID_STYLE);
        });
        errorTextProperty.addListener((observable, oldValue, newValue) -> {
            errorText.setText(newValue);
            errorText.setStyle(TEXT_NOT_VALID_STYLE);
        });

        // Set observable list into orderTableView
        itemsTableView.setItems(sales);

        // Get all items in sales
        CatalogController.getItemsInSales_CMW();
    }

    /**
     * This method represent a click on Remove Button in GUI. <p>
     * @param event button press
     */
    @FXML
    void onButtonPressRemove(ActionEvent event) {
        idLabel.setText("");
        nameLabel.setText("");
        priceLabel.setText("");
        priceAfterDiscountLabel.setText("");
        removeButton.setDisable(true);

        CatalogController.removeItemFromSales_CMW(itemToRemoveFromSales, "0");
    }

    /**
     * This method will put selected item info in appropriate labels
     * @param event TableRow click
     */
    @FXML
    void onMouseClickTableRow(MouseEvent event) {
        try{
            // Get data from Table row into text fields
            idLabel.setText(String.valueOf(itemsTableView.getSelectionModel().getSelectedItem().getId()));
            nameLabel.setText(itemsTableView.getSelectionModel().getSelectedItem().getName());
            priceLabel.setText(String.valueOf(itemsTableView.getSelectionModel().getSelectedItem().getPriceWithoutDiscount()));
            priceAfterDiscountLabel.setText(String.valueOf(itemsTableView.getSelectionModel().getSelectedItem().getPrice()));
            itemToRemoveFromSales = itemsTableView.getSelectionModel().getSelectedItem();

            removeButton.setDisable(false);
            successTextProperty.setValue("");
            errorTextProperty.setValue("");
        }catch (Exception ignored){
        }
    }
}

