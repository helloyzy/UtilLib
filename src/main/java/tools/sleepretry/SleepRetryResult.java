package tools.sleepretry;

/**
 * This class encapsulates the result and some return value from SleepRetry
 *
 */
public class SleepRetryResult {
	
	/** The actual result the SleepRetry returns */
	private Object actualResult;

	/** The value the invoker may want to know */
	private Object retVal;
	
	/** The actual result the SleepRetry returns */
	public Object getActualResult() {
		return actualResult;
	}

	public void setActualResult(Object actualResult) {
		this.actualResult = actualResult;
	}

	/** The value the invoker may want to know */
	public Object getRetVal() {
		return retVal;
	}

	public void setRetVal(Object retVal) {
		this.retVal = retVal;
	}
	
}

