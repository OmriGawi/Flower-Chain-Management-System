package gui.company_marketing_worker;

import classes.Item;
import controllers.CatalogController;
import controllers.ItemController;
import gui.sidenavigation.SidenavigationCompanyMarketingWorkerFXController;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.Constants.*;

public class CompanyMarketingWorkerAddProductScreenFXController extends Pane implements Initializable {

    private static final int ALL_VALUES_CORRECT = 3 ;

    // Items TableView
    @FXML
    private TableView<Item> itemTableView;
        @FXML
        private TableColumn<Item, Integer> itemIdCol;
        @FXML
        private TableColumn<Item, String> nameCol;
        @FXML
        private TableColumn<Item, Double> priceCol;
        @FXML
        private TableColumn<Item, Integer> quantityCol;

    // Buttons
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button saveButton;

    // ComboBox
    @FXML
    private ComboBox<String> colorComboBox;

    // Text
    @FXML
    private Text nameErrorText;
    @FXML
    private Text priceErrorText;
    @FXML
    private Text colorText;
    @FXML
    private Text errorText;
    @FXML
    private Text successText;

    // Text Fields
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField priceTextField;

    // Properties
    public static StringProperty successTextProperty = new SimpleStringProperty("");
    public static StringProperty errorTextProperty = new SimpleStringProperty("");

    // Controllers
    public static ItemController itemController;
    public static CatalogController catalogController;

    // Observable List
    public static ObservableList<Item> catalog = FXCollections.observableArrayList();

    // Variables
    private final SidenavigationCompanyMarketingWorkerFXController sidenavigationCompanyMarketingWorkerFXController;
    private Item productToAdd;

    public CompanyMarketingWorkerAddProductScreenFXController(SidenavigationCompanyMarketingWorkerFXController sidenavigationCompanyMarketingWorkerFXController){
        this.sidenavigationCompanyMarketingWorkerFXController = sidenavigationCompanyMarketingWorkerFXController;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/company_marketing_worker/CompanyMarketingWorkerAddProductScreenFX.fxml"));

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
        // Initialize
        itemController = new ItemController();
        catalogController = new CatalogController();
        productToAdd = new Item();

        // Set values factory
        itemIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("priceWithoutDiscount"));
        quantityCol.setCellValueFactory(param -> new SimpleObjectProperty<>(productToAdd.getItemsMap().get(param.getValue()) == null ? 0 : productToAdd.getItemsMap().get(param.getValue())));

        // Properties
        successTextProperty.addListener((observable, oldValue, newValue) -> {
            successText.setText(newValue);
            successText.setStyle(TEXT_VALID_STYLE);
        });
        errorTextProperty.addListener((observable, oldValue, newValue) -> {
            errorText.setText(newValue);
            errorText.setStyle(TEXT_NOT_VALID_STYLE);
        });

        // Initialize ComboBox with Colors
        ObservableList<String> list = FXCollections.observableArrayList(ITEMS_COLORS);
        colorComboBox.setItems(list);

        // Set observable list into orderTableView
        itemTableView.setItems(catalog);

        // Get Items in Catalog
        CatalogController.getAllItemsForAddingNewProductItem();
    }

    @FXML
    void onButtonPressBack(ActionEvent event) {
        CompanyMarketingWorkerTypeOfItemToAddScreenFXController companyMarketingWorkerTypeOfItemToAddScreenFXController = new CompanyMarketingWorkerTypeOfItemToAddScreenFXController(sidenavigationCompanyMarketingWorkerFXController);
        sidenavigationCompanyMarketingWorkerFXController.setMainPaneCenter(companyMarketingWorkerTypeOfItemToAddScreenFXController);
        sidenavigationCompanyMarketingWorkerFXController.setTopBarText("Login -> User Portal -> Add Item");
    }

    @FXML
    void onButtonPressAdd(ActionEvent event) {
        try{
            productToAdd.getItemsMap().merge(itemTableView.getSelectionModel().getSelectedItem(), 1, Integer::sum);
            itemTableView.refresh();
            productToAdd.calPrimaryColor();
            colorText.setText(productToAdd.getColor());
        }catch (NullPointerException ignored){}
    }


    @FXML
    void onButtonPressRemove(ActionEvent event) {
        try{
            Item item = itemTableView.getSelectionModel().getSelectedItem();
            if (productToAdd.getItemsMap().containsKey(item)) {
                int quantity = productToAdd.getItemsMap().get(item) - 1;
                if (quantity == 0) {
                    productToAdd.getItemsMap().remove(item);
                } else {
                    productToAdd.getItemsMap().put(item, quantity);
                }
                itemTableView.refresh();
                productToAdd.calPrimaryColor();
                if (productToAdd.getColor() == null) {
                    colorText.setText("Auto calculated");
                } else {
                    colorText.setText(productToAdd.getColor());
                }
            }
        }catch (NullPointerException ignored){}
    }

    /**
     * This method will validate values entered from user and call an appropriate controller to create a Task
     * for creating a new product item.
     *
     * @param event button press
     */
    @FXML
    void onButtonPressSave(ActionEvent event) {
        successTextProperty.set("");
        errorTextProperty.set("");

        boolean isValid = true;
        if(!isNameValid())
            isValid = false;
        if(!isPriceValid())
            isValid = false;
        if(!isItemSelected())
            isValid = false;


        if(isValid){
            String name = nameTextField.getText();
            double price = Double.valueOf(priceTextField.getText());
            productToAdd.setName(name);
            productToAdd.setPrice(price);
            ItemController.createNewProductItem(productToAdd);
            this.setDisable(true);
        }
    }

    /**
     * This method checks if at least one item has been selected on screen
     * @return True if at least one item is selected
     */
    private boolean isItemSelected() {
        if (productToAdd.getItemsMap().isEmpty()) {
            errorText.setStyle(TEXT_NOT_VALID_STYLE);
            errorText.setText("Please select at least 1 item");
            errorText.setVisible(true);
            return false;
        } else {
            errorText.setVisible(false);
        }
        return true;
    }

    /**
     * This method validates info entered in name text field from user.
     * @return boolean
     */
    private boolean isNameValid(){
        String name = nameTextField.getText();
        if (name.isBlank() || !name.matches("^[a-zA-Z\\s]*$")){
            nameErrorText.setText("Letters only");
            nameTextField.setStyle(TEXT_FIELD_NOT_VALID_STYLE);
            nameErrorText.setStyle(TEXT_NOT_VALID_STYLE);
            return false;
        }else{
            nameErrorText.setText("");
            nameTextField.setStyle(TEXT_FIELD_VALID_STYLE);
            return true;
        }
    }

    /**
     * This method validates info entered in price text field from user.
     * @return boolean
     */
    private boolean isPriceValid() {
        try {
            double price = Double.parseDouble(priceTextField.getText());
            if(price < 1 || price > 99999999){
                priceErrorText.setText("Number between 1-99999999");
                priceErrorText.setStyle(TEXT_NOT_VALID_STYLE);
                priceTextField.setStyle(TEXT_FIELD_NOT_VALID_STYLE);
                return false;
            }
        }catch(Exception ignored) {
            priceErrorText.setText("Numbers only");
            priceErrorText.setStyle(TEXT_NOT_VALID_STYLE);
            priceTextField.setStyle(TEXT_FIELD_NOT_VALID_STYLE);
            return false;
        }
        priceErrorText.setText("");
        priceTextField.setStyle(TEXT_FIELD_VALID_STYLE);
        return true;
    }
}
