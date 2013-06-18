package tools.time;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeProfile {
	
	private static Logger logger = LoggerFactory.getLogger("timeprofile");
	
	private static TimeProfile sharedInstance = new TimeProfile();
	
	private static final String LOG_FORMAT = "Thread(%s) running %s elapsed: %d";
	
	private static final String LOG_FORMAT_ACCUMULATIVE = "Running %s elapsed: %d (accumulative)";
	
	private boolean isWriteSingleProfile = true;
	
	private boolean isWriteAccumulativeProfile = false;
	
	private ThreadLocal<Map<String, Long>> singleProfile;
	
	private Lock lock;
	
	private Map<String, Long> accumulativeProfile;
	
	private TimeProfile() {
		singleProfile = new ThreadLocal<Map<String,Long>>();
		lock = new ReentrantLock();
		accumulativeProfile = new HashMap<String, Long>();
	}
	
	public static void profileStart(String key) {
		if (sharedInstance.singleProfile.get() == null) {
			sharedInstance.singleProfile.set(new HashMap<String, Long>());
		}
		sharedInstance.singleProfile.get().put(key, new Date().getTime());
	}
	
	public static void profileEnd(String key) {
		Map<String, Long> localMap = sharedInstance.singleProfile.get();
		if (localMap.containsKey(key)) {
			Long startTime = localMap.remove(key);
			Long endTime = new Date().getTime();
			long elapsed = endTime - startTime;
			if (sharedInstance.isWriteSingleProfile) {
				logger.info(String.format(LOG_FORMAT, Thread.currentThread().getName(), key, elapsed));
			}
			if (sharedInstance.isWriteAccumulativeProfile) {
				try {
					sharedInstance.lock.lock();
					if (sharedInstance.accumulativeProfile.containsKey(key)) {
						long original = sharedInstance.accumulativeProfile.get(key);
						sharedInstance.accumulativeProfile.put(key, original + elapsed);
					} else {
						sharedInstance.accumulativeProfile.put(key, elapsed); 
					}
				} finally {
					sharedInstance.lock.unlock();
				}
			}
		}
	}
	
	public static void printAllAccumulativeProfiles() {
		for (String key : sharedInstance.accumulativeProfile.keySet()) {
			logger.info(String.format(LOG_FORMAT_ACCUMULATIVE, key, sharedInstance.accumulativeProfile.get(key)));
		}
	}

}
