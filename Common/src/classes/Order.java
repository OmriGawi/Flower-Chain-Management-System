package classes;

import enums.OrderEnum;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * A class that represents an Order, saves all the data from the DB relevant to an order in this class
 * And its data is used in various controllers
 */
public class Order implements Serializable {
	private int id;
	private Account account;
	private Store store;
	private Date supplyDate;
	private String deliveryAddress;
	private double totalPrice;
	private String greetingCard;
	private Date creationDate;
	private Date cancelDate;
	private OrderEnum status;

	/**
	 * Constructor
	 * @param id  order id
	 * @param account Account
	 * @param store Store
	 * @param supplyDate supply date
	 * @param deliveryAddress delivery address
	 * @param totalPrice total price
	 * @param greetingCard greeting
	 * @param creationDate creation date of order
	 * @param cancelDate cancellation date of order
	 * @param status order status
	 */
	public Order(int id, Account account, Store store, Date supplyDate, String deliveryAddress, double totalPrice,
				 String greetingCard, Date creationDate, Date cancelDate, OrderEnum status) {
		this.id = id;
		this.account = account;
		this.store = store;
		this.supplyDate = supplyDate;
		this.deliveryAddress = deliveryAddress;
		this.totalPrice = totalPrice;
		this.greetingCard = greetingCard;
		this.creationDate = creationDate == null ? new Date(System.currentTimeMillis()) : creationDate;
		this.cancelDate = cancelDate;
		this.status = status;
	}

	/**
	 * Constructor
	 */
	public Order() {
		this.creationDate = new Date(System.currentTimeMillis());
		this.store = new Store();
		this.status = OrderEnum.PENDING_APPROVAL;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public void setSupplyDate(Date supplyDate) {
		this.supplyDate = supplyDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setGreetingCard(String greetingCard) {
		this.greetingCard = greetingCard;
	}

	public void setStatus(OrderEnum status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public Account getAccount() {
		return account;
	}

	public Store getStore() {
		return store;
	}

	public Date getSupplyDate() {
		return supplyDate;
	}

	public Date getCancelDate(){return cancelDate;}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public String getGreetingCard() {
		return greetingCard;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public OrderEnum getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "Order{" +
				"orderNumber=" + id +
				", account=" + account +
				", store='" + store + '\'' +
				", supplyDate=" + supplyDate +
				", deliveryAddress='" + deliveryAddress + '\'' +
				", totalPrice=" + totalPrice +
				", greetingCard='" + greetingCard + '\'' +
				", creationDate=" + creationDate +
				", cancelDate=" + cancelDate +
				", status=" + status +
				'}';
	}

	/**
	 * Creates a new list of Order with a given ResultSet
	 * @param rs ResultSet
	 * @return List of Order
	 */
	public static List<Order> createOrderListFromResultSet(ResultSet rs) {
		List<Order> orders = new ArrayList<>();
		try {
			while (rs.next()) {
				OrderEnum orderEnum = OrderEnum.valueOf(rs.getString("status"));

				orders.add(new Order(
						rs.getInt("id"),
						new Account(rs.getInt("account_id")),
						new Store(rs.getInt("store_id"), rs.getString("name")),
						rs.getTimestamp("supply_date"),
						rs.getString("delivery_address"),
						rs.getDouble("total_price"),
						rs.getString("greeting"),
						rs.getTimestamp("creation_date"),
						rs.getTimestamp("cancel_date"),
						orderEnum
				));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;
	}
}
