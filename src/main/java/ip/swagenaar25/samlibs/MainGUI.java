package ip.swagenaar25.samlibs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Objects;

/*
 * Author: Sam Wagenaar
 * Created: 1 December 2021
 * Modified: 10 December 2021
 * Purpose: Fun Madlibs!
 * Class: Introduction to Programming
 */

public class MainGUI extends JFrame {

    private static final long serialVersionUID = 0; //stop whining compiler

    protected GameGUI game;
    protected EditorGUI editor;

    public MainGUI(GameGUI game, EditorGUI editor) {
        super();
        this.game = game;
        this.editor = editor;
        getContentPane().setLayout(new BorderLayout(0, 0));
        //getContentPane().add(this.game, BorderLayout.CENTER);
        getContentPane().add(this.editor, BorderLayout.CENTER);

        game.setLayout(new BorderLayout(0,0));

        //game.addComponents();
        editor.addComponents();
    }
    public static void main(String[] args) throws IOException {
        for (String arg : args) {
            System.out.println(arg);
            if (Objects.equals(arg, "--dev")) {
                GameGUI.DEV = true;
            }
        }

        GameGUI game = new GameGUI();
        EditorGUI editor = new EditorGUI();

        MainGUI gui = new MainGUI(game, editor);

        gui.setTitle("SamLibs");

        gui.setPreferredSize(new Dimension(800, 650));
        gui.pack();

        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
        game.init();
        editor.init();
    }

    protected void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}
