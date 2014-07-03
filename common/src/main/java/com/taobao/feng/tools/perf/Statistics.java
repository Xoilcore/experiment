package com.taobao.feng.tools.perf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Maps;
import static com.taobao.feng.tools.PrintUtil.*;

public class Statistics {
	private CounterManager counterManager;

	public static final Log logger = LogFactory.getLog("stat");

	public void startStat() {
		new Thread() {
			public void run() {
				while (true) {
					stat();
					try {
						sleep(8000);
					} catch (InterruptedException e) {

					}
				}
			}
		}.start();
	}

	private void stat() {
		Map<String, String> reqMap = Maps.newHashMap();

		for (Map.Entry<String, Counter> entry : counterManager.counterMap
				.entrySet()) {
			String key = entry.getKey();

			Double rt = counterManager.getRtCounter(key).get();
			reqMap.put(key,
					String.format("req->%s;rt->%s", entry.getValue().get(), rt));
		}
		logger.warn(String.format("req:%s", reqMap.toString()));
		p(reqMap.toString(), "stat");
	}

	public CounterManager getCounterManager() {
		return counterManager;
	}

	public void setCounterManager(CounterManager counterManager) {
		this.counterManager = counterManager;
	}
}
