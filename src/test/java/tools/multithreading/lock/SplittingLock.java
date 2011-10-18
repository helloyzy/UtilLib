package tools.multithreading.lock;

import java.util.HashSet;
import java.util.Set;

/**
 * Using different locks for different operation, the mechanism behind the scene is the same with stripping lock
 * @author Whitman.Yang
 *
 */
public class SplittingLock implements Runnable{
	private final Set<String> users = new HashSet<String>();
	private final Set<String> queries = new HashSet<String>();
        private int opNum;
	public SplittingLock(int on)
	{
		opNum = on;
	}
	
	public synchronized void addUser1(String u) {
        users.add(u);
	}
	
	public synchronized void addQuery1(String q) {
		queries.add(q);
	}
	
	public void addUser2(String u) {
		synchronized(users){
			//System.out.println(Thread.currentThread()+" Add:" + u);
			users.add(u);
		}
	}
	
	public void addQuery2(String q) {
		synchronized(queries){
			//System.out.println(Thread.currentThread()+" Query:" + q);
			queries.add(q);
		}
	}
	
	public void run()
	{
		for (int i=0; i<opNum; i++)
		{
			String user = new String("user");
			user+=i;
			//addUser1(user);
		    addUser2(user);

			String query = new String("query");
			query+=i;
			//addQuery1(query);
            addQuery2(query);
		}
	}
}
