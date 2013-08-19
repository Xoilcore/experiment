package m.tool;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import static m.util.PrintUtil.*;

public class FastJsonUse {

	@Test
	public void testUse() {
		String str = "{\"a\":\"b\"}";
		Object obj = JSON.parse(str);
		p(obj);
	}

	@Test
	public void test_prase_obj() {
		String str = "{\"a\":\"b\"}";
		JSONObject obj = JSON.parseObject(str);
		p(obj);
	}

	@Test
	public void test_prase_arr() {
		String str = "[{\"a\":\"b\"},{\"a\":\"c\"}]";
		JSONArray arr = JSON.parseArray(str);
		p(arr);
	}

	@Test
	public void test_prase_to_obj() {
		String str = "{\"a\":\"str\",\"b\":\"100\",\"e\":\"1.4142135\"}";
		A obj = JSON.parseObject(str, A.class);
		p(obj);
	}

	@Test
	public void test_prase_obj_json() {
		String str = "{\"a\":\"str\",\"b\":\"100\",\"e\":\"1.4142135\"}";
		A obj = JSON.parseObject(str, A.class);
		str = JSON.toJSONString(obj);

		p(str);
	}

	static class A {
		private String a;
		private int b;
		private double e;

		public String getA() {
			return a;
		}

		public void setA(String a) {
			this.a = a;
		}

		public int getB() {
			return b;
		}

		public void setB(int b) {
			this.b = b;
		}

		public double getE() {
			return e;
		}

		public void setE(double e) {
			this.e = e;
		}
	}
}
