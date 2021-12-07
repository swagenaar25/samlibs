package ip.swagenaar25.samlibs;

import org.json.JSONException;

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
