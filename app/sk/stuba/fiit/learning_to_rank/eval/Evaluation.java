package sk.stuba.fiit.learning_to_rank.eval;

import java.io.IOException;

import org.jblas.*;

import sk.stuba.fiit.learning_to_rank.datasets.DatasetAbsoluteLabels;



public class Evaluation {
	
	private DatasetAbsoluteLabels test;
	
	public Evaluation(String testFilename, DoubleMatrix mu, DoubleMatrix sigma,
			boolean isBiasTerm) throws IOException {
		
		// Load test data set
		test = new DatasetAbsoluteLabels(testFilename, true);
		
		// Add column of ones
		if (isBiasTerm) {
			test.addColumnOfOnes();
		}
	}
	
	public double[] evaluateModelsWithMeanNDCG(DoubleMatrix models_w) {		
		double[] meanNDCGs = new double[models_w.rows];
		
		for (int i = 0; i < models_w.rows; i++)
			meanNDCGs[i] = computeNDCG(models_w.getRow(i).transpose()).mean();
		
		return meanNDCGs;
	}
	
	public final DoubleMatrix computeNDCG(DoubleMatrix w) {	
		DoubleMatrix ranks, ndcg, y, X, labels;
		int numQueries;
		
		X      = test.getXMatrix();
		labels = test.getyVector();
		
		numQueries = test.getNumQueries();
		ndcg = DoubleMatrix.zeros(numQueries, 1);
		
		// Compute predicted relevance scores
		y = X.mmul(w);
		
		int queryStart, queryEnd, numDocs;
		int[] idx;
		
		for (int i = 0; i < numQueries; i++) {
			queryStart = test.getQueryStart(i);
			queryEnd = test.getQueryEnd(i);
			
			numDocs = test.getNumDocsOfQuery(i);
			ranks = DoubleMatrix.zeros(numDocs, 1);
			
			idx = y.getRange(queryStart, queryEnd).sortingPermutation();
			
			for (int d = (numDocs - 1), r = 1; d >= 0; d--, r++) {
				ranks.put(idx[d], r);
			}
			
			ndcg.put(i, NDCG(labels.getRange(queryStart, queryEnd), ranks));
		}
		
		return ndcg;
	}
	
	public static final double NDCG(DoubleMatrix labels, DoubleMatrix ranks) {
		DoubleMatrix gains;
		double v, dcg, idcg;
		int nd;
		
		nd = labels.length;
		
		gains = new DoubleMatrix(nd, 1);
		
		for (int i = 0; i < nd; i++) {
			v = Math.pow(2.0, labels.get(i)) - 1.0;
			gains.put((int) ranks.get(i) - 1, v);
		}
		
		dcg = 0.0;
		
		for (int i = 0; i < Math.min(10, nd); i++) {
			dcg += gains.get(i) / (Math.log(2 + i) / Math.log(2));
		}
		
		gains.sorti();
		idcg = 0.0;
		
		for (int i = 0; i < Math.min(10, nd); i++) {
			idcg += gains.get(nd - 1 - i) / (Math.log(2 + i) / Math.log(2));
		}
		
		if (idcg != 0.0)
			return (dcg / idcg);
		else
			return 1.0;
	}	
}
