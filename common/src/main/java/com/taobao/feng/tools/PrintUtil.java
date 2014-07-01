package com.taobao.feng.tools;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class PrintUtil {
	public static void p(Object o) {

		System.out.println(reflectToString(o));
	}

	public static String reflectToString(Object o) {
		String s = null;
		if (o instanceof String) {
			s = o.toString();
		} else if (o instanceof List) {
			List<?> list = (List<?>) o;
			StringBuilder sb = new StringBuilder();
			sb.append("List[\n");
			for (Object obj : list) {
				sb.append("\t").append(reflectToString(obj)).append(";\n");
			}
			sb.append("]\n");
			s = sb.toString();
		} else {
			s = ToStringBuilder.reflectionToString(o,
					ToStringStyle.SIMPLE_STYLE);
		}
		return s;
	}

	public static void p(Object o, String t) {
		pf("[%s]%s\n", t, reflectToString(o));
	}

	public static void pf(String fmt, Object... objects) {
		System.out.printf(fmt, objects);
	}

}
