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

public class CompanyMarketingWorkerRemoveItemScreenFXController extends Pane implements Initializable {

    // Items Table
    @FXML
    private TableView<Item> itemsTableView;
        @FXML
        private TableColumn<Item, Integer> itemIdCol;
        @FXML
        private TableColumn<Item, String> nameCol;
        @FXML
        private TableColumn<Item, Double> priceCol;

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

    // Variables
    private Item itemToRemove;

    // Observable List
    public static ObservableList<Item> catalog = FXCollections.observableArrayList();


    /**
     * Constructor to initialize the screen
     */
    public CompanyMarketingWorkerRemoveItemScreenFXController(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/company_marketing_worker/CompanyMarketingWorkerRemoveItemScreenFX.fxml"));

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
        itemsTableView.setItems(catalog);

        // Get all items in catalog
        CatalogController.getAllItemsToRemove_CMW();
    }

    /**
     * Remove an Item from the catalog once after pressing the 'Remove' button
     * @param event Button press
     */
    @FXML
    void onButtonPressRemove(ActionEvent event) {
        CatalogController.removeItem_CMW(itemToRemove);
        removeButton.setDisable(true);
    }

    /**
     * Once pressed a table row on screen, fetch the Item's data and enable the 'Remove' button
     * @param event Click a table row on screen
     */
    @FXML
    void onMouseClickTableRow(MouseEvent event) {
        try{
            itemToRemove = itemsTableView.getSelectionModel().getSelectedItem();
            if(itemToRemove != null){
                removeButton.setDisable(false);
            }

            if(itemsTableView.getItems().isEmpty())
                removeButton.setDisable(true);

            successTextProperty.setValue("");
            errorTextProperty.setValue("");
        }catch (Exception ignored){
        }
    }
}

