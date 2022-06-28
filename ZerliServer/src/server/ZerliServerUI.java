package server;

import cronjob.CronjobScheduler;
import database.DatabaseController;
import gui.ServerScreenFXController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.TimeZone;

/**
 * This class is the server client UI - its responsible to show the "connect to database" screen when first launching the
 * server.
 * */
public class ZerliServerUI extends Application {
	private ServerScreenFXController serverScreenFXController;
	private static ZerliServerCommunication serverCommunication;

	/**
	 * @param primaryStage the primary stage for this application, onto which
	 *                     the application scene can be set.
	 *                     Applications may create other stages, if needed, but they will not be
	 *                     primary stages.
	 * @throws Exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		serverScreenFXController = new ServerScreenFXController();
		serverScreenFXController.start(primaryStage);
	}

	public static void connectToServer(String portString) {
		int port = 0;	// Port to listen on
		try {
			port = Integer.parseInt(portString);	// Set port to 5555
		}
		catch(Throwable t) {
			System.out.println("ERROR - Could not connect!");
		}

		if (serverCommunication == null) {
			serverCommunication = new ZerliServerCommunication(port);
		}
		try {
			serverCommunication.listen(); // Start listening for connections
		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}

	/**
	 * The main method that runs the default data and shows it on screen for the user
	 * @param args
	 */
	public static void main(String[] args) {
		/* Connect to database automatically - ONLY FOR TESTING */
		String isServerAutoConnect = System.getenv("SERVER_AUTO_CONNECT");
		if (isServerAutoConnect != null && isServerAutoConnect.equals("true")) {
			try {
				DatabaseController.getInstance().connectToDB(
						"localhost",
						"5555",
						"jdbc:mysql://localhost/zerli?serverTimezone=" + TimeZone.getDefault().getID(),
						"root",
						"mysql");
				ZerliServerUI.connectToServer("5555");
			} catch (Exception ignored) {}
		}
		else
		//////////////////////////////////////////////////////////

		launch(args);
	}
}
