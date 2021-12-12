package ip.swagenaar25.samlibs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
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

    protected JButton playButton;
    protected JButton editButton;

    public MainGUI(GameGUI game, EditorGUI editor) {
        super();
        this.game = game;
        this.editor = editor;

        this.editor.setOnClose(this::onPanelClose);
        this.game.setOnClose(this::onPanelClose);

        this.playButton = new JButton("Play a Samlib");
        this.playButton.setFont(new Font("Ink Free", Font.PLAIN, 14));
        this.playButton.addActionListener(this::playClicked);

        this.editButton = new JButton("Edit a Samlib");
        this.editButton.setFont(new Font("Ink Free", Font.PLAIN, 14));
        this.editButton.addActionListener(this::editClicked);

        getContentPane().setLayout(new BorderLayout(0, 0));

        this.game.setLayout(new BorderLayout(0,0));

        this.addButtons();
    }

    protected void removeButtons() {
        getContentPane().remove(this.editButton);
        getContentPane().remove(this.playButton);
    }

    protected void addButtons() {
        getContentPane().add(this.playButton, BorderLayout.NORTH);
        getContentPane().add(this.editButton, BorderLayout.SOUTH);
    }

    public void playClicked(ActionEvent e) {
        this.removeButtons();
        this.game.removeAll();

        getContentPane().add(this.game, BorderLayout.CENTER);
        this.game.addComponents();

        getContentPane().repaint();

        try {
            this.game.init();
        } catch (IOException ex) {
            ex.printStackTrace();

            getContentPane().remove(this.game);
            this.addButtons();

            getContentPane().repaint();
        }
    }

    public void editClicked(ActionEvent e) {
        this.removeButtons();
        this.editor.removeAll();

        getContentPane().add(this.editor, BorderLayout.CENTER);
        this.editor.addComponents();

        this.pack();

        this.setVisible(true);

        getContentPane().repaint();

        try {
            this.editor.init();
        } catch (IOException ex) {
            ex.printStackTrace();

            getContentPane().remove(this.editor);
            this.addButtons();

            getContentPane().repaint();
        }
    }

    protected void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void onPanelClose(Object caller) {
        getContentPane().remove(this.editor);
        getContentPane().remove(this.game);
        this.addButtons();
        getContentPane().repaint();
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
    }
}
