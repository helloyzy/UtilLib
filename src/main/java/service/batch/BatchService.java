package service.batch;

public interface BatchService {
	
public static final int DEFAULT_BATCH_SIZE = 800;
	
	public static final int NO_DISPATCH_FLAG = 0;
	
	public void cancel();
	
	public boolean isCancelled();
	
	public void process();
	
	public void setDataReader(DataReader dr);
	
	public void onSingleBatchCompleted();

}
