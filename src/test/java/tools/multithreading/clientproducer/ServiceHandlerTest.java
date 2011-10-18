package tools.multithreading.clientproducer;

import org.junit.Ignore;


@Ignore
public class ServiceHandlerTest {
	
	public static void main(String[] args) {
		ServiceHandler handler = new ServiceHandler();
		SayHelloService helloService = new SayHelloService();
		handler.addRequest(new SayHelloServiceAdapter(helloService));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		handler.addRequest(new SayHelloServiceAdapter(helloService));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		handler.stopService();
		handler.addRequest(new SayHelloServiceAdapter(helloService));
		handler.addRequest(new SayHelloServiceAdapter(helloService));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
