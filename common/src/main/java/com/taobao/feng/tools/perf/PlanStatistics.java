package com.taobao.feng.tools.perf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.google.common.collect.Maps;
import com.taobao.feng.tools.FileUtils;

import static com.taobao.feng.tools.PrintUtil.*;

public class PlanStatistics {
	private CounterManager counterManager;
	private LoadRunner runner;

	public static final Log logger = LogFactory.getLog("stat");

	private int maxQps = 100;
	private int maxRt = 5000;
	private String rtKey;
	private int currentQps = 1;
	private int step;
	private BufferedWriter bw;

	public PlanStatistics(LoadRunner runner, int maxQps, int maxRt,
			String path, String rtKey) {
		this.runner = runner;
		this.maxQps = maxQps;
		this.maxRt = maxRt;
		this.rtKey = rtKey;
		step = maxQps / 12;
		bw = FileUtils.getFileWriter(path);
	}

	private boolean isFinish = false;

	public void startStat() {
		runner.closeFlush();
		runner.sleeptime = 800;
		new Thread() {
			public void run() {
				while (!isFinish) {
					try {
						stat();
						sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				try {
					p("stat-finsih");
					runner.stop();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	Double[] rtArray = new Double[4];
	Double[] qpsArray = new Double[4];
	int idx = 0;

	public Double getAvgRt() {
		return avg(rtArray);
	}

	public Double getAvgQps() {
		return avg(qpsArray);
	}

	private Double avg(Double[] arr) {
		int c = 0;
		double sum = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == null) {
				break;
			}
			c++;
			sum += arr[i];
		}
		return c == 0 ? 0 : sum / c;
	}

	public boolean updateRtAndQps(Double rt, Double qps) {
		if (++idx >= 4) {
			idx = 0;
		}
		rtArray[idx] = rt;
		qpsArray[idx] = qps;
		double diff = 0.0d;
		for (int i = 0; i < 4; i++) {
			if (rtArray[i] == null) {
				return false;
			}
		}
		for (int i = 0; i < 3; i++) {
			diff += Math.abs(rtArray[i + 1] - rtArray[i]);
		}
		// 计算rt波动
		double rtWaveRate = (diff / 4) / getAvgRt();
		p("diff->" + diff / 4 + "rate->" + rtWaveRate);

		boolean isStable = rtWaveRate < 0.15;
		if (isStable) {
			idx = 0;
		}
		return isStable;
	}

	private void stat() throws IOException {

		if (currentQps >= maxQps) {
			isFinish = true;
			bw.write(String.format("当前qps设置 %s,超过最大值 %s\r\n", currentQps,
					maxQps));
		}

		runner.qps = currentQps;

		Map<String, String> reqMap = Maps.newHashMap();

		for (Map.Entry<String, Counter> entry : counterManager.counterMap
				.entrySet()) {
			String key = entry.getKey();

			Double rt = counterManager.getRtCounter(key).get();
			if (rt > maxRt) {
				bw.write(String.format("当前rt %s,超过最大值 %s\r\n", rt, maxRt));
				isFinish = true;
			}

			if (rtKey.equals(key)) {
				boolean isStable = updateRtAndQps(rt,
						LoadRunner.qpsCounter.getQps());

				if (isStable) {
					currentQps += step;
					pf("set qps from %s to %s", currentQps - step, currentQps);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String line = String.format("%s,%s,%s\r\n",
							sdf.format(new Date()), getAvgQps(), getAvgRt());
					bw.write(line);
					bw.flush();
					rtArray = new Double[4];
					qpsArray = new Double[4];
				}
			}

			reqMap.put(key, String.format("req->%s;rt->%s;", entry.getValue()
					.get(), rt));

		}
		logger.warn(String.format("req:%s", reqMap.toString()));

		p(reqMap.toString(), "stat");
	}

	public CounterManager getCounterManager() {
		return counterManager;
	}

	public void setCounterManager(CounterManager counterManager) {
		this.counterManager = counterManager;
	}
}
