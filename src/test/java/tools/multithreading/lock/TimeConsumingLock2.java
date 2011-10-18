package tools.multithreading.lock;

import java.util.HashMap;
import java.util.Map;

public class TimeConsumingLock2 implements Runnable {
	
	private final Map<String, String> maph = new HashMap<String, String>();

	private int opNum;
	
	public TimeConsumingLock2(int on)
	{
		opNum = on;
	}
		
	public void foo2(int k)
	{
		String key = Integer.toString(k);
		String value = key+"value";
        if (null == key)
		{
			return ;
		}else {
			synchronized(this){
				maph.put(key, value);
			}
		}
	}			
		
	public void run()
	{
		for (int i=0; i<opNum; i++)
		{		
			foo2(i);  //This will be better
		}
	}

}
