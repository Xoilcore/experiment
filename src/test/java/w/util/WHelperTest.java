package w.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import m.util.FileUtils;

import org.junit.Test;
import static m.util.PrintUtil.*;

public class WHelperTest {

	@Test
	public void testIkSeg() throws IOException {
		p(WHelper.ikSeg("你好中国"));
		List<String> inputStrs = FileUtils.eachLines("w/c/ww.txt", "utf-8");
		for (String line : inputStrs) {
			p(WHelper.ikSeg(line));
		}
	}

}
