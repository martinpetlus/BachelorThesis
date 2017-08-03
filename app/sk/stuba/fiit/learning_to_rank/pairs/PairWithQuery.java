package sk.stuba.fiit.learning_to_rank.pairs;


public class PairWithQuery {

	public final String query;
	public final Pair pair;
	
	public PairWithQuery(String query, Pair pair) {
		this.query = query;
		this.pair = pair;
	}
}
