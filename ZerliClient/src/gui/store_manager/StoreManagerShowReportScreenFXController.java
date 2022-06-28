package gui.store_manager;

import gui.sidenavigation.SidenavigationStoreManagerFXController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
/**
 * This class defines the Store Manager show a Report screen
 */
public class StoreManagerShowReportScreenFXController extends Pane implements Initializable {

    // Buttons
    @FXML
    private Button backButton;

    // Variables
    private final SidenavigationStoreManagerFXController sidenavigationStoreManagerFXController;

    /**
     * Constructor
     * @param sidenavigationStoreManagerFXController
     */
    public StoreManagerShowReportScreenFXController(SidenavigationStoreManagerFXController sidenavigationStoreManagerFXController){
        this.sidenavigationStoreManagerFXController = sidenavigationStoreManagerFXController;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/store_manager/StoreManagerShowReportScreenFX.fxml"));

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
     * Show the 'View all reports' screen to the manager
     * @param event Button Press
     */
    @FXML
    void onButtonPressBack(ActionEvent event) {
        StoreManagerViewReportsScreenFXController storeManagerViewReportsScreenFXController = new StoreManagerViewReportsScreenFXController(sidenavigationStoreManagerFXController);
        sidenavigationStoreManagerFXController.setMainPaneCenter(storeManagerViewReportsScreenFXController);
        sidenavigationStoreManagerFXController.setTopBarText("Login -> User Portal -> View Reports");
    }
}
