package ip.swagenaar25.samlibs.lib;

import org.json.JSONException;

public class JSONStructureException extends JSONException {

    private static final long serialVersionUID = 0; //the Java compiler likes this as a safety blanket. WHY?

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
