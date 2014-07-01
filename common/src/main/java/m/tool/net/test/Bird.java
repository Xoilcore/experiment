package m.tool.net.test;

import static m.util.PrintUtil.*;

public class Bird implements sayable {

	public void say(String words) {
		pf("a bird say:[%s]\n", words);
	}

}
