package tools.multithreading.lock;

import java.util.Map;
import java.util.HashMap;

public class TimeConsumingLock implements Runnable {
	
	private final Map<String, String> maph = new HashMap<String, String>();

	private int opNum;
	
	public TimeConsumingLock(int on)
	{
		opNum = on;
	}
		
	public synchronized void foo1(int k)
	{
        String key = Integer.toString(k);
		String value = key+"value";
		if (null == key)
		{
			return ;
		}else {
			maph.put(key, value);		
		}
	}	
		
		
	public void run()
	{
		for (int i=0; i<opNum; i++)
		{		
			foo1(i);  //Time consuming
		}
	}
}


