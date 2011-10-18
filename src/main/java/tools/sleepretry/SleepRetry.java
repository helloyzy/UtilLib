package tools.sleepretry;

/**
 * Description -- try to execute a certain function(implemented by child class) and get the expected result,
 *                if it fails, it will sleep for <bold>"interval"</bold> and then try again until it gets the expected
 *                result or the execution time has been greater than the <bold>"duration"</bold>.
 *
 */
public abstract class SleepRetry {
	
	protected final static long DEFAULT_INTERVAL = 500;
	
	protected final static long DEFAULT_DURATION = 10000;
	
	/** interval between retries, this should be equal to or smaller than the duration */
	private long interval;

	/** after this period of time,if we still can not get the expected value, abort and mark the result failed */
	private long duration;
	
	/** the value we want to get through the execution of a certain function(implemented by child class) */
	protected Object expected;
	
	/** the value returned from doFunction() */
	protected Object retObj;

	/**
	 * check whether the interval is equal to or smaller than duration etc.
	 */
	private void verifyLogic() {
		if (interval < 0) {
			interval = 0;
		}
		if (duration < 0) {
			duration = 0;
		}
		if (interval > duration) {
			interval = duration;
		}
	}
	
	public SleepRetry(Object expected) {
		this(DEFAULT_INTERVAL, DEFAULT_DURATION, expected);
	}
	
	public SleepRetry(long interval, long duration, Object expected) {
		this.interval = interval;
		this.duration = duration;
		this.expected = expected;
		verifyLogic();
	}
	
	/**
	 * Description -- Child class will override this to provide a certain function
	 * @return Object -- the actual execution result of the function, it will be used to test whether 
	 *                   the function has gotten the expected result. 
	 */
	protected abstract Object doFunction() throws Exception ;
	
	/**
	 * It will keeping invoking the doFunction() for the given duration 
	 * until getting the expected.
	 */
	public final void retryExecute() throws Exception {
		long currentMillSec = System.currentTimeMillis();
		long endMillSec = currentMillSec + duration;
		while (System.currentTimeMillis() < endMillSec) {
			retObj = doFunction();
			if (isExpected()) {
				// if succeeded, break the loop
				break;
			} else {
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					// Ignore this exception
				}
			}
		}
	}
	
	/**
	 * Define the logic of whether the retObj gotten from doFunction() is the expected value.
	 * Default logic is to compare the retObj with the expected.
	 * Child class may override the logic to have its own.
	 * @param retObj
	 * @return boolean
	 */
	public boolean isExpected() {
		if (expected == null) {
			return (retObj == null);
		}
		return expected.equals(retObj);
	}
	
	/**
	 * The object returned from the last call of doFunction().
	 * @return Object
	 */
	public Object getRetObj() {
		return retObj;
	}
	
}
