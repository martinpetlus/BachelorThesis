package sk.stuba.fiit.learning_to_rank.features;

import java.util.List;

import org.jblas.DoubleMatrix;

import sk.stuba.fiit.learning_to_rank.documents.AcmPaper;
import sk.stuba.fiit.learning_to_rank.documents.Document;
import sk.stuba.fiit.learning_to_rank.parsing.Parser;
import sk.stuba.fiit.learning_to_rank.util.CosineSimilarity;


public class AcmPaperFeaturesExtractor implements FeaturesExtractor {
	
	private static final int DOCUMENT_FEATURES = 13;
	private final CosineSimilarity sim = new CosineSimilarity();
	
	/**
	 * Returns extraxted features as vector from document. 
	 * 
	 * @param document document to extract features from
	 * @param query for query-dependet features
	 * @return extracted features vector
	 */
	@Override
	public DoubleMatrix extract(Document document, String query) {
		DoubleMatrix features = DoubleMatrix.zeros(DOCUMENT_FEATURES + AuthorsInstitutionsPublishers.size());
		AcmPaper paper = (AcmPaper) document;
		
		String[] queryTerms = Parser.stemAndRemoveNonWords(Parser.normalize(query)).split("[\\s\\W]+");
		
		features.put(0, cosineSimilarity(queryTerms, paper.getTitleOfPaper()));
		features.put(1, cosineSimilarity(queryTerms, paper.getAbstractOfPaper()));
		features.put(2, cosineSimilarity(queryTerms, join(paper.getKeywordsOfPaper())));
		features.put(3, cosineSimilarity(queryTerms, join(paper.getAnnotaTopTags())));
		
		features.put(4, Math.log(paper.getDownloadsCount() + 1.));
		features.put(5, Math.log(paper.getCitationsCount() + 1.));
		features.put(6, Math.log(paper.getNumberOfPages()  + 1.));
		features.put(7, Math.log(paper.getAnnotaBookmarkCount() + 1.));
		features.put(8, Math.log(100. / paper.getAcceptanceRate()));
		
		int year = paper.getYear();
		
		if (year >= 2011) {
			features.put(9, 1.);
		}
		if (year >= 2004) {
			features.put(10, 1.);
		}
		if (year >= 1999) {
			features.put(11, 1.);
		}
		if (year >= 1980) {
			features.put(12, 1.);
		}
		
		if (AuthorsInstitutionsPublishers.size() > 0) {
			List<String> tmp;
			int idx;
			
			tmp = paper.getAuthors();
			for (String author : tmp) {
				idx = AuthorsInstitutionsPublishers.getIndex(author);
				if (idx >= 0) {
					features.put(idx + DOCUMENT_FEATURES, 1.0);
				}
			}
			
			tmp = paper.getInstitutions();
			for (String institution : tmp) {
				idx = AuthorsInstitutionsPublishers.getIndex(institution);
				if (idx >= 0) {
					features.put(idx + DOCUMENT_FEATURES, 1.0);
				}
			}
			
			idx = AuthorsInstitutionsPublishers.getIndex(paper.getPublisher());
			if (idx >= 0) {
				features.put(idx + DOCUMENT_FEATURES, 1.0);
			}
		}
		
		return  features;
	}
	
	/**
	 * Returns number of features this extractor extracts.
	 * 
	 * @return number of extracting features
	 */
	@Override
	public int getNumberOfFeatures() {
		return DOCUMENT_FEATURES + AuthorsInstitutionsPublishers.size();
	}
	
	/**
	 * Calculates cosine similarity between terms and text.
	 * 
	 * @param terms array of terms
	 * @param text text to calculate cosine similarity
	 * @return cosine similarity
	 */
	public double cosineSimilarity(String[] terms, String text) {
		String[] textWords = text.split("[\\s\\W]+");
		return sim.calculate(terms, textWords);
	}
	
	/**
	 * Joins elements of list with space.
	 * 
	 * @param words elements of list
	 * @return joined elements with space
	 */
	private static String join(List<String> words) {
		String result = "";
		
		for (int i = 0; i < words.size(); i++) {
			result += words.get(i);
			if (i < words.size() - 1) {
				result += " ";
			}
		}
		
		return result;
	}
}
