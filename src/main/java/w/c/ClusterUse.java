package w.c;

import java.util.List;
import m.util.FileUtils;
import w.util.WHelper;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import static m.util.PrintUtil.*;

public class ClusterUse {

	public static void main(String[] args) throws Exception {

		List<String> inputStrs = FileUtils.eachLines("D:/weka/content_2.txt",
				"gb2312");
		for (int i = 0; i < inputStrs.size(); i++) {
			String str = inputStrs.get(i);
			inputStrs.set(i,
					str.substring(0, str.length() < 40 ? str.length() : 40));
		}

		Instances dataset = WHelper
				.translateFromList(inputStrs, "" + (char) 30);
		p(dataset);
		StringToWordVector sv = new StringToWordVector();
		sv.setOptions(new String[] { "-R", "1", "-tokenizer",
				"w.util.IkTokenizer" });
		sv.setInputFormat(dataset); //
		dataset = Filter.useFilter(dataset, sv);
		p(dataset);

		SimpleKMeans cluster = new SimpleKMeans();

		// EM cluster=new EM();
		cluster.setOptions(new String[] { "-N", "4", "-S", "8000" });
		cluster.setNumClusters(6);
		cluster.buildClusterer(dataset);
		p(cluster.displayStdDevsTipText());

		ClusterEvaluation ce = new ClusterEvaluation();
		ce.setClusterer(cluster);
		ce.evaluateClusterer(new Instances(dataset));
		// p(ce.clusterResultsToString());
		p(ce.getNumClusters());
		for (int i = 0; i < dataset.size(); i++) {
			// p(cluster.clusterInstance(dataset.get(i)));
		}

	}
}
