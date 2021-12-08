package ip.swagenaar25.samlibs;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Samlib {
	
	public String[] words;
	public HashMap<Integer, String> orderedWords;
	public HashMap<String, String> prompts; //(word:prompt)
	public HashMap<String, String> wordChoices;
	public String author;
	protected String rawStory;
	protected String rawOrder;
	public boolean initialized;
	public int inputIndex = 0;

	protected static String STORY_TAG = "story";
	protected static String PROMPTS_TAG = "prompts";
	protected static String ORDER_TAG = "order";
	protected static String AUTHOR_TAG = "author";
	
	public Samlib() {
		initialized = false;
	}

	public String currentWord() {
		return this.orderedWords.get(this.inputIndex);
	}

	public String currentPrompt() {
		return this.prompts.get(this.currentWord());
	}

	public void pushInput(String input) {
		this.wordChoices.put(this.currentWord(), input);
		this.inputIndex++;
	}
	
	public Samlib build(File file) throws IOException {
		initialized = true;

		//initialize variables
		this.rawStory = "";
		this.rawOrder = "";
		this.prompts = new HashMap<>();
		this.wordChoices = new HashMap<>();
		this.orderedWords = new HashMap<>();

		Path path = Path.of(file.getAbsolutePath());
		String content = Files.readString(path);
		JSONObject json = new JSONObject(new JSONTokener(content));

		//load story
		JSONArray story_array = json.getJSONArray(STORY_TAG);

		if (story_array.isEmpty()) {
			throw new JSONStructureException("Empty story");
		}

		for (int i = 0; i < story_array.length()-1; i++) {
			this.rawStory += story_array.getString(i)+'\n';
		}
		this.rawStory += story_array.getString(story_array.length()-1);

		if (this.rawStory.isEmpty()) {
			throw new JSONStructureException("Empty story");
		}

		//load prompts
		JSONObject prompts = json.getJSONObject(PROMPTS_TAG);
		if (prompts.isEmpty()) {
			throw new JSONStructureException("No prompts given");
		}

		Set<String> keys = prompts.keySet();
		for (String word : keys) {
			String prompt = prompts.getString(word);
			if (prompt.isEmpty()) {
				throw new JSONStructureException("Empty prompt");
			}
			this.prompts.put(word, prompt);
		}

		this.words = keys.toArray(new String[0]);

		//confirm we have prompts for every replacement in the story
		boolean started = false;
		String word = "";
		for (int i = 0; i < this.rawStory.length(); i++) {
			String c = this.rawStory.substring(i, i+1);
			if (c.equals("{")) {
				started = true;
			} else if (c.equals("}")) {
				if (!started) {
					throw new JSONStructureException("Incorrect bracketing");
				} else {
					started = false;
					if (!this.prompts.containsKey(word)) {
						throw new JSONStructureException("["+word+"] is missing a prompt");
					}
					word = "";
				}
			} else if (started) {
				word += c;
			}
		}

		//load order
		this.rawOrder = json.getString(ORDER_TAG);

		if (this.rawOrder.isEmpty()) {
			throw new JSONStructureException("Empty order");
		}

		//parse order
		word = "";
		int index = 0;
		for (int i = 0; i < this.rawOrder.length(); i++) {
			String c = this.rawOrder.substring(i, i+1);
			if (c.equals(";")) {
				this.orderedWords.put(index, word);
				word = "";
				index++;
			} else {
				word += c;
			}
		}

		for (String w : this.words) {
			if (!this.orderedWords.containsValue(w)) {
				throw new JSONStructureException("Order map not fully completed for word: "+w);
			}
		}

		//load author
		this.author = json.optString(AUTHOR_TAG, "Unknown");

		return this;
	}

	public String getFilledStory() {
		String story = this.rawStory;
		for (String word : this.words) {
			while (story.contains("{" + word + "}")) {
				story = story.replace("{" + word + "}", this.wordChoices.getOrDefault(word, "[BROKEN INPUT]"));
			}
		}
		story += "\nBy: "+this.author;
		return story;
	}
}