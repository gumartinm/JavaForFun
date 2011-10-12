package test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericStaticFactory {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Parameterized type instance creation with constructor
		Map<String, List<String>> anagramsRedundancy = new HashMap<String, List<String>>();
		
		// Parameterized type instance creation with static factory. Without type parameters explicitly 
		// when invoking generic constructors (it is annoying...)
		Map<String, List<String>> anagrams = newHashMap();
	}

	public static <K,V> HashMap<K,V> newHashMap() {
		return new HashMap<K,V>();
	}
}
