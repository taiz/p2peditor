package synchronizer;

import java.util.HashMap;
import java.util.Map;

public class Request implements Common {
    public static Request parse(String line) {
        Request request = new Request();
        String[] toks = line.split(DLM_REQUEST);
        if (toks.length != 2) return request;
        request.method = toks[0].trim();
        request.params = Util.parseParams(toks[1].trim());
        return request;
    }
    
    public static String createString(String request) {
        return createString(request,
                new HashMap<String, String>() {{
                    put(KEY_TIME, Util.getCurrentTime());
                }});
    }
    
    public static String createString(String request, final String key, final Object value) {
        return createString(request,
                new HashMap<String, String>() {{
                    put(key, value.toString());
                    put(KEY_TIME, Util.getCurrentTime());
                }});
    }
    
    public static String createString(String request, Map<String, String> params) {
        return request + DLM_REQUEST + Util.paramsToString(params);
    }
    
    
    
    protected String method;
    protected Map<String, String> params;
    
    protected Request() {}
    
    public String getMethod() { return this.method; }

    public String getParam(String key) { return params.get(key); }
}
