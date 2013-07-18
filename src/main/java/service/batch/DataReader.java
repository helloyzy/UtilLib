package service.batch;

public interface DataReader {
	
	public boolean hasNext();
	
	public BatchTaskItem next();

}
