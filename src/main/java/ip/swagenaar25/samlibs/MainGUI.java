package ip.swagenaar25.samlibs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Objects;

public class MainGUI extends JFrame {

    private static final long serialVersionUID = 0; //stop whining compiler

    protected GameGUI game;

    public MainGUI(GameGUI game) {
        super();
        this.game = game;
        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().add(this.game, BorderLayout.CENTER);
        this.game.setBackground(Color.GREEN);
        game.setLayout(new BorderLayout(0,0));

        game.addComponents();

    }
    public static void main(String[] args) throws IOException {
        for (String arg : args) {
            System.out.println(arg);
            if (Objects.equals(arg, "--dev")) {
                GameGUI.DEV = true;
            }
        }

        GameGUI game = new GameGUI();

        MainGUI gui = new MainGUI(game);

        gui.setTitle("SamLibs");

        gui.setPreferredSize(new Dimension(800, 600));
        gui.pack();

        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
        game.init();
    }

    protected void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}
