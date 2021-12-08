package ip.swagenaar25.samlibs;

import java.awt.*;

public enum OutputFormat {
    STORY(Color.DARK_GRAY, false, true, false),
    PROMPT(Color.BLACK, true, false, false),
    USER_RESPONSE(Color.ORANGE, false, false, false),
    INFO(Color.RED, false, false, true)
    ;

    private final Color color;
    private final boolean bold;
    private final boolean italic;
    private final boolean underline;

    OutputFormat(Color color, boolean bold, boolean italic, boolean underline) {
        this.color = color;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
    }

    public Color getColor() {
        return color;
    }

    public boolean isBold() {
        return this.bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public boolean isUnderline() {
        return underline;
    }

}
