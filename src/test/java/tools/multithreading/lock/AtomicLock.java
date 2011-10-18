package tools.multithreading.lock;

import java.util.concurrent.atomic.AtomicLongArray;

/**
 * AtomicLongArray will use read lock and write lock to gain better performance
 * @author Whitman.Yang
 *
 */
public class AtomicLock implements Runnable{
	private final long d[];
	private final AtomicLongArray a;
    	private int a_size;
    
	public AtomicLock(int size) {
		a_size = size;
		d = new long[size];
		a = new AtomicLongArray(size);
	}

	public synchronized void set1(int idx, long val) {
		//System.out.println(Thread.currentThread()+" Set:" + val);
		d[idx] = val;
	}

	public synchronized long get1(int idx) {
		long ret = d[idx];
		//System.out.println(Thread.currentThread()+" Get:" + ret);
		return ret;
	}
  
	public void set2(int idx, long val) {
		//System.out.println(Thread.currentThread()+" Set:" + val);
		a.addAndGet(idx, val);  
	}

	public long get2(int idx) {
		long ret = a.get(idx);
		//System.out.println(Thread.currentThread()+" Get:" + ret);
		return ret;
	}
	
	public void run()
	{
		for (int i=0; i<a_size; i++)
		{    
			//The slower operations
			//set1(i, i);
			//get1(i);
			
			//The quicker operations
			set2(i, i);
			get2(i);
		}
	}	
}
