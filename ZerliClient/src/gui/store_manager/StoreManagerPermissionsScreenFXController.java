package gui.store_manager;

import classes.Worker;
import controllers.StoreController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
/**
 * This class defines the Store Manager Permissions screen
 */
public class StoreManagerPermissionsScreenFXController extends Pane implements Initializable {
    @FXML
    private TableView<Worker> workersTableView;

    @FXML
    private TableColumn<Worker, Integer> workerIdCol;

    @FXML
    private TableColumn<Worker, String> workerFirstNameCol;
    @FXML
    private TableColumn<Worker, String> workerLastNameCol;

    @FXML
    private TableColumn<Worker, Boolean> isAllowedToFillSurveysCol;

    public static ObservableList<Worker> workersList = FXCollections.observableArrayList();

    /**
     * Constructor
     */
    public StoreManagerPermissionsScreenFXController(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/store_manager/StoreManagerPermissionsScreenFX.fxml"));

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
        // Set values factory
        workerIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        workerFirstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        workerLastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        isAllowedToFillSurveysCol.setCellFactory(column -> new CheckBoxTableCell<>());
        isAllowedToFillSurveysCol.setCellValueFactory(cellData -> {
            Worker cellValue = cellData.getValue();
            BooleanProperty property = new SimpleBooleanProperty(cellValue.isAllowedToFillSurveys());

            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> {
                cellValue.setIsAllowedToFillSurveys(newValue);
                StoreController.updateWorkerPermission(cellData.getValue().getId(), cellData.getValue().getStoreId(), newValue);
            });

            return property;
        });

        // Set observable list into workersTableView
        workersTableView.setItems(workersList);

        // Add listener for changes
        workersList.addListener((ListChangeListener<Worker>) c -> {
            workersTableView.refresh();
        });

        StoreController.getWorkersInStore(Utils.currUser.getId());
    }
}
