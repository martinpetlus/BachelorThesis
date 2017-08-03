package sk.stuba.fiit.learning_to_rank.parsing;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sk.stuba.fiit.learning_to_rank.documents.Document;
import sk.stuba.fiit.learning_to_rank.stemming.Stemmer;


public abstract class Parser {

	public abstract Document parse(String html);
	
	public static final String stemAndRemoveNonWords(String s) {
		List<String> words = new ArrayList<String>();
		Collections.addAll(words, s.split("[\\s\\W]+"));
		// Remove words with length less than 4 characters
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).length() < 4) {
				words.remove(i--);
			}
		}
		
		
		// Stem remaining words
		Stemmer stemmer = new Stemmer();
		for (int i = 0; i < words.size(); i++) {
			String word = words.get(i);
			
			stemmer.add(word.toCharArray(), word.length());
			stemmer.stem();
			
			String resultWord = "";
			for (int j = 0; j < stemmer.getResultLength(); j++) {
				resultWord += Character.toString(stemmer.getResultBuffer()[j]);
			}
			
			words.set(i, resultWord);
		}
		
		// Concatenate words
		String result = "";
		for (int i = 0; i < words.size(); i++) {
			result += words.get(i);
			if (i < words.size() - 1) {
				result += " ";
			}
		}
		
		return result;
	}
	
	public static final String normalize(String s) {
		return deAccent(s).trim().toLowerCase();
	}
	
	private static final String deAccent(String s) {
		String d = Normalizer.normalize(s, Normalizer.Form.NFD);
		return d.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
}
