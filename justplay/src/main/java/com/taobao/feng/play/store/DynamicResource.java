package com.taobao.feng.play.store;

import static com.taobao.feng.tools.PrintUtil.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.taobao.feng.play.store.DynamicResource.ResManager.keysServiceRes;
import com.taobao.feng.play.store.DynamicResource.ResRoute.ResInfo;
import com.taobao.feng.tools.ServerClient;
import com.taobao.hsf.app.spring.util.HSFSpringConsumerBean;
import com.taobao.hsf.app.spring.util.HSFSpringProviderBean;
import com.taobao.hsf.hsfunit.HSFEasyStarter;


public class DynamicResource extends ServerClient {

	public static ThreadLocal<String> appHolder = new ThreadLocal<String>();

	public enum ResType {
		KEY_RELATIVE, FREE
	}

	public static class ResRoute {
		private static Map<String, ResInfo> irMap = Maps.newConcurrentMap();

		public static ResInfo getResInfo(String interfaceName) {
			return irMap.get(interfaceName);
		}

		public static void addResInfo(String interfaceName, ResInfo info) {
			irMap.put(interfaceName, info);
		}

		public static class ResInfo {

			public ResInfo(String[] hosts, ResType type, int keyIdx) {
				if (hosts != null) {
					this.hosts = Lists.newArrayList(hosts);
				}
				resType = type;
				this.keyIdx = keyIdx;
			}

			private List<String> hosts;
			private ResType resType;

			public List<String> getHosts() {
				return hosts;
			}

			public void setHosts(List<String> hosts) {
				this.hosts = hosts;
			}

			public ResType getResType() {
				return resType;
			}

			public void setResType(ResType resType) {
				this.resType = resType;
			}

			public int getKeyIdx() {
				return keyIdx;
			}

			public void setKeyIdx(int keyIdx) {
				this.keyIdx = keyIdx;
			}

			private int keyIdx;
		}
	}

	public static class ResManager {

		public static enum ExecType {
			local, remoteHsf, keyRelative
		};

