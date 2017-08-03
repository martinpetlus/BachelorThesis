	package sk.stuba.fiit.learning_to_rank.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;


final public class AppProperties {
	
	private static final String propertiesFilename = "app.properties";
	
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String DBNAME = "dbname";
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String ITERATIONS = "iterations";
	public static final String ALPHA = "alpha";
	public static final String LAMBDA = "lambda";
	public static final String HTTP_PROXY_HOST = "http-proxy-host";
	public static final String HTTP_PROXY_PORT = "http-proxy-port";
	public static final String NUMBER_OF_DOWNLOADERS = "number-of-downloaders";
	
	public final static String username;
	public final static String password;
	public final static String dbname;
	public final static String host;
	public final static int port;
	public final static int iterations;
	public final static double alpha;
	public final static double lambda;
	public final static String httpProxyPort;
	public final static String httpProxyHost;
	public final static int numberOfDownloaders;
	
	static {
		Properties properties = new Properties();
		
		try {
			properties.load(new InputStreamReader(new FileInputStream(propertiesFilename), "UTF8"));
		}
		catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		username = isNull(properties.getProperty(USERNAME), USERNAME);
		password = isNull(properties.getProperty(PASSWORD), PASSWORD);
		dbname = isNull(properties.getProperty(DBNAME), DBNAME);
		host = isNull(properties.getProperty(HOST), HOST);
		port = Integer.parseInt(isNull(properties.getProperty(PORT), PORT));
		iterations = Integer.parseInt(isNull(properties.getProperty(ITERATIONS), ITERATIONS));
		alpha = Double.parseDouble(isNull(properties.getProperty(ALPHA), ALPHA));
		lambda = Double.parseDouble(isNull(properties.getProperty(LAMBDA), LAMBDA));
		httpProxyHost = properties.getProperty(HTTP_PROXY_HOST);
		httpProxyPort = properties.getProperty(HTTP_PROXY_PORT);
		numberOfDownloaders = Integer.parseInt(isNull(properties.getProperty(NUMBER_OF_DOWNLOADERS), 
				NUMBER_OF_DOWNLOADERS));
		
		checkIterations();
		checkAlpha();
		checkLambda();
	}
	
	private static String isNull(String s, String prop) {
		if (s == null) {
			System.err.println("Error: property " + prop + " not specified...");
			System.exit(1);
			return null;
		}
		else {
			return s;
		}
	}
	
	private static void checkIterations() {
		if (iterations <= 0) {
			System.err.println("Error: number of iterations must be greater than 0...");
			System.exit(1);
		}
	}
	
	private static void checkAlpha() {
		if (alpha <= 0.0) {
			System.err.println("Error: alpha must be greater than 0.0...");
			System.exit(1);
		}
	}
	
	private static void checkLambda() {
		if (lambda < 0.0) {
			System.err.println("Error: lambda must be greater or equal to 0.0...");
			System.exit(1);
		}
	}
}
