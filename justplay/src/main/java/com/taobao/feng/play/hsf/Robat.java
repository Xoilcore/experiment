package com.taobao.feng.play.hsf;

import static com.taobao.feng.tools.PrintUtil.*;

public final class Robat implements Sayable {

	String key = null;

	Robat(String key) {
		this.key = key;
	}

	public void say(String str) {
		p(str, key);
	}

}
