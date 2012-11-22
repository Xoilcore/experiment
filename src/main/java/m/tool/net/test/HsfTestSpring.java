package m.tool.net.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.hsf.hsfunit.HSFEasyStarter;

import m.tool.net.ServerClient;
import static m.util.PrintUtil.*;

public class HsfTestSpring extends ServerClient {

	ApplicationContext server;
	ApplicationContext client;

	@Override
	public void startUp() throws Exception {
		HSFEasyStarter.startFromPath("d:/hsf");
		server = new ClassPathXmlApplicationContext("app-server.xml");
		client  =  new ClassPathXmlApplicationContext("app-client.xml");
		p("start");
	}

	@Override
	public void serverRun() throws InterruptedException {
		Thread.sleep(1000);
	}

	@Override
	public void clientRun() throws Exception {
		sayable bird = (sayable) client.getBean("bird");
		p(bird, "bird");
		bird.say("hello world");
		p("client run");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ServerClient.start(3, new HsfTestSpring());
	}

}
