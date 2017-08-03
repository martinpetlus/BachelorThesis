package sk.stuba.fiit.learning_to_rank.features;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.stuba.fiit.learning_to_rank.db.AcmPapersManager;
import sk.stuba.fiit.learning_to_rank.db.AnnotaManager;


public class AuthorsInstitutionsPublishers {
	
	private static Map<String, Integer> indices;
	
	static {
		loadFromAcmPapers();
	}
	
	public static void loadFromAnnota() {
		AnnotaManager m = new AnnotaManager();
		indices = m.getAuthorsPublishersInstitutions();
		m.close();
	}
	
	public static void loadFromAcmPapers() {
		indices = new HashMap<String, Integer>();
		AcmPapersManager papersManager = new AcmPapersManager();
		int idx = 0;
		
		List<String> allAuthors =  papersManager.getAllAuthors();
		List<String> allPublishers = papersManager.getAllPublishers();
		List<String> allInstitutions = papersManager.getAllInstitutions();
		
		papersManager.close();
		
		for (String author : allAuthors) {
			if (!indices.containsKey(author)) {
				indices.put(author, idx++);
			}
		}
		
		for (String publisher : allPublishers) {
			if (!indices.containsKey(publisher)) {
				indices.put(publisher, idx++);
			}
		}
		
		for (String institution : allInstitutions) {
			if (!indices.containsKey(institution)) {
				indices.put(institution, idx++);
			}
		}
	}

	public static void save(AnnotaManager annotaManager) {
		annotaManager.saveAuthorsPublishersInstitutions(indices);
	}
	
	public static int getIndex(String authorOrInstitutionOrPublisher) {
		Integer i = indices.get(authorOrInstitutionOrPublisher);
		if (i != null) {
			return i.intValue();
		}
		else {
			return -1;
		}
	}
	
	public static int size() {
		return indices.size();
	}
}
