package tools.hotswap;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Ignore;

/**
while the test is running, try modifying the sayHello method of Foo
and we may have the following output
	hello world! (version one)
	hello world! (version one)
	hello world! (version two)
	hello world! (version three)
	hello world! (version three)
*/
@Ignore
public class CustomCLTest {

	private Timer timer;
	
	private int loopTimes = 0;

	private void init() throws Exception {
		timer = new Timer();
		timer.schedule(new SayHelloTask(), 2 * 1000, 8 * 1000);
	}

	public static void main(String args[]) {		
		CustomCLTest clTest = new CustomCLTest();
		try {
			clTest.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class SayHelloTask extends TimerTask {
		public void run() {
			try {
				// String basedir = "C:\\yzy\\Workingspace\\Eclipse\\3_4\\UtilLib\\bin";
				// String clazzName = "tools.hotswap.Foo";
				// current directory is C:\yzy\Workingspace\Eclipse\3_4\UtilLib
				String basedir = "./bin";
				String clazzName = "tools.hotswap.Foo";
				String testMethod = "sayHello";
				CustomCL cl = new CustomCL(basedir, new String[] { clazzName });
				Class<?> cls = cl.loadClass(clazzName);
				Object foo = cls.newInstance();
				Method m = foo.getClass().getMethod(testMethod, new Class[] {});
				m.invoke(foo, new Object[] {});
				loopTimes ++;
				if (loopTimes >= 5) {
					timer.cancel();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
