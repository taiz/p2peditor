package synchronizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Communicator {
    protected Socket socket;
    protected BufferedReader reader;
    protected PrintWriter writer;

    protected Communicator() {}
    
    public Communicator(Socket socket) {
        setSocket(socket);
    }

    protected void setSocket(Socket socket) {
        try {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String receive(int timeout) throws IOException {
        socket.setSoTimeout(timeout);
        return reader.readLine();
    }
    
    public void send(String message) {
        writer.println(message);
        writer.flush();
    }
    
    public void closeSocket() {
        if (socket == null) return;
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
