package synchronizer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SynchroAccepter implements Runnable, Common {
    protected SynchroManager manager;
    protected ServerSocket serverSocket;

    public SynchroAccepter(SynchroManager manager) throws IOException {
        this.manager = manager;
        serverSocket = new ServerSocket(manager.getConnectInfo().getPort());
    }

    @Override
    public void run() {
        try {
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                Communicator com = new Communicator(socket);
                String line;
                try {
                    line = com.receive(TIMEOUT_RES_CONNECT);
                } catch (IOException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING, ex.getMessage());
                    continue;
                }
                ConnectRequest request = ConnectRequest.parse(line);
                if (request.isValid())
                    connect(com, request.getConnectInfo());
                else
                    sendBadResponse(com);
            }
        } catch (SocketException ex) {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Server stopped.");
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void connect(final Communicator com, final ConnectInfo info) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                manager.connect(info, com);
            }
        }).start();
    }

    protected void sendBadResponse(Communicator com) {
        com.send(Response.createString(STS_BADREQUEST));
    }
    
    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Server stopped.");
        }
    }
}