		public static void init() {
			for (dynamic d : resSet) {
				try {
					appHolder.set(d.getApp());
					d.init();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public static Set<dynamic> resSet = Sets.newHashSet();

		public abstract static class dynamic implements FactoryBean<Object>,
				InvocationHandler {

			public dynamic() {
				ResManager.resSet.add(this);
			}

			public abstract void load();

			public abstract void unload();

			public abstract boolean isload();

			public abstract Object getTarget();

			public abstract Class<?> getExClass();

			public String getInterfaceName() {
				return interfaceName;
			}

			public dynamic setInterfaceName(String interfaceName) {
				this.interfaceName = interfaceName;
				return this;
			}

			public String getVersion() {
				return version;
			}

			public void setVersion(String version) {
				this.version = version;
			}

			private String interfaceName;

			private String version;

			public Object getObject() {

				Class<?> interfaceClass = getObjectType();
				return Proxy.newProxyInstance(this.getClass().getClassLoader(),
						new Class<?>[] { interfaceClass }, this);
			}

			public Class<?> getObjectType() {
				Class<?> clazz = null;
				try {
					clazz = Class.forName(interfaceName);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return clazz;
			}

			ExecType execType;
			int keyIdx = -1;

			private Object getDisTarget(Object keyPara) throws Exception {

				int hash = keyPara.toString().hashCode();
				int idx = Math.abs(hash % appConsumer.keySet().size());
				String app = appConsumer.keySet().toArray(new String[0])[idx];
				if (app.equals(appHolder.get())) {
					return getTarget();
				}
				Object consumer = appConsumer.get(app).getObject();
				return consumer;
			}

			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				if (execType == ExecType.local) {
					return method.invoke(getTarget(), args);
				} else if (execType == ExecType.remoteHsf) {
					return method.invoke(consumer.getObject(), args);
				} else if (execType == ExecType.keyRelative) {
					Object keyPara = args[keyIdx];
					return method.invoke(getDisTarget(keyPara), args);
				} else {
					init();
					return invoke(proxy, method, args);
				}
			}

			private HSFSpringProviderBean provider = null;
			private HSFSpringConsumerBean consumer = null;
			private Map<String, HSFSpringConsumerBean> appConsumer = null;

			public synchronized void init() throws Exception {
				if (execType != null) {
					return;
				}
				ResInfo info = ResRoute.getResInfo(interfaceName);
				if (info.getResType() == ResType.FREE) {
					String app = appHolder.get();
					if (info.getHosts().contains(app)) {//
						load();
						execType = ExecType.local;
						provider = new HSFSpringProviderBean();
						provider.setServiceInterface(interfaceName);
						provider.setServiceGroup(app + "_group");
						provider.setServiceVersion(version);
						provider.setTarget(getTarget());
						provider.init();
					} else {
						consumer = new HSFSpringConsumerBean();
						consumer.setInterfaceName(interfaceName);
						consumer.setVersion(version);
						consumer.setGroup(app + "_group");
						consumer.init();
						execType = ExecType.remoteHsf;
					}
				}
				if (info.getResType() == ResType.KEY_RELATIVE) {
					execType = ExecType.keyRelative;
					String app = appHolder.get();
					if (info.getHosts().contains(app)) {
						load();
						provider = new HSFSpringProviderBean();
						provider.setServiceInterface(interfaceName);
						provider.setServiceGroup(app + "_group");
						provider.setServiceVersion(app); // 闁俺绻冮張宥呭閸ｃ劌骞撻崠鍝勫瀻鐠嬪啰鏁�
						provider.setTarget(getTarget());
						provider.init();
					}
					appConsumer = Maps.newHashMap();
					HSFSpringConsumerBean consumerBean = null;
					for (String h : info.getHosts()) {
						consumerBean = new HSFSpringConsumerBean();
						consumerBean.setInterfaceName(interfaceName);
						consumerBean.setVersion(h);
						consumerBean.setGroup(app + "_group");
						consumerBean.init();
						appConsumer.put(h, consumerBean);
					}
					keyIdx = info.getKeyIdx();

				}
			}

			public boolean isSingleton() {
				return true;
			}

			public String getApp() {
				return app;
			}

			public void setApp(String app) {
				this.app = app;
			}

			private String app;
		}

		public static class keysServiceRes extends dynamic {

			KeysManager resManager;

			boolean isLoad = false;

			public void load() {
				p("keysServiceRes loaded!");
				isLoad = true;

				resManager = new KeysManager();
			}

			public void unload() {
				resManager = null;
			}

			public boolean isload() {
				return isLoad;
			}

			@Override
			public Class<?> getExClass() {
				return keysServiceRes.class;
			}

			@Override
			public Object getTarget() {
				return resManager;
			}
		}
	}

	public static enum StoreType {
		Mem
	}

	public static class Key {
		public Key(StoreType storeType, String key) {
			this.storeType = storeType;
			this.key = key;
		}

		public Key(String key) {
			this.storeType = StoreType.Mem;
			this.key = key;
		}

		public String toString() {
			return key;
		}

		public int hashCode() {
			return key.hashCode();
		}

		public boolean equals(Object other) {
			if (other instanceof Key) {
				Key o = (Key) other;
				return key.equals(o.key);
			}
			return false;
		}

		StoreType storeType = StoreType.Mem;
		String key;
	}

	public interface keysservice {
		void addKeys(int space, Collection<Key> keys);

		public Set<Key> getKeys(int space);
	}

	public static class KeysManager implements keysservice {

		private Map<Integer, Set<Key>> keysSetMap = Maps.newConcurrentMap();

		public void addKeys(int space, Collection<Key> keys) {

			Set<Key> keyset = keysSetMap.get(space);
			if (keyset == null) {
				keyset = Sets.newCopyOnWriteArraySet();
				keysSetMap.put(space, keyset);
			}
			keyset.addAll(keys);
		}

		public Set<Key> getKeys(int space) {
			Set<Key> set = keysSetMap.get(space);
			if (set == null)
				return Sets.newHashSet();
			return set;
		}
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws ClassNotFoundException {
		ServerClient.start(3, new DynamicResource());
	}

	public static void spring(String path) {
		// ApplicationContext ctx = new ClassPathXmlApplicationContext(path);
	}

	ApplicationContext atx;
	ApplicationContext btx;

	@Override
	public void startUp() throws Exception {
		// 鐠у嫭绨崚鍡楃
		ResRoute.addResInfo("m.ic.DynamicResource$keysservice", new ResInfo(
				new String[] { "A", "B" }, ResType.KEY_RELATIVE, 0));

		HSFEasyStarter.startFromPath("d:/hsf");
		// atx = new ClassPathXmlApplicationContext("ctx-a.xml");
		// btx = new ClassPathXmlApplicationContext("ctx-b.xml");
		// 閸掓繂顬婇崠锟�
		keysServiceRes fac = new keysServiceRes();
		fac.setInterfaceName("m.ic.DynamicResource$keysservice");
		fac.setVersion("1.0.0.daily");
		keysManager_server = (keysservice) fac.getObject();
		keysServiceRes fac2 = new keysServiceRes();
		fac2.setInterfaceName("m.ic.DynamicResource$keysservice");
		fac2.setVersion("1.0.0.daily");
		keysManager_client = (keysservice) fac2.getObject();

	}

	keysservice keysManager_server;
	keysservice keysManager_client;

	@Override
	public void serverRun() throws InterruptedException {
		appHolder.set("A");
		p("server run!");

		keysManager_server.addKeys(
				1,
				Lists.newArrayList(new Key[] { new Key("A"), new Key("B"),
						new Key("C") }));
		p("Server:" + keysManager_server.getKeys(1));
		Thread.sleep(1000);
	}

	@Override
	public void clientRun() throws Exception {
		Thread.sleep(1000);
		appHolder.set("B");
		keysManager_client.addKeys(2,
				Lists.newArrayList(new Key[] { new Key("B") }));
		p("Client:" + keysManager_client.getKeys(2));
		p("Client:" + keysManager_client.getKeys(1));
	}

}
