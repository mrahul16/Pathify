package in.maru.pathify.utils.core;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class that implements the Dictionary interface with a HashMap
 */


public class DictionaryHashMap implements Dictionary {

    private HashMap<String, ArrayList<String>> words;
	
	public DictionaryHashMap() {
	    words = new HashMap<String, ArrayList<String>>();
	}
	
    /** Add this word to the dictionary.
     * @param word The word to add
     * @return true if the word was added to the dictionary 
     * (it wasn't already there). */
	@Override
	public boolean addWord(String word, ArrayList<String> dOne) {
		if(words.put(word.toLowerCase(), dOne) != null) {
			return true;
		}
		return false;
	}

	/** Return the number of words in the dictionary */
    @Override
	public int size() {
    	
    	 return words.size();
	}
	
	/** Is this a word according to this dictionary? */
    @Override
	public boolean isWord(String s) {
    	return words.containsKey(s.toLowerCase());
	}

    /** Return a list of words that are one distance away. */
	@Override
	public ArrayList<String> getDOne(String s) {
		return words.get(s);
	}
	
   
}
