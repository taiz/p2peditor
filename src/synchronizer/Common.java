package synchronizer;

public interface Common {
    public String DLM_REQUEST   = "\t";
    public String DLM_RESPONSE  = "\t";
    public String DLM_PARAMETER = ";";

    public String MTH_CONNECT     = "connect";
    public String MTH_DISCONNECT  = "disconnect";
    public String MTH_SYHCHRONIZE = "synchronize";

    public String STS_CONNECTED    = "connected";
    public String STS_DISCONNECTED = "disconnected";
    public String STS_REFUSED      = "refused";
    public String STS_BADREQUEST   = "bad_request";

    public String KEY_HOST  = "host";
    public String KEY_PORT  = "port";
    public String KEY_NAME  = "name";
    public String KEY_TIME  = "time";
    public String KEY_DATA  = "data";
    
    public int TIMEOUT_CONNECT        = 3 * 1000;
    public int TIMEOUT_RES_CONNECT    = 2 * 1000;
    public int TIMEOUT_REQ_CONNECT    = 20 * 1000;
    public int TIMEOUT_REQ_DISCONNECT = 2 * 1000;
    public int TIMEOUT_SYNC_LISTEN    = 60 * 60 * 1000;
}
