package client;

import communication.Answer;
import communication.Task;
import ocsf.client.*;
import communication.Message;
import java.io.*;

/**
 * This class overrides some methods defined in the abstract superclass
 * in order to give more functionality to the client.
 */
public class ZerliClientCommunication extends AbstractClient {

	public static ZerliClientCommunication zerliClientCommunication;
	public static boolean awaitResponse = false;

	/**
	 * Constructs an instance of the ZerliClientCommunication
	 *
	 * @param host The server to connect to.
	 * @param port The port number to connect on.
	 */
	public ZerliClientCommunication(String host, int port) throws IOException {
		super(host, port);
		openConnection();

		ZerliClientUI.confirmIpMessage = new Message(Task.CONFIRM_IP, Answer.WAIT_RESPONSE);
		sendToServer(ZerliClientUI.confirmIpMessage);
	}

	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object msg) {
		awaitResponse = false;
		if (msg instanceof Message) {
			boolean isSuccess = AnalyzeMessageFromServer.analyze(msg);
			if (!isSuccess)
				System.out.println("Message to server failed");
		}
	}

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param msg The message from the UI.
	 */
	public void handleMessageFromClientUI(Message msg) {
		try {
			awaitResponse = true;
			openConnection();
			sendToServer(msg);
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			closeClientConnection();
		}
	}

	/**
	 * This method terminates the client.
	 */
	public void closeClientConnection() {
		try {
			closeConnection();
		} catch (IOException e) {}
		System.exit(0);
	}
}
