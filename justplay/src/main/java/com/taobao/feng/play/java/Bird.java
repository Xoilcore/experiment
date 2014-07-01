package com.taobao.feng.play.java;

import static com.taobao.feng.tools.PrintUtil.*;

public class Bird implements sayable {

	public void say(String words) {
		pf("a bird say:[%s]\n", words);
	}

}
