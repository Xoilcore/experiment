package com.taobao.feng.tools;

import com.taobao.hsf.hsfunit.HSFEasyStarter;
import com.taobao.hsf.hsfunit.HSFStarter;

public class HsfServiceUtil {

	static {
		init();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {

		}
	}

	public static void init() {
		String workdir = System.getProperty("user.dir") + "/hsfservice";
		System.setProperty("user.dir", workdir);
		System.setProperty("HSF.LOG.PATH", workdir);
		System.setProperty("JM.LOG.PATH", workdir);
		HSFEasyStarter.start(System.getProperty("user.home") + "/hsf",
				"1.4.9.2");
	}

	public static <T> T createConsummer(String service, String version) {
		return createConsummer(service, version, "HSF");
	}

	public static <T> T createConsummer(String service, String version,
			String group) {
		@SuppressWarnings("unchecked")
		T t = (T) HSFStarter.getRemotingServiceWithoutSpring(service, version,
				group);
		return t;
	}
}
