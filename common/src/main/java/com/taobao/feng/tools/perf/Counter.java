package com.taobao.feng.tools.perf;

import java.util.concurrent.atomic.AtomicLong;

public class Counter {
	AtomicLong total = new AtomicLong(0);
	AtomicLong count = new AtomicLong(0);

	public void incr() {
		count.incrementAndGet();
	}

	public long get() {
		long c = count.getAndSet(0);
		total.getAndAdd(c);
		return c;
	}

	public long getTotal() {
		return total.get() + count.get();
	}
}
