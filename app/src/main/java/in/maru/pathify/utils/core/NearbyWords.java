package in.maru.pathify.utils.core;

import java.util.List;


public class NearbyWords {

	Dictionary dict;

	public NearbyWords (Dictionary dict) {
		this.dict = dict;
	}

	/** Return the list of words that are one modification away
	 * from the input string.  
	 * @param s The original String
	 * @return list of words that are one modification away from the original string
	 */
	public List<String> distanceOne(String s)  {
		return dict.getDOne(s);
	}

}