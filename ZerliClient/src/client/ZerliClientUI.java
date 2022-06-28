package client;

import communication.Answer;
import communication.Message;
import gui.login.ConnectToServerScreenFXController;
import javafx.application.Application;
import javafx.stage.Stage;
/**
 * This class is the main client UI - its responsible to show the "connect to server" screen when first launching the
 * client.
 * */

public class ZerliClientUI extends Application {
	public static ZerliClientController zerliClientController;
	public static Message confirmIpMessage;
	public static ZerliClientUI zerliClientUI;

	/**
	 * @param primaryStage the primary stage for this application, onto which
	 *                     the application scene can be set.
	 *                     Applications may create other stages, if needed, but they will not be
	 *                     primary stages.
	 * @throws Exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		zerliClientUI = this;
		ConnectToServerScreenFXController connectToServerScreenFXController = new ConnectToServerScreenFXController();
		connectToServerScreenFXController.start(primaryStage);
	}

	/**
	 * This method will stop the client's connection
	 * @throws Exception
	 */
	@Override
	public void stop() throws Exception {
		logoutFromServer();
		super.stop();
	}

	/**
	 * This method analyzes the answer from the server, if answer succeeded it will log out the client from the server
	 * using the client controller.
	 */
	public static void logoutFromServer() {
		if (confirmIpMessage.getAnswer() == Answer.SUCCEED)
			zerliClientController.logoutFromServer();
	}

	/**
	 * Main method for launching the primary stage
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
