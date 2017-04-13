package in.maru.pathify.utils.core;

import java.util.ArrayList;

/**
 * Dictionary interface, representing and old school word-lookup dictionary
 */


public interface Dictionary {
	/** Add this word to the dictionary.
	 * @param word The word to add
	 * @return true if the word was added to the dictionary 
	 * (it wasn't already there). */
	public abstract boolean addWord(String word, ArrayList<String> dOne);

	/** Is this a word according to this dictionary? */
	public abstract boolean isWord(String s);

	public abstract ArrayList<String> getDOne(String s);
	
	/** Return the number of words in the dictionary */
	public abstract int size();
	
}
