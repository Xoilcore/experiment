package m.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

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
			FileInputStream fi = null;
			fi = new FileInputStream(dir);
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
}
