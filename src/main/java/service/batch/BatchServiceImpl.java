package service.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class BatchServiceImpl implements BatchService {
	
	private Executor taskExecutor;
	
	protected DataReader dr;
	
	/**
	 * Define how many BatchItems a batch should handle
	 */
	int batchSize = DEFAULT_BATCH_SIZE;
	
	/**
	 * Used to dispatch BatchTask between caller thread and threads from threading pool.
	 * If set to NO_DISPATCH_FLAG (default), caller thread will not be involved 
	 * unless the threading pool cannot handle it (depending on the POLICY of the threading pool)
	 */
	int dispatchSize = NO_DISPATCH_FLAG;
	
	int dispatchCouter = 0;
	
	boolean isFirstBatch = true;
	
	/**
	 * This flag is useful when you want the first BatchTask to be executed immediately without having to follow FIFO.
	 * Actually, it just lets the first BatchTask run in the caller thread.
	 * This feature is disabled by default.
	 */
	boolean isForceFirstBatch = false;
	
	private int batchCount = 0;
	
	private int completedBatchCount = 0;
	
	private boolean isAllBatchDispatched = false;
	
	private Lock batchLock = new ReentrantLock();
	
	private volatile boolean isCancelled = false;
	
	List<BatchTaskItem> taskItems = new ArrayList<BatchTaskItem>();

	public void setIsForceFirstBatch(boolean flag) {
		this.isForceFirstBatch = flag;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public void setTaskExecutor(Executor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setDispatchSize(int dispatchSize) {
		this.dispatchSize = dispatchSize;
	}
	
	/**
	 * When dispatch is on, identify whether it should dispatch to the caller thread 
	 * @return boolean
	 */
	boolean needDispatchToCallerThread() {
		boolean dispatchEnabled = dispatchSize > NO_DISPATCH_FLAG;
		return (dispatchEnabled && dispatchCouter >= dispatchSize);
	}
	
	/**
	 * If it is the first batch and has no free worker/thread in the parsingFilePool, or it has reached the dispatchSize, make
	 * the batch executed in the current thread
	 * @return boolean
	 */
	boolean shouldRunInCallerThread() {
		if (isFirstBatch) {
			isFirstBatch = false;
			return isForceFirstBatch;
		}
		return needDispatchToCallerThread();
	}
	
	void runBatchInCallerThread(BatchTask task) {
		task.run();
	}
	
	void runBatchInThreadPool(BatchTask task) {
		taskExecutor.execute(task);
	}
	
	void submitBatchTask(BatchTask task) {
		if (shouldRunInCallerThread()) {
			dispatchCouter = 0;
			runBatchInCallerThread(task);
		} else {
			dispatchCouter ++;
			runBatchInThreadPool(task);
		}
	}
	
	void checkLastBatch() {
		if (isAllBatchDispatched && batchCount == completedBatchCount) {
			postProcessing();
		}
	}
	
	void onAllBatchDispatched() {
		try {
			batchLock.lock();
			isAllBatchDispatched = true;
			checkLastBatch();
		} finally {
			batchLock.unlock();
		}
		
	}
	
	void addTaskItem(BatchTaskItem taskItem) {
		taskItems.add(taskItem);
		if (taskItems.size() >= batchSize) {
			doBatch();
		}
	}

	void doBatch() {
		if (taskItems.size() > 0) {
			List<BatchTaskItem> temp = new ArrayList<BatchTaskItem>(taskItems);
			taskItems.clear();
			BatchTask task = createBatchTask(temp);
			submitBatchTask(task);
			batchCount ++;
		}
	}
	
	protected BatchTaskItem createBatchTaskItem(BatchTaskItem item) {
		return item;
	}
	
	protected void postProcessing() {}
	
	protected abstract BatchTask createBatchTask(List<BatchTaskItem> taskItems);
	
	@Override
	public void cancel() {
		isCancelled = true;
	}
	
	@Override
	public boolean isCancelled() {
		return isCancelled;
	}
	
	@Override
	public void setDataReader(DataReader dr) {
		this.dr = dr;
	}
	
	@Override
	public void process() {
		isCancelled = false;
		while (dr.hasNext() && !isCancelled) {
			addTaskItem(createBatchTaskItem(dr.next()));
		}
		// handle the last batch
		doBatch();
		onAllBatchDispatched();
	}
	
	@Override
	public void onSingleBatchCompleted() {
		try {
			batchLock.lock();
			completedBatchCount ++;
			checkLastBatch();
		} finally {
			batchLock.unlock();
		}
	}

}
