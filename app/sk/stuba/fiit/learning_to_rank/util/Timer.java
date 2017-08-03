package sk.stuba.fiit.learning_to_rank.util;


public class Timer {
	
	private long start;
	private long end;
	
	/**
	 * Starts timer
	 */
	public void start() {
		start = System.currentTimeMillis();
	}
	
	/**
	 * Ends timer
	 */
	public void end() {
		end = System.currentTimeMillis();
	}
	
	/**
	 * Returns difference between start and end
	 * 
	 * @return time in milliseconds
	 */
	public long getDiffInMillis() {
		return end - start;
	}
	
	/**
	 * Returns difference between start and end
	 * 
	 * @return time in seconds
	 */
	public double getDiffInSeconds() {
		return (end - start) / 1000.0;
	}
}
