package com.taobao.feng.tools.perf;

import java.util.Map;

import com.google.common.collect.Maps;

public class CounterManager {
	Map<String, Counter> counterMap = Maps.newConcurrentMap();
	Map<String, RtCouner> rtCounterMap = Maps.newConcurrentMap();

	public static final String ERROR_DEFAULT = "default_error";
	public static final String REQ_DEFAULT = "default_req";

	public Counter getErrorCounter() {
		return createIfNotExist(ERROR_DEFAULT, counterMap);
	}

	public Counter getCounter(String key) {
		return createIfNotExist(key, counterMap);
	}

	public RtCouner getRtCounter(String key) {
		RtCouner counter = rtCounterMap.get(key);
		if (counter != null) {
			return counter;
		}
		synchronized (rtCounterMap) {
			counter = rtCounterMap.get(key);
			if (counter == null) {
				counter = new RtCouner();
				rtCounterMap.put(key, counter);
			}
			return counter;
		}
	}

	public void enterRtStat() {
		RtCouner.entry();
	}

	public void endRtStat(String label) {
		getRtCounter(label).release();
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
