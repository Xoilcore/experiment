package m.text;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import m.util.FileUtils;

public class TextSample {

	private static String dir = FileUtils.getResourceHome() + "sample/text/";
	public static final String WWDIALOG = "ww.txt";

	public static List<String> get(String fileName) throws IOException {
		return FileUtils.eachLines(dir + fileName);
	}

	public static List<String> getPart(String fileName, int amount)
			throws IOException {
		List<String> sample = FileUtils.eachLines(dir + fileName);
		if (amount >= sample.size())
			return sample;

		List<String> some = Lists.newArrayList();
		for (int i = 0; i < amount; i++) {
			some.add(sample.get(i));

		}
		return some;
	}
}
