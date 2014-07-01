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
	String serviceName = "tools.taobao.hsf.Sayable";

	public void registerService(String group) throws Exception {
		try {
			provider = new HSFSpringProviderBean();
			provider.setServiceInterface(serviceName);
			Robat r = new Robat("a");
			provider.setTarget(r);
			provider.setServiceName("say");
			provider.setServiceGroup(group);
			provider.setSerializeType("java");
			provider.setServiceVersion("1.0.0.daily");
			p("init provider");
			provider.afterPropertiesSet();
		} catch (Exception e) {
			p("ERROR:" + e);
		}
	}

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
		registerService("t2");
		p("start hsfEasyStarter!");
		// client
		consummer = new HSFSpringConsumerBean();
		consummer.setInterfaceName(serviceName);
		consummer.setVersion("1.0.0.daily");
		consummer.setGroup("t1");
		p("start consummer!");
		consummer.init();
		sayable = (Sayable) consummer.getObject();
		Thread.sleep(2000);
	}

	@Override
	public void serverRun() throws InterruptedException {
		p("server run");
	}

	@Override
	public void clientRun() throws Exception {
		sayable.say(String.format("1.hello.%d", getCurrentRunTime()));
		if (getCurrentRunTime() == 20) {
			// Object service =
			// HSFStarter.getRemotingServiceWithoutSpring("tools.taobao.hsf.Sayable",
			// "1.0.0.daily", "t");
			this.provider.unregister();
			// try {
			// ClassLoader osgiLoader = HSFStarter.getHSFSpringProviderBean()
			// .getClassLoader();
			// Class<?> containerClass = Class.forName(
			// "com.taobao.hsf.container.HSFContainer", false,
			// osgiLoader);
			// ReflectUtil.invokeStatic(containerClass, "stop");
			// } catch (Exception e) {
			// p("error:" + e);
			// }
			//
			// String boundleName = "hsf.services";
			// ClassLoader classLoader = (ClassLoader) ReflectUtil
			// .invokeStatic(
			// containerClass,
			// "getBundleClassLoader",
			// new Class[] { String.class, String.class },
			// new Object[] { boundleName,
			// "com.taobao.hsf.model.metadata.ServiceMetadataManager" });
			// Class<?> metaManagerClass = Class.forName(
			// "com.taobao.hsf.model.metadata.ServiceMetadataManager",
			// false, classLoader);
			// Object metas = ReflectUtil.invokeStatic(metaManagerClass,
			// "getCenterService");
			//
			// Set<Object> objs = (Set<Object>) metas;
			// for (Object obj : objs) {
			// p(obj.getClass().getName(), "xxxx");
			// }
			// com.taobao.hsf.app.spring.util.HSFSpringProviderBean
			// innerProvider =
			// (com.taobao.hsf.app.spring.util.HSFSpringProviderBean )proxy;

		}

		Thread.sleep(1000);
	}

	public static void main(String[] args) {
		ServerClient.start(1000, new HsfTest());
	}

	public void destroy() {
		p("destroy");
	};
}
