package com.taobao.feng.tools;

import static com.taobao.feng.tools.PrintUtil.*;

public class QpsCounter {
	double count = 0;
	static double COUNT = 50;
	long lastTime = 0;
	Double dynamic = COUNT;
	Double qps = 0.0D;

	String name;

	public QpsCounter(String name) {
		this.name = name;

		Thread thread = new Thread("QpsCounter-deamon") {
			public void run() {
				while (true) {
					if (System.currentTimeMillis() - lastTime > 2000) {
						dynamic = dynamic / 2;
					}

					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {

					}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	public void step() {
		boolean p = false;
		double calCount = 0.0f;
		synchronized (this) {
			count++;
			if (count > dynamic) {
				p = true;
			}
		}
		if (p) {
			long tmis = System.currentTimeMillis();
			if (tmis - lastTime >= 1000) {
				calCount = count;
				count = 0;
				double fps = 0.0f;
				synchronized (dynamic) {
					qps = fps = calCount * 1000 / (tmis + 1 - lastTime);
					if (Math.abs(fps - dynamic) > 4) {
						dynamic = fps;
					}
				}
				pf(String.format("%s fps=%f\n", name, fps));

				lastTime = tmis;
			}
		}
	}

	public Double getQps() {
		return qps;
	}
}
