package sk.stuba.fiit.learning_to_rank.features;

import org.jblas.DoubleMatrix;
import sk.stuba.fiit.learning_to_rank.documents.Document;


public interface FeaturesExtractor {
	
	public DoubleMatrix extract(Document document, String query);
	public int getNumberOfFeatures();
}
