package sk.stuba.fiit.learning_to_rank.learning;

import org.jblas.DoubleMatrix;


public class HingeLossBased implements ILossFunc {
	
	private boolean isBiasTerm;
	private double lambda;
	
	public HingeLossBased() {
		this(0.0, false); // Without regularization term
	}
	
	/**
	 * Hinge loss constructor.
	 * 
	 * @param lambda lambda in regularization term
	 * @param isBiasTermIncluded if bias term is included in model
	 */
	public HingeLossBased(double lambda, boolean isBiasTermIncluded) {
		this.isBiasTerm = isBiasTermIncluded;
		this.lambda = lambda;
	}
	
	/**
	 * Returns loss on data.
	 * 
	 * @param w model w
	 * @param X X matrix from training set
	 * @param y y labels from training set for X
	 * @return loss on data
	 */
	public final double loss(DoubleMatrix w, DoubleMatrix X, DoubleMatrix y) {	
		DoubleMatrix sc;
		double reg, sum;
		
		int m = y.length;
		
		reg = (lambda / 2) * w.dot(w);
		
		sc = X.mmul(w);
		sum = sc.muli(y)
				.negi()
				.addi(1.0)
				.maxi(0.0)
				.sum();
		
		return (reg + sum / m);
	}
	
	/**
	 * Computes gradient on data.
	 * 
	 * @param w model w
	 * @param X X matrix from training set
	 * @param y y labels from training set for X
	 * @return gradient vector
	 */
	public final DoubleMatrix grad(DoubleMatrix w, DoubleMatrix X, DoubleMatrix y) {
		DoubleMatrix grad, z, _w, _X;
		
		int m = y.length;
		
		z = X.mmul(w);
		z.muli(y);
		z.lti(1.0);
		
		_X = X.mulColumnVector(z);
		_X.muliColumnVector(y.neg());
		_X = _X.columnSums().transpose();
		_X.divi(m);
		
		_w = w.dup();
		
		// Ignore bias term in regularization term, sometimes
		// bias term is not included in model w
		if (isBiasTerm) _w.put(0, 0.0);
		
		grad = _w.muli(lambda);
		grad.addi(_X);
		
		return grad;
	}
	
	/**
	 * Used for dataset with absolute labels.
	 * 
	 * @param y difference of two absolut labels y_a - y_b
	 * @return -1 or 1
	 */
	public final double t(double y) {
		if (y > 0)
			return 1;
		else
			return -1;
	}
	
	/**
	 * Sets lambda parameter in regularization term.
	 * 
	 * @param lambda parameter
	 */
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}
	
	/**
	 * Returns lambda.
	 * 
	 * @return lambda
	 */
	public double getLambda() {
		return lambda;
	}
	
	/**
	 * Sets if bias term is used in model.
	 * 
	 * @param isBiasTerm true or false
	 */
	public void setIsBiasTerm(boolean isBiasTerm) {
		this.isBiasTerm = isBiasTerm;
	}
	
	/**
	 * Returns if bias term is used in model.
	 * 
	 * @return true or false
	 */
	public boolean isBiasTerm() {
		return isBiasTerm;
	}
}
