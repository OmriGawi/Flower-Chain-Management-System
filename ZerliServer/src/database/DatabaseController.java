package database;

import classes.User;
import communication.Answer;
import communication.Message;
import communication.Task;

import java.sql.*;
import java.util.List;
/**
 * This class defines the methods that send query's to the DB and receive the data back from the DB
 * and return it to the relevant controllers and classes that need to use that information
 */
public class DatabaseController {
	private static DatabaseController databaseController;
	private static Connection connection;
	private static String dbPassword = "mysql";

	private DatabaseController() {}

	public static DatabaseController getInstance() {
		if (databaseController == null) {
			databaseController = new DatabaseController();
		}
		return databaseController;
	}

	/**
	 * Connect a client to the DB
	 * @param ip
	 * @param port
	 * @param url
	 * @param username
	 * @param password
	 * @return A message containing the answer to the connection of the server
	 */
	public Message connectToDB(String ip, String port, String url, String username, String password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("Driver definition failed");
		}

		try {
			connection = DriverManager.getConnection(url, username, password);
			dbPassword = password;
			System.out.println("SQL connection succeed");
			return new Message(Task.CONNECT_TO_SERVER, Answer.SUCCEED, "SQL connection succeed");
		} catch (SQLException ex) {/* handle any errors */
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return new Message(Task.CONNECT_TO_SERVER, Answer.FAILED, "SQL connection failed!");
		}
	}
	
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @param query SELECT query
	 * @return ResultSet
	 */
	public ResultSet executeQuery(String query) {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			return stmt.executeQuery(query);
		} catch (SQLException e) {e.printStackTrace();}
		return null;
	}

	/**
	 * @param query  UPDATE/INSERT/DELETE queries
	 * @return Number of values that has been changed
	 */
	public int executeUpdate(String query){
		PreparedStatement prepareStatement = null;

		try {
			prepareStatement = connection.prepareStatement(query);
			return prepareStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	/**
	 * @param preparedStatement  UPDATE/INSERT/DELETE queries
	 * @return Number of values that has been changed
	 */
	public int executeUpdate(PreparedStatement preparedStatement) {
		try {
			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * @param query ALTER query
	 * @return isSucceed
	 */
	public boolean execute(String query) {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			return stmt.execute(query);
		} catch (SQLException e) {e.printStackTrace();}
		return false;
	}

	/**
	 * Update the users table in the DB
	 * @param usersList
	 * @return The sum of changes made in the DB
	 */
	public String updateUsersTable(List<User> usersList) {
		int countNewOrUnchanged = 0, countUpdates = 0, countFailure = 0;
		for (User user : usersList) {
			Integer accountId = user.getAccount() != null ? user.getAccountId() : null;
			String query = "INSERT INTO `zerli`.`users` (`id`, `username`, `password`, `is_logged_in`, `first_name`, `last_name`, `email`, `telephone`, `role`, `account_id`) " +
					"VALUES (" + user.getId() + ", '" + user.getUsername() + "', '" + user.getPassword() + "', " + user.isLoggedIn() + ", '" + user.getFirstName() + "', '" + user.getLastName() + "', '" + user.getEmail() + "', '" + user.getTelephone() + "', '" + user.getRole() + "', " + accountId + ") " +
					"ON DUPLICATE KEY UPDATE id=" + user.getId() + ", username='" + user.getUsername() + "', password='" + user.getPassword() + "', is_logged_in=" + user.isLoggedIn() + ", first_name='" + user.getFirstName() + "', last_name='" + user.getLastName() + "', email='" + user.getEmail() + "', telephone='" + user.getTelephone() + "', role='" + user.getRole() + "', account_id=" + accountId + ";";
			int affectedRows = executeUpdate(query);
			if (affectedRows == -1) {
				countFailure++;
			}
			else if (affectedRows == 1) {
				countNewOrUnchanged++;
			}
			else if (affectedRows == 2) {
				countUpdates++;
			}
		}
		return "External DB imported successfully";
	}
}