package service.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public abstract class BatchServiceImpl implements BatchService {
	
	Executor taskExecutor;
	
	/**
	 * Define how many BatchItems a batch should handle
	 */
	int batchSize = DEFAULT_BATCH_SIZE;
	
	/**
	 * Used to dispatch BatchTask submitted by different users
	 * This feature is disabled by default.
	 */
	int dispatchSize = NO_DISPATCH_FLAG;
	
	int dispatchCouter = 0;
	
	boolean isFirstBatch = true;
	
	/**
	 * This flag is useful when you want the first BatchTask to be executed immediately without having to follow FIFO
	 * This feature is disabled by default
	 */
	boolean isForceFirstBatch = false;
	
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

	protected abstract BatchTask createBatchTask(List<BatchTaskItem> taskItems);
	
	public void addTaskItem(BatchTaskItem taskItem) {
		taskItems.add(taskItem);
		if (taskItems.size() >= batchSize) {
			doBatch();
		}
	}

	public void doBatch() {
		if (taskItems.size() > 0) {
			List<BatchTaskItem> temp = new ArrayList<BatchTaskItem>(taskItems);
			taskItems.clear();
			BatchTask task = createBatchTask(temp);
			submitBatchTask(task);
		}
	}

}
