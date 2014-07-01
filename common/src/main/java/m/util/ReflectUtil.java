package m.util;

import java.lang.reflect.Method;

public class ReflectUtil {
	public static Object invokeStatic(Class<?> clazz, String method)
			throws Exception {
		Method m = clazz.getMethod(method, (Class[]) null);
		return m.invoke(null, (Object[]) null);
	}

	public static Object invokeStatic(Class<?> clazz, String method,
			Class<?>[] types, Object[] paras) throws Exception {
		Method m = clazz.getMethod(method, types);
		return m.invoke(null, paras);
	}
}
