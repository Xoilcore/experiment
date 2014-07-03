package com.taobao.promotion;
import org.junit.Test;

import com.taobao.feng.tools.perf.DataGenerator;
import com.taobao.feng.tools.perf.Invoker;

public class MaybachInvokerTest {

	@Test
	public void test() {
		Invoker invoker = new MaybachInvoker();
		DataGenerator dataGen = new QueryDataGen();
		invoker.invoke(dataGen.get());
	}

}
