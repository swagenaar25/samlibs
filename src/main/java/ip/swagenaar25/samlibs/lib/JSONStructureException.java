package ip.swagenaar25.samlibs.lib;

import org.json.JSONException;

@SuppressWarnings("serial")
public class JSONStructureException extends JSONException {

    public JSONStructureException(String message) {
        super(message);
    }

    public JSONStructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSONStructureException(Throwable cause) {
        super(cause);
    }
}
