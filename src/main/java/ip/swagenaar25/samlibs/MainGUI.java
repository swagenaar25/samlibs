package ip.swagenaar25.samlibs;

/*
 * Author: Sam Wagenaar
 * Created: 1 December 2021
 * Modified: 6 December 2021
 * Purpose: Fun Madlibs!
 * Class: Introduction to Programming
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Scanner;

public class MainGUI extends JFrame implements ActionListener {

	public static boolean DEV = false;
	public Scanner kboard;
	protected JTextField input;
	protected JTextPane output;
	protected ArrayDeque<String> inputs;
	protected Console console;

	public MainGUI() {
		kboard = new Scanner(System.in); //TEMP

		//set up inputs storage
		inputs = new ArrayDeque<>();

		//set up input field
		input = new JTextField();
		input.setToolTipText("Input");
		input.addActionListener(this);
		getContentPane().add(input, BorderLayout.SOUTH); //add it to the window
		input.setColumns(10);

		//set up output field
		output = new JTextPane();
		output.setFont(new Font("Sylfaen", Font.PLAIN, 18));
		output.setText("Sample Text");
		output.setAutoscrolls(true);
		output.setDisabledTextColor(output.getSelectedTextColor());
		output.setEnabled(false);
		getContentPane().add(output, BorderLayout.CENTER);

		//set up manager for output
		console = new Console(output).clear();
	}

	public void init() {
		this.input.requestFocusInWindow();
	}
	
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			System.out.println(arg);
			if (Objects.equals(arg, "--dev")) {
				DEV = true;
			}
		}
		String file = "doc/example_samlib.json";
		Samlib test = new Samlib().build(file);
		MainGUI gui = new MainGUI();

		gui.setTitle("SamLibs");

		gui.setPreferredSize(new Dimension(800, 600));
		gui.pack();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setVisible(true);
		gui.init();

		for (int i=0; i<test.words.length; i++) {
			String word = test.orderedWords.getOrDefault(i, "BROKEN WORD FOR INDEX: "+i);
			String choice = gui.getWord(test.prompts.getOrDefault(word,
					"The author of this samlib messed up, please enter the best input you can for the word ["+word+"]: "));
			test.wordChoices.put(word, choice);
		}

		gui.showStory(test.getFilledStory());
	}

	public String getWord(String prompt) {
		this.console.println(prompt);
		String input = this.getInput().replace("{","").replace("}","");
		this.console.println("> "+input);
		return input;
	}

	public void showStory(String story) {
		System.out.println(story);
	}

	protected String getInput() {
		while (this.inputs.isEmpty()) {}
		return this.inputs.poll();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		this.inputs.add(e.getActionCommand());
	}
}
