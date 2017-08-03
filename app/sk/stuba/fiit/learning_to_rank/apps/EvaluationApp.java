package sk.stuba.fiit.learning_to_rank.apps;

import java.util.List;

import sk.stuba.fiit.learning_to_rank.db.AnnotaManager;
import sk.stuba.fiit.learning_to_rank.eval.LeraningToRankEvaluation;


public class EvaluationApp {

	public static void main(String[] args) {
		LeraningToRankEvaluation eval = new LeraningToRankEvaluation();
		
		AnnotaManager annota = new AnnotaManager();
		List<Integer> userIds = annota.getAllUserIdsFromSearchLogs();
		annota.close();
		
		for (Integer userId : userIds) {
			eval.evaluate(userId.intValue());
		
			System.out.println("User id = " + userId);
			System.out.println("More clicks on learning to rank: " + eval.getMoreClicksLearningToRank());
			System.out.println("Less clicks on learning to rank: " + eval.getLessClicksLearningToRank());
			System.out.println("Clicks tie:                      " + eval.getClicksTie());
			System.out.println("No clicks:                       " + eval.getNoClicks() + "\n");
		}
	}

}
