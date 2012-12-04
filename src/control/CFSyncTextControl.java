package control;

import synchronizer.ConnectInfo;

public interface CFSyncTextControl {
    public void addConnection(ConnectInfo info);
    public void removeConnection(ConnectInfo info);
    public boolean perimitConnection(ConnectInfo info);
    public void receivedSyncText(String text);
}
