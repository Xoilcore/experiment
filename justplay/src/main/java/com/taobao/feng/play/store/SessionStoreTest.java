package com.taobao.feng.play.store;

import static com.taobao.feng.tools.PrintUtil.p;

import java.util.List;
import java.util.Map;


import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.taobao.feng.play.store.DynamicResource.Key;
import com.taobao.feng.play.store.DynamicResource.ResManager;
import com.taobao.feng.play.store.DynamicResource.ResRoute;
import com.taobao.feng.play.store.DynamicResource.ResType;
import com.taobao.feng.play.store.DynamicResource.keysservice;
import com.taobao.feng.play.store.DynamicResource.ResManager.dynamic;
import com.taobao.feng.play.store.DynamicResource.ResManager.keysServiceRes;
import com.taobao.feng.play.store.DynamicResource.ResRoute.ResInfo;
import com.taobao.feng.tools.MMUtil;
import com.taobao.feng.tools.ServerClient;
import com.taobao.hsf.hsfunit.HSFEasyStarter;



public class SessionStoreTest extends ServerClient {

	public static class SessionStore {

		List<Object[]> dataList = Lists.newArrayList();

		public String toString() {
			return ToStringBuilder.reflectionToString(dataList,
					ToStringStyle.SIMPLE_STYLE);
		}
	}

	public static class SessionManager extends dynamic implements
			sessionAccesser {

		private keysservice keysService;

		Map<Integer, Map<String, SessionStore>> memSession;

		public void newKeyEvent(int space, String key) {
			keysService.addKeys(space,
					Lists.newArrayList(new Key[] { new Key(key) }));
		}

		public void add(int space, String key, Object[] data) {
			Map<String, SessionStore> sMap = MMUtil.getIfNotExitHashMap(space,
					memSession);
			SessionStore store = sMap.get(key);
			if (store == null) {
				newKeyEvent(space, key);
				store = new SessionStore();
				sMap.put(key, store);
			}
			store.dataList.add(data);
		}

		public int edit(int space, Object[] condition, Object[] newData) {
			return 0;
		}

		public keysservice getKeysService() {
			return keysService;
		}

		public void setKeysService(keysservice keysService) {
			this.keysService = keysService;
		}

		@Override
		public void load() {
			memSession = Maps.newConcurrentMap();
			this.isLoad = true;
			p("sessonStore init");
		}

		@Override
		public void unload() {
			memSession = null;
			this.isLoad = false;
		}

		boolean isLoad = false;

		@Override
		public boolean isload() {
			return isLoad;
		}

		@Override
		public Object getTarget() {
			return this;
		}

		@Override
		public Class<?> getExClass() {
			return SessionManager.class;
		}

		public SessionStore getSessionStore(int space, String key) {

			Map<String, SessionStore> sessionMap = memSession.get(space);
			if (sessionMap == null)
				return null;
			return sessionMap.get(key);
		}

	}

	public static interface sessionAccesser {
		public void add(int space, String key, Object[] data);

		public int edit(int space, Object[] condition, Object newData[]);

		public SessionStore getSessionStore(int space, String key);
	}

	public static void main(String[] args) {
		ServerClient.start(50, new SessionStoreTest());
	}

	sessionAccesser sm_a;
	sessionAccesser sm_b;
	keysservice keysManager_a;
	keysservice keysManager_b;

	@Override
	public void startUp() throws Exception {
		// 资源分布
		ResRoute.addResInfo("m.ic.DynamicResource$keysservice", new ResInfo(
				new String[] { "A" }, ResType.FREE, -1));
		ResRoute.addResInfo("m.ic.SessionStoreTest$sessionAccesser",
				new ResInfo(new String[] { "A", "B" }, ResType.KEY_RELATIVE, 0));

		HSFEasyStarter.startFromPath("d:/hsf");

		keysServiceRes fac = new keysServiceRes();
		fac.setInterfaceName("m.ic.DynamicResource$keysservice");
		fac.setVersion("1.0.0.daily");
		keysManager_a = (keysservice) fac.getObject();
		fac.setApp("A");
		keysServiceRes fac2 = new keysServiceRes();
		fac2.setApp("B");
		fac2.setInterfaceName("m.ic.DynamicResource$keysservice");
		fac2.setVersion("1.0.0.daily");
		keysManager_b = (keysservice) fac2.getObject();

		SessionManager sm = new SessionManager();
		sm.setInterfaceName("m.ic.SessionStoreTest$sessionAccesser");
		sm.setKeysService(keysManager_a);
		sm.setApp("A");
		sm_a = (sessionAccesser) sm.getObject();
		sm = new SessionManager();
		sm.setInterfaceName("m.ic.SessionStoreTest$sessionAccesser");
		sm.setKeysService(keysManager_b);
		sm.setApp("B");
		sm_b = (sessionAccesser) sm.getObject();
		ResManager.init();
	}

	ThreadLocal<String> appLocal = new ThreadLocal<String>();

	@Override
	public void serverRun() throws InterruptedException {

		appLocal.set("A");
		Object[] data = new Object[] { System.currentTimeMillis(), "A" };

		sm_a.add(1, "testa", data);
		Thread.sleep(500);

	}

	@Override
	public void clientRun() throws Exception {

		appLocal.set("B");
		Object[] data = new Object[] { System.currentTimeMillis(), "B" };
		sm_b.add(2, "testb", data);
         SessionStore sessionStore=sm_b.getSessionStore(1, "testa");
		p(sessionStore);
		Thread.sleep(1000);

	}

}
