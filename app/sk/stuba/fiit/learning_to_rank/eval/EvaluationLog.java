package sk.stuba.fiit.learning_to_rank.eval;

import sk.stuba.fiit.learning_to_rank.pairs.SearchLog;


public class EvaluationLog {

	private final SearchLog searchLog;
	private final CombinedRanking combinedRanking;
	
	public EvaluationLog(SearchLog searchLog, CombinedRanking combinedRanking) {
		this.searchLog = searchLog;
		this.combinedRanking = combinedRanking;
	}
	
	public SearchLog getSearchLog() {
		return searchLog;
	}
	
	public CombinedRanking getCombinedRanking() {
		return combinedRanking;
	}
}
