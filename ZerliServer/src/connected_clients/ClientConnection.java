package connected_clients;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ocsf.server.ConnectionToClient;

import java.util.Objects;

/**
 * This class defines the methods and properties for a Client Connection to the server.
 * The client ip address, host name, connection status etc.. are all saved in this class for each client connected.
 */
public class ClientConnection {
    public static final String CONNECTED = "Connected";
    public static final String DISCONNECTED = "Disconnected";

    private String ipAddress;
    private String hostName;
    private final SimpleStringProperty status = new SimpleStringProperty();
    private ConnectionToClient clientConnection;

    public ClientConnection(String ipAddress, String hostName, String status, ConnectionToClient clientConnection) {
        this.ipAddress = ipAddress;
        this.hostName = hostName;
        statusProperty().set(status);
        this.clientConnection = clientConnection;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getHostName() {
        return hostName;
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    /**
     * Set a users status in the user local ClientConnection object field.
     * @param status
     */
    public void setStatus(String status) {
        if (!(status.equals(CONNECTED) || status.equals(DISCONNECTED)))
            throw new IllegalArgumentException("Client connection status must be set as 'Connected' or 'Disconnected' only!");
        this.status.set(status);
    }

    public ConnectionToClient getClientConnection() {
        return clientConnection;
    }

    public boolean isConnected() {
        return getStatus().equals("Connected");
    }

    public boolean isDisconnected() {
        return getStatus().equals("Disconnected");
    }

    /*Override the Objects equals and hashCode methods*/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientConnection that = (ClientConnection) o;
        return Objects.equals(ipAddress, that.ipAddress) && Objects.equals(hostName, that.hostName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddress, hostName);
    }
}
