package sk.stuba.fiit.learning_to_rank.documents;

import java.util.ArrayList;


public class AcmPaper extends Document {
	
	private String titleOfPaper;
	private String abstractOfPaper;
	private String publisher;
	private int downloadsCount;
	private int citationsCount;
	private int acceptanceRate;
	private int numberOfPages;
	private int year;
	private final ArrayList<String> authors;
	private final ArrayList<String> institutions;
	private final ArrayList<String> keywordsOfPaper;
	private int annotaBookmarkCount;
	private int annotaId;
	private final ArrayList<String> annotaTopTags;

	public AcmPaper() {
		authors = new ArrayList<String>();
		institutions = new ArrayList<String>();
		keywordsOfPaper = new ArrayList<String>();
		annotaTopTags = new ArrayList<String>();
	}
	
	public String getTitleOfPaper() {
		return titleOfPaper;
	}
	
	public void setTitleOfPaper(String titleOfPaper) {
		this.titleOfPaper = titleOfPaper;
	}
	
	public String getAbstractOfPaper() {
		return abstractOfPaper;
	}
	
	public void setAbstractOfPaper(String abstractOfPaper) {
		this.abstractOfPaper = abstractOfPaper;
	}
	
	public ArrayList<String> getKeywordsOfPaper() {
		return keywordsOfPaper;
	}
	
	public void addKeyword(String keyword) {
		if (keyword.length() > 0 && !keywordsOfPaper.contains(keyword)) {
			keywordsOfPaper.add(keyword);
		}
	}
	
	public void setKeywords(String[] keywords) {
		for (String keyword : keywords) {
			addKeyword(keyword);
		}
	}
	
	public String getPublisher() {
		return publisher;
	}
	
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
	public int getDownloadsCount() {
		return downloadsCount;
	}
	
	public void setDownloadsCount(int downloadsCount) {
		this.downloadsCount = downloadsCount;
	}
	
	public int getCitationsCount() {
		return citationsCount;
	}
	
	public void setCitationsCount(int citationsCount) {
		this.citationsCount = citationsCount;
	}
	
	public int getAcceptanceRate() {
		return acceptanceRate;
	}
	
	public void setAcceptanceRate(int acceptanceRate) {
		this.acceptanceRate = acceptanceRate;
	}
	
	public int getNumberOfPages() {
		return numberOfPages;
	}
	
	public void setNumberOfPages(int numberOfPages) {
		this.numberOfPages = numberOfPages;
	}
	
	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public ArrayList<String> getAuthors() {
		return authors;
	}
	
	public void addAuthor(String author) {
		if (author.length() > 0 &&!authors.contains(author)) {
			authors.add(author);
		}
	}
	
	public void setAuthors(String[] authors) {
		for (String author : authors) {
			addAuthor(author);
		}
	}
	
	public ArrayList<String> getInstitutions() {
		return institutions;
	}
	
	public void addInstitution(String institution) {
		if (institution.length() > 0 && !institutions.contains(institution)) {
			institutions.add(institution);
		}
	}
	
	public void setInstitutions(String[] institutions) {
		for (String institution : institutions) {
			addInstitution(institution);
		}
	}
	
	public int getAnnotaBookmarkCount() {
		return annotaBookmarkCount;
	}

	public void setAnnotaBookmarkCount(int annotaBookmarkCount) {
		this.annotaBookmarkCount = annotaBookmarkCount;
	}
	
	public void addAnnotaTopTag(String topTag) {
		if (topTag.length() > 0 && !annotaTopTags.contains(topTag)) {
			annotaTopTags.add(topTag);
		}
	}

	public ArrayList<String> getAnnotaTopTags() {
		return annotaTopTags;
	}

	public void setAnnotaTopTags(String[] annotaTopTags) {
		for (String annotaTopTag : annotaTopTags) {
			addAnnotaTopTag(annotaTopTag);
		}
	}
	
	public void setAnnotaId(int annotaId) {
		this.annotaId = annotaId;
	}
	
	public int getAnnotaId() {
		return annotaId;
	}
	
	public static boolean isAcmPaperUrl(String url) {
		return url.startsWith("http://dl.acm.org/citation.cfm?id=") ||
				url.startsWith("https://dl.acm.org/citation.cfm?id=");
	}
	
	public String toString() {
		String s = "Title:       " + titleOfPaper + "\n"
				 + "Abstract:    " + abstractOfPaper + "\n"
				 + "Keywords:    " + keywordsOfPaper.toString() + "\n"
				 + "Publisher:   " + publisher + "\n"
				 + "Downloads:   " + downloadsCount + "\n"
				 + "Citations:   " + citationsCount + "\n"
				 + "Year:        " + year + "\n"
				 + "Acc Rate:    " + acceptanceRate + "\n"
				 + "Pages:       " + numberOfPages + "\n"
				 + "Authors:     " + authors.toString() + "\n"
				 + "Insts:       " + institutions.toString() + "\n"
				 + "AnntBkmCnt:  " + annotaBookmarkCount +"\n"
				 + "AnntId:      " + annotaId + "\n"
				 + "AnntTopTags: " + annotaTopTags.toString();
		
		return s;
	}
}
