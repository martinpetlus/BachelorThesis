package sk.stuba.fiit.learning_to_rank.eval;

import java.util.ArrayList;

import org.jblas.DoubleMatrix;

import sk.stuba.fiit.learning_to_rank.db.AnnotaManager;
import sk.stuba.fiit.learning_to_rank.learning.ILossFunc;
import sk.stuba.fiit.learning_to_rank.pairs.SearchLog;


public class LeraningToRankEvaluation {
	
	private int moreClicksLearningToRank;
	private int lessClicksLearningToRank;
	private int clicksTie;
	private int noClicks;

	public void evaluate(int userId) {
		moreClicksLearningToRank = 0;
		lessClicksLearningToRank = 0;
		clicksTie = 0;
		noClicks = 0;
		
		AnnotaManager annota = new AnnotaManager();
		ArrayList<EvaluationLog> evaluationLogs = annota.getEvaluationLogs(userId);
		annota.close();
		
		for (EvaluationLog evaluationLog : evaluationLogs) {
			SearchLog searchLog = evaluationLog.getSearchLog();
			CombinedRanking combinedRanking = evaluationLog.getCombinedRanking();
			
			int learningToRankRankingClicks = 0;
			int originalRankingClicks = 0;
			
			if (searchLog.getClickedIds().size() > 0) {
				int lowestClickedResult = -1;
				int lowestClickedDocId = -1;
				
				for (int clickedDocId : searchLog.getClickedIds()) {
					ArrayList<Integer> rankedResultsIds = searchLog.getRankedResultsIds();
					
					int idx = rankedResultsIds.indexOf(clickedDocId);
					
					if (idx > lowestClickedResult && (combinedRanking.getOriginalRanking().contains(clickedDocId) ||
							combinedRanking.getLearningToRankRanking().contains(clickedDocId))) {
						
						lowestClickedDocId = clickedDocId;
						lowestClickedResult = idx;
					}
				}
				
				int lowestScanningPosition = 0;
				
				for (int i = 0; i <= lowestClickedResult; i++) {
					if (combinedRanking.getLearningToRankRanking().get(i).intValue() == lowestClickedDocId) {
						lowestScanningPosition = i;
						break;
					}
					
					if (combinedRanking.getOriginalRanking().get(i).intValue() == lowestClickedDocId) {
						lowestScanningPosition = i;
						break;
					}
				}
				
				ArrayList<Integer> learningToRankRanking = combinedRanking.getLearningToRankRanking();
				ArrayList<Integer> originalRanking = combinedRanking.getOriginalRanking();
				
				for (int clickedDocId : searchLog.getClickedIds()) {
					for (int i = 0; i <= lowestScanningPosition; i++) {
						if (clickedDocId == learningToRankRanking.get(i).intValue()) {
							learningToRankRankingClicks++;
						}
						
						if (clickedDocId == originalRanking.get(i).intValue()) {
							originalRankingClicks++;
						}
					}
				}
			}
			else  {
				noClicks++;
				continue;
			}
			
			if (learningToRankRankingClicks == originalRankingClicks) {
				clicksTie++;
			}
			else if (learningToRankRankingClicks > originalRankingClicks) {
				moreClicksLearningToRank++;
			}
			else {
				lessClicksLearningToRank++;
			}
		}
	}
	
	public void evaluateModels(DoubleMatrix X, DoubleMatrix y, DoubleMatrix models, 
			ILossFunc lossFunction) {
		
		for (int i = 0; i < models.rows; i++) {
			DoubleMatrix w = models.getRow(i).transpose();
			System.out.println(lossFunction.loss(w, X, y));
		}
	}
	
	public void sortWeights(int userId) {
	}
	
	public int getClicksTie() {
		return clicksTie;
	}

	public int getNoClicks() {
		return noClicks;
	}

	public int getMoreClicksLearningToRank() {
		return moreClicksLearningToRank;
	}

	public int getLessClicksLearningToRank() {
		return lessClicksLearningToRank;
	}
}
