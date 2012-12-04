package synchronizer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Synchronizer extends Communicator implements Common, Runnable {
    protected SynchroManager manager;
    protected ConnectInfo info;

    public Synchronizer(SynchroManager manager, ConnectInfo info) throws SynchronizerException {
        this.manager = manager;
        this.info = info;
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(info.getHost(), info.getPort()), TIMEOUT_CONNECT);
        } catch (UnknownHostException ex) {
            throw new SynchronizerException("Unknown host..", ex);
        } catch (ConnectException ex) {
            throw new SynchronizerException("Socket conection refused.", ex);
        } catch (SocketTimeoutException ex) {    
            throw new SynchronizerException("Socket conection timeouted.", ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            throw new SynchronizerException("Unknown exception has occured.", ex);
        }
        setSocket(socket);
    }

    public Synchronizer(SynchroManager manager, ConnectInfo info, Communicator com) {
        this.manager = manager;
        this.info = info;
        takeover(com);
    }

    public ConnectInfo getConnectInfo() { return info; }
    
    protected void takeover(Communicator com) {
        this.socket = com.socket;
        this.reader = com.reader;
        this.writer = com.writer;
    }

    public void connect() throws SynchronizerException {
        sendConnectRequest();
        ConnectResponse response;
        try {
            response = receviceConnectResponse();
        } catch (SocketTimeoutException ex) {
            throw new SynchronizerException("Connect request timeouted.", ex);
        } catch (IOException ex) {
            throw new SynchronizerException("Unknown exception has occured.", ex);
        }
        if (response.isSuccessful()) {
            info.renew(response.getConnectInfo());
        } else {
            if (response.getStatus().equals(STS_REFUSED))
                throw new SynchronizerException("Connect request refused.");
            else
                throw new SynchronizerException("Unknown  exception has occured.");
        }
    }

    protected void sendConnectRequest() {
        String request = ConnectRequest.createString(manager.getConnectInfo());
        send(request);
    }

    protected ConnectResponse receviceConnectResponse() throws IOException {
        String response = receive(TIMEOUT_REQ_CONNECT);
        return ConnectResponse.parse(response);
    }

    public void sendConnectResponse(String status) {
        String response = ConnectResponse.createString(status, manager.getConnectInfo());
        send(response);
    }

    public void disconnect() throws SynchronizerException {
        sendDisconnectRequest();
        Response response;
        try {
            response = receviceDisconnectResponse();
        } catch (IOException ex) {
            throw new SynchronizerException("Unknown exception has occured.", ex);
        }
        if (response.getStatus().equals(STS_DISCONNECTED)) {
            closeSocket();
        } else {
            throw new SynchronizerException("Failde to disconncet by " + response.getStatus() + ".");
        }
    }

    protected void sendDisconnectRequest() {
        String request = Request.createString(MTH_DISCONNECT);
        send(request);
    }
    
    protected Response receviceDisconnectResponse() throws IOException {
        String response = receive(TIMEOUT_REQ_DISCONNECT);
        return Response.parse(response);
    }

    public void sendSyncData(String text) {
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            send(Request.createString(MTH_SYHCHRONIZE, KEY_DATA, encodedText));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            handle();
        } catch (IOException ex) {
            // To be modified
            Logger.getLogger(Synchronizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void handle() throws IOException {
        String line;
        while ((line = receive(TIMEOUT_SYNC_LISTEN)) != null) {
            if (line.isEmpty()) return;
            Request request = Request.parse(line);
            if (request.getMethod().equals(MTH_SYHCHRONIZE))
                receivedSyncData(request.getParam(KEY_DATA));
            else if (request.getMethod().equals(MTH_DISCONNECT))
                receivedDisconnectRequest();
            else
                Logger.getLogger(getClass().getName()).log(Level.WARNING,
                        "received unknown request : " + request.getMethod());
        }
    }

    protected void receivedSyncData(String text) {
        try {
            String decodedText = URLDecoder.decode(text, "UTF-8");
            manager.recievedSyncData(decodedText);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void receivedDisconnectRequest() {
        manager.disconnect(this);
    }

    public void sendDisconnectResponse() {
        String response = Response.createString(STS_DISCONNECTED);
        send("");
        send(response);
    }
}
