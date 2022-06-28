package utils;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * This class implements Functions that have many uses by different classes
 */
public class Functions {
    /**
     * Center window in screen
     * @param primaryStage probably ((Stage)((Button) event.getSource()).getScene().getWindow())
     */
    public static void centerWindow(Stage primaryStage) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
    }
    /**
     * Open new FX window instead of the current one
     * @param primaryStage probably ((Stage)((Button) event.getSource()).getScene().getWindow())
     * @param currClass getClass()
     * @param fxmlPath "/gui/.../XXXX.fxml"
     */
    public static void openNewWindowInsteadOfCurrent(Stage primaryStage, Class<?> currClass, String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(currClass.getResource("/gui/login/LoginScreenFX.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            primaryStage.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open new FX window above the current one
     * @param currClass getClass()
     * @param fxmlPath "/gui/.../XXXX.fxml"
     */
    public static void openNewWindow(Class<?> currClass, String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(currClass.getResource("/gui/login/LoginScreenFX.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the first element form the provided list
     * @param list
     * @return First element of the list, if not exist return null
     */
    public static <T> T getFirstElementFromList(List<T> list) {
        if (list == null || list.isEmpty())
            return null;
        else return list.get(0);
    }

    /**
     * Round a number
     * @param number Number to round
     * @param digits Digits after the dot
     * @return Rounded number
     */
    public static Double round(Double number, int digits) {
        return Math.round(number * (10 * digits)) / (10.0 * digits);
    }

    /**
     * Get data from a file
     * @param path The path of the file location in the PC
     * @return The string of all the data in the file.
     */
    public static String readFile(String path) {
        StringBuilder fileStr = new StringBuilder();
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                fileStr.append(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return fileStr.toString();
    }
}
