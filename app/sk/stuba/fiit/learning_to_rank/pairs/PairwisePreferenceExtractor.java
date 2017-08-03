package sk.stuba.fiit.learning_to_rank.pairs;

import java.util.ArrayList;



public class PairwisePreferenceExtractor {
	
	private ArrayList<Pair> preferencePairs;
	
	/**
	 * Extracts preference pairs from search log
	 * 
	 * @param searchLog search log with ranked results and clicked results
	 * @return extracted pairs
	 */
	public ArrayList<Pair> extract(SearchLog searchLog) {
		preferencePairs = new ArrayList<Pair>();
		
		ArrayList<Integer> clickedIds = searchLog.getClickedIds();
		ArrayList<Integer> rankedResultsIds = searchLog.getRankedResultsIds();
		
		for (int i = 0; i < clickedIds.size(); i++) {
			int idx = rankedResultsIds.indexOf(clickedIds.get(i));
			
			for (int j = 0; j < idx; j++) {
				if (!clickedIds.contains(rankedResultsIds.get(j))) {
					preferencePairs.add(new Pair(clickedIds.get(i), rankedResultsIds.get(j), 1));
				}
			}
		}
		
		return preferencePairs;
	}
}
