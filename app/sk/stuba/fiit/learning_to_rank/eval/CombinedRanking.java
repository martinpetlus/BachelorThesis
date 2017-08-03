package sk.stuba.fiit.learning_to_rank.eval;

import java.util.ArrayList;


public class CombinedRanking {
	
	private int searchLogId;
	private ArrayList<Integer> learningToRankRanking;
	private ArrayList<Integer> originalRanking;
	
	public int getSearchLogId() {
		return searchLogId;
	}
	
	public void setSearchLogId(int searchLogId) {
		this.searchLogId = searchLogId;
	}

	public ArrayList<Integer> getLearningToRankRanking() {
		return learningToRankRanking;
	}

	public void setLearningToRankRanking(Integer[] learningToRankRanking) {
		this.learningToRankRanking = new ArrayList<Integer>(learningToRankRanking.length);

		for (Integer docId : learningToRankRanking) {
			this.learningToRankRanking.add(docId);
		}
	}

	public ArrayList<Integer> getOriginalRanking() {
		return originalRanking;
	}

	public void setOriginalRanking(Integer[] originalRanking) {
		this.originalRanking = new ArrayList<Integer>(originalRanking.length);
		
		for (Integer docId : originalRanking) {
			this.originalRanking.add(docId);
		}
	}
}
