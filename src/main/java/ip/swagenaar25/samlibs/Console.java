package ip.swagenaar25.samlibs;

import javax.swing.*;

public class Console {
    protected JTextPane pane;
    protected String text;

    public Console(JTextPane pane) {
        this.pane = pane;
        this.text = "";
    }

    public Console clear() {
        this.text = "";
        return this;
    }

    public Console print(String msg) {
        this.text += msg;
        this.refreshPane();
        return this;
    }

    public Console println(String msg) {
        this.print(msg+"\n");
        return this;
    }

    protected void refreshPane() {
        this.pane.setText(this.text);
    }
}
