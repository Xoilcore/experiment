package tools.taobao.hsf;

import com.taobao.hsf.app.spring.util.HSFSpringConsumerBean;
import com.taobao.hsf.app.spring.util.HSFSpringProviderBean;
import com.taobao.hsf.hsfunit.HSFEasyStarter;

import static m.util.PrintUtil.*;
import m.tool.net.ServerClient;

public class HsfTest extends ServerClient {

	HSFSpringProviderBean provider;
	HSFSpringConsumerBean consummer;
	Sayable sayable = null;
	Thread hsfthread;
	@Override
	public void startUp() throws Exception {
		
		hsfthread = new Thread(new Runnable() {
			public void run() {
				HSFEasyStarter.start();
			}
		});
		hsfthread.setDaemon(true);
		hsfthread.start();
		Thread.sleep(5000);
		
		// server
		String serviceName = "tools.taobao.hsf.Sayable";
		provider = new HSFSpringProviderBean();
		provider.setServiceInterface(serviceName);
		Robat r = new Robat("a");
		provider.setTarget(r);
		provider.setServiceName("say");
		provider.setServiceGroup("t");
		provider.setSerializeType("java");
		provider.setServiceVersion("1.0.0.daily");
		p("init provider");
		provider.init();
		p("start hsfEasyStarter!");		
		// client
		consummer = new HSFSpringConsumerBean();
		consummer.setInterfaceName(serviceName);
		consummer.setVersion("1.0.0.daily");
		consummer.setGroup("t");
		p("start consummer!");
		consummer.init();
		sayable = (Sayable) consummer.getObject();
	}

	@Override
	public void serverRun() throws InterruptedException {
		p("server run");
	}

	@Override
	public void clientRun() throws Exception {
		sayable.say(String.format("hello.%d", getCurrentRunTime()));
		if(getCurrentRunTime()==3){
			hsfthread.interrupt();
		}
	}

	public static void main(String[] args) {
		ServerClient.start(10, new HsfTest());
	}
	
	public void destroy() {
		p("destroy");
		hsfthread.interrupt();
	};
}
