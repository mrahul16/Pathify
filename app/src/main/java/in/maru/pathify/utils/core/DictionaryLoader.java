package in.maru.pathify.utils.core;


import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DictionaryLoader {

    /** Load the words from the dictionary file into the dictionary
     * 
     * @param d  The dictionary to load
     * @param filename The file containing the words to load.  Each word must be on a separate line.
     */    
	public static void loadDictionary(Context context, Dictionary d, String filename) {
        // Dictionary files have 1 word per line

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)))) {
			
			String nextWord = null;
			while ((nextWord = reader.readLine()) != null) {
				String key = nextWord;
				
				ArrayList<String> value = new ArrayList<>();
				while ((nextWord = reader.readLine()) != null && !nextWord.isEmpty()) {
					value.add(nextWord);
				}
				
                d.addWord(key, value);
			}
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
        
    }
}
