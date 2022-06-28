package client;

import java.io.IOException;

import communication.Answer;
import communication.Message;
import communication.Task;

/**
 * This Class creates a new client object with the host and port of that connection and handles the input of UI received
 * from the client
 */
public class ZerliClientController {

	public static ZerliClientCommunication client;

	/**
	 * Constructs an instance of the ZerliClientController.
	 *
	 * @param host The host to connect to.
	 * @param port The port to connect on.
	 */
	public ZerliClientController(String host, int port) {
		try {
			client = new ZerliClientCommunication(host, port);
		} catch (IOException exception) {
			System.out.println("Error: Can't setup connection! Terminating client.");
		}
	}

	/**
	 * This method waits for input from the client UI. Once it is received, it sends
	 * it to the client's message handler.
	 */
	public static void accept(Message message) {
		client.handleMessageFromClientUI(message);
	}

	/**
	 * This method will log out a client and revoke its connection.
	 */
	public void logoutFromServer() {
		Message msg = new Message(Task.LOGOUT, Answer.WAIT_RESPONSE);
		ZerliClientController.accept(msg);
	}

}
