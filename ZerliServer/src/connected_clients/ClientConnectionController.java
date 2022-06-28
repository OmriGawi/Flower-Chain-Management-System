package connected_clients;

import communication.Answer;
import communication.Message;
import communication.Task;
import gui.ServerScreenFXController;
import javafx.collections.ObservableList;
import ocsf.server.ConnectionToClient;
import server.ZerliServerCommunication;

import static connected_clients.ClientConnection.CONNECTED;
import static connected_clients.ClientConnection.DISCONNECTED;
/**
 * This class defines the methods for client connections
 */
public class ClientConnectionController {

    /**
     * Create a new ClientConnection with the relevant details of the client and connect it to the server
     * @param message
     * @param client
     */
    public static void connectClientToServer(Message message, ConnectionToClient client) {
        ClientConnection clientConnectionToSearch = new ClientConnection(client.getInetAddress().getHostAddress(), client.getInetAddress().getHostName(), CONNECTED, client);
        ObservableList<ClientConnection> connectedClients = ServerScreenFXController.connectedClients;
        int clientIndex = connectedClients.indexOf(clientConnectionToSearch);
        if (clientIndex == -1) {
            connectedClients.add(clientConnectionToSearch);
            message.setAnswer(Answer.SUCCEED);
        }
        else if (connectedClients.get(clientIndex).isConnected()) {
            message.setAnswer(Answer.ALREADY_CONNECTED);
        }
        else {
            connectedClients.get(clientIndex).setStatus(CONNECTED);
            message.setAnswer(Answer.SUCCEED);
        }
    }

    /**
     * Disconnect a specific client from the server
     * @param message
     * @param client
     */
    public static void disconnectClientFromServer(Message message, ConnectionToClient client) {
        ClientConnection clientConnection = new ClientConnection(client.getInetAddress().getHostAddress(), client.getInetAddress().getHostName(), CONNECTED, client);
        int clientIndex = ServerScreenFXController.connectedClients.indexOf(clientConnection);
        if (clientIndex != -1) {
            ServerScreenFXController.connectedClients.get(clientIndex).setStatus(DISCONNECTED);
            message.setAnswer(Answer.SUCCEED);
        }
        else message.setAnswer(Answer.FAILED);
    }

    /**
     * Disconnect all clients from the server
     */
    public static void disconnectAllClients() {
        Message message = new Message(Task.LOGOUT, Answer.WAIT_RESPONSE);
        for (ClientConnection client : ServerScreenFXController.connectedClients) {
            if (client.isConnected())
                ZerliServerCommunication.sentToSpecificClient(client.getClientConnection(), message);
        }
        ServerScreenFXController.connectedClients.clear();
    }
}
