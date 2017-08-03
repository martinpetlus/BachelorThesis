package sk.stuba.fiit.learning_to_rank.parsing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import sk.stuba.fiit.learning_to_rank.documents.AcmPaper;


public class AcmPaperParser extends Parser {
	
	private AcmPaper paper;
	
	/**
	 * Parses acm paper
	 * 
	 * @param html html of acm paper
	 * @return acm paper object
	 */
	public AcmPaper parse(String html) {
		String str, num;
		String[] tokens;
		Elements els;
		int idx;
		
		Document doc = Jsoup.parse(html);
		paper = new AcmPaper();
		
		paper.setTitleOfPaper(stemAndRemoveNonWords(normalize(doc.select("meta[name=citation_title]").attr("content"))));
		paper.setPublisher(normalize(doc.select("meta[name=citation_publisher]").attr("content")));
		
		els = doc.select("div[class=flatbody] > div");
		if (els.size() > 0) {
			paper.setAbstractOfPaper(stemAndRemoveNonWords(normalize(els.first().text())));
		} else {
			paper.setAbstractOfPaper("");
		}
		
		str = doc.select("meta[name=citation_keywords]").attr("content");
		tokens = str.split(";");
		for (int i = 0; i < tokens.length; i++) {
			paper.addKeyword(stemAndRemoveNonWords(normalize(tokens[i])));
		}
		
		str = doc.select("div[id=divmain] td[class=small-text]:matchesOwn(Downloads \\(cumulative\\): [,\\d]+)").text().trim();
		if (str.length() > 0) {
			idx = str.indexOf("Downloads (cumulative): ");
			num = str.substring(idx + 24, str.indexOf(' ', idx + 24));
			num = num.replaceAll(",", "");
			paper.setDownloadsCount(Integer.parseInt(num));
		}
		else {
			paper.setDownloadsCount(0);
		}
		
		str = doc.select("div[id=divmain] td[class=small-text]:matchesOwn(Citation Count: [,\\d]+)").text().trim();
		if (str.length() > 0) {
			idx = str.indexOf("Citation Count: ");
			num = str.substring(idx + 16);
			num = num.replaceAll(",", "");
			paper.setCitationsCount(Integer.parseInt(num));
		}
		else {
			paper.setCitationsCount(0);
		}
		
		str = doc.select("div[id=fback] table[class=medium-text] td:matchesOwn(Overall Acceptance Rate "
				+ "[,\\d]+ of [,\\d]+ submissions, \\d+%)").text().trim();
		if (str.length() > 0) {
			num = str.substring(str.lastIndexOf(' ') + 1, str.length() - 1);
			paper.setAcceptanceRate(Integer.parseInt(num));
		}
		else {
			paper.setAcceptanceRate(100);
		}
		
		str =  doc.select("meta[name=citation_date]").attr("content");
		if (str.length() > 0) {
			tokens = str.split("/");
			paper.setYear(Integer.parseInt(tokens[tokens.length - 1]));
		}
		else {
			paper.setYear(0);
		}
		
		String firstPage = doc.select("meta[name=citation_firstpage]").attr("content"),
				lastPage = doc.select("meta[name=citation_lastpage]").attr("content");	
		if (firstPage.length() != 0 && lastPage.length() != 0) {
			paper.setNumberOfPages(Integer.parseInt(lastPage) - Integer.parseInt(firstPage) + 1);
		}
		else {
			paper.setNumberOfPages(0);
		}
		
		str = doc.select("meta[name=citation_authors]").attr("content");
		tokens = str.split(";");
		for (int i = 0; i < tokens.length; i++) {
			paper.addAuthor(normalize(tokens[i]));
		}
		
		els = doc.select("div[id=divmain] a[title=Institutional Profile Page]");
		for (int i = 0; i < els.size(); i++) {
			paper.addInstitution(normalize(els.get(i).text()));
		}
		
		paper.setAnnotaId(-1);
		paper.setAnnotaBookmarkCount(0);
		
		return paper;
	}
}
