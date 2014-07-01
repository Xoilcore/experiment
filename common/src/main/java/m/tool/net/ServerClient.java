package m.tool.net;

import static m.util.PrintUtil.*;

public abstract class ServerClient {

	public static void pf(String format, Object... objects) {
		System.out.printf(format, objects);
		p("\n");
	}

	private boolean run = true;
	static protected ServerClient inst = null;

	public abstract void startUp() throws Exception;

	public abstract void serverRun() throws InterruptedException;

	public abstract void clientRun() throws Exception;

	public static abstract class Runner implements Runnable {
		ServerClient sc;

		public Runnable set(ServerClient sc) {
			this.sc = sc;
			return this;
		}

	}

	public static void start(ServerClient sc) {
		start(0, sc);
	}

	public int getCurrentRunTime() {
		return runCount;
	}

	public static void start(int i, ServerClient sc) {
		inst = sc;
		inst.RunTimes = i;
		try {
			inst.startUp();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Thread serverThread = new Thread(new Runner() {
			public void run() {
				// while (inst.isRun()) {
				try {
					inst.serverRun();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// }
			}
		}.set(inst));

		serverThread.setDaemon(true);
		serverThread.start();

		new Thread(new Runner() {
			public void run() {

				int incr = 0;
				if (sc.RunTimes > 0) {
					incr = 1;
				} else {
					sc.RunTimes = 1;
				}
				for (sc.runCount = 0; sc.runCount < sc.RunTimes; sc.runCount += incr) {

					if (!sc.isRun()) {
						break;
					}
					try {
						inst.clientRun();
					} catch (Exception e) {
						p(e);
					}
				}
				inst.destroy();
			}

		}.set(inst)).start();
	}

	public void destroy() {
	};

	protected int RunTimes = 0;

	public void setRunTimes(int i) {
		RunTimes = i;
	}

	private int runCount = 0;

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public int getRunCount() {
		return runCount;
	}

	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}

}
