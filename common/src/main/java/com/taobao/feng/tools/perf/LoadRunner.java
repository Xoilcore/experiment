package com.taobao.feng.tools.perf;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.taobao.feng.tools.FileUtils;
import com.taobao.feng.tools.QpsCounter;
import static com.taobao.feng.tools.PrintUtil.*;

public class LoadRunner {

	public static final Log logger = LogFactory.getLog(LoadRunner.class);

	List<Worker> workers = Lists.newArrayList();

	private CounterManager counterManager;
	private Invoker invoker;
	private DataGenerator dataGenerator;
	private String configPath;

	int threadCount = 2;

	int qps = 100;

	public CounterManager getCounterManager() {
		return counterManager;
	}

	public void setCounterManager(CounterManager counterManager) {
		this.counterManager = counterManager;
	}

	public Invoker getInvoker() {
		return invoker;
	}

	public void setInvoker(Invoker invoker) {
		this.invoker = invoker;
	}

	public DataGenerator getDataGenerator() {
		return dataGenerator;
	}

	public void setDataGenerator(DataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	int sleeptime = 20;

	final static QpsCounter qpsCounter = new QpsCounter("LoadRunner");

	boolean isStoped = false;
	boolean isFlushConfig = true;

	public void closeFlush() {
		isFlushConfig = false;
	}

	public void start() {
		new Thread() {
			public void run() {
				while (!isStoped) {
					try {
						sleep(2000);
					} catch (InterruptedException e) {

					}
					if (isFlushConfig) {
						runner.flushConfig();
					}

					runner.checkerWorkers();
				}
			}

			public Thread setRunner(LoadRunner runner) {
				this.runner = runner;
				return this;
			}

			LoadRunner runner;
		}.setRunner(this).start();
	}

	public void flushConfig() {
		try {
			Config conf = getConfig();
			if (qps != conf.qps) {
				p(String.format("update qps from %s to %s", qps, conf.qps));
				qps = conf.qps;
			}
			if (conf.sleeptime > 0) {
				if (sleeptime != conf.sleeptime) {
					p(String.format("update sleeptime from %s to %s",
							sleeptime, conf.sleeptime));
					sleeptime = conf.sleeptime;
				}
			}

		} catch (IOException e) {
		}
	}

	public void stop() {
		isStoped = true;
		createWorker(-1 * workers.size());
	}

	public void checkerWorkers() {
		Double currentQps = qpsCounter.getQps();
		int workerNum = workers.size();
		if (workerNum == 0) {
			createWorker(1);

		} else {
			Double qpsPerWorker = currentQps / workerNum;
			double diff = (qps - currentQps) / qpsPerWorker;
			createWorker((int) Math.round(diff));
		}
	}

	private synchronized void createWorker(int count) {
		int addcount = 0;
		int curCount = workers.size();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				Worker worker = new Worker("request-worker");
				worker.runner = this;
				worker.dataGenerator = dataGenerator;
				worker.start();
				workers.add(worker);
				if (++addcount > curCount / 2) {
					break;
				}
			}
		}
		int n = count;

		if (count < 0) {
			while (!(n++ == 0) && workers.size() > 0) {
				workers.get(0).quit();
				workers.remove(0);
			}
		}
		if (count != 0) {
			p(String.format("worker数变化  [%s] ,剩余worker[%s]", count,
					workers.size()));
		}
	}

	public Config getConfig() throws IOException {
		Config conf = new Config();
		Map<String, String> configMap = Maps.newHashMap();
		for (String line : FileUtils.eachLines(configPath)) {
			if (line.startsWith("#")) {
				continue;
			}
			String[] arr = line.split("=");
			if (arr.length == 2) {
				configMap.put(arr[0], arr[1]);
			}
		}
		conf.qps = Integer.parseInt(configMap.get("qps"));
		if (configMap.get("sleeptime") != null) {
			conf.sleeptime = Integer.parseInt(configMap.get("sleeptime"));
		}
		return conf;
	}

	public static class Config {
		int qps;
		int sleeptime = 20;
	}

	public static class Worker extends Thread {

		LoadRunner runner;
		boolean run = true;
		DataGenerator dataGenerator;

		public Worker(String name) {
			super(name);
		}

		public void quit() {
			run = false;
		}

		@Override
		public void run() {
			while (run) {
				if (isInterrupted()) {
					break;
				}
				try {
					runner.counterManager.enterRtStat();
					String label = runner.invoker.invoke(dataGenerator.get());
					runner.counterManager.getCounter(label).incr();
					runner.counterManager.endRtStat(label);
				} catch (Exception e) {
					runner.counterManager.getErrorCounter().incr();
				}
				qpsCounter.step();
				try {
					sleep(runner.sleeptime);
				} catch (InterruptedException e) {

				}
			}
		}
	};
}
