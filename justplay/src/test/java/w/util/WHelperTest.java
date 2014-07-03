package w.util;

//import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;


import org.junit.Test;

import com.taobao.feng.play.weka.WHelper;
import com.taobao.feng.tools.QpsCounter;
import com.taobao.feng.tools.FileUtils;

import static com.taobao.feng.tools.PrintUtil.*;

public class WHelperTest {

	@Test
	public void testIkSeg() throws IOException {
		QpsCounter qps = new QpsCounter("seg2");

		p(WHelper.ikSeg("你好中国"));

		List<String> inputStrs = FileUtils.eachLines("w/c/ww.txt", "utf-8");
		for (String line : inputStrs) {
			WHelper.ikseg2(line);
			qps.step();
		}
	}

}
