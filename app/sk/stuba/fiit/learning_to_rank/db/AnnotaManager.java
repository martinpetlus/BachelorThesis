package sk.stuba.fiit.learning_to_rank.db;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jblas.DoubleMatrix;

import sk.stuba.fiit.learning_to_rank.documents.AnnotaDocument;
import sk.stuba.fiit.learning_to_rank.eval.CombinedRanking;
import sk.stuba.fiit.learning_to_rank.eval.EvaluationLog;
import sk.stuba.fiit.learning_to_rank.learning.Model;
import sk.stuba.fiit.learning_to_rank.pairs.SearchLog;


public class AnnotaManager extends DatabaseManager {
	
	public ArrayList<AnnotaDocument> getListOfAcmPapers() {
		ArrayList<AnnotaDocument> docs = new ArrayList<AnnotaDocument>();
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select id, url, bookmark_count, top_tags from documents where "
					+ "url ~* 'https?://dl\\.acm\\.org/citation\\.cfm\\?id=.+' and public=true;");
			
			while (rs.next()) {
				AnnotaDocument d = new AnnotaDocument();
				
				d.setId(rs.getInt("id"));
				d.setUrl(rs.getString("url"));
				d.setBookmarkCount(rs.getInt("bookmark_count"));
				
				Array array = rs.getArray("top_tags");
				if (array != null) {
					d.setTopTags((String[]) array.getArray());
				}
				else {
					d.setTopTags(new String[0]);
				}
				
				docs.add(d);
			}
		}
		catch (SQLException e) {
			printSQLException(e);
		}
		finally {
			closeResultSet();
			closeStatement();
		}
		
		return docs;
	}
	
	public ArrayList<Double> getWeights(int userId) {
		ArrayList<Double> weights = new ArrayList<Double>();
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select unnest(user_weight_vector) as weight " +
					"from ltr_models where user_id=" + userId + ";");
			
			while (rs.next()) {
				weights.add(rs.getDouble("weight"));
			}
		}
		catch(SQLException e) {
			printSQLException(e);
		}
		finally {
			closeResultSet();
			closeStatement();
		}
		
		return weights;
	}
	
	public HashMap<String, Integer> getAuthorsPublishersInstitutions() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from ltr_features;");
			
			while (rs.next()) {
				String name = rs.getString("author_or_publisher_or_institution");
				int index = rs.getInt("index");
				map.put(name, index);
			}
		}
		catch (SQLException e) {
			printSQLException(e);
		}
		finally {
			closeResultSet();
			closeStatement();
		}
		
		return map;
	}
	
	public ArrayList<String> getAllAcmPapersUrlsToDownload() {
		ArrayList<String> urls = new ArrayList<String>();
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select url from documents where id not in "
					+ "(select annota_id from ltr_acm_papers) and public=true and "
					+ "url ~* 'https?://dl\\.acm\\.org/citation\\.cfm\\?id=.+';");
			
			while (rs.next()) {
				String url = rs.getString("url");
				urls.add(url);
			}
		}
		catch(SQLException e) {
			printSQLException(e);
		}
		finally {
			closeResultSet();
			closeStatement();
		}
		
		return urls;
	}
	
	public ArrayList<Integer> getAllUserIdsFromSearchLogs() {
		ArrayList<Integer> userIds = new ArrayList<Integer>();
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select distinct user_id from search_logs;");
			
			while (rs.next()) {
				int userId = rs.getInt("user_id");
				userIds.add(userId);
			}
		}
		catch(SQLException e) {
			printSQLException(e);
		}
		finally {
			closeResultSet();
			closeStatement();
		}
		
		return userIds;
	}
	
	public ArrayList<Integer> getAllClickedIdsOfAcmPapersFromSearchLogs() {
		ArrayList<Integer> clickedIds = new ArrayList<Integer>();
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("with clicked_id as (select distinct unnest( clicked_ids ) as id, "
					+ "query, user_id from search_logs) select clicked_id.id from clicked_id inner join ltr_acm_papers "
					+ "on clicked_id.id = ltr_acm_papers.annota_id where char_length(clicked_id.query) > 0;");
			
			while (rs.next()) {
				clickedIds.add(rs.getInt("id"));
			}
		}
		catch(SQLException e) {
			printSQLException(e);
		}
		finally {
			closeResultSet();
			closeStatement();
		}
		
		return clickedIds;
	}
	
	public ArrayList<Integer> getAllNotClickedIdsOfAcmPapersFromSearchLogs() {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("with annota_id as (select distinct unnest( ranked_results_ids ) as id, "
					+ "query, user_id from search_logs) select annota_id.id from annota_id inner join ltr_acm_papers on "
					+ "annota_id.id = ltr_acm_papers.annota_id where annota_id.id not in (select distinct "
					+ "unnest( clicked_ids ) as id from search_logs) and char_length(annota_id.query) > 0;");
			
			while (rs.next()) {
				ids.add(rs.getInt("id"));
			}
		}
		catch(SQLException e) {
			printSQLException(e);
		}
		finally {
			closeResultSet();
			closeStatement();
		}
		
		return ids;
	}
	
	public ArrayList<SearchLog> getSearchLogsOnlyAcmPapers(int userId) {
		ArrayList<SearchLog> searches = new ArrayList<SearchLog>();
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("with results as (select id, user_id, query, array(with ids as "
					+ "(select unnest(ranked_results_ids) as id from search_logs as a_inner_search_logs " 
					+ "where a_inner_search_logs.id=outer_search_logs.id) select * from ids where ids.id " 
					+ "in (select annota_id from ltr_acm_papers)) as ranked_results_ids, array(with ids " 
					+ "as (select distinct unnest(clicked_ids) as id from search_logs as b_inner_search_logs "
					+ "where b_inner_search_logs.id=outer_search_logs.id) select * from ids where ids.id in "
					+ "(select annota_id from ltr_acm_papers)) as clicked_ids from search_logs as outer_search_logs where "
					+ "char_length(query)>0 and user_id=" + userId + ") select * from results where "
					+ "array_length(clicked_ids,1)>0 and array_length(ranked_results_ids,1)>0;");
			
			while (rs.next()) {
				SearchLog s = new SearchLog();
				
				s.setId(rs.getInt("id"));
				s.setUserId(rs.getInt("user_id"));
				s.setQuery(rs.getString("query"));
				s.setRankedResultsIds((Integer[]) rs.getArray("ranked_results_ids").getArray());
				s.setClickedIds((Integer[]) rs.getArray("clicked_ids").getArray());
				
				searches.add(s);
			}
		}
		catch(SQLException e) {
			printSQLException(e);
		}
		finally {
			closeResultSet();
			closeStatement();
		}
		
		return searches;
	}
	
	public ArrayList<EvaluationLog> getEvaluationLogs(int userId) {
		ArrayList<EvaluationLog> evaluationLogs = new ArrayList<EvaluationLog>();
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from ltr_rankings as cr inner join search_logs as sl on "
					+ "(cr.search_log_id = sl.id) where char_length(query)>0 and array_length(original_ranking,1)>0 "
					+ "and array_length(ltr_ranking,1)>0 and array_length(ranked_results_ids,1)>0 and sl.user_id=" 
					+  userId + ";");
			
			while (rs.next()) {
				SearchLog searchLog = new SearchLog();
				CombinedRanking combinedRanking = new CombinedRanking();
				
				searchLog.setId(rs.getInt("search_log_id"));
				searchLog.setUserId(rs.getInt("user_id"));
				searchLog.setQuery(rs.getString("query"));
				searchLog.setRankedResultsIds((Integer[]) rs.getArray("ranked_results_ids").getArray());
				
				Array array = rs.getArray("clicked_ids");
				if (array != null)
					searchLog.setClickedIds((Integer[]) array.getArray());
				else 
					searchLog.setClickedIds(new Integer[0]);
				
				combinedRanking.setSearchLogId(rs.getInt("search_log_id"));
				combinedRanking.setOriginalRanking((Integer[]) rs.getArray("original_ranking").getArray());
				combinedRanking.setLearningToRankRanking((Integer[]) rs.getArray("ltr_ranking").getArray());
				
				evaluationLogs.add(new EvaluationLog(searchLog, combinedRanking));
			}
		}
		catch(SQLException e) {
			printSQLException(e);
		}
		finally {
			closeResultSet();
			closeStatement();
		}
		
		return evaluationLogs;
	}
	
	public boolean saveUserModel(int userId, Model model) {
		boolean b = false;
		
		try {
			Array arr1 = conn.createArrayOf("float", castToObject(model.getWeightVector().toArray()));
			
			Array arr2 = null;
			if (model.getMeanVector() != null)
				arr2 = conn.createArrayOf("float", castToObject(model.getMeanVector().toArray()));
			
			Array arr3 = null;
			if (model.getSigmaVector() != null)
				arr3 = conn.createArrayOf("float", castToObject(model.getSigmaVector().toArray()));
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select count(*) as count from ltr_models where user_id=" + userId + ";");
			
			rs.next();
			
			if (rs.getInt("count") > 0) {
				pStmt = conn.prepareStatement("update ltr_models set user_weight_vector=?, "
						+ "user_mean_vector=?, user_sigma_vector=?, updated_at=? where user_id="
						+ userId + ";");
				
				pStmt.setArray(1, arr1);
				pStmt.setArray(2, arr2);
				pStmt.setArray(3, arr3);
				pStmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
				
				b = (pStmt.executeUpdate() > 0) ? true : false;
			}
			else {
				pStmt = conn.prepareStatement("insert into ltr_models (user_id, user_weight_vector, "
						+ "user_mean_vector, user_sigma_vector, created_at, updated_at) "
						+ "values (?,?,?,?,?,?);");
				
				pStmt.setInt(1, userId);
				pStmt.setArray(2, arr1);
				pStmt.setArray(3, arr2);
				pStmt.setArray(4, arr3);
				
				long currentTime = System.currentTimeMillis();	
				pStmt.setTimestamp(5, new Timestamp(currentTime));
				pStmt.setTimestamp(6, new Timestamp(currentTime));
				
				b = (pStmt.executeUpdate() > 0) ? true : false;
			}
		}
		catch(SQLException e) {
			printSQLException(e);
		}
		finally {
			closePreparedStatement();
			closeResultSet();
			closeStatement();
		}
		
		return b;
	}
	
	public Model getUserModel(int userId) {
		Model model = null;
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from ltr_models where user_id=" + userId + ";");
			
			if (rs.next()) {
				DoubleMatrix meanVector = null;
				DoubleMatrix sigmaVector = null;
				Array array;
				
				double[] weightVector = castToPrimitive((Double[]) rs.getArray("user_weight_vector").getArray());
				
				array = rs.getArray("user_mean_vector");
				if (array != null)
					meanVector = new DoubleMatrix(castToPrimitive((Double[]) array.getArray()));
				
				array = rs.getArray("user_sigma_vector");
				if (array != null)
					sigmaVector = new DoubleMatrix(castToPrimitive((Double[]) array.getArray()));
				
				model = new Model(new DoubleMatrix(weightVector), meanVector, sigmaVector);
			}
		}
		catch(SQLException e) {
			printSQLException(e);
		}
		finally {
			closeResultSet();
			closeStatement();
		}
		
		return model;
	}
	
	public void saveAuthorsPublishersInstitutions(Map<String, Integer> authorsPublishersInstitutions) {
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("truncate ltr_features;");
			closeStatement();
			
			pStmt = conn.prepareStatement("insert into ltr_features (author_or_publisher_or_institution, "
					+ "index, created_at, updated_at) values (?,?,?,?);");
			
			long currentTime = System.currentTimeMillis();
			
			for (Map.Entry<String, Integer> entry : authorsPublishersInstitutions.entrySet()) {
				pStmt.setString(1, entry.getKey());
				pStmt.setInt(2, entry.getValue());
				
				pStmt.setTimestamp(3, new Timestamp(currentTime));
				pStmt.setTimestamp(4, new Timestamp(currentTime));
				
				pStmt.executeUpdate();
			}
		}
		catch(SQLException e) {
			printSQLException(e);
		}
		finally {
			closePreparedStatement();
			closeStatement();
		}
	}
	
	public void close() {
		closeConnection();
	}
	
	private Object[] castToObject(double[] values) {
		Object[] newArray = new Object[values.length];
		
		for (int i = 0; i < values.length; i++) {
			newArray[i] = new Double(values[i]);
		}
		
		return newArray;
	}
	
	private double[] castToPrimitive(Double[] values) {
		double[] primitives = new double[values.length];
		
		for (int i = 0; i < values.length; i++) {
			primitives[i] = values[i].doubleValue();
		}
		
		return primitives;
	}
}