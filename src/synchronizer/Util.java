package synchronizer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Util implements Common {
    public static Map<String, String> parseParams (String line) {
        Map<String, String> params = new HashMap<String, String>();
        for (String paramSting : line.split(DLM_PARAMETER)) {
            String[] toks = paramSting.split("=");
            if (toks.length != 2) continue;
            params.put(toks[0], toks[1]);
        }
        return params;
    }

    public static String paramsToString(String key, String value) {
        return key + "=" + value;
    }
    
    public static String paramsToString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Entry<String,String> e : map.entrySet()) {
            if (first)
                first = false;
            else
                sb.append(DLM_PARAMETER);
            sb.append(paramsToString(e.getKey(), e.getValue()));
        }
        return sb.toString();
    }
    
    public static Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
    
    public static boolean isNullorEmpty(String s) {
        if (s == null) return true;
        if (s.trim().isEmpty()) return true;
        return false;
    }

    public static String getCurrentTime() {
        Date now = Calendar.getInstance().getTime();
        Calendar.getInstance().getTimeInMillis();
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(now);
    }
}
