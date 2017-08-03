package sk.stuba.fiit.learning_to_rank.learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jblas.DoubleMatrix;

import sk.stuba.fiit.learning_to_rank.datasets.DocumentPairs;
import sk.stuba.fiit.learning_to_rank.documents.Document;
import sk.stuba.fiit.learning_to_rank.eval.LeraningToRankEvaluation;
import sk.stuba.fiit.learning_to_rank.features.FeaturesExtractor;
import sk.stuba.fiit.learning_to_rank.pairs.PairWithQuery;
import sk.stuba.fiit.learning_to_rank.util.AppProperties;


public class LearnModel {
	
	private final Map<Integer, HashMap<String, DoubleMatrix>> docFeaturesByIdAndQuery;
	private final Map<Integer, ? extends Document> documentById;
	private final BatchGradientDescent gradientDescent;
	private final FeaturesExtractor featuresExtractor;
	private final HingeLossBased hingeLossBased;
	
	/**
	 * Constructs learner.
	 * 
	 * @param documentById loaded documents in memory by their id
	 * @param featuresExtractor features extractor from documents
	 */
	public LearnModel(HashMap<Integer, ? extends Document> documentById, FeaturesExtractor featuresExtractor) {
		this.docFeaturesByIdAndQuery = new HashMap<Integer, HashMap<String, DoubleMatrix>>();
		this.gradientDescent = new BatchGradientDescent();
		this.hingeLossBased = new HingeLossBased();
		this.featuresExtractor = featuresExtractor;
		this.documentById = documentById;
	}
	
	/**
	 * Learns model from a given pairs of documents and their preferences and queries associated with pairs.
	 * 
	 * @param pairs pairs of documents with queries
	 * @return learned model w with sigma vector and mean vector
	 */
	public Model learnModel(ArrayList<PairWithQuery> pairs) {
		DoubleMatrix features = new DoubleMatrix(pairs.size(), featuresExtractor.getNumberOfFeatures());
		DoubleMatrix y = new DoubleMatrix(pairs.size());
		int i = 0;
		
		docFeaturesByIdAndQuery.clear();
		
		for (PairWithQuery p : pairs) {
			DoubleMatrix vecA = getFeaturesVector(p.pair.aId, p.query);
			DoubleMatrix vecB = getFeaturesVector(p.pair.bId, p.query);
			features.putRow(i, vecA.sub(vecB));
			y.put(i++, p.pair.pref);
		}
		
		boolean isBiasTerm = false;
		
		DocumentPairs docPairs = new DocumentPairs(features, y);
		if (true) {
			docPairs.normalize();
		}
		
		hingeLossBased.setIsBiasTerm(isBiasTerm);
		hingeLossBased.setLambda(AppProperties.lambda);
		
		if (isBiasTerm) {
			docPairs.addColumnOfOnes();
		}
		
		//new LearningCurves().run(docPairs.getXMatrixPairs(), docPairs.getyVectorPairs(), hingeLossBased);
		
		DoubleMatrix w = DoubleMatrix.zeros(docPairs.getNumFeatures());
		
		// gradientDescent.setOutputModelAfterIters(10);
		
		w = gradientDescent.run(hingeLossBased, w, docPairs.getXMatrixPairs(), docPairs.getyVectorPairs(), 
				AppProperties.iterations, AppProperties.alpha);
		
		/*new LeraningToRankEvaluation().evaluateModels(docPairs.getXMatrixPairs(), docPairs.getyVectorPairs(),
				gradientDescent.getOutputModels(), hingeLossBased);*/
		
		return new Model(w, docPairs.getMean(), docPairs.getSigma());
	}
	
	/**
	 * Returns weight vector of document given by id and query.
	 * 
	 * @param id id of document
	 * @param query query
	 * @return weight vector
	 */
	private DoubleMatrix getFeaturesVector(int id, String query) {
		HashMap<String, DoubleMatrix> map; 
		DoubleMatrix vector = null;
		
		if (!docFeaturesByIdAndQuery.containsKey(id)) {
			map = new HashMap<String, DoubleMatrix>();
			docFeaturesByIdAndQuery.put(id, map);
			vector = featuresExtractor.extract(documentById.get(id), query);
			map.put(query, vector);
		}
		else {
			map = docFeaturesByIdAndQuery.get(id);
			
			if (map.containsKey(query)) {
				vector = map.get(query);
			}
			else {
				vector = featuresExtractor.extract(documentById.get(id), query);
				map.put(query, vector);
			}
		}
		
		return vector;
	}
}
