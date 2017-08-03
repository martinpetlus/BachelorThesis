package sk.stuba.fiit.learning_to_rank.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sk.stuba.fiit.learning_to_rank.util.AppProperties;


public abstract class DatabaseManager {
	
	protected Connection conn;
	protected ResultSet rs;
	protected Statement stmt;
	protected PreparedStatement pStmt;	

	protected DatabaseManager() {
		createConnection();
	}
	
	private void createConnection() {
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://" + AppProperties.host + ":" + 
					AppProperties.port + "/" + AppProperties.dbname, AppProperties.username,
					AppProperties.password);
		}
		catch (SQLException e) {
			printSQLException(e);
			System.exit(1);
		}
	}
	
	protected void closeResultSet() {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		}
		catch (SQLException e) {
			printSQLException(e);
		}
	}
	
	protected void closeStatement() {
		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		}
		catch (SQLException e) {
			printSQLException(e);
		}
	}
	
	protected void closePreparedStatement() {
		try {
			if (pStmt != null) {
				pStmt.close();
				pStmt = null;
			}
		}
		catch (SQLException e) {
			printSQLException(e);
		}
	}
	
	protected void closeConnection() {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		}
		catch (SQLException e) {
			printSQLException(e);
		}
	}
	
	protected void printSQLException(SQLException e) {
		e.printStackTrace(System.err);
	}
}
