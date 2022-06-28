package gui.ceo;

import gui.sidenavigation.SidenavigationCEOFXController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CeoTypeOfReportScreenFXController extends Pane implements Initializable {

    // Buttons
    @FXML
    private Button allStoresButton;
    @FXML
    private Button singleStoreButton;


    // Side-Navigation
    private SidenavigationCEOFXController sidenavigationCEOFXController;

    /**
     * Constructor
     * @param sidenavigationCEOFXController side-navigation
     */
    public CeoTypeOfReportScreenFXController(SidenavigationCEOFXController sidenavigationCEOFXController){
        this.sidenavigationCEOFXController = sidenavigationCEOFXController;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/ceo/CeoTypeOfReportScreenFX.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setControllerFactory(callback -> this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize a new Pane CeoTypeOfReportScreenFXController
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Initialize a new Pane CeoViewAllStoresReportScreenFXController
     * @param event Button press
     */
    @FXML
    void onButtonPressAllStores(ActionEvent event) {
        CeoViewAllStoresReportScreenFXController ceoViewAllStoresReportScreenFXController = new CeoViewAllStoresReportScreenFXController(sidenavigationCEOFXController);
        sidenavigationCEOFXController.setMainPaneCenter(ceoViewAllStoresReportScreenFXController);
        sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports");
    }

    /**
     * Initialize a new Pane CeoViewSingleStoreReportScreenFXController
     * @param event Button press
     */
    @FXML
    void onButtonPressSingleStore(ActionEvent event) {
        CeoViewSingleStoreReportScreenFXController ceoViewSingleStoreReportScreenFXController = new CeoViewSingleStoreReportScreenFXController(sidenavigationCEOFXController);
        sidenavigationCEOFXController.setMainPaneCenter(ceoViewSingleStoreReportScreenFXController);
        sidenavigationCEOFXController.setTopBarText("Login -> User Portal -> Choose Report Type -> View Reports");
    }
}
