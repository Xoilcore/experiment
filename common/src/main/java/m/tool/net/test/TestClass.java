package m.tool.net.test;

public class TestClass {
	private static Bird bird;

	public static void setBird(Bird bird) {
		TestClass.bird = bird;
	}

	public static Bird getBird() {
		return bird;
	}
}
