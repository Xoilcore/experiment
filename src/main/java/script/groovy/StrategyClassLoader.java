package script.groovy;

import java.io.IOException;

public class StrategyClassLoader extends ClassLoader {

	private ResClient resClient;
/*
	public URL getResource(String name) {

		URL url = StrategyClassLoader.class.getClassLoader().getResource(name);
		if (url != null)
			return url;
		byte[] datas = null;
		try {
			datas = resClient.getResource(name);
		} catch (IOException e) {
			return null;
		}
		ByteArrayInputStream is = new ByteArrayInputStream(datas);

		return null;
	}
*/
	@Override
	public synchronized Class<?> loadClass(String className, boolean resloveIt) {

		Class<?> clazz = null;

		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {

		}
		if (clazz != null)
			return clazz;
		clazz = findLoadedClass(className);
		if (clazz != null)
			return clazz;

		byte[] classBytes = null;
		try {
			classBytes = resClient.getResource(className);
		} catch (IOException e) {

		}
		if (classBytes != null)
			clazz = defineClass(className, classBytes, 0, classBytes.length);

		if (resloveIt) {
			if (clazz != null)
				resolveClass(clazz);
		}
		return clazz;

	}

	public void setResClient(ResClient resClient) {
		this.resClient = resClient;
	}
}
