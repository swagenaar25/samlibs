package ip.swagenaar25.samlibs.lib;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;

public class Samlib { //data container for a Samlib, includes loader and writer
	
	public String[] words; //list of words that get replaced
	public HashMap<Integer, String> orderedWords; //Words, in the order that we should be prompting them (index:word)
	public HashMap<String, String> prompts; //The prompts for each word (word:prompt)
	public HashMap<String, String> wordChoices;//The user's choices for each substitution (word:choice)
	public String author; //who made this story

	protected String rawStory; //raw story date
	protected String rawOrder; //raw order data
	public String[] storyLines; //the separate lines of the story (as defined in JSON), newlines in the story do not make a separate line here

	public boolean initialized; //whether we have all the data that is needed for playing or saving
	public int inputIndex; //which word we are asking the user for

	//prevent typos in other places, allow globally changing JSON key names
	protected static final String STORY_TAG = "story";
	protected static final String PROMPTS_TAG = "prompts";
	protected static final String ORDER_TAG = "order";
	protected static final String AUTHOR_TAG = "author";
	
	public Samlib() {
		initialized = false;
		inputIndex = 0;
	}

	//which word are we currently getting replacement for
	public String currentWord() {
		return this.orderedWords.get(this.inputIndex);
	}

	//what is the prompt for this word
	public String currentPrompt() {
		return this.prompts.get(this.currentWord());
	}

	//store user's input, advance to next input
	public void pushInput(String input) {
		this.wordChoices.put(this.currentWord(), input.replace("{", "").replace("}", ""));
		this.inputIndex++;
	}

	//write data to JSON file
	public Samlib save(File file) throws IOException {
		if (!initialized) {
			throw new IllegalStateException("Cannot save without being marked as initialized");
		}
		JSONObject json = new JSONObject();

		JSONArray story_array = new JSONArray(this.storyLines);

		json.put(STORY_TAG, story_array);

		JSONObject prompt_object = new JSONObject();
		for (String key : this.prompts.keySet()) {
			prompt_object.put(key, this.prompts.get(key));
		}

		json.put(PROMPTS_TAG, prompt_object);

		StringBuilder order = new StringBuilder();

		for (Integer i : this.orderedWords.keySet()) {
			order.append(this.orderedWords.get(i)).append(";");
		}

		String orderString = order.toString();

		json.put(ORDER_TAG, orderString);

		json.put(AUTHOR_TAG, this.author);

		json.put("__comment", "This samlib was generated by the Samlib Editor");

		Path path = Path.of(file.getAbsolutePath());
		Files.writeString(path, json.toString(4));

		return this;
	}

	//set stuff to default values and initialize it
	public Samlib reset() {
		this.rawStory = "";
		this.rawOrder = "";
		this.prompts = new HashMap<>();
		this.wordChoices = new HashMap<>();
		this.orderedWords = new HashMap<>();
		this.words = new String[0];
		this.author = "";
		this.inputIndex = 0;
		return this;
	}

	//load from file
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

		this.storyLines	= story_array.toList().toArray(new String[0]);

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

	//return single string with all word replacements filled in
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

	//get raw lines, editor needs this
	public String[] getRawLines() {
		return this.storyLines;
	}
}