package com.taobao.feng.tools.perf;

import java.util.Map;

import com.google.common.collect.Maps;

public class CounterManager {
	Map<String, Counter> errorCounterMap = Maps.newConcurrentMap();
	Map<String, Counter> requestCounterMap = Maps.newConcurrentMap();
	public static final String ERROR_DEFAULT = "default_error";
	public static final String REQ_DEFAULT = "default_req";

	public Counter getErrorCounter() {
		return createIfNotExist(ERROR_DEFAULT, errorCounterMap);
	}

	public Counter getReqCounter(String key) {
		return createIfNotExist(REQ_DEFAULT, errorCounterMap);
	}

	private Counter createIfNotExist(String key, Map<String, Counter> map) {
		Counter counter = map.get(key);
		if (counter != null) {
			return counter;
		}
		synchronized (map) {
			counter = map.get(key);
			if (counter == null) {
				counter = new Counter();
				map.put(key, counter);
			}
			return counter;
		}
	}
}
