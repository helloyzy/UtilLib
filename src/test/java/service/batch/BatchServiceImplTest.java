package service.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.Executor;

import org.junit.Before;
import org.junit.Test;

public class BatchServiceImplTest {

BatchServiceChild batchService;
	
	@Before
	public void setup() {
		batchService = new BatchServiceChild();
	}

	@Test
	public void testNeedDispatchToCallerThread() {
		// dispatch is by default disabled
		assertEquals(BatchService.NO_DISPATCH_FLAG, batchService.dispatchSize);
		assertFalse(batchService.needDispatchToCallerThread());
		
		int dispatchSize = BatchService.NO_DISPATCH_FLAG + 5;
		batchService.setDispatchSize(dispatchSize);
		// not reach dispatch counter
		assertFalse(batchService.needDispatchToCallerThread());
		batchService.dispatchCouter = dispatchSize - 1;
		assertFalse(batchService.needDispatchToCallerThread());
		
		// reach dispatch counter
		batchService.dispatchCouter = dispatchSize;
		assertTrue(batchService.needDispatchToCallerThread());
		batchService.dispatchCouter = dispatchSize + 1;
		assertTrue(batchService.needDispatchToCallerThread());
		
		// reach dispatch counter but dispatch is disabled, only for testing purpose
		batchService.dispatchSize = BatchService.NO_DISPATCH_FLAG;
		assertFalse(batchService.needDispatchToCallerThread());
	}
	
	@Test
	public void testShouldRunInCallerThread() {
		assertTrue(batchService.isFirstBatch);
		assertFalse(batchService.shouldRunInCallerThread());
		assertFalse(batchService.isFirstBatch);
		
		// dispatch counter is disabled
		assertFalse(batchService.shouldRunInCallerThread());
		// dispatch counter is enabled and reach the condition
		batchService.dispatchSize = 5;
		batchService.dispatchCouter = batchService.dispatchSize;
		assertTrue(batchService.shouldRunInCallerThread());
		assertFalse(batchService.isFirstBatch);
		
		// force firstBatch and has free thread in thread pool -> run in thread pool
		batchService = new BatchServiceChild();
		batchService.isForceFirstBatch = true; 
		batchService.setTaskExecutor(new ExecutorChild());
		assertTrue(batchService.shouldRunInCallerThread());
	}
	
	@Test
	public void testSubmitBatchTask_RunInThreadPool() {
		assertFalse(batchService.runInThreadPool);
		assertFalse(batchService.runInCaller);
		assertEquals(0, batchService.dispatchCouter);
		batchService.submitBatchTask(null);
		assertEquals(1, batchService.dispatchCouter);
		assertTrue(batchService.runInThreadPool);
		assertFalse(batchService.runInCaller);
	}
	
	@Test
	public void testSubmitBatchTask_RunInCaller() {
		assertFalse(batchService.runInThreadPool);
		assertFalse(batchService.runInCaller);
		batchService.isFirstBatch = false;
		batchService.dispatchSize = 5;
		batchService.dispatchCouter = batchService.dispatchSize;
		batchService.submitBatchTask(null);
		assertEquals(0, batchService.dispatchCouter);
		assertFalse(batchService.runInThreadPool);
		assertTrue(batchService.runInCaller);
	}
	
	public static class BatchTaskItemImpl implements BatchTaskItem {
		
	}
	
	public static class BatchServiceChild extends BatchServiceImpl {
		
		boolean runInCaller = false;
		
		boolean runInThreadPool = false;

		@Override
		protected BatchTask createBatchTask(List<BatchTaskItem> taskItems) {
			return null;
		}
		
		void runBatchInCallerThread(BatchTask task) {
			runInCaller = true;
		}
		
		void runBatchInThreadPool(BatchTask task) {
			runInThreadPool = true;
		}
		
	}
	
	public static class ExecutorChild implements Executor {

		public void execute(Runnable command) {
			
		}
		
	}

}
