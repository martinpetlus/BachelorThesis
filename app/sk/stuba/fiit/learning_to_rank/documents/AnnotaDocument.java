package sk.stuba.fiit.learning_to_rank.documents;

import sk.stuba.fiit.learning_to_rank.parsing.Parser;


public class AnnotaDocument extends Document {

	private int id;
	private int bookmarkCount;
	private String[] topTags;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public int getBookmarkCount() {
		return bookmarkCount;
	}
	
	public void setBookmarkCount(int bookmarkCount) {
		this.bookmarkCount = bookmarkCount;
	}
	
	public String[] getTopTags() {
		return topTags;
	}
	
	public void setTopTags(String[] topTags) {
		this.topTags = new String[topTags.length];
		
		for (int i = 0; i < topTags.length; i++) {
			this.topTags[i] = Parser.stemAndRemoveNonWords(Parser.normalize(topTags[i]));
		}
	}
}
