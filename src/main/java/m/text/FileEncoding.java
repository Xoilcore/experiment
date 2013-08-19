package m.text;

import static m.util.PrintUtil.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.CharSet;

import m.util.FileUtils;

public class FileEncoding {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static String home = "C:/Users/mufeng.qcg/";

	public static void main(String[] args) throws IOException {
		badEncode();
	}

	public static void badEncode() throws IOException {

		String file = home + "/extra.log";

		List<String> list = FileUtils.eachLines(file, "utf-8");

		for (String str : list) {
			p(str);
			byte[] bs = str.getBytes("utf-8");
			 
			p(new String(str.getBytes("utf-8"), "gbk"));
			break;
		}

		String str = "HH\u0030你好\u0030吗\u0031";
		str = "你好呀的";
		p(str);
		byte[] gb2312 = str.getBytes("gb2312");
		p(gb2312);
		str=new String(gb2312,"utf-8");
		byte[] utf8=str.getBytes("utf-8");
		p(utf8);
		str=new String(utf8,"gb2312");
		p(str);
		

	}

	public static void readWriteFile() throws IOException {
		// p(FileUtils.eachLines(home + "extra.log", "utf-8"));
		String file = home + "/modelRes.txt";
		String encoding = "utf-8";
		BufferedWriter bw = FileUtils.getFileWriter(file, encoding);
		String datastr = new Date().toString();
		for (int i = 0; i < 100; i++) {
			int rmuleId = i % 4 + 300;
			double score = 100.0 / (i + 1);
			boolean hit = i % 2 == 0;
			long buyerId = 334124124151L + i;
			long orderId = 998877978789L + i;
			String data = String.format("MR\t%s\t%d\t%f\t%b\t%d\t%d", datastr,
					rmuleId, score, hit, buyerId, orderId);
			bw.write(String.format("%s\u001E%d\u001E%s\u001F\n", new Date(),
					rmuleId, data));
		}
		bw.close();
		p("\u001E");

		BufferedReader br = FileUtils.getFileReader(file, encoding);
		String line = null;
		while ((line = br.readLine()) != null) {
			p(line.trim().split("\u001E"));
		}
	}

}
