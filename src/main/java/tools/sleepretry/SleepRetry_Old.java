package tools.sleepretry;

/**
 * Description -- try to execute a certain function(implemented by child class) and get the expected result,
 *                if it fails, it will sleep for <bold>"interval"</bold> and then try again until it gets the expected
 *                result or the execution time has been greater than the <bold>"duration"</bold>.
 *
 */
public abstract class SleepRetry_Old {
	
	/** interval between retries, this should be equal to or smaller than the duration */
	private long interval;

	/** after this period of time,if we still can not get the expected value, abort and mark the result failed */
	private long duration;
	
	/** the value we want to get through the execution of a certain function(implemented by child class) */
	private Object expected;
	
	/** it may return some value(child class may fill this value in its implementation) */
	protected Object retVal;
	
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
	
	public SleepRetry_Old(long interval, long duration, Object expected) {
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
	 * @return Object -- the final actual result of the function 
	 * @throws Exception
	 */
	public final Object retryExecute() throws Exception {
		long currentMillSec = System.currentTimeMillis();
		long endMillSec = currentMillSec + duration;
		Object actualResult = null;
		while (System.currentTimeMillis() < endMillSec) {
			actualResult = doFunction();
			if (expected.equals(actualResult)) {
				// if it is succeeded, break the loop
				break;
			} else {
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					// Ignore this exception
				}
			}
		}
		return actualResult;
	}
	
	/**
	 * This method is used when external invoker want to get some values from this class,
	 * and the value may be provided by the child class's implementation of doFunction(). 
	 * @return SleepRetryResult
	 */
	public final SleepRetryResult retryExecuteAndRetVal() throws Exception {
		Object actualResult = retryExecute();
		SleepRetryResult result = new SleepRetryResult();
		result.setActualResult(actualResult);
		result.setRetVal(retVal);
		return result;
	}
}
