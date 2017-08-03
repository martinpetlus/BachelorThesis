package sk.stuba.fiit.learning_to_rank.learning;

import org.jblas.DoubleMatrix;


public interface ILossFunc {
	
	/**
	 * Computes and returns loss on data
	 * 
	 * @param w model
	 * @param X data
	 * @param y labels
	 * @return loss
	 */
	public double loss(DoubleMatrix w, DoubleMatrix X, DoubleMatrix y);
	
	/**
	 * Computes and returns gradient on data
	 * 
	 * @param w model
	 * @param X data
	 * @param y labels
	 * @return gradient
	 */
	public DoubleMatrix grad(DoubleMatrix w, DoubleMatrix X, DoubleMatrix y);

	/**
	 * Transforms the difference of the absolute labels (used in pairwise approach with
	 * dataset with absolute labels)
	 * 
	 * @param y y1 - y2
	 * @return transformed difference
	 */
	public double t(double y);
}
