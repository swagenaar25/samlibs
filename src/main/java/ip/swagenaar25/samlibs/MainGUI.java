package ip.swagenaar25.samlibs;

/*
 * Author: Sam Wagenaar
 * Created: 1 December 2021
 * Modified: 6 December 2021
 * Purpose: Fun Madlibs!
 * Class: Introduction to Programming
 */

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class MainGUI {

	public static boolean DEV = false;
	public Scanner input;

	public MainGUI() {
		input = new Scanner(System.in);
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

		for (int i=0; i<test.words.length; i++) {
			String word = test.orderedWords.getOrDefault(i, "BROKEN WORD FOR INDEX: "+i);
			String choice = gui.getWord(test.prompts.getOrDefault(word,
					"The author of this samlib messed up, please enter the best input you can for the word ["+word+"]: "));
			test.wordChoices.put(word, choice);
		}

		gui.showStory(test.getFilledStory());
	}

	public String getWord(String prompt) {
		System.out.println(prompt);
		return this.input.nextLine().replace("{","").replace("}","");
	}

	public void showStory(String story) {
		System.out.println(story);
	}
}
