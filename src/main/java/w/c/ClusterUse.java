package w.c;

import java.io.File;
import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import m.util.FileUtils;
import w.util.WHelper;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import static m.util.PrintUtil.*;

public class ClusterUse {

	public static String filter(String str) {
		CharBuffer bf = CharBuffer.allocate(str.length());
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if ((int) ch > 256) {
				bf.append(ch);
			}
		}
		return new String(bf.array()).trim();
	}

	public static void main(String[] args) throws Exception {

		String clusterFile = "d:/cluster.cal";
		boolean force = false;

		List<String> inputStrs = FileUtils.eachLines("w/c/ww.txt", "utf-8");
		int max = 3000;
		List<String> strs = Lists.newArrayList();
		for (int i = 0; i < inputStrs.size(); i++) {
			String str = inputStrs.get(i);
			str = str.substring(0, str.length() < 40 ? str.length() : 40);
			strs.add(filter(str));

			if (i > max)
				break;
		}
		SimpleKMeans cluster = null;

		Instances dataset = WHelper.translateFromList(strs, "\t");
		StringToWordVector sv = new StringToWordVector();
		sv.setOptions(new String[] { "-R", "1", "-tokenizer",
				"w.util.IkTokenizer" });
		sv.setInputFormat(dataset); //
		p("read file finish!");
		dataset = Filter.useFilter(dataset, sv);
		p("seg finish!");

		if (!new File(clusterFile).exists() || force) {
			p("开始训练！");
			cluster = new SimpleKMeans();

			// EM cluster=new EM();
			cluster.setOptions(new String[] { "-N", "20", "-S", "1", "-A",
					"weka.core.ManhattanDistance" });
			// cluster.setNumClusters(4);
			// p(dataset);
			cluster.buildClusterer(dataset);
			SerializationHelper.write(clusterFile, cluster);
			p("build cluster finish!");
		} else {
			p("读取文件！");
			cluster = (SimpleKMeans) SerializationHelper.read(clusterFile);
		}
		// p(cluster.toString());
		/*
		 * ClusterEvaluation ce = new ClusterEvaluation();
		 * ce.setClusterer(cluster); ce.evaluateClusterer(new
		 * Instances(dataset)); p("summary:" + ce.clusterResultsToString());
		 * 
		 * p("num:" + ce.getNumClusters());
		 */
		// 打印分类内容
		Map<Integer, List<String>> resMap = Maps.newHashMap();
		for (int i = 0; i < 2000; i++) {
			int cls = cluster.clusterInstance(dataset.get(i));
			List<String> strList = resMap.get(cls);
			if (strList == null) {
				strList = Lists.newArrayList();
				resMap.put(cls, strList);
			}
			strList.add(strs.get(i));
		}

		for (Map.Entry<Integer, List<String>> kv : resMap.entrySet()) {
			pf("========%s========", kv.getKey());
			for (String str : kv.getValue()) {
				p(str);
			}
		}

	}
}
