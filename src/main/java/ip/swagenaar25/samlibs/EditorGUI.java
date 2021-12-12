package ip.swagenaar25.samlibs;

import ip.swagenaar25.samlibs.lib.Samlib;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

@SuppressWarnings("serial")
public class EditorGUI extends JPanel {

	public static boolean DEV = false;

	//text and input for author
	protected JLabel authorLabel;
	protected JTextField authorField;

	//dito for story
	protected JLabel storyLabel;
	protected JTextArea storyArea;

	//dito for prompts
	protected JLabel promptsLabel;
	protected JTextArea promptsArea;

	//BUTTONS!!!
	protected JButton saveButton;
	protected JButton openButton;
	protected JButton closeButton;

	//data storage
	protected Samlib samlib;

	//method to call when we close, lets MainGUI put back the buttons
	protected Consumer<Object> onClose;

	public EditorGUI() {
		super();

		//CREATE all the components, but do not ADD them to the panel

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 99, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		authorLabel = new JLabel("Author:");
		authorLabel.setFont(new Font("Georgia", Font.PLAIN, 14));

		authorField = new JTextField();
		authorField.setFont(new Font("DialogInput", Font.PLAIN, 12));
		authorField.setEnabled(true);
		authorField.setEditable(true);
		authorField.setText("");
		authorField.setColumns(40);

		storyLabel = new JLabel("Story:");
		storyLabel.setToolTipText("Use {word} to indicate replacement.");
		storyLabel.setFont(new Font("Georgia", Font.PLAIN, 14));

		storyArea = new JTextArea();
		storyArea.setFont(new Font("DialogInput", Font.PLAIN, 12));
		storyArea.setLineWrap(true);

		promptsLabel = new JLabel("Prompts:");
		promptsLabel.setToolTipText("Use form word:This is a prompt: ");
		promptsLabel.setFont(new Font("Georgia", Font.PLAIN, 14));

		promptsArea = new JTextArea();
		promptsArea.setFont(new Font("DialogInput", Font.PLAIN, 12));
		promptsArea.setLineWrap(true);

		saveButton = new JButton("Save");
		saveButton.setFont(new Font("Ink Free", Font.PLAIN, 14));
		saveButton.addActionListener(this::saveButtonPerformed);

		openButton = new JButton("Open");
		openButton.setFont(new Font("Ink Free", Font.PLAIN, 14));
		openButton.addActionListener(this::openButtonPerformed);

		closeButton = new JButton("Close");
		closeButton.setFont(new Font("Ink Free", Font.PLAIN, 14));
		closeButton.addActionListener(this::closeButtonPerformed);
	}

	//does what it says on the tin
	public void setOnClose(Consumer<Object> onClose) {
		this.onClose = onClose;
	}

	//actually add the components in, has to be done separately b/c if it goes in wrong order nothing shows up
	public void addComponents() {
		GridBagConstraints gbc_authorLabel = new GridBagConstraints();
		gbc_authorLabel.insets = new Insets(0, 0, 5, 5);
		gbc_authorLabel.gridx = 0;
		gbc_authorLabel.gridy = 0;
		add(authorLabel, gbc_authorLabel);


		GridBagConstraints gbc_authorField = new GridBagConstraints();
		gbc_authorField.fill = GridBagConstraints.HORIZONTAL;
		gbc_authorField.insets = new Insets(5, 0, 5, 0);
		gbc_authorField.gridx = 1;
		gbc_authorField.gridy = 0;
		add(authorField, gbc_authorField);

		GridBagConstraints gbc_storyLabel = new GridBagConstraints();
		gbc_storyLabel.insets = new Insets(0, 0, 5, 5);
		gbc_storyLabel.gridx = 0;
		gbc_storyLabel.gridy = 1;
		add(storyLabel, gbc_storyLabel);

		GridBagConstraints gbc_promptsLabel = new GridBagConstraints();
		gbc_promptsLabel.insets = new Insets(0, 0, 5, 5);
		gbc_promptsLabel.gridx = 0;
		gbc_promptsLabel.gridy = 2;
		add(promptsLabel, gbc_promptsLabel);

		GridBagConstraints gbc_promptsArea = new GridBagConstraints();
		gbc_promptsArea.insets = new Insets(0, 0, 5, 0);
		gbc_promptsArea.fill = GridBagConstraints.BOTH;
		gbc_promptsArea.gridx = 1;
		gbc_promptsArea.gridy = 2;
		add(promptsArea, gbc_promptsArea);

		GridBagConstraints gbc_storyArea = new GridBagConstraints();
		gbc_storyArea.insets = new Insets(0, 0, 5, 0);
		gbc_storyArea.fill = GridBagConstraints.BOTH;
		gbc_storyArea.gridx = 1;
		gbc_storyArea.gridy = 1;
		add(storyArea, gbc_storyArea);

		GridBagConstraints gbc_saveButton = new GridBagConstraints();
		gbc_saveButton.insets = new Insets(0, 0, 0, 5);
		gbc_saveButton.gridx = 0;
		gbc_saveButton.gridy = 3;
		add(saveButton, gbc_saveButton);

		GridBagConstraints gbc_openButton = new GridBagConstraints();
		gbc_openButton.gridx = 1;
		gbc_openButton.gridy = 3;
		add(openButton, gbc_openButton);

		GridBagConstraints gbc_closeButton = new GridBagConstraints();
		gbc_closeButton.insets = new Insets(0, 0, 0, 5);
		gbc_closeButton.gridx = 0;
		gbc_closeButton.gridy = 4;
		add(closeButton, gbc_closeButton);
	}

