package gui.client;

import classes.Item;
import interfaces.IItemCatalog;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static utils.Constants.SALE_ITEM_COLORS;
import static utils.Constants.SALE_ITEM_HOVER_COLORS;

public class ItemSaleFXController implements IItemCatalog {
    @FXML
    private HBox box;
    @FXML
    private ImageView itemImage;

    @FXML
    private Label itemNameLabel;

    @FXML
    private Text itemPriceAfterDiscountLabel;

    @FXML
    private Text itemPriceWithoutDiscountLabel;

    @FXML
    private Text amountText;
    private String color, colorHover;

    /**
     * Set and show the data of an Item on sale on the screen
     * @param item The Item whose data we need to show on screen
     */
    public void setData(Item item){
        int randomNumber = (int)(Math.random() * SALE_ITEM_COLORS.length);
        color = SALE_ITEM_COLORS[randomNumber];
        colorHover = SALE_ITEM_HOVER_COLORS[randomNumber];
        box.setStyle("-fx-background-color: #"+ color + ";" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropShadow(three-pass-box, rgba(0,0,0,0.1), 10, 0 , 0, 10);");
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(item.getImageSrc())));
        itemImage.setImage(image);
        itemNameLabel.setText(item.getName());
        itemPriceAfterDiscountLabel.setText(String.valueOf(Math.round(item.getPrice() * 100.0) / 100.0));
        itemPriceWithoutDiscountLabel.setText(String.valueOf(item.getPriceWithoutDiscount()));
    }

    /**
     * Set the amount of a selected Item by the user on screen
     * @param amount Amount to be entered and selected by the user
     */
    public void setAmountText(Integer amount) {
        amountText.setVisible(amount != null);
        amountText.setText(amount + " selected");
    }


    @FXML
    void onMouseEnteredSaleItem(MouseEvent event) {
        ((HBox) event.getSource()).setStyle("-fx-background-color: #"+ colorHover + ";\n" +
                "    -fx-background-radius: 15;\n" +
                "    -fx-effect: dropShadow(three-pass-box, rgba(0,0,0,0.1), 10, 0 , 0, 10);");
    }

    @FXML
    void onMouseExitedSaleItem(MouseEvent event) {
        ((HBox) event.getSource()).setStyle("-fx-background-color: #"+ color + ";" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropShadow(three-pass-box, rgba(0,0,0,0.1), 10, 0 , 0, 10);");
    }

    @FXML
    void onMouseClickedSaleItem(MouseEvent event) {
        ((HBox) event.getSource()).setStyle("-fx-background-color: #"+ colorHover + ";\n" +
                "    -fx-background-radius: 15;\n" +
                "    -fx-translate-y: 4px;\n" +
                "    -fx-effect: dropShadow(three-pass-box, rgba(0,0,0,0.1), 10, 0 , 0, 10);");

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ((HBox) event.getSource()).setStyle("-fx-background-color: #"+ colorHover + ";\n" +
                        "    -fx-background-radius: 15;\n" +
                        "    -fx-effect: dropShadow(three-pass-box, rgba(0,0,0,0.1), 10, 0 , 0, 10);");
            }
        };
        new Timer().schedule(task, 50L);
    }
}
