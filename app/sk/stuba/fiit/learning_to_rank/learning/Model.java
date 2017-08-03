package sk.stuba.fiit.learning_to_rank.learning;

import org.jblas.DoubleMatrix;


public class Model {

	private final DoubleMatrix weightVector;
	private final DoubleMatrix sigmaVector;
	private final DoubleMatrix meanVector;
	
	public Model(DoubleMatrix weightVector, DoubleMatrix meanVector, 
			DoubleMatrix sigmaVector) {
		
		this.weightVector = weightVector;
		this.meanVector = meanVector;
		this.sigmaVector = sigmaVector;
	}
	
	public DoubleMatrix getWeightVector() {
		return weightVector;
	}
	
	public DoubleMatrix getMeanVector() {
		return meanVector;
	}
	
	public DoubleMatrix getSigmaVector() {
		return sigmaVector;
	}
}
