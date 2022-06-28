package classes;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static utils.Constants.*;
/**
 * A class that represents an Item, saves all the data from the DB relevant to an item in this class
 * And its data is used in various controllers
 */
public class Item implements Serializable, Comparable<Item>, Comparator<Item> {
    private Integer id;
    private String name;
    private double price;
    private String color;
    private int discountPercentage;
    private String imageSrc;
    private boolean isProduct;
    private Integer accountID;

    private HashMap<Item, Integer> itemsMap;

    /**
     * Constructor
     */
    public Item() {
        this.discountPercentage = 0;
        this.imageSrc = DEFAULT_PRODUCT_ITEM_ICON_PATH;
        this.isProduct = true;
        this.itemsMap = new HashMap<>();
    }

    /**
     * Constructor
     * @param id id
     * @param name name
     * @param price price
     * @param color color
     * @param discountPercentage discount percentage
     * @param imageSrc image path
     * @param accountID account id
     * @param isProduct boolean
     * @param itemsMap map
     */
    public Item(int id, String name, double price, String color, int discountPercentage, String imageSrc, Integer accountID, boolean isProduct, HashMap<Item, Integer> itemsMap) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.color = color;
        this.discountPercentage = discountPercentage;
        this.imageSrc = imageSrc;
        this.accountID = accountID;
        this.isProduct = isProduct;
        this.itemsMap = itemsMap;
    }

    /**
     * Constructor<p>
     * Mainly for user's custom item
     */
    public Item(String name, double price, HashMap<Item, Integer> itemsMap, int accountID) {
        this.name = name;
        this.price = price;
        this.discountPercentage = 0;
        this.imageSrc = DEFAULT_CUSTOM_ITEM_ICON_PATH;
        this.isProduct = true;
        this.itemsMap = itemsMap;
        this.accountID = accountID;
        calPrimaryColor();
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getPrice() {
        return price * (1 - getDiscountPercentage() / 100.0);
    }

    public double getPriceWithoutDiscount() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public boolean isProduct() {
        return isProduct;
    }

    public void setProduct(boolean product) {
        isProduct = product;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public HashMap<Item, Integer> getItemsMap() {
        return itemsMap;
    }

    public void setItemsMap(HashMap<Item, Integer> itemsMap) {
        this.itemsMap = itemsMap;
    }

    /**
     * Inorder to know to which account_id this custom item is related to
     * @return account_id
     */
    public Integer getAccountID() {
        return accountID;
    }

    /**
     * @return True if this item is a custom item (e.g. Created by one of the users)
     */
    public boolean isCustom() {
        return accountID != null;
    }

    /**
     * Calculate and set the product primary color to the primary color of his sub-items
     */
    public void calPrimaryColor() {
        int maxValue = 0;
        String maxColor = null;
        for (Item item : itemsMap.keySet()) {
            if (itemsMap.get(item) > maxValue) {
                maxValue = itemsMap.get(item);
                maxColor = item.getColor();
            }
        }
        setColor(maxColor);
    }

    /**
     * Creates a new list of Item with a given ResultSet
     * @param rs ResultSet
     * @return List of Item
     */
    public static List<Item> createItemsListFromResultSet(ResultSet rs) {
        List<Item> items = new ArrayList<>();
        try {
            while (rs.next()) {
                items.add(new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("color"),
                        rs.getInt("discount_percentage"),
                        rs.getString("img"),
                        rs.getInt("account_id") != 0 ? rs.getInt("account_id") : null,
                        rs.getBoolean("isProduct"),
                        null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", color='" + color + '\'' +
                ", discountPercentage=" + discountPercentage +
                ", imageSrc='" + imageSrc + '\'' +
                ", isProduct=" + isProduct +
                ", itemList=" + itemsMap +
                '}';
    }


    @Override
    public int compareTo(Item item) {
        return Double.compare(this.getPrice(), item.getPrice());
    }

    @Override
    public int compare(Item o1, Item o2) {
        return Double.compare(o1.getPrice(), o2.getPrice());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id.equals(item.id) && Double.compare(item.price, price) == 0 && discountPercentage == item.discountPercentage && isProduct == item.isProduct && Objects.equals(name, item.name) && Objects.equals(color, item.color) && Objects.equals(imageSrc, item.imageSrc) && Objects.equals(itemsMap, item.itemsMap);
    }

    @Override
    public Comparator<Item> reversed() {
        return Comparator.super.reversed();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, color, discountPercentage, imageSrc, isProduct, itemsMap);
    }
}