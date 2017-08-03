package sk.stuba.fiit.learning_to_rank.learning;

import org.jblas.DoubleMatrix;


public class BatchGradientDescent {
	
	private boolean outputModels;
	private int outputAfterIter;
	private DoubleMatrix output;
	
	public BatchGradientDescent() {
		outputModels = false;
	}
	
	/**
	 * Learns model on training set with given loss function.
	 * 
	 * @param lossFunc loss function to use in learning
	 * @param w initial model w
	 * @param X X matrix from training set (rows are examples, columns are features)
	 * @param y y labels from training set for X
	 * @param iters number of iterations of algorithm
	 * @param alpha learning rate alpha
	 * @return learned model
	 */
	public final DoubleMatrix run(ILossFunc lossFunc, DoubleMatrix w, DoubleMatrix X,
			DoubleMatrix y, int iters, double alpha) {

		if (outputModels) {
			output = DoubleMatrix.zeros((int) Math.floor(iters / outputAfterIter), w.length);
		}
		
		DoubleMatrix _w = w.dup();
		
		for (int i = 1; i <= iters; i++) {
			_w.subi(lossFunc.grad(_w, X, y).muli(alpha));
			
			if (outputModels) {
				if ((i % outputAfterIter) == 0) {
					output.putRow(i / outputAfterIter - 1, _w.transpose());
				}
			}
		}
		
		return _w;
	}
	
	/**
	 * Outputs model for every number of iterations.
	 * 
	 * @param iter number of iterations to output model
	 */
	public void setOutputModelAfterIters(int iter) {
		if (iter > 0) {
			outputModels = true;
			outputAfterIter = iter;
		}
	}
	
	/**
	 * Truns off output models.
	 */
	public void turnOffOutputModels() {
		outputModels = false;
		output = null;
	}
	
	/**
	 * Returns outputed models after learning.
	 * 
	 * @return models in matrix
	 */
	public DoubleMatrix getOutputModels() {
		return output;
	}
}
