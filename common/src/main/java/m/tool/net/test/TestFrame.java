package m.tool.net.test;

import m.tool.net.ServerClient;
import static m.util.PrintUtil.*;
public class TestFrame extends ServerClient {

	@Override
	public void startUp() {
		p("startUp");
	}

	@Override
	public void serverRun() throws InterruptedException {
		Thread.sleep(500);
	}

	@Override
	public void clientRun() throws Exception {

		Thread.sleep(500);
		pf("count=%d", inst.getRunCount());

	}

	public static void main(String[] args) {
		TestFrame.start(3, new TestFrame());
	}

}
