package gui.company_marketing_worker;

import gui.sidenavigation.SidenavigationCompanyMarketingWorkerFXController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CompanyMarketingWorkerTypeOfItemToAddScreenFXController extends Pane implements Initializable {

    // Buttons
    @FXML
    private Button addItemButton;
    @FXML
    private Button addProductButton;

    // Variables
    SidenavigationCompanyMarketingWorkerFXController sidenavigationCompanyMarketingWorkerFXController;


    /**
     * Constructor
     * @param sidenavigationCompanyMarketingWorkerFXController side navigation
     */
    public CompanyMarketingWorkerTypeOfItemToAddScreenFXController(SidenavigationCompanyMarketingWorkerFXController sidenavigationCompanyMarketingWorkerFXController){
        this.sidenavigationCompanyMarketingWorkerFXController = sidenavigationCompanyMarketingWorkerFXController;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/company_marketing_worker/CompanyMarketingWorkerTypeOfItemToAddScreenFX.fxml"));

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

    }

    /**
     * Move to the add item screen once pressed 'Single Item' button
     * @param event Button Press on 'Single Item'
     */
    @FXML
    void onButtonPressAddItem(ActionEvent event) {
        CompanyMarketingWorkerAddItemScreenFXController companyMarketingWorkerAddItemScreenFXController = new CompanyMarketingWorkerAddItemScreenFXController(sidenavigationCompanyMarketingWorkerFXController);
        sidenavigationCompanyMarketingWorkerFXController.setMainPaneCenter(companyMarketingWorkerAddItemScreenFXController);
        sidenavigationCompanyMarketingWorkerFXController.setTopBarText("Login -> User Portal -> Add Item -> Item");

    }

    /**
     * Move to the add product screen once pressed 'Product' button
     * @param event Button Press on 'Product'
     */
    @FXML
    void onButtonPressAddProduct(ActionEvent event) {
        CompanyMarketingWorkerAddProductScreenFXController companyMarketingWorkerAddProductScreenFXController = new CompanyMarketingWorkerAddProductScreenFXController(sidenavigationCompanyMarketingWorkerFXController);
        sidenavigationCompanyMarketingWorkerFXController.setMainPaneCenter(companyMarketingWorkerAddProductScreenFXController);
        sidenavigationCompanyMarketingWorkerFXController.setTopBarText("Login -> User Portal -> Add Item -> Product");
    }
}
