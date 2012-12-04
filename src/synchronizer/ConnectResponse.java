package synchronizer;

import java.util.HashMap;

public class ConnectResponse extends Response {
    
    public static ConnectResponse parse(String line) {
        Response res = Response.parse(line);
        ConnectResponse conRes = new ConnectResponse();
        conRes.status = res.status;
        conRes.params = res.params;
        conRes.info.setHost(res.getParam(KEY_HOST));
        conRes.info.setPort(Util.parseInt(res.getParam(KEY_PORT)));
        conRes.info.setName(res.getParam(KEY_NAME));
        conRes.info.setTime(res.getParam(KEY_TIME));
        return conRes;
    }

    public static String createString(final String status, final ConnectInfo info) {
        return status + DLM_RESPONSE + 
                Util.paramsToString(new HashMap<String, String>() {{
                    put(KEY_HOST, info.getHost());
                    put(KEY_PORT, info.getPort().toString());
                    put(KEY_NAME, info.getName());
                    put(KEY_TIME, Util.getCurrentTime());
                }});
    }

    protected ConnectInfo info = new ConnectInfo();

    public boolean isValid() {
        if (status == null) return false;
        boolean chk = false;
        for (String s : new String[] {STS_CONNECTED,STS_DISCONNECTED,STS_REFUSED,STS_BADREQUEST}) {
            if (status.equals(s)) {
                chk = true;
                break;
            }
        }
        if (!chk) return false;
        if (info.isEmpty()) return false;
        return true;
    }

    public boolean isSuccessful() {
        return isValid() && status.equals(STS_CONNECTED);
    }

    public ConnectInfo getConnectInfo() { return this.info; }

}
