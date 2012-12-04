package synchronizer;

public class ConnectInfo {
    private String host;
    private Integer port;
    private String name;
    private String time;

    public ConnectInfo() {}

    public ConnectInfo(String host, Integer port) {
        this(host, port, null);
    }
    
    public ConnectInfo(Integer port, String name) {
        this(null, port, name);
    }
    
    public ConnectInfo(String host, Integer port, String name) {
        this(host, port, name, null);
    }

    public ConnectInfo(String host, Integer port, String name, String time) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.time = time;
    }

    public void renew(ConnectInfo info) {
        this.host = info.host;
        this.port = info.port;
        this.name = info.name;
        this.time = info.time;
    }
    public boolean isEmpty() {
        for (String f : new String[] {host, name, time}) {
            if (Util.isNullorEmpty(f))
                return true;
        }
        if (port == null) return true;
        return false;
    }
    
    public String getHost() { return host; }

    public void setHost(String host) { this.host = host; }

    public Integer getPort() { return port; }

    public void setPort(Integer port) { this.port = port; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }
}
