package classes;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * A class that represents a Store, saves all the data from the DB relevant to a store in this class
 * And its data is used in various controllers
 */
public class Store implements Serializable{

    private int id;
    private int managerId;
    private String name;
    private String address;

    /**
     * Constructor
     * @param id id
     * @param managerId manager id
     * @param name store name
     * @param address store address
     */
    public Store(int id, int managerId, String name, String address) {
        this.id = id;
        this.managerId = managerId;
        this.name = name;
        this.address = address;
    }

    /**
     * Constructor
     * @param storeId store id
     * @param name store name
     */
    public Store(int storeId, String name) {
        this.id = storeId;
        this.name = name;
    }

    public Store(int storeId) {
        this.id = storeId;
    }

    public Store() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Creates a new list with a given ResultSet
     * @param rs ResultSet
     * @return List of Store
     */
    public static List<Store> createStoreListFromResultSet(ResultSet rs) {
        List<Store> store = new ArrayList<>();
        try {
            while (rs.next()) {
                store.add(new Store(
                        rs.getInt("id"),
                        rs.getInt("manager_id"),
                        rs.getString("name"),
                        rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return store;
    }

    @Override
    public String toString() {
        if(id != -1)
            return name + ", ID:" + id;
        else return name;
    }
}
