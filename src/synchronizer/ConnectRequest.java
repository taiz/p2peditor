package synchronizer;

import java.util.HashMap;

public class ConnectRequest extends Request {

    public static ConnectRequest parse(String line) {
        Request req = Request.parse(line);
        ConnectRequest conReq = new ConnectRequest();
        conReq.method = req.method;
        conReq.params = req.params;
        conReq.info.setHost(req.getParam(KEY_HOST));
        conReq.info.setPort(Util.parseInt(req.getParam(KEY_PORT)));
        conReq.info.setName(req.getParam(KEY_NAME));
        conReq.info.setTime(req.getParam(KEY_TIME));
        return conReq;
    }

    public static String createString(final ConnectInfo info) {
        return MTH_CONNECT + DLM_REQUEST + 
                Util.paramsToString(new HashMap<String, String>() {{
                    put(KEY_HOST, info.getHost());
                    put(KEY_PORT, info.getPort().toString());
                    put(KEY_NAME, info.getName());
                    put(KEY_TIME, Util.getCurrentTime());
                }});
    }

    private ConnectInfo info = new ConnectInfo();

    protected ConnectRequest() {}

    public boolean isValid() {
        return method.equals(MTH_CONNECT) && !info.isEmpty();
    }

    public ConnectInfo getConnectInfo() { return this.info; }
}
