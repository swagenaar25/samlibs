package ip.swagenaar25.samlibs;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

//manages output in game
public class Console {
    protected JTextPane pane;
    protected StyledDocument doc;
    protected Style style;

    public Console(JTextPane pane) {
        this.pane = pane;
        this.doc = this.pane.getStyledDocument();
        this.style = this.doc.addStyle(null, null);
    }

    //reset
    public Console clear() {
        this.pane.setText("");
        return this;
    }

    //add formatted text to output
    public Console print(String msg, OutputFormat format) {
        StyleConstants.setForeground(this.style, format.getColor());
        StyleConstants.setBold(this.style, format.isBold());
        StyleConstants.setItalic(this.style, format.isItalic());
        StyleConstants.setUnderline(this.style, format.isUnderline());
        try {
            this.doc.insertString(this.doc.getLength(), msg, style);
        } catch (BadLocationException ignored) {}
        return this;
    }

    //automatically include newline for printing
    public Console println(String msg, OutputFormat color) {
        this.print(msg+"\n", color);
        return this;
    }
}
