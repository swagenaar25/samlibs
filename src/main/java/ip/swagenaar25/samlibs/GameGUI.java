package ip.swagenaar25.samlibs;

import ip.swagenaar25.samlibs.lib.Samlib;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.function.Consumer;

import static ip.swagenaar25.samlibs.OutputFormat.*;

@SuppressWarnings("serial")
public class GameGUI extends JPanel implements ActionListener {

	//keep track of whether we are in a dev environment, set default open and save path to not try to check the JAR file
	public static boolean DEV = false;

	protected JTextField input; //user input
	protected JTextPane output; //display to user
	protected Console console; //manages the output
	protected Samlib samlib; //store data
	protected Stage stage; //keep track of what we are doing right now

	protected Consumer<Object> onClose;

	public GameGUI() {
		super();
		this.stage = Stage.GET_FILE;

		//set up input field
		input = new JTextField();
		input.addActionListener(this);
		input.setColumns(10);

		//set up output field
		output = new JTextPane();
		output.setFont(new Font("Sylfaen", Font.PLAIN, 18));
		output.setText("Sample Text");
		output.setAutoscrolls(true);
		output.setFocusable(false);

		//set up manager for output
		console = new Console(output).clear();
	}

	//it says it all on the tin
	public void setOnClose(Consumer<Object> onClose) {
		this.onClose = onClose;
	}

	//actually addComponents, see EditorGUI for why it is separate
	public void addComponents() {
		this.add(input, BorderLayout.SOUTH);
		this.add(output, BorderLayout.CENTER);
	}

	//reset fields, ask user what file they want to play
	public void init() throws IOException {
		this.input.setText("");
		this.console.clear();
		this.input.requestFocusInWindow();

		JFileChooser filePicker = new JFileChooser();
		filePicker.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
		filePicker.setCurrentDirectory(new File(Util.getJarPath()));
		this.console.println("Please select a file.", INFO);
		filePicker.showOpenDialog(null);
		File file = filePicker.getSelectedFile();
		if (file==null) {
			this.close();
		}

		this.samlib = new Samlib().build(file);
		this.stage = Stage.GET_INPUTS;
		this.console.println(this.samlib.currentPrompt(), PROMPT);
	}

	//just like in EditorGUI
	protected void close() {
		Container parent = this.getParent();
		parent.remove(this);
		this.onClose.accept(this);
		parent.repaint();
	}

	//handle user inputs!
	@Override
	public void actionPerformed(ActionEvent e) {
		String input = e.getActionCommand();
		if (this.stage == Stage.GET_INPUTS) {
			this.samlib.pushInput(input);
			this.console.println("> "+input, USER_RESPONSE);
			this.input.setText("");
			String prompt = this.samlib.currentPrompt();
			if (prompt != null) {
				this.console.println(prompt, PROMPT);
			}
			if (this.samlib.inputIndex==this.samlib.words.length) {
				this.stage = Stage.SHOW_STORY;
				this.console.println(this.samlib.getFilledStory(), STORY);
				this.stage = Stage.GO_AGAIN;
				this.console.println("Play again? ", PROMPT);
			}
		} else if (this.stage == Stage.GO_AGAIN) {
			this.console.println("> "+input, USER_RESPONSE);
			this.input.setText("");
			boolean again = input.substring(0, 1).equalsIgnoreCase("y");
			if (again) {
				try {
					this.init();
				} catch (IOException ex) {
					ex.printStackTrace();
					this.close();
				}
			} else {
				this.close();
			}
		}
	}

	protected enum Stage {
		GET_FILE,
		GET_INPUTS,
		SHOW_STORY,
		GO_AGAIN
	}
}
