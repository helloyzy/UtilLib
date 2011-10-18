package tools.multithreading.lock;

/**
 * Using multiple small locks to optimize the function
 * @author Whitman.Yang
 *
 */
public class StrippingLock implements Runnable{
	private final Object[] locks;
	private static final int N_LOCKS = 4;
	private final String [] share ;
    private int opNum;
    private int size;

	public StrippingLock(int on, int anum) {
		opNum = on;
        size = anum;
        share = new String[size];
		locks = new Object[N_LOCKS];
	        for (int i = 0; i<N_LOCKS; i++) locks[i] = new Object();
	}

	public synchronized void put1(int indx, String k) {
		share[indx] = k;	//acquire the object lock 		
	}

  	
	public void put2(int indx, String k) {
		synchronized (locks[indx%N_LOCKS]) {
			share[indx] = k;	// acquire the corresponding lock
        }
	}
	
    public void run()
	{
		//The expensive put 
		/*for (int i=0; i<opNum; i++)
		{
			put1(i%N_ANUM, Integer.toString(i+1));
		}*/
		//The cheap put 
		for (int i=0; i<opNum; i++)
		{
			put2(i%size, Integer.toString(i+1));
		}
	}	
}
