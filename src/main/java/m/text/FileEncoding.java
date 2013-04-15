package m.text;
import static m.util.PrintUtil.*;

import java.io.IOException;

import m.util.FileUtils;
public class FileEncoding {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String home = "C:/Users/mufeng.qcg/";
		p(FileUtils.eachLines(home+"extra.log", "utf-8"));
	}

}