	//reset/prepare editor for use, should happen AFTER addComponents()
	public void init() throws IOException {
		this.authorField.setText("");
		this.promptsArea.setText("");
		this.storyArea.setText("");
		this.authorField.requestFocusInWindow();

		this.samlib = new Samlib().reset();
	}

	//helper function to hide the editor and notify MainGUI we are done
	protected void close() {
		Container parent = this.getParent();
		parent.remove(this);
		this.onClose.accept(this);
		parent.repaint();
	}

	//convert special characters to normal characters in gui
	//so that user can have same editing flexibility as in raw JSON
	private static String extractSpecialChars(String s) {
		// \b  \t  \n  \f  \r  \"  \'  \\
		return s
				.replace("\\", "\\\\")
				.replace("\b", "\\b")
				.replace("\t", "\\t")
				.replace("\n", "\\n")
				.replace("\f", "\\f")
				.replace("\r", "\\r");
	}

	//convert back to real special characters
	private static String parseSpecialChars(String s) {
		// \b  \t  \n  \f  \r  \"  \'  \\
		return s
				.replace("\\b", "\b")
				.replace("\\t", "\t")
				.replace("\\n", "\n")
				.replace("\\f", "\f")
				.replace("\\r", "\r")
				.replace("\\\"","\"")
				.replace("\\\'", "\'")
				.replace("\\\\", "\\");
	}

	//load up a file and set our input fields to match
	public void openButtonPerformed(ActionEvent e) {
		JFileChooser filePicker = new JFileChooser();
		filePicker.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
		filePicker.setCurrentDirectory(new File(Util.getJarPath()));

		filePicker.showOpenDialog(null);

		File file = filePicker.getSelectedFile();
		if (file != null) {
			try {
				this.samlib = new Samlib().build(file);
			} catch (IOException ex) {
				ex.printStackTrace();
				this.close();
				return;
			}

			this.authorField.setText(extractSpecialChars(this.samlib.author));

			StringBuilder story_text = new StringBuilder();
			for (String line : this.samlib.getRawLines()) {
				story_text.append(extractSpecialChars(line)).append("\n");
			}
			story_text.deleteCharAt(story_text.length()-1);

			this.storyArea.setText(story_text.toString());

			StringBuilder prompts_text = new StringBuilder();
			for (String word : this.samlib.prompts.keySet()) {
				prompts_text.append(word).append(":").append(extractSpecialChars(this.samlib.prompts.getOrDefault(word, ""))).append("\n");
			}
			prompts_text.deleteCharAt(prompts_text.length()-1);

			this.promptsArea.setText(prompts_text.toString());
		}
	}

	//save input fields into a file
	public void saveButtonPerformed(ActionEvent e) {
		JFileChooser filePicker = new JFileChooser();
		filePicker.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
		filePicker.setCurrentDirectory(new File(Util.getJarPath()));

		filePicker.showSaveDialog(null);

		File file = filePicker.getSelectedFile();

		/*
		Values of Samlib that need to be set to save successfully:

		storyLines
		prompts
		orderedWords
		author
		 */

		if (file != null) {
			this.samlib.reset();

			this.samlib.author = this.authorField.getText();

			String story = this.storyArea.getText();
			String[] storyLines = story.split("\n");

			ArrayList<String> parsedStoryLines = new ArrayList<>();

			for (String line : storyLines) {
				parsedStoryLines.add(parseSpecialChars(line));
			}

			this.samlib.storyLines = parsedStoryLines.toArray(new String[0]);

			String[] promptLines = this.promptsArea.getText().split("\n");

			int index = 0;

			for (String compound : promptLines) {
				String[] split = compound.split(":", 2);
				String word = split[0];
				String prompt = parseSpecialChars(split[1]);

				this.samlib.orderedWords.put(index, word);
				this.samlib.prompts.put(word, prompt);

				index++;
			}

			this.samlib.initialized = true;

			try {
				this.samlib.save(file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	//allow closing from our cute little close button
	public void closeButtonPerformed(ActionEvent e) {
		close();
	}
}