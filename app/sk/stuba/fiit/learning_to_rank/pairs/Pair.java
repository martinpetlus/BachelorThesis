package sk.stuba.fiit.learning_to_rank.pairs;


public class Pair {

	public final int aId;
	public final int bId;
	public final int pref;
	
	public Pair(int aId, int bId, int pref) {
		this.aId = aId;
		this.bId = bId;
		this.pref = pref;
	}
}
