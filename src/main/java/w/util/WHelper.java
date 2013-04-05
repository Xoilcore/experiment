package w.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.google.common.collect.Lists;

import static m.util.PrintUtil.*;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class WHelper {

	private WHelper() {
	}

	public static Instances translateFromList(List<String[]> dataList) {
		return new WHelper().fromList(dataList);
	}

	public static Instances translateFromList(List<String> strList, String split) {

		if (strList == null || strList.size() == 0)
			return null;
		if (split == null)
			split = ",";
		List<String[]> dataList = new ArrayList<String[]>(strList.size());
		for (String str : strList) {
			String[] arr = str.split(",");
			dataList.add(arr);
		}
		return new WHelper().fromList(dataList);
	}

	private Instances fromList(List<String[]> dataList) {
		if (dataList == null || dataList.size() == 0)
			return null;
		ArrayList<Attribute> atts = check(dataList);
		Instances dataset = new Instances("", atts, 1);
		for (String[] arr : dataList) {

			double[] data = new double[arr.length];
			for (int i = 0; i < arr.length; i++) {
				String val = arr[i];
				boolean isMissing = "?".equals(val);
				Attribute att = atts.get(i);
				if (!isMissing) {
					if (att.isNumeric()) {
						try {
							data[i] = Double.parseDouble(val);
						} catch (Exception e) {
							data[i] = Double.NaN;
						}
					} else {

						data[i] = att.addStringValue(val);
					}
				} else {
					data[i] = Double.NaN;
				}

			}
			Instance inst = new DenseInstance(0, data);
			dataset.add(inst);
		}
		return dataset;
	}

	enum Type {
		Norminol, String, Number
	};

	// 判断类型
	private ArrayList<Attribute> check(List<String[]> dataArrayList) {
		String[] arr = dataArrayList.get(0);
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		for (int i = 0; i < arr.length; i++) {
			if (isNum(arr[i])) {
				atts.add(i, new Attribute("" + i));
			} else {
				atts.add(i, new Attribute("" + i, (List<String>) null));
			}
		}

		return atts;
	}

	private boolean isNum(String str) {
		Pattern pat = Pattern.compile("[0-9]*\\.?[0-9]+");
		return pat.matcher(str).matches();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Instances insts = WHelper.translateFromList(
				Lists.newArrayList(new String[] { "a,2,c", "a1,3.14,c1",
						"a2,2324324,234234" }), null);
		p(insts);
		p(new WHelper().isNum("2"));
		String str = "帮派虽然是帮主一个人创立，然而成功却离不开大家的支持。希望大家把SC帮派当成一个家，每日都回家看看。希望大家都能成为一个合格的帮众，人人为帮派，帮派为人人！！";
		System.out.println(ikSeg(str));
	}

	public static List<String> ikSeg(String doc) {

		List<String> words = new ArrayList<String>();
		if (StringUtils.isBlank(doc))
			return words;
		// 使用最小粒度分割
		Analyzer analyser = new IKAnalyzer(false);

		
		TokenStream tokenStream = analyser.tokenStream("content",
				new StringReader(doc));

		try {
			while (tokenStream.incrementToken()) {

				TermAttribute term = tokenStream
						.addAttribute(TermAttribute.class);
				if (!StringUtils.isBlank(doc))
					words.add(term.term());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return words;
	}
}
