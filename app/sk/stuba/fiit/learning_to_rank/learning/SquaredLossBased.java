package sk.stuba.fiit.learning_to_rank.learning;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;


public class SquaredLossBased implements ILossFunc {
	
	private boolean isBiasTerm;
	private double lambda;
	
	public SquaredLossBased() {
		// Without regularization term
		this(0.0, false);
	}
	
	public SquaredLossBased(double lambda, boolean isBiasTermIncluded) {
		this.isBiasTerm = isBiasTermIncluded;
		this.lambda = lambda;
	}

	public double loss(DoubleMatrix w, DoubleMatrix X, DoubleMatrix y) {
		DoubleMatrix sc;
		double sum, reg;
		
		int m = y.length;
		
		reg = (lambda / (2 * m)) * w.dot(w);
		
		sc = X.mmul(w);		
		sum = MatrixFunctions.powi(sc.subiColumnVector(y), 2).sum();
		
		return (reg + sum / (2 * m));
	}

	public DoubleMatrix grad(DoubleMatrix w, DoubleMatrix X, DoubleMatrix y) {
		DoubleMatrix _y, _w, _x, grad;
		
		int m = y.length;
		
		_y = X.mmul(w).subiColumnVector(y);
		_x = X.transpose().mmul(_y).divi(m);
		
		_w = w.dup();
		
		// Ignore bias term in regularization term, sometimes
		// bias term is not included in model w
		if (isBiasTerm) _w.put(0, 0.0);
		
		grad = _w.muli(lambda / m);
		grad.addi(_x);
		
		return grad;
	}

	public double t(double y) {
		return y;
	}
	
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}
	
	public double getLambda() {
		return lambda;
	}
	
	public void setIsBiasTerm(boolean isBiasTerm) {
		this.isBiasTerm = isBiasTerm;
	}
	
	public boolean isBiasTerm() {
		return isBiasTerm;
	}
}
