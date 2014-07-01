package m.util;

import static m.util.PrintUtil.*;

public class FPSCounter {
	double count = 0;
	static double COUNT = 100;
	long lastTime = 0;
	Double dynamic = COUNT;

	String name;

	public FPSCounter(String name) {
		this.name = name;
	}

	public void step() {
		boolean p = false;
		double calCount = 0.0f;
		synchronized (this) {
			count++;
			if (count > dynamic) {
				calCount = count;
				count = 0;
				p = true;
			}
		}
		if (p) {

			long tmis = System.currentTimeMillis();
			double fps = 0.0f;
			synchronized (dynamic) {
				fps = calCount * 1000 / (tmis + 1 - lastTime);
				if (Math.abs(fps - dynamic) > 4) {
					dynamic = fps;
				}
			}
			pf(String.format("%s fps=%f\n", name, fps));

			lastTime = tmis;
		}
	}
}
