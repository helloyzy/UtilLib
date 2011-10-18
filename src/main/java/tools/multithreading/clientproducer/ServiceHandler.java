package tools.multithreading.clientproducer;

import java.util.LinkedList;
import java.util.Queue;


/**
 * a service handler serves to handle requests in its own thread
 * @author Whitman.Yang
 *
 */
public class ServiceHandler implements Runnable{
	
	Queue<ServiceRequest> requestQueue = new LinkedList<ServiceRequest>();
	
	private boolean stop = false;
	
	public static final int MAX_REQUEST_COUNT = 15;
	
	public ServiceHandler() {
		new Thread(this).start();
	}	
	
	public synchronized void addRequest(ServiceRequest request) {
		while (requestQueue.size() >= MAX_REQUEST_COUNT) {
			try {
				wait();
			} catch (InterruptedException e) {
				// ignore
			}
		}
		requestQueue.offer(request);
		notifyAll();
	}
	
	protected synchronized void handleRequest() {
		while (requestQueue.size() <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// ignore
			}
		}
		ServiceRequest request = requestQueue.poll();
		request.call();
		notifyAll();		
	}
	
	public void run() {
		while (!stop) {
			handleRequest();
		}
	}
	
	public void stopService() {
		stop = true;
	}

}
