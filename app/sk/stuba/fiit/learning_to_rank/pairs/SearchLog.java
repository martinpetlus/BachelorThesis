package sk.stuba.fiit.learning_to_rank.pairs;

import java.util.ArrayList;


public class SearchLog {

	private int id;
	private int userId;
	private ArrayList<Integer> rankedResultsIds;
	private ArrayList<Integer> clickedIds;
	private String query;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public ArrayList<Integer> getRankedResultsIds() {
		return rankedResultsIds;
	}
	
	public void setRankedResultsIds(Integer[] rankedResultsIds) {
		this.rankedResultsIds = new ArrayList<Integer>(rankedResultsIds.length);
		
		for (int docId : rankedResultsIds) {
			this.rankedResultsIds.add(docId);
		}
	}
	
	public ArrayList<Integer> getClickedIds() {
		return clickedIds;
	}
	
	public void setClickedIds(Integer[] clickedIds) {
		this.clickedIds = new ArrayList<Integer>(clickedIds.length);
		
		for (int docId : clickedIds) {
			this.clickedIds.add(docId);
		}
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}
}
