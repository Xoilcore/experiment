package com.taobao.feng.play.java;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.feng.tools.ServerClient;
import com.taobao.hsf.hsfunit.HSFEasyStarter;

import static com.taobao.feng.tools.PrintUtil.*;

public class HsfTestSpring extends ServerClient {

	ApplicationContext server;
	ApplicationContext client;

	@Override
	public void startUp() throws Exception {
		HSFEasyStarter.startFromPath("d:/hsf");
		server = new ClassPathXmlApplicationContext("app-server.xml");
		client  =  new ClassPathXmlApplicationContext("app-client.xml");
		p(server.getBeanDefinitionNames());
		p(client.getBeanDefinitionNames());
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
