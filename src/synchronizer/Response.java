package synchronizer;

import java.util.HashMap;
import java.util.Map;

public class Response implements Common {
    public static Response parse(String line) {
        Response response = new Response();
        String[] toks = line.split(DLM_REQUEST);
        if (toks.length != 2) return response;
        response.status = toks[0].trim();
        response.params = Util.parseParams(toks[1].trim());
        return response;
    }
    
    public static String createString(String status) {
        return status + DLM_RESPONSE +
                Util.paramsToString(new HashMap<String, String>() {{
                    put(KEY_TIME, Util.getCurrentTime());
                }});
    }
    
    protected String status;
    protected Map<String, String> params;
    
    protected Response() {}
    
    public String getStatus() { return this.status; }

    public String getParam(String key) { return params.get(key); }
}
