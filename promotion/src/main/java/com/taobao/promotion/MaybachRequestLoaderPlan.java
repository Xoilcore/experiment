package com.taobao.promotion;

import com.taobao.feng.tools.perf.CounterManager;
import com.taobao.feng.tools.perf.DataGenerator;
import com.taobao.feng.tools.perf.Invoker;
import com.taobao.feng.tools.perf.LoadRunner;
import com.taobao.feng.tools.perf.PlanStatistics;

public class MaybachRequestLoaderPlan {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Invoker invoker = new MaybachInvoker();
		DataGenerator dataGen = new QueryDataGen();
		CounterManager counterManager = new CounterManager();

		LoadRunner runner = new LoadRunner();
		runner.setConfigPath("F:/tmp/statconf.txt");
		runner.setCounterManager(counterManager);
		runner.setDataGenerator(dataGen);
		runner.setInvoker(invoker);
		runner.start();

		PlanStatistics stat = new PlanStatistics(runner, 400, 3000,
				"F:/tmp/getPromotion.txt", "getPromotion");
		stat.setCounterManager(counterManager);
		stat.startStat();
	}

}
