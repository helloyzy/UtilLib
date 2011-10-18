package tools.multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Boost multi-threads to execute on a specific task
 * @author Whitman.Yang
 *
 */
public class ThreadExecutor implements Runnable{
	
	public static final int DEF_THREADS = 5;
	
	private int threadsCount = DEF_THREADS;
	
	public static final int DEF_EXECUTIONTIME = 60;
	
	/** the time (in seconds) for execution in total */
	private int executionTime = DEF_EXECUTIONTIME;
	
	private Runnable task;	

	public ThreadExecutor(Runnable task) {
		this.task = task;
	}
	
	public ThreadExecutor(int threads, int executionTime, Runnable task) {
		this.threadsCount = threads;
		this.executionTime = executionTime;
		this.task = task;
	}

	public void setTask(Runnable task) {
		this.task = task;
	}

	public void setThreads(int threads) {
		this.threadsCount = threads;
	}

	public void setExecutionTime(int executionTime) {
		this.executionTime = executionTime;
	}

	public void run() {
		ExecutorService exec = Executors.newFixedThreadPool(threadsCount);
		for (int i = 0; i < threadsCount; i++)
		{
			exec.execute(task);
		}
       	exec.shutdown();
        //Waiting all the tasks to be finished
        try {
			exec.awaitTermination(executionTime, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// Ignore this exception
			e.printStackTrace();
		}
	}
	
}
