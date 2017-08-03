package sk.stuba.fiit.learning_to_rank.apps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sk.stuba.fiit.learning_to_rank.db.AnnotaManager;


public class WeightsApp {

	public static void main(String[] args) {
		AnnotaManager m = new AnnotaManager();
		List<Double> weights = m.getWeights(3);
		m.close();
		
		class WeightWithIndex {
			public final int index;
			public final double weight;
			
			public WeightWithIndex(int index, double weight) {
				this.index = index;
				this.weight = weight;
			}
		}
		
		List<WeightWithIndex> weightsWithIndex = new ArrayList<WeightWithIndex>();
		int i = 0;
		
		for (Double weight : weights) {
			weightsWithIndex.add(new WeightWithIndex(i++, weight.doubleValue()));
		}
		
		Collections.sort(weightsWithIndex, new Comparator<WeightWithIndex>() {

			@Override
			public int compare(WeightWithIndex w1, WeightWithIndex w2) {
				return (w1.weight > w2.weight ? -1 : (w1.weight == w2.weight ? 0 : 1));
			}
		});
		
		for (WeightWithIndex w : weightsWithIndex) {
			System.out.println(w.index + " " + w.weight);
		}
	
	}
}
