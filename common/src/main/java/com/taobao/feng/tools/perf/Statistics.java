package com.taobao.feng.tools.perf;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Maps;

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
		Map<String, Long> reqMap = Maps.newHashMap();
		for (Map.Entry<String, Counter> entry : counterManager.errorCounterMap
				.entrySet()) {
			reqMap.put(entry.getKey(), entry.getValue().get());
		}
		logger.warn(String.format("req:%s", reqMap.toString()));
	}

	public CounterManager getCounterManager() {
		return counterManager;
	}

	public void setCounterManager(CounterManager counterManager) {
		this.counterManager = counterManager;
	}
}
