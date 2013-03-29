package m.tool.net.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static m.util.PrintUtil.*;
public class SprintTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"app-server.xml");
		Object b=ctx.getBean("test");
		p("ss");
		p(b);
	}

}
