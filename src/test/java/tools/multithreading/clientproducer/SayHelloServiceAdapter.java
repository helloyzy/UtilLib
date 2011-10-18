package tools.multithreading.clientproducer;


public class SayHelloServiceAdapter implements ServiceRequest {
	
	private SayHelloService helloService;
	
	public SayHelloServiceAdapter(SayHelloService helloService) {
		this.helloService = helloService;
	}

	public void call() {
		helloService.sayHello();
	}

}
