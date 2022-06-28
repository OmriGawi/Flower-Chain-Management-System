package server;

import communication.Message;
import communication.Task;
import cronjob.CronjobScheduler;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import javax.management.Query;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 */
public class ZerliServerCommunication extends AbstractServer {
	private Connection connection;

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 * 
	 */
	public ZerliServerCommunication(int port) {
		super(port);
	}

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 * @param
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		 System.out.println("Message received: " + msg + " from " + client);
		if (msg instanceof Message) {
			try {
				Message message = (Message) msg;
				AnalyzeMessageFromClient.analyze(message, client);
				if (message.isSendToAll()) {
					sendToAllClients(msg);
				} else {
					sentToSpecificClient(client, msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			sentToSpecificClient(client, "Message was dismissed");
			System.out.println("Message was dismissed");
		}
	}


	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort() + "\n");
		CronjobScheduler.startCronjobScheduler();
		System.out.println("Starting CronjobScheduler...");
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.\n");
	}

	/**
	 * This method sends a message to specific client connected to the server.
	 * 
	 * @param client  The connection from which the message originated.
	 * @param message Object the message to be sent.
	 */
	public static void sentToSpecificClient(ConnectionToClient client, Object message) {
		if (message instanceof Serializable) {
			try {
				client.sendToClient(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else throw new RuntimeException("This message is not Serializable!");
	}
}
