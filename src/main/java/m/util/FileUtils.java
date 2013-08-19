package m.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

import com.google.common.collect.Lists;

public class FileUtils {

	public static BufferedReader getFileReader(String dir) {
		BufferedReader br = null;
		try {
			FileReader fr = null;
			fr = new FileReader(dir);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return br;
	}

	public static BufferedReader getFileReader(String dir, String charset)
			throws UnsupportedEncodingException {
		BufferedReader br = null;
		try {
			InputStream fi = null;
			File f = new File(dir);
			if (f.exists()) {
				fi = new FileInputStream(dir);
			} else {
				fi = FileUtils.class.getClassLoader().getResourceAsStream(dir);
			}

			InputStreamReader fis = new InputStreamReader(fi, charset);
			br = new BufferedReader(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return br;
	}

	public static BufferedWriter getFileWriter(String dir) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(dir));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bw;
	}

	public static BufferedWriter getFileWriter(String dir, String charset) {
		BufferedWriter bw = null;
		try {
			FileOutputStream fos = new FileOutputStream(dir);
			OutputStreamWriter or = new OutputStreamWriter(fos, charset);
			bw = new BufferedWriter(or);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bw;
	}

	public static void writeObj(String dir, Object obj) throws Exception {
		ObjectOutputStream ow = new ObjectOutputStream(
				new FileOutputStream(dir));
		ow.writeObject(obj);
		ow.close();
	}

	public static Object readObj(String dir) throws Exception {
		ObjectInputStream oi = new ObjectInputStream(new FileInputStream(dir));
		Object obj = oi.readObject();
		oi.close();
		return obj;
	}

	public static void write(String pathName, byte[] datas) throws IOException {
		int k1 = pathName.lastIndexOf("/");
		int k2 = pathName.lastIndexOf("\\");
		int k = k1 > k2 ? k1 : k2;
		String path = pathName.substring(0, k + 1);
		String fileName = pathName.substring(k + 1);
		write(path, fileName, datas);
	}

	public static void write(String path, String name, byte[] datas)
			throws IOException {
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		if (!path.endsWith("/") && !path.endsWith("\\")) {
			path += File.pathSeparator;
		}
		String filePath = path + name;
		File f = new File(filePath);
		FileOutputStream os = new FileOutputStream(f);
		os.write(datas);
		os.close();
	}

	public static List<String> eachLines(String f, String encode)
			throws IOException {
		BufferedReader br = getFileReader(f, encode);
		List<String> lines = Lists.newArrayList();
		String line = null;
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()) {
				lines.add(line);
			}
		}
		return lines;
	}

	public static List<String> eachLines(String f) throws IOException {
		BufferedReader br = getFileReader(f);
		List<String> lines = Lists.newArrayList();
		String line = null;
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()) {
				lines.add(line);
			}
		}
		return lines;
	}

	public static String getResourceHome() {
		URL url = FileUtils.class.getClassLoader().getResource("util.flag");
		return url.getPath().replaceAll("util.flag", "");
	}

}
