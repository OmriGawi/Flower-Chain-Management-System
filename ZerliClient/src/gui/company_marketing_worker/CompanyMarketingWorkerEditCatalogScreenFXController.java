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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.Constants.*;

public class CompanyMarketingWorkerEditCatalogScreenFXController extends Pane implements Initializable {

    // Items Table
    @FXML
    private TableView<Item> itemTableView;
        @FXML
        private TableColumn<Item, Integer> itemIdCol;
        @FXML
        private TableColumn<Item, String> nameCol;
        @FXML
        private TableColumn<Item, Double> priceCol;

    // Text
    @FXML
    private Text priceErrorText;
    @FXML
    private Text successText;
    @FXML
    private Text errorText;

    // Text Fields
    @FXML
    private TextField itemIdTextField;
    @FXML
    private TextField itemNameTextField;
    @FXML
    private TextField itemPriceTextField;

    // Buttons
    @FXML
    private Button refreshButton;
    @FXML
    private Button saveButton;

    // Properties
    public static StringProperty successTextProperty = new SimpleStringProperty("");
    public static StringProperty errorTextProperty = new SimpleStringProperty("");

    // Controllers
    public static CatalogController catalogController;

    // Observable List
    public static ObservableList<Item> catalog = FXCollections.observableArrayList();

    /**
     * Constructor
     */
    public CompanyMarketingWorkerEditCatalogScreenFXController(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/company_marketing_worker/CompanyMarketingWorkerEditCatalogScreenFX.fxml"));

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
        itemTableView.setItems(catalog);

        // Get All item data from Database
        CatalogController.getAllItems_CMW();
    }

    /**
     * This method will execute when "Refresh Table" button is pressed
     * @param event Button press
     */
    @FXML
    void onButtonPressRefreshTable(ActionEvent event) {
        // Reset values to start
        itemIdTextField.clear();
        itemNameTextField.clear();
        itemPriceTextField.clear();
        saveButton.setDisable(true);
        successText.setText("");
        errorText.setText("");
        priceErrorText.setText("");
        itemPriceTextField.setStyle(TEXT_FIELD_VALID_STYLE);

        // Get All items from Database
        CatalogController.getAllItems_CMW();
    }

    /**
     * This method will execute when "Save" button is pressed
     * @param event Button press
     */
    @FXML
    void onButtonPressSave(ActionEvent event) {
        // case price text field is empty
        if(itemPriceTextField.getText().isEmpty()){
            priceErrorText.setVisible(true);
            priceErrorText.setStyle(TEXT_NOT_VALID_STYLE);
            priceErrorText.setText("Please enter price");
            itemPriceTextField.setStyle(TEXT_FIELD_NOT_VALID_STYLE);
        }
        // case price value is not valid
        else if(!isValuesValid()){
            priceErrorText.setVisible(true);
            priceErrorText.setStyle(TEXT_NOT_VALID_STYLE);
            priceErrorText.setText("Price between 1-99999999");
            itemPriceTextField.setStyle(TEXT_FIELD_NOT_VALID_STYLE);
        }
        else{
            itemPriceTextField.setStyle(TEXT_FIELD_VALID_STYLE);
            priceErrorText.setVisible(false);
            String newPrice = itemPriceTextField.getText();
            String itemId = itemIdTextField.getText();
            CatalogController.updateItemPrice_CMW(itemId, newPrice);
        }
    }

    /**
     * This method will put selected item info in appropriate text fields
     * @param event TableRow click
     */
    @FXML
    void onMouseClickTableRow(MouseEvent event) {
        try{
            // Get data from Table row into text fields
            itemIdTextField.setText(String.valueOf(itemTableView.getSelectionModel().getSelectedItem().getId()));
            itemNameTextField.setText(itemTableView.getSelectionModel().getSelectedItem().getName());
            itemPriceTextField.setText(String.valueOf(itemTableView.getSelectionModel().getSelectedItem().getPriceWithoutDiscount()));

            itemPriceTextField.setStyle(TEXT_FIELD_VALID_STYLE);
            priceErrorText.setVisible(false);
            saveButton.setDisable(false);
            successTextProperty.setValue("");
            errorTextProperty.setValue("");
        }catch (Exception ignored){
        }
    }

    /**
     * This method validate new price value
     * @return boolean
     */
    private boolean isValuesValid(){
       try{
           double newPrice = Double.parseDouble(itemPriceTextField.getText());
           if (newPrice < 1 || newPrice > 99999999)
               return false;
       }catch(Exception ignored){
            return false;
       }
       return true;
    }

}