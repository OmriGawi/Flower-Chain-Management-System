package interfaces;

import classes.Item;
/**
 * This class defines the interface needed to be implemented by the Catalog's classes and item sales class
 */
public interface IItemCatalog {
    void setData(Item item);
    void setAmountText(Integer amount);
}
