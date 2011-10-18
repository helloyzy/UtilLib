package tools.time;

public class TimeUtils {
	
	/**
	 * Calculate the time to execute a process (in milliseconds)
	 * @param process
	 * @return long 
	 */
	public static long caculateTime(Runnable process) {
		long start = System.currentTimeMillis();
		process.run();
		long end = System.currentTimeMillis();
		return end - start;
	}

}
