package tools.taobao.hsf;

import static m.util.PrintUtil.*;

public final class Robat implements Sayable {

	String key = null;

	Robat(String key) {
		this.key = key;
	}

	public void say(String str) {
		p(str, key);
	}

}
