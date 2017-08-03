package sk.stuba.fiit.learning_to_rank.datasets;

import java.io.*;
import java.util.ArrayList;
import java.util.Vector;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;


public class DatasetAbsoluteLabels {
	
	private class QueryBoundary {
		public int start;
		public int end;
		
		public QueryBoundary(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}
	
	private DoubleMatrix X;
	private DoubleMatrix y;
	
	private DoubleMatrix mean;
	private DoubleMatrix sigma;
	
	private ArrayList<QueryBoundary> boundariesOfQueries;
	private ArrayList<Integer> qids;
	
	private boolean isColumnOfOnes;
	private boolean isNormalized;
	
	private int m;
	private int n;
	
	public DatasetAbsoluteLabels(String fileName, boolean normalize) throws IOException {
		this(fileName);	
		if (normalize) {
			normalize();
		}
	}
	
	public DatasetAbsoluteLabels(String fileName, DoubleMatrix mean, DoubleMatrix sigma) throws IOException {
		this(fileName);
		normalize(mean, sigma);
	}

	public DatasetAbsoluteLabels(String fileName) throws IOException {
		BufferedReader file = new BufferedReader(new FileReader(fileName));
		Vector<String> lines = new Vector<String>();
		
		String line;
		m = 0;
		
		while ((line = file.readLine()) != null) {
			line = line.trim();
			
			if (line.length() > 0) {
				lines.add(line);
				m++;
			}
		}
		
		file.close();
		
		if (lines.size() > 0) {
			String delims = "[: ]+";
			
			n = (lines.get(0).split(delims).length - 3) / 2;
			
			X = new DoubleMatrix(m, n);
			y = new DoubleMatrix(m);
			
			qids = new ArrayList<Integer>();
			boundariesOfQueries = new ArrayList<QueryBoundary>();
			
			int qStart = 0, qEnd = 1, curr_qid = -1;
			
			for (int i = 0; i < lines.size(); i++) {	
				String[] tokens = lines.get(i).split(delims);
				
				if (n != (tokens.length - 3) / 2) {
					System.err.println("Error: inconsistent number of features in file " + fileName + "...");
					System.exit(1);
				}
				
				double ty = Double.parseDouble(tokens[0]);
				int qid = Integer.parseInt(tokens[2]);
				
				y.put(i, ty);
				qids.add(qid);
				
				if (curr_qid != qid) {
					if (i > 0) { // Not first example
						boundariesOfQueries.add(new QueryBoundary(qStart, qEnd));
						qStart = qEnd++;
					}
					curr_qid = qid;
				}
				else {
					qEnd++;
				}
				
				// Read features in line
				for (int j = 4, k = 0; j < tokens.length; j += 2, k++) {
					double feature = Double.parseDouble(tokens[j]);
					X.put(i, k, feature);
				}
			}
			boundariesOfQueries.add(new QueryBoundary(qStart, qEnd));
			
			isColumnOfOnes = false;
			isNormalized = false;
		}
	}
	
	private void normalize() {
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
	
	private void normalize(DoubleMatrix mean, DoubleMatrix sigma) {
		this.mean = mean;
		this.sigma = sigma;
		
		X.subiRowVector(mean);
		X.diviRowVector(sigma);
		
		isNormalized = true;
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
	
	public DoubleMatrix getXMatrix() {
		return X;
	}
	
	public DoubleMatrix getyVector() {
		return y;
	}
	
	public ArrayList<Integer> getqidsVector() {
		return qids;
	}
	
	public int getNumExamples() {
		return m;
	}
	
	public int getNumFeatures() {
		return n;
	}
	
	public DoubleMatrix getSigma() {
		return sigma;
	}
	
	public DoubleMatrix getMean() {
		return mean;
	}
	
	public boolean isNormalized() {
		return isNormalized;
	}
	
	public int getNumQueries() {
		if (boundariesOfQueries != null)
			return boundariesOfQueries.size();
		else
			return -1;
	}
	
	private boolean checkIndexToBoundariesOfQueries(int idx) {
		return boundariesOfQueries != null && idx >= 0 && idx < boundariesOfQueries.size();
	}
	
	public int getNumDocsOfQuery(int idx) {
		if (checkIndexToBoundariesOfQueries(idx))
			return boundariesOfQueries.get(idx).end - boundariesOfQueries.get(idx).start;
		else
			return -1;
	}
	
	private int getQueryBoundary(int idx, int b) {
		if (checkIndexToBoundariesOfQueries(idx)) {
			if (b == 0)
				return boundariesOfQueries.get(idx).start;
			else
				return boundariesOfQueries.get(idx).end;
		}
		else
			return -1;
	}
	
	public int getQueryStart(int idx) {
		return getQueryBoundary(idx, 0);
	}
	
	public int getQueryEnd(int idx) {
		return getQueryBoundary(idx, 1);
	}
}
