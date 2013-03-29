package script.groovy;

import static m.util.PrintUtil.p;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class GetClassOfGroovy {

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void main(String[] args) throws ClassNotFoundException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException {
		Binding b = new Binding();
		String script = "package g.t; public class Dynamic{\npublic void p(){\nSystem.out.println(\"g.test.dynamic!\");\n}\n}\n Dynamic dync =new Dynamic();\ndync.p();true";
		// script +="System.out.println(\"a\");true;";
		GroovyShell shell = new GroovyShell(b);

		Script scpt = shell.parse(script);
		
		p("script:"+scpt.getClass().getSuperclass().getName());

		Class<?> clazz = Class.forName("g.t.Dynamic", true,
				shell.getClassLoader());

		p(clazz.getName());

		Method m = clazz.getMethod("p");
		
		p("res:"+clazz.getClassLoader().getResourceAsStream("g.t.Dynamic.class"));
		//clazz.getClassLoader().
		m.invoke(clazz.newInstance());
		
		/*
		 * try { Object ret = srt.run();
		 * 
		 * p(ret); } catch (Exception e) { p("Ex:" + e); }
		 */
	}

}
