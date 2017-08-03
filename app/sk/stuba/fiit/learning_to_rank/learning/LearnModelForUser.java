package sk.stuba.fiit.learning_to_rank.learning;

import java.util.ArrayList;
import java.util.HashMap;

import sk.stuba.fiit.learning_to_rank.db.AcmPapersManager;
import sk.stuba.fiit.learning_to_rank.documents.AcmPaper;
import sk.stuba.fiit.learning_to_rank.features.AcmPaperFeaturesExtractor;
import sk.stuba.fiit.learning_to_rank.pairs.Pair;
import sk.stuba.fiit.learning_to_rank.pairs.PairWithQuery;
import sk.stuba.fiit.learning_to_rank.pairs.PairwisePreferenceExtractor;
import sk.stuba.fiit.learning_to_rank.pairs.SearchLog;


public class LearnModelForUser {
	
	private final PairwisePreferenceExtractor pairwiseExtractor;
	private final HashMap<Integer, AcmPaper> acmPapersByAnnotaId;
	private final AcmPapersManager papersManager;
	private ArrayList<Integer> notClickedAcmPapersIds;

	public LearnModelForUser(AcmPapersManager papersManager, HashMap<Integer, AcmPaper> acmPapersByAnnotaId) {
		this.acmPapersByAnnotaId = acmPapersByAnnotaId;
		this.pairwiseExtractor = new PairwisePreferenceExtractor();
		this.papersManager = papersManager;
	}

	public Model learn(ArrayList<SearchLog> userSearchLogs) {
		ArrayList<PairWithQuery> acmPaperPreferences = new ArrayList<PairWithQuery>();
		
		for (SearchLog searchLog : userSearchLogs) {
			ArrayList<Pair> allPreferences = pairwiseExtractor.extract(searchLog);
			
			for (Pair pair : allPreferences) {
				if (loadAcmPaper(pair.aId) && loadAcmPaper(pair.bId)) {
					acmPaperPreferences.add(new PairWithQuery(searchLog.getQuery(), pair));
				}
			}
			
			if (notClickedAcmPapersIds != null) {
				for (int annotaId : searchLog.getClickedIds()) {
					if (loadAcmPaper(annotaId)) {
						for (int i = 0; i < 25; i++) {
							int rndIndex = (int) (Math.random() * notClickedAcmPapersIds.size());
							int rndPaperId = notClickedAcmPapersIds.get(rndIndex);
							
							if (loadAcmPaper(rndPaperId)) {
								if (Math.random() < 0.5) {
									acmPaperPreferences.add(new PairWithQuery(searchLog.getQuery(), new Pair(rndPaperId, annotaId, -1)));
								}
								else {
									acmPaperPreferences.add(new PairWithQuery(searchLog.getQuery(), new Pair(annotaId, rndPaperId, 1)));
								}
							}
						}
					}
				}
			}
		}
		
		if (acmPaperPreferences.size() == 0) {
			return null;
		}
		
		AcmPaperFeaturesExtractor featuresExtractor = new AcmPaperFeaturesExtractor();
		
		System.out.println("Number of preferences (m): " + acmPaperPreferences.size());
		System.out.println("Number of features    (n): " + featuresExtractor.getNumberOfFeatures());

		LearnModel learn = new LearnModel(acmPapersByAnnotaId, featuresExtractor);
		return learn.learnModel(acmPaperPreferences);
	}

	private boolean loadAcmPaper(int annotaId) {
		AcmPaper paper;
		
		if ((paper = acmPapersByAnnotaId.get(annotaId)) == null) {
			if ((paper = papersManager.getAcmPaperByAnnotaId(annotaId)) != null) {
				acmPapersByAnnotaId.put(annotaId, paper);
			}
			else {
				return false;
			}
		}
		
		return true;
	}
	
	public ArrayList<Integer> getNotClickedAcmPapersIds() {
		return notClickedAcmPapersIds;
	}
	
	public void setNotClickedAcmPapersIds(ArrayList<Integer> notClickedAcmPapersIds) {
		this.notClickedAcmPapersIds = notClickedAcmPapersIds;
	}
}
