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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.Constants.*;

public class CompanyMarketingWorkerEnableSalesPromotionScreenFXController extends Pane implements Initializable {

    // Items table
    @FXML
    private TableView<Item> itemsTableView;
        @FXML
        private TableColumn<Item, Integer> itemIdCol;
        @FXML
        private TableColumn<Item, String> nameCol;
        @FXML
        private TableColumn<Item, Double> priceCol;

    // Text
    @FXML
    private Text discountErrorText;
    @FXML
    private Text successText;
    @FXML
    private Text errorText;

    // Labels
    @FXML
    private Label idLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label priceAfterDiscountLabel;
    @FXML
    private Label priceLabel;

    // Text Fields
    @FXML
    private TextField discountTextField;

    // Buttons
    @FXML
    private Button saveButton;
    @FXML
    private Button refreshTableButton;

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
    public CompanyMarketingWorkerEnableSalesPromotionScreenFXController(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/company_marketing_worker/CompanyMarketingWorkerEnableSalesPromotionScreenFX.fxml"));

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
        CatalogController.getItemsInCatalog_CMW();
    }


    /**
     * Save the sales promotion for an item once pressed the Save button in the GUI
     * @param event Button Press on the 'Save' button
     */
    @FXML
    void onButtonPressSave(ActionEvent event) {
        // case discount price text field is empty
        if(discountTextField.getText().isEmpty()){
            successText.setText("");
            errorText.setText("");
            discountErrorText.setVisible(true);
            discountTextField.setStyle(TEXT_FIELD_NOT_VALID_STYLE);
            discountErrorText.setText("Please Enter % Discount");
        }
        // case price value is not valid
        else if(!isValuesValid()) {
            successText.setText("");
            errorText.setText("");
            discountErrorText.setVisible(true);
            discountTextField.setStyle(TEXT_FIELD_NOT_VALID_STYLE);
            discountErrorText.setText("Discount between 1-99");
        }
        else{
            discountTextField.setStyle(TEXT_FIELD_VALID_STYLE);
            discountErrorText.setVisible(false);
            String newDiscount = discountTextField.getText();
            String itemId = idLabel.getText();
            CatalogController.setItemDiscount_CMW(itemId, newDiscount);
        }
    }

    /**
     * This method validate the discount entered by user in GUI.<p>
     * @return return true if discount is 1-99 <p>
     *     return false if discount is not integer or not in range.
     */
    private boolean isValuesValid(){
        try{
            int newDiscount = Integer.parseInt(discountTextField.getText());
            if (newDiscount < 1 || newDiscount > 99)
                return false;
        }catch(Exception ignored){
            return false;
        }
        return true;
    }

    /**
     * This function will refresh Items table and clear all labels and text fields.
     * @param event Button press
     */
    @FXML
    void onButtonPressRefreshTable(ActionEvent event) {
        idLabel.setText("");
        nameLabel.setText("");
        priceLabel.setText("");
        priceAfterDiscountLabel.setText("");
        discountTextField.clear();
        discountTextField.setStyle(TEXT_FIELD_VALID_STYLE);
        discountErrorText.setText("");
        successText.setText("");
        errorText.setText("");
        saveButton.setDisable(true);
        CatalogController.getItemsInCatalog_CMW();
    }


    /**
     * This method will put selected item info in appropriate text fields
     * @param event TableRow click
     */
    @FXML
    void onMouseClickTableRow(MouseEvent event) {
        try{
            // Get data from Table row into text fields
            idLabel.setText(String.valueOf(itemsTableView.getSelectionModel().getSelectedItem().getId()));
            nameLabel.setText(itemsTableView.getSelectionModel().getSelectedItem().getName());
            priceLabel.setText(String.valueOf(itemsTableView.getSelectionModel().getSelectedItem().getPriceWithoutDiscount()));
            priceAfterDiscountLabel.setText(priceLabel.getText());

            discountTextField.clear();
            discountTextField.setStyle(TEXT_FIELD_VALID_STYLE);
            discountErrorText.setVisible(false);
            saveButton.setDisable(false);
            successTextProperty.setValue("");
            errorTextProperty.setValue("");
        }catch (Exception ignored){
        }
    }

    /**
     * Defines a function to be called when this Node or its child Node has input focus and a key has been typed.<p>
     * The function is called only if the event hasn't been already consumed during its capturing or bubbling phase.<p>
     * It will present the item price after a discount has been entered in GUI.
     * @param event key entered
     */
    @FXML
    void onTextChange(KeyEvent event) {
        try{
            priceAfterDiscountLabel.setText(String.valueOf(itemsTableView.getSelectionModel().getSelectedItem().getPriceWithoutDiscount()));
            String discountStr = discountTextField.getText();
            double discount = Integer.parseInt(discountStr);
            double price = Double.parseDouble(priceLabel.getText());
            priceAfterDiscountLabel.setText(String.valueOf(price * (1 - discount/100)));

            if(discountStr.isEmpty())
                priceAfterDiscountLabel.setText(String.valueOf(itemsTableView.getSelectionModel().getSelectedItem().getPriceWithoutDiscount()));
        }catch (Exception ignored){
        }
    }
}
