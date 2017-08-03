package sk.stuba.fiit.learning_to_rank.datasets;

import java.util.Vector;


import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import sk.stuba.fiit.learning_to_rank.learning.ILossFunc;


public class DocumentPairs {
	
	private static class Pair {
		public int rowA;
		public int rowB;
		public double y;
		
		public Pair(int rowA, int rowB, double y) {
			this.rowA = rowA;
			this.rowB = rowB;
			this.y = y;
		}
	}
	
	private DoubleMatrix X;
	private DoubleMatrix y;
	private DoubleMatrix mean;
	private DoubleMatrix sigma;
	
	private boolean isColumnOfOnes;
	private boolean isNormalized;
	
	private int m;
	private int n;

	public DocumentPairs(DatasetAbsoluteLabels ds, ILossFunc lossFunc) {
		Vector<Pair> pairs = new Vector<Pair>();
		
		DoubleMatrix _X = ds.getXMatrix();
		DoubleMatrix _y = ds.getyVector();
		
		n = ds.getNumFeatures();
		m = 0;
		
		int numQueries = ds.getNumQueries(), docs = 0;
		
		// Compute all possible pairs, have to be done first because
		// concatHorizontally si very computative expensive
		for (int q = 0; q < numQueries; q++) {
			int numDocsOfQuery = ds.getNumDocsOfQuery(q);
			
			for (int i = 0; i < numDocsOfQuery; i++) {
				for (int j = i + 1; j < numDocsOfQuery; j++) {				
					double ya = _y.get(i + docs);
					double yb = _y.get(j + docs);
					
					if (ya != yb) {
						pairs.add(new Pair(i + docs, j + docs, lossFunc.t(ya - yb)));
						m++;
					}
				}
			}
			docs += ds.getNumDocsOfQuery(q);
		}
		
		X = DoubleMatrix.zeros(m, n);
		y = DoubleMatrix.zeros(m);
		
		for (int i = 0; i < pairs.size(); i++) {
			Pair p = pairs.get(i);
			
			DoubleMatrix a = _X.getRow(p.rowA);
			DoubleMatrix b = _X.getRow(p.rowB);
			
			X.putRow(i, a.subi(b));
			y.put(i, p.y);
		}
	}
	
	public DocumentPairs(DoubleMatrix docPairsFeatures, DoubleMatrix y) {
		this.X = docPairsFeatures;
		this.y = y;
		
		this.m = y.getLength();
		this.n = X.getColumns();
		
		this.isColumnOfOnes = false;
		this.isNormalized = false;
	}
	
	public DoubleMatrix getXMatrixPairs() {
		return X;
	}
	
	public DoubleMatrix getyVectorPairs() {
		return y;
	}
	
	public int getNumPairs() {
		return m;
	}
	
	public int getNumFeatures() {
		return n;
	}
	
	public void addColumnOfOnes() {
		if (!isColumnOfOnes) {
			DoubleMatrix ones = DoubleMatrix.ones(m, 1);
			X = DoubleMatrix.concatHorizontally(ones, X);
			isColumnOfOnes = true;
			n++;
		}
	}
	
	public boolean isColumnOfOnesAdded() {
		return isColumnOfOnes;
	}
	
	public void normalize() {
		if (!isNormalized) {
			mean = X.columnMeans().transpose();
			
			// Ignore features with std(X(col)) = 0,
			// are all set to have value of zero
			X.subiRowVector(mean);
			
			sigma = X.mul(X);
			sigma = sigma.columnSums().transpose();
			sigma.divi(m - 1);
			MatrixFunctions.sqrti(sigma);
		
			// If std(X(col)) = 0, then all elems of feature
			// column are same and we ignore them in model w
			for (int i = 0; i < sigma.length; i++) {
				if (sigma.get(i) == 0.0) {
					sigma.put(i, 1.0);
				}
			}
			
			X.diviRowVector(sigma);			
			isNormalized = true;
		}
	}
	
	public boolean isNormalized() {
		return isNormalized;
	}
	
	public DoubleMatrix getMean() {
		return mean;
	}
	
	public DoubleMatrix getSigma() {
		return sigma;
	}
}
