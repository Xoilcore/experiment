package com.taobao.feng.play.perf;

import com.taobao.feng.tools.perf.CounterManager;
import com.taobao.feng.tools.perf.Data;
import com.taobao.feng.tools.perf.DataGenerator;
import com.taobao.feng.tools.perf.Invoker;
import com.taobao.feng.tools.perf.LoadRunner;
import com.taobao.feng.tools.perf.Statistics;

public class PerfSample {

	public static void main(String[] args) {
		Invoker invoker = new Invoker() {
			@Override
			public String invoke(Data data) {
				return "hello";
			}
		};
		
		DataGenerator dataGen = new DataGenerator() {

			@Override
			public Data get() {
				Data data = new Data();
				return data;
			}
		};

		CounterManager counterManager = new CounterManager();
		Statistics stat = new Statistics();
		stat.setCounterManager(counterManager);
		stat.startStat();

		LoadRunner runner = new LoadRunner();
		runner.setConfigPath("F:/tmp/statconf.txt");
		runner.setCounterManager(counterManager);
		runner.setDataGenerator(dataGen);
		runner.setInvoker(invoker);
		runner.start();
	}
}
