package com.taobao.feng.tools.perf;

import java.util.Random;

public class RtCouner {
	double timesum = 0;
	int count = 0;
	static Double sampleRate = 0.8;
	static Random random = new Random();

	static ThreadLocal<Long> enterTmis = new ThreadLocal<Long>();

	public static void entry() {
		if ((random.nextInt(1000) / 1000) < sampleRate) {
			enterTmis.set(System.currentTimeMillis());
		} else {
			enterTmis.set(0L);
		}
	}

	public void release() {
		long enter = enterTmis.get();
		if (enter != 0) {
			synchronized (this) {
				count++;
				timesum += System.currentTimeMillis() - enter;
			}
		}
	}

	public Double get() {
		if (count == 0) {
			return 0.0D;
		}
		synchronized (this) {
			double avgTime = timesum / count;
			count = 0;
			timesum = 0;
			return avgTime;
		}
	}

	public String toString() {
		return String.format("%s,%s,%s", count, timesum, timesum / count);
	}
}