package sk.stuba.fiit.learning_to_rank.db;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.HashSet;

import sk.stuba.fiit.learning_to_rank.documents.AcmPaper;
import sk.stuba.fiit.learning_to_rank.documents.AnnotaDocument;


public class AcmPapersManager extends DatabaseManager {
	
	private PreparedStatement saveAcmPaper;
	private PreparedStatement getAcmPaperByAnnotaId;
	
	public AcmPapersManager() {
		init();
	}
	
	private void init() {
		try {
			saveAcmPaper = conn.prepareStatement("insert into ltr_acm_papers (url, title, abstract, keywords, "
					+ "downloads_count, citations_count, acceptance_rate, number_of_pages, authors, "
					+ "institutions, publisher, year, annota_id, annota_bookmark_count, annota_top_tags, "
					+ "created_at, updated_at) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
			
			getAcmPaperByAnnotaId = conn.prepareStatement("select * from ltr_acm_papers where annota_id=? limit 1;");
		}
		catch (SQLException e) {
			printSQLException(e);
			System.exit(1);
		}
	}
	
	public void updateAnnotaData(ArrayList<AnnotaDocument> documents) {
		try {
			pStmt = conn.prepareStatement("update ltr_acm_papers set annota_bookmark_count=?, "
					+ "annota_id=?, annota_top_tags=? where url=?;");
			
			for (AnnotaDocument document : documents) {
				pStmt.setInt(1, document.getBookmarkCount());
				pStmt.setInt(2, document.getId());
				
				Array sqlArray = conn.createArrayOf("varchar", document.getTopTags());
				pStmt.setArray(3, sqlArray);
				
				pStmt.setString(4, document.getUrl());
				
				pStmt.executeUpdate();
			}
		}
		catch(SQLException e) {
			printSQLException(e);
		}
		finally {
			closePreparedStatement();
		}
	}
	
	public ArrayList<String> getAllPublishers() {
		ArrayList<String> publishers = new ArrayList<String>();
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select distinct publisher from ltr_acm_papers where char_length(publisher) > 0;");
			
			while (rs.next()) {
				publishers.add(rs.getString("publisher"));
			}
		}
		catch (SQLException e) {
			printSQLException(e);
		}
		finally {
			closeStatement();
			closeResultSet();
		}
		
		return publishers;
	}
	
	public ArrayList<String> getAllInstitutions() {
		ArrayList<String> institutions = new ArrayList<String>();
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select distinct unnest(institutions) as institution from ltr_acm_papers;");
			
			while (rs.next()) {
				institutions.add(rs.getString("institution"));
			}
		}
		catch (SQLException e) {
			printSQLException(e);
		}
		finally {
			closeStatement();
			closeResultSet();
		}
		
		return institutions;
	}
	
	public ArrayList<String> getAllAuthors() {
		ArrayList<String> authors = new ArrayList<String>();
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select distinct unnest(authors) as author from ltr_acm_papers;");
			
			while (rs.next()) {
				authors.add(rs.getString("author"));
			}
		}
		catch (SQLException e) {
			printSQLException(e);
		}
		finally {
			closeStatement();
			closeResultSet();
		}
		
		return authors;
	}
	
	public HashSet<String> getUrlsOfAllDownloadedAcmPapers() {
		HashSet<String> urls = new HashSet<String>();
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select url from ltr_acm_papers;");
			
			while (rs.next()) {
				urls.add(rs.getString("url"));
			}
		}
		catch (SQLException e) {
			printSQLException(e);
		}
		finally {
			closeStatement();
			closeResultSet();
		}
		
		return urls;
	}
	
	public boolean saveAcmPaper(AcmPaper paper) {
		boolean b = false;
		
		try {
			saveAcmPaper.setString(1, paper.getUrl());
			saveAcmPaper.setString(2, paper.getTitleOfPaper());
			saveAcmPaper.setString(3, paper.getAbstractOfPaper());
			
			Array sqlArray = conn.createArrayOf("varchar", paper.getKeywordsOfPaper().toArray());
			saveAcmPaper.setArray(4, sqlArray);
			
			saveAcmPaper.setInt(5, paper.getDownloadsCount());
			saveAcmPaper.setInt(6, paper.getCitationsCount());
			saveAcmPaper.setInt(7, paper.getAcceptanceRate());
			saveAcmPaper.setInt(8, paper.getNumberOfPages());
			
			sqlArray = conn.createArrayOf("varchar", paper.getAuthors().toArray());
			saveAcmPaper.setArray(9, sqlArray);
			
			sqlArray = conn.createArrayOf("varchar", paper.getInstitutions().toArray());
			saveAcmPaper.setArray(10, sqlArray);
			
			saveAcmPaper.setString(11, paper.getPublisher());
			saveAcmPaper.setInt(12, paper.getYear());
			saveAcmPaper.setInt(13, paper.getAnnotaId());
			saveAcmPaper.setInt(14, paper.getAnnotaBookmarkCount());
			
			sqlArray = conn.createArrayOf("varchar", paper.getAnnotaTopTags().toArray());
			saveAcmPaper.setArray(15, sqlArray);
			
			long currentTime = System.currentTimeMillis();	
			saveAcmPaper.setTimestamp(16, new Timestamp(currentTime));
			saveAcmPaper.setTimestamp(17, new Timestamp(currentTime));
			
			b = (saveAcmPaper.executeUpdate() > 0) ? true : false;
		} 
		catch (SQLException e) {
			printSQLException(e);
		}
		
		return b;
	}
	
	public AcmPaper getAcmPaperByAnnotaId(int annotaId) {
		AcmPaper paper = null;
		
		try {
			getAcmPaperByAnnotaId.setInt(1, annotaId);
			rs = getAcmPaperByAnnotaId.executeQuery();
			
			if (rs.next()) {
				paper = new AcmPaper();
				Array array;
				String str;
				
				paper.setUrl(rs.getString("url"));
				
				if ((str = rs.getString("title")) != null)
					paper.setTitleOfPaper(str);
				else
					paper.setTitleOfPaper("");
				
				if ((str = rs.getString("abstract")) != null)
					paper.setAbstractOfPaper(str);
				else
					paper.setAbstractOfPaper("");
				
				array = rs.getArray("keywords");
				if (array != null)
					paper.setKeywords((String[]) array.getArray());
				
				paper.setDownloadsCount(rs.getInt("downloads_count"));
				paper.setCitationsCount(rs.getInt("citations_count"));
				paper.setAcceptanceRate(rs.getInt("acceptance_rate"));
				paper.setNumberOfPages(rs.getInt("number_of_pages"));
				
				array = rs.getArray("authors");
				if (array != null)
					paper.setAuthors((String[]) array.getArray());
				
				array = rs.getArray("institutions");
				if (array != null)
					paper.setInstitutions((String[]) array.getArray());
				
				if ((str = rs.getString("publisher")) != null)
					paper.setPublisher(str);
				else
					paper.setPublisher("");
				
				paper.setYear(rs.getInt("year"));
				paper.setAnnotaId(rs.getInt("annota_id"));
				paper.setAnnotaBookmarkCount(rs.getInt("annota_bookmark_count"));
				
				array = rs.getArray("annota_top_tags");
				if (array != null)
					paper.setAnnotaTopTags((String[]) array.getArray());
			}
		}
		catch (SQLException e) {
			printSQLException(e);
		}
		finally {
			closeResultSet();
		}
		
		return paper;
	}
	
	public void close() {
		try {
			saveAcmPaper.close();
			getAcmPaperByAnnotaId.close();
		}
		catch (SQLException e) {
			printSQLException(e);
		}
		
		closeConnection();
	}
}
