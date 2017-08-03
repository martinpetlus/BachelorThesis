package sk.stuba.fiit.learning_to_rank.learning;

import org.jblas.DoubleMatrix;

import sk.stuba.fiit.learning_to_rank.util.AppProperties;


public class LearningCurves {
	
	private double split;
	private final BatchGradientDescent alg;
	
	public LearningCurves() {
		split = 0.8;
		alg = new BatchGradientDescent();
	}
	
	public void run(DoubleMatrix X, DoubleMatrix y, ILossFunc lossFunc) {
		int idx = (int) (X.rows * split);
				
		run(X.getRange(0, idx, 0, X.columns), y.getRange(0, idx),
				X.getRange(idx + 1, X.rows, 0, X.columns), y.getRange(idx + 1, X.rows),
				lossFunc, 5);
	}
	
	private void run(DoubleMatrix trainX, DoubleMatrix trainy, 
			DoubleMatrix testX, DoubleMatrix testy, ILossFunc lossFunction, 
			int timesToDivideDataset) {
		
		if (trainX.rows < timesToDivideDataset) return;
		
		int partSize = (int) Math.floor((double) trainX.rows) / timesToDivideDataset;
		DoubleMatrix tX, ty;
		
		for (int i = 1; i <= timesToDivideDataset; i++) {
			if (i == timesToDivideDataset) {
				tX = trainX;
				ty = trainy;
			}
			else {
				tX = trainX.getRange(0, partSize * i, 0, trainX.columns);
				ty = trainy.getRange(0, partSize * i);
			}
			
			DoubleMatrix w = DoubleMatrix.zeros(trainX.columns);
			
			w = alg.run(lossFunction, w, tX, ty, AppProperties.iterations, AppProperties.alpha);
			
			System.out.printf("%f %f\n", lossFunction.loss(w, tX, ty),
				lossFunction.loss(w, testX, testy));
		}
	}
	
	public void setSplit(double percent) {
		if (split < 0.0 || split > 1.0) {
			throw new IllegalArgumentException("Illegal setSplit() argument");
		}
		split = percent;
	}
	
	public double getSplit() {
		return split;
	}
}
