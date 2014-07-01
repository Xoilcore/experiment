package com.taobao.feng.play.java;

import java.util.regex.Pattern;


import com.taobao.feng.tools.ServerClient;
import com.taobao.hsf.app.spring.util.HSFSpringConsumerBean;
import com.taobao.hsf.app.spring.util.HSFSpringProviderBean;

import static com.taobao.feng.tools.PrintUtil.*;
public class TestHsf extends ServerClient {

	HSFSpringProviderBean provider;
	HSFSpringConsumerBean consumer;

	public class Bird implements sayable {
		public void say(String words) {
			p("a bird say:" + words);
		}
	}

	@Override
	public void startUp() throws Exception {
		p("startUp");
		String interfaceName = "m.tool.net.test.sayable";
		String version = "daily";
		String group = "demo";
		provider = new HSFSpringProviderBean();
		provider.setServiceInterface(interfaceName);
		provider.setServiceGroup(group);
		provider.setServiceVersion(version);
		provider.setTarget(new Bird());
		provider.init();
		//Thread.sleep(500);
		consumer = new HSFSpringConsumerBean();
		consumer.setInterfaceName(interfaceName);
		consumer.setVersion(version);
		consumer.setGroup(group);
		//consumer.setTarget("10.7.78.242");
		consumer.init();
		Thread.sleep(5000);
		System.currentTimeMillis();
	

	}

	@Override
	public void serverRun() throws InterruptedException {
		Thread.sleep(500);
	}

	@Override
	public void clientRun() throws Exception {

		Thread.sleep(500);
		pf("count=%d", inst.getRunCount());
		sayable o = (sayable) consumer.getObject();
		o.say("hello!");
		System.currentTimeMillis();

	}

	public static void main(String[] args) {
		TestHsf.start(3, new TestHsf());
		p(Pattern.matches("[:|,| ]{4}", ",: "));
	}

}
