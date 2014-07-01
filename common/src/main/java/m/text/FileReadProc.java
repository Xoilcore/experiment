package m.text;

import java.io.BufferedReader;
import java.io.IOException;

import m.util.FileUtils;
import static m.util.PrintUtil.*;

public class FileReadProc {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader br = FileUtils.getFileReader(
				"E:/taobao work/TBCTU/Mtee/alipay/province.txt", "utf-8");
		String line = null;
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		while ((line = br.readLine()) != null) {
			String[] arr = line.split("\t");
			sb.append(String.format("%s:\"%s\",", arr[0], arr[1]));
		}
		sb.append("]");

		p(sb.toString());
	}

}
