package ja.base;

import static m.util.PrintUtil.*;

import java.net.URL;

public class ResourceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		loadClassFile();

	}

	public static void loadClassFile() {
		// poj class
		String fName = ResourceTest.class.getName();
		fName = fName.replaceAll("\\.", "/") + ".class";
		p(fName);
		URL url = ResourceTest.class.getResource("ResourceTest.class");
		p(url, "a1");
		url = ResourceTest.class.getClassLoader().getResource(fName);
		p(url, "a2");
		// jar class
		

		// jre class
		fName = String.class.getName();
		fName = fName.replaceAll("\\.", "/") + ".class";
		url = String.class.getResource("String.class");
		p(url, "c1");
		p(String.class.getClassLoader(), "string class loader");
		url = ResourceTest.class.getClassLoader().getResource(fName);

		p(url, "c2");
	}
}
