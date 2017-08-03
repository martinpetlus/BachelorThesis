package sk.stuba.fiit.learning_to_rank.util;

import java.util.HashSet;


public class CosineSimilarity {
	
	/**
	 * Returns union of two string arrays
	 * 
	 * @param one first array of strings
	 * @param two second array of strings
	 * @return union of two string arrays
	 */
	public static String[] unionOfStringArrays(String[] one, String[] two) {
		HashSet<String> stringSet = new HashSet<String>();

		for (String el : one) {
			stringSet.add(el);
		}
		
		for (String el : two) {
			stringSet.add(el);
		}
		
		return stringSet.toArray(new String[stringSet.size()]);
	}
	
	/**
	 * Counts occurrence of strings in array of strings
	 * 
	 * @param a array of strings to count occurrence in
	 * @param union array of strings to count occurrence
	 * @return occurrence vector
	 */
	private static Integer[] occurenceVectorWithFrequency(String[] a, String[] union) {
		Integer[] vector = new Integer[union.length];
		int i = 0;
		
		for (String el : union) {
			vector[i++] = countOccurence(el, a);
		}
		
		return vector;
	}
	
	/**
	 * Counts occurrence of string in string array
	 * 
	 * @param s string to count
	 * @param a array of strings
	 * @return number of occurrences
	 */
	private static int countOccurence(String s, String[] a) {
		int count = 0;
		
		for (String el : a) {
			if (el.equals(s)) {
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Computes dot product between two vectors
	 * 
	 * @param vector1 first vector
	 * @param vector2 second vector
	 * @param length length of vectors
	 * @return dot product
	 */
	public static double dotProduct(Integer[] vector1, Integer[] vector2, int length) {
		double dotProd = 0.0;
		
		for (int i = 0; i < length; i++) {
			dotProd += vector1[i] * vector2[i];
		}
		
		return dotProd;
	}
	
	/**
	 * Returns size of vector
	 * 
	 * @param vector vector
	 * @return size of vector
	 */
	public static double magnitude(Integer[] vector) {
		double mag = 0.0;
		
		for (int i = 0; i < vector.length; i++) {
			mag += Math.pow(vector[i], 2);
		}
		
		return Math.sqrt(mag);
	}
	
	/**
	 * Calculates cosine similarity between two arrays of strings
	 * 
	 * @param one first array of strings
	 * @param two second array of strings
	 * @return cosine similarity
	 */
	public double calculate(String[] one, String[] two) {
		double sim;
		
		if (one.length == 0 || two.length == 0) {
			return 0.0;
		}
		
		String[] union = unionOfStringArrays(one, two);
		
		Integer[] vectorOneOccurence = occurenceVectorWithFrequency(one, union);
		Integer[] vectorTwoOccurence = occurenceVectorWithFrequency(two, union);
		
		double vectorOneMagnitude = magnitude(vectorOneOccurence);
		double vectorTwoMagnitude = magnitude(vectorTwoOccurence);
		
		sim = dotProduct(vectorOneOccurence, vectorTwoOccurence, union.length) /
					(vectorOneMagnitude * vectorTwoMagnitude);
		
		return sim;
	}
}
