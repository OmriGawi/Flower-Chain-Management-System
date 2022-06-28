package gui.company_marketing_worker;

import controllers.ItemController;
import gui.sidenavigation.SidenavigationCompanyMarketingWorkerFXController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static utils.Constants.*;

public class CompanyMarketingWorkerAddItemScreenFXController extends Pane implements Initializable {

    private static final int ALL_VALUES_CORRECT = 3 ;

    // ComboBox
    @FXML
    private ComboBox<String> colorComboBox;

    // Text
    @FXML
    private Text nameErrorText;
    @FXML
    private Text priceErrorText;
    @FXML
    private Text colorErrorText;

    @FXML
    private Text errorText;
    @FXML
    private Text successText;

    // Text Fields
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField priceTextField;

    // Buttons
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;

    // Properties
    public static StringProperty successTextProperty = new SimpleStringProperty("");
    public static StringProperty errorTextProperty = new SimpleStringProperty("");

    // Controllers
    public static ItemController itemController;

    // Variables
    private final SidenavigationCompanyMarketingWorkerFXController sidenavigationCompanyMarketingWorkerFXController;

    /**
     * Constructor
     * @param sidenavigationCompanyMarketingWorkerFXController side-navigation
     */
    public CompanyMarketingWorkerAddItemScreenFXController(SidenavigationCompanyMarketingWorkerFXController sidenavigationCompanyMarketingWorkerFXController) {
        this.sidenavigationCompanyMarketingWorkerFXController = sidenavigationCompanyMarketingWorkerFXController;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/company_marketing_worker/CompanyMarketingWorkerAddItemScreenFX.fxml"));

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
        itemController = new ItemController();

        // Initialize ComboBox with Colors
        ObservableList<String> list = FXCollections.observableArrayList(ITEMS_COLORS);
        colorComboBox.setItems(list);

        // Properties
        successTextProperty.addListener((observable, oldValue, newValue) -> {
            successText.setStyle(TEXT_VALID_STYLE);
            successText.setText(newValue);
        });
        errorTextProperty.addListener((observable, oldValue, newValue) -> {
            errorText.setStyle(TEXT_NOT_VALID_STYLE);
            errorText.setText(newValue);
        });

    }

    /**
     * This method will go to previous page by creating a new instance of CompanyMarketingWorkerTypeOfItemToAddScreenFXController
     * @param event button press
     */
    @FXML
    void onButtonPressBack(ActionEvent event) {
        CompanyMarketingWorkerTypeOfItemToAddScreenFXController companyMarketingWorkerTypeOfItemToAddScreenFXController = new CompanyMarketingWorkerTypeOfItemToAddScreenFXController(sidenavigationCompanyMarketingWorkerFXController);
        sidenavigationCompanyMarketingWorkerFXController.setMainPaneCenter(companyMarketingWorkerTypeOfItemToAddScreenFXController);
        sidenavigationCompanyMarketingWorkerFXController.setTopBarText("Login -> User Portal -> Add Item");
    }

    /**
     * This method will validate values entered from user and call an appropriate controller to create a Task
     * for creating a new single item.
     *
     * @param event button press
     */
    @FXML
    void onButtonPressSave(ActionEvent event) {
        int correctValues = 0;
        if (isComboBoxValid())
            correctValues++;
        if(isNameValid())
            correctValues++;
        if(isPriceValid())
            correctValues++;

        if(correctValues == ALL_VALUES_CORRECT){
            String name = nameTextField.getText();
            String price = String.valueOf(priceTextField.getText());
            String color = colorComboBox.getSelectionModel().getSelectedItem();
            ItemController.createNewSingleItem(name, price, color, DEFAULT_SINGLE_ITEM_ICON_PATH);
            saveButton.setDisable(true);
        }
    }

    /**
     * This method validates info entered in name text field from user.
     * @return boolean
     */
    private boolean isNameValid(){
        String name = nameTextField.getText();
        if (!name.matches("^[a-zA-Z\\s]*$") || name.isBlank()){
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

    /**
     * This method validates info entered in comboBox from user.
     * @return boolean
     */
    private boolean isComboBoxValid() {
        if(colorComboBox.getSelectionModel().isSelected(-1)){
            colorErrorText.setStyle(TEXT_NOT_VALID_STYLE);
            colorErrorText.setText("Please choose color");
            return false;
        }
        else{
            colorErrorText.setText("");
            return true;
        }
    }

}

