package script.groovy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import m.util.FileUtils;

public class ResClient {

	String cachePath = "D:/resCache/";

	public byte[] getResource(String name) throws IOException {

		String path = name.replaceAll("\\.", "/") + ".class";

		String local = cachePath + path;
		File f = new File(local);

		InputStream is = null;

		boolean write = false;
		// 本地存在？
		if (f.exists()) {
			is = new FileInputStream(f);
		} else {
			is = getResRemote(path);
			// 需要写缓存
			write = true;
		}

		
		if (is == null)
			throw new FileNotFoundException();

		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			bs.write(i);
		}
		is.close();
		byte[] b = bs.toByteArray();
		if (write) {
			FileUtils.write(local, b);
		}
		return b;
	}

	private InputStream getResRemote(String path) {
		return ResClient.class.getClassLoader().getResourceAsStream(path);
	}
}
