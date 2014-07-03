package com.taobao.promotion;

import com.taobao.feng.tools.perf.CounterManager;
import com.taobao.feng.tools.perf.DataGenerator;
import com.taobao.feng.tools.perf.Invoker;
import com.taobao.feng.tools.perf.LoadRunner;
import com.taobao.feng.tools.perf.Statistics;

public class MaybachRequestLoader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Invoker invoker =new MaybachInvoker();
		
		DataGenerator dataGen = new QueryDataGen();

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
