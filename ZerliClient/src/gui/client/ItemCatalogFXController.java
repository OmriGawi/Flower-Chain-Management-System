package gui.client;

import classes.Item;
import interfaces.IItemCatalog;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.Objects;

public class ItemCatalogFXController implements IItemCatalog {
    @FXML
    private ImageView itemImage;

    @FXML
    private Text itemNameText;

    @FXML
    private Text itemPriceText;

    @FXML
    private Text amountText;

    /**
     * Set and show the data of an Item in the Item screen
     * @param item The Item whose data we need to show on screen
     */
    public void setData(Item item){
        itemImage.setImage(new Image(item.getImageSrc()));
        itemNameText.setText(item.getName());
        itemPriceText.setText(String.valueOf(item.getPrice()));
    }

    /**
     * Set the amount of a selected Item by the user on screen
     * @param amount Amount to be entered and selected by the user
     */
    public void setAmountText(Integer amount) {
        amountText.setVisible(amount != null);
        amountText.setText(amount + " selected");
    }
}

