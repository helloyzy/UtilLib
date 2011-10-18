package tools.multithreading.lock;

import static org.junit.Assert.*;

import org.junit.Test;

import tools.multithreading.ThreadExecutor;
import tools.time.TimeUtils;

public class TimeConsumingLockTest {
	
	@Test
	public void testPerformance() {
		int opNum = 50000;
		TimeConsumingLock lock1 = new TimeConsumingLock(opNum);
		TimeConsumingLock2 lock2 = new TimeConsumingLock2(opNum);
		ThreadExecutor executor = new ThreadExecutor(10, 60, lock1);
		long time1 = TimeUtils.caculateTime(executor);
		executor.setTask(lock2);
		long time2 = TimeUtils.caculateTime(executor);
		System.out.println("time1 = " + time1);
		System.out.println("time2 = " + time2);
		System.out.println("Time different = " + (time1 - time2));		
		assertTrue(time1 >= time2);
	}

}
