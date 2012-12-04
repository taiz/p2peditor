package synchronizer;

import control.CFSyncTextControl;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SynchroManager implements Common {
    protected CFSyncTextControl syncCntl;
    protected ConnectInfo connectInfo;
    protected SynchroAccepter accepter;
    protected List<Synchronizer> synchronizers = new ArrayList<Synchronizer>();
    protected boolean status = false;
            
    public SynchroManager(CFSyncTextControl syncCntl) {
        this.syncCntl = syncCntl;
    }
    
    public synchronized void startService(ConnectInfo info) throws SynchronizerException {
        if (status)
            throw new SynchronizerException("Service is runnning.");
        try {
            info.setHost(InetAddress.getLocalHost().getHostName());
            connectInfo = info;
            accepter = new SynchroAccepter(this);
            new Thread(accepter).start();
            status = true;
        } catch (UnknownHostException ex) {
            throw new SynchronizerException("Can not get local host name.", ex);
        } catch (IOException ex) {
            throw new SynchronizerException("Can not open server socket.", ex);
        }
    }

    public synchronized void stopService() throws SynchronizerException {
        if (!status)
            throw new SynchronizerException("Service is not runnning.");
        accepter.stop();
        List<Synchronizer> dup = new ArrayList<Synchronizer>();
        for (Synchronizer sync : synchronizers) {dup.add(sync);}
        for (Synchronizer sync : dup) {
            sync.disconnect();
            synchronizers.remove(sync);
            syncCntl.removeConnection(sync.getConnectInfo());
        }
        status = false;
    }

    public ConnectInfo getConnectInfo() { return connectInfo; }
    
    public void connect(ConnectInfo info) throws SynchronizerException {
        Synchronizer sync = new Synchronizer(this, info);
        sync.connect();
        addSynchronizer(sync);
        new Thread(sync).start();
    }

    public synchronized void connect(ConnectInfo info, Communicator com) {
        Synchronizer sync = new Synchronizer(this, info, com);
        if (syncCntl.perimitConnection(info)) {
            sync.sendConnectResponse(STS_CONNECTED);
            new Thread(sync).start();
            synchronizers.add(sync);
            syncCntl.addConnection(info);
        } else {
            sync.sendConnectResponse(STS_CONNECTED);
        }
    }
    
    public void disconnect (ConnectInfo info) throws SynchronizerException {
        Synchronizer sync = getSynchronizer(info);
        removeSynchronizer(sync);
        sync.disconnect();
    }

    public synchronized void disconnect(Synchronizer sync) {
        synchronizers.remove(sync);
        sync.sendDisconnectResponse();
        sync.closeSocket();
        syncCntl.removeConnection(sync.getConnectInfo());
    }

    public synchronized void sendSyncData(String text) {
        for (Synchronizer sync : synchronizers)
            sync.sendSyncData(text);
    }

    public synchronized void recievedSyncData(String text) {
        syncCntl.receivedSyncText(text);
    }

    protected synchronized void addSynchronizer(Synchronizer sync) {
        synchronizers.add(sync);
    }

    protected synchronized void removeSynchronizer(Synchronizer sync) {
        synchronizers.remove(sync);
    }

    protected synchronized Synchronizer getSynchronizer(ConnectInfo info) {
        for (Synchronizer sync : synchronizers) {
            if (sync.getConnectInfo() == info) return sync;
        }
        return null;
    }
    
    public boolean isRunnnig() {
        return status;
    }
}
