package com.taobao.feng.play.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import static com.taobao.feng.tools.PrintUtil.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GroovyTest {
	public static void main(String[] args) throws Exception {
		Binding b = new Binding();

		b.setVariable("str", "hello world!");
		GroovyShell shell = new GroovyShell(b);

		Script srt = shell.parse("System.out.println(str)");
		srt = shell
				.parse("String s=str;System.out.println(s);s=null;s=s.toString();return new Integer(3);");
		//Class<?> clazz = Class.forName("Script2", true, shell.getClassLoader());
		// shell.getClassLoader().
		
		// p(c.getName());
		try {
			Object ret = srt.run();

			System.currentTimeMillis();
			p(ret.getClass().getName());
		} catch (Exception e) {
			p("Ex:" + e);
		}
	}
	
	public static class Ctx implements Map<String, Object> {

		public void clear() {

		}

		public boolean containsKey(Object key) {
			return false;
		}

		public boolean containsValue(Object value) {
			return false;
		}

		public Set<java.util.Map.Entry<String, Object>> entrySet() {
			return null;
		}

		public Object get(Object key) {
			return null;
		}

		public boolean isEmpty() {
			return false;
		}

		public Set<String> keySet() {
			return null;
		}

		public Object put(String key, Object value) {
			return null;
		}

		public void putAll(Map<? extends String, ? extends Object> m) {

		}

		public Object remove(Object key) {
			return null;
		}

		public int size() {
			return 0;
		}

		public Collection<Object> values() {
			return null;
		}

	}
}
