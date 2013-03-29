/**********************************************************\
|                                                          |
| The implementation of PHPRPC Protocol 3.0                |
|                                                          |
| PHPSerializer.java                                       |
|                                                          |
| Release 3.0.2                                            |
| Copyright by Team-PHPRPC                                 |
|                                                          |
| WebSite:  http://www.phprpc.org/                         |
|           http://www.phprpc.net/                         |
|           http://www.phprpc.com/                         |
|           http://sourceforge.net/projects/php-rpc/       |
|                                                          |
| Authors:  Ma Bingyao <andot@ujn.edu.cn>                  |
|                                                          |
| This file may be distributed and/or modified under the   |
| terms of the GNU Lesser General Public License (LGPL)    |
| version 3.0 as published by the Free Software Foundation |
| and appearing in the included file LICENSE.              |
|                                                          |
\**********************************************************/

/* PHP serialize/unserialize library.
 *
 * Copyright: Ma Bingyao <andot@ujn.edu.cn>
 * Version: 3.0.2
 * LastModified: Apr 26, 2009
 * This library is free.  You can redistribute it and/or modify it.
 */

package com.taobao.mtee.ext.alimamabbs;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PHPSerializer {
	private static final HashMap<String, Class<?>> clscache = new HashMap<String, Class<?>>();
	private static final HashMap<Class<?>, HashMap<String, Field>> fieldcache = new HashMap<Class<?>, HashMap<String, Field>>();
	private static final HashMap<Class<?>, Method> __sleepcache = new HashMap<Class<?>, Method>();
	private static final HashMap<Class<?>, Method> __wakeupcache = new HashMap<Class<?>, Method>();
	private static final byte __Quote = 34;
	private static final byte __0 = 48;
	private static final byte __1 = 49;
	private static final byte __Colon = 58;
	private static final byte __Semicolon = 59;
	private static final byte __C = 67;
	private static final byte __N = 78;
	private static final byte __O = 79;
	private static final byte __R = 82;
	private static final byte __S = 83;
	private static final byte __U = 85;
	private static final byte __Slash = 92;
	private static final byte __a = 97;
	private static final byte __b = 98;
	private static final byte __d = 100;
	private static final byte __i = 105;
	private static final byte __r = 114;
	private static final byte __s = 115;
	private static final byte __LeftB = 123;
	private static final byte __RightB = 125;
	private static final String __NAN = "NAN";
	private static final String __INF = "INF";
	private static final String __NINF = "-INF";
	private String charset = "UTF-8";

	private static Class<?> enumClass;
	private static Field enumOrdinal;

	static {
		try {
			enumClass = Class.forName("java.lang.Enum");
			enumOrdinal = enumClass.getDeclaredField("ordinal");
			enumOrdinal.setAccessible(true);
		} catch (Exception e) {
			enumClass = null;
			enumOrdinal = null;
		}
	}

	public PHPSerializer() {
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public byte[] serialize(Object obj) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		HashMap<Object, Integer> ht = new HashMap<Object, Integer>();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		serialize(stream, obj, ht, 1);
		byte[] result = stream.toByteArray();
		return result;
	}

	private int serialize(ByteArrayOutputStream stream, Object obj,
			HashMap<Object, Integer> ht, int hv) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		if (obj == null) {
			hv++;
			writeNull(stream);
		} else if (obj instanceof Boolean) {
			hv++;
			writeBoolean(stream, ((Boolean) obj).booleanValue() ? __1 : __0);
		} else if ((obj instanceof Byte) || (obj instanceof Short)
				|| (obj instanceof Integer)) {
			hv++;
			writeInteger(stream, getAsciiBytes(obj));
		} else if (obj instanceof Long) {
			hv++;
			writeDouble(stream, getAsciiBytes(obj));
		} else if (obj instanceof Float) {
			hv++;
			Float f = (Float) obj;
			obj = f.isNaN() ? __NAN : (!f.isInfinite() ? obj
					: (f.floatValue() > 0 ? __INF : __NINF));
			writeDouble(stream, getAsciiBytes(obj));
		} else if (obj instanceof Double) {
			hv++;
			Double d = (Double) obj;
			obj = d.isNaN() ? __NAN : (!d.isInfinite() ? obj
					: (d.doubleValue() > 0 ? __INF : __NINF));
			writeDouble(stream, getAsciiBytes(obj));
		} else if (obj instanceof byte[]) {
			if (ht.containsKey(obj)) {
				writeRef(stream, getAsciiBytes(ht.get(obj)));
			} else {
				ht.put(obj, new Integer(hv));
				writeString(stream, (byte[]) obj);
			}
			hv++;
		} else if (obj instanceof char[]) {
			if (ht.containsKey(obj)) {
				writeRef(stream, getAsciiBytes(ht.get(obj)));
			} else {
				ht.put(obj, new Integer(hv));
				writeString(stream, getBytes(new String((char[]) obj)));
			}
			hv++;
		} else if ((obj instanceof Character) || (obj instanceof String)
				|| (obj instanceof StringBuffer)) {
			if (ht.containsKey(obj)) {
				writeRef(stream, getAsciiBytes(ht.get(obj)));
			} else {
				ht.put(obj, new Integer(hv));
				writeString(stream, getBytes(obj));
			}
			hv++;
		} else if ((obj instanceof BigInteger) || (obj instanceof BigDecimal)
				|| (obj instanceof Number)) {
			if (ht.containsKey(obj)) {
				writeRef(stream, getAsciiBytes(ht.get(obj)));
			} else {
				ht.put(obj, new Integer(hv));
				writeString(stream, getAsciiBytes(obj));
			}
			hv++;
		} else if (obj instanceof Date) {
			if (ht.containsKey(obj)) {
				hv++;
				writeRef(stream, getAsciiBytes(ht.get(obj)));
			} else {
				ht.put(obj, new Integer(hv));
				hv += 8;
				writeDate(stream, (Date) obj);
			}
		} else if (obj instanceof Calendar) {
			if (ht.containsKey(obj)) {
				hv++;
				writeRef(stream, getAsciiBytes(ht.get(obj)));
			} else {
				ht.put(obj, new Integer(hv));
				hv += 8;
				writeCalendar(stream, (Calendar) obj);
			}
		} else if (!(obj instanceof java.io.Serializable)) {
			writeNull(stream);
		} else if (obj instanceof AssocArray) {
			obj = ((AssocArray) obj).toHashMap();
			if (ht.containsKey(obj)) {
				writePointRef(stream, getAsciiBytes(ht.get(obj)));
			} else {
				ht.put(obj, new Integer(hv++));
				hv = writeMap(stream, (Map<?, ?>) obj, ht, hv);
			}
		} else if (obj.getClass().isArray()) {
			if (ht.containsKey(obj)) {
				writePointRef(stream, getAsciiBytes(ht.get(obj)));
			} else {
				ht.put(obj, new Integer(hv++));
				hv = writeArray(stream, obj, ht, hv);
			}
		} else if (obj instanceof List) {
			if (ht.containsKey(obj)) {
				writePointRef(stream, getAsciiBytes(ht.get(obj)));
			} else {
				ht.put(obj, new Integer(hv++));
				hv = writeList(stream, (List<?>) obj, ht, hv);
			}
		} else if (obj instanceof Collection) {
			if (ht.containsKey(obj)) {
				writePointRef(stream, getAsciiBytes(ht.get(obj)));
			} else {
				ht.put(obj, new Integer(hv++));
				hv = writeCollection(stream, (Collection<?>) obj, ht, hv);
			}
		} else if (obj instanceof Map) {
			if (ht.containsKey(obj)) {
				writePointRef(stream, getAsciiBytes(ht.get(obj)));
			} else {
				ht.put(obj, new Integer(hv++));
				hv = writeMap(stream, (Map<?, ?>) obj, ht, hv);
			}
		} else if ((enumClass != null)
				&& enumClass.isAssignableFrom(obj.getClass())) {
			hv++;
			writeInteger(stream, getAsciiBytes(enumOrdinal.get(obj)));
		} else {
			if (ht.containsKey(obj)) {
				hv++;
				writeRef(stream, getAsciiBytes(ht.get(obj)));
			} else {
				ht.put(obj, new Integer(hv++));
				hv = writeObject(stream, obj, ht, hv);
			}
		}
		return hv;
	}

	private void writeNull(ByteArrayOutputStream stream) {
		stream.write(__N);
		stream.write(__Semicolon);
	}

	private void writeRef(ByteArrayOutputStream stream, byte[] r) {
		stream.write(__r);
		stream.write(__Colon);
		stream.write(r, 0, r.length);
		stream.write(__Semicolon);
	}

	private void writePointRef(ByteArrayOutputStream stream, byte[] p) {
		stream.write(__R);
		stream.write(__Colon);
		stream.write(p, 0, p.length);
		stream.write(__Semicolon);
	}

	private void writeBoolean(ByteArrayOutputStream stream, byte b) {
		stream.write(__b);
		stream.write(__Colon);
		stream.write(b);
		stream.write(__Semicolon);
	}

	private void writeInteger(ByteArrayOutputStream stream, byte[] i) {
		stream.write(__i);
		stream.write(__Colon);
		stream.write(i, 0, i.length);
		stream.write(__Semicolon);
	}

	private void writeDouble(ByteArrayOutputStream stream, byte[] d) {
		stream.write(__d);
		stream.write(__Colon);
		stream.write(d, 0, d.length);
		stream.write(__Semicolon);
	}

	private void writeString(ByteArrayOutputStream stream, byte[] s) {
		byte[] slen = getAsciiBytes(new Integer(s.length));
		stream.write(__s);
		stream.write(__Colon);
		stream.write(slen, 0, slen.length);
		stream.write(__Colon);
		stream.write(__Quote);
		stream.write(s, 0, s.length);
		stream.write(__Quote);
		stream.write(__Semicolon);
	}

	private void writeCalendar(ByteArrayOutputStream stream, Calendar calendar) {
		byte[] typeName = getBytes("PHPRPC_Date");
		byte[] classNameLen = getAsciiBytes(new Integer(typeName.length));
		stream.write(__O);
		stream.write(__Colon);
		stream.write(classNameLen, 0, classNameLen.length);
		stream.write(__Colon);
		stream.write(__Quote);
		stream.write(typeName, 0, typeName.length);
		stream.write(__Quote);
		stream.write(__Colon);
		stream.write(0x37);
		stream.write(__Colon);
		stream.write(__LeftB);
		writeString(stream, getBytes("year"));
		writeInteger(stream,
				getAsciiBytes(new Integer(calendar.get(Calendar.YEAR))));
		writeString(stream, getBytes("month"));
		writeInteger(stream,
				getAsciiBytes(new Integer(calendar.get(Calendar.MONTH) + 1)));
		writeString(stream, getBytes("day"));
		writeInteger(stream,
				getAsciiBytes(new Integer(calendar.get(Calendar.DATE))));
		writeString(stream, getBytes("hour"));
		writeInteger(stream,
				getAsciiBytes(new Integer(calendar.get(Calendar.HOUR_OF_DAY))));
		writeString(stream, getBytes("minute"));
		writeInteger(stream,
				getAsciiBytes(new Integer(calendar.get(Calendar.MINUTE))));
		writeString(stream, getBytes("second"));
		writeInteger(stream,
				getAsciiBytes(new Integer(calendar.get(Calendar.SECOND))));
		writeString(stream, getBytes("millisecond"));
		writeInteger(stream, getAsciiBytes(new Integer(0)));
		stream.write(__RightB);
	}

	private void writeDate(ByteArrayOutputStream stream, Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		writeCalendar(stream, calendar);
	}

	private int writeArray(ByteArrayOutputStream stream, Object a,
			HashMap<Object, Integer> ht, int hv) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		int len = Array.getLength(a);
		byte[] alen = getAsciiBytes(new Integer(len));
		stream.write(__a);
		stream.write(__Colon);
		stream.write(alen, 0, alen.length);
		stream.write(__Colon);
		stream.write(__LeftB);
		for (int i = 0; i < len; i++) {
			writeInteger(stream, getAsciiBytes(new Integer(i)));
			hv = serialize(stream, Array.get(a, i), ht, hv);
		}
		stream.write(__RightB);
		return hv;
	}

	private int writeCollection(ByteArrayOutputStream stream, Collection<?> c,
			HashMap<Object, Integer> ht, int hv) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		int len = c.size();
		byte[] alen = getAsciiBytes(new Integer(len));
		stream.write(__a);
		stream.write(__Colon);
		stream.write(alen, 0, alen.length);
		stream.write(__Colon);
		stream.write(__LeftB);
		int i = 0;
		for (Iterator<?> values = c.iterator(); values.hasNext();) {
			writeInteger(stream, getAsciiBytes(new Integer(i++)));
			Object value = values.next();
			hv = serialize(stream, value, ht, hv);
		}
		stream.write(__RightB);
		return hv;
	}

	private int writeList(ByteArrayOutputStream stream, List<?> a,
			HashMap<Object, Integer> ht, int hv) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		int len = a.size();
		byte[] alen = getAsciiBytes(new Integer(len));
		stream.write(__a);
		stream.write(__Colon);
		stream.write(alen, 0, alen.length);
		stream.write(__Colon);
		stream.write(__LeftB);
		for (int i = 0; i < len; i++) {
			writeInteger(stream, getAsciiBytes(new Integer(i)));
			hv = serialize(stream, a.get(i), ht, hv);
		}
		stream.write(__RightB);
		return hv;
	}

	private int writeMap(ByteArrayOutputStream stream, Map<?, ?> h,
			HashMap<Object, Integer> ht, int hv) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		int len = h.size();
		byte[] hlen = getAsciiBytes(new Integer(len));
		stream.write(__a);
		stream.write(__Colon);
		stream.write(hlen, 0, hlen.length);
		stream.write(__Colon);
		stream.write(__LeftB);
		for (Iterator<?> keys = h.keySet().iterator(); keys.hasNext();) {
			Object key = keys.next();
			if ((key instanceof Byte) || (key instanceof Short)
					|| (key instanceof Integer)) {
				writeInteger(stream, getAsciiBytes(key));
			} else if (key instanceof Boolean) {
				writeInteger(
						stream,
						new byte[] { ((Boolean) key).booleanValue() ? __1 : __0 });
			} else {
				writeString(stream, getBytes(key));
			}
			hv = serialize(stream, h.get(key), ht, hv);
		}
		stream.write(__RightB);
		return hv;
	}

	public static interface Serializable {
		byte[] serialize();

		void unserialize(byte[] ss);
	}

	public static final class Cast {

		private static Class<?> enumClass;
		private static Method getEnumConstants;

		static {
			try {
				enumClass = Class.forName("java.lang.Enum");
				getEnumConstants = Class.class.getDeclaredMethod(
						"getEnumConstants", new Class[0]);
				getEnumConstants.setAccessible(true);
			} catch (Exception e) {
				enumClass = null;
				getEnumConstants = null;
			}
		}

		private Cast() {
		};

		public static byte[] getBytes(Object obj, String charset) {
			if (obj instanceof byte[]) {
				return (byte[]) obj;
			}
			try {
				return obj.toString().getBytes(charset);
			} catch (Exception e) {
				return obj.toString().getBytes();
			}
		}

		public static byte[] getBytes(Object obj) {
			return getBytes(obj, "utf-8");
		}

		public static String toString(Object obj, String charset) {
			if (obj instanceof byte[]) {
				try {
					return new String((byte[]) obj, charset);
				} catch (Exception e) {
					return new String((byte[]) obj);
				}
			} else {
				return obj.toString();
			}
		}

		public static String toString(Object obj) {
			return toString(obj, "utf-8");
		}

		public static Object cast(Number n, Class<?> destClass) {
			if (destClass == Byte.class || destClass == Byte.TYPE) {
				return new Byte(n.byteValue());
			}
			if (destClass == Short.class || destClass == Short.TYPE) {
				return new Short(n.shortValue());
			}
			if (destClass == Integer.class || destClass == Integer.TYPE) {
				return new Integer(n.intValue());
			}
			if (destClass == Long.class || destClass == Long.TYPE) {
				return new Long(n.longValue());
			}
			if (destClass == Float.class || destClass == Float.TYPE) {
				return new Float(n.floatValue());
			}
			if (destClass == Double.class || destClass == Double.TYPE) {
				return new Double(n.doubleValue());
			}
			if (destClass == Boolean.class || destClass == Boolean.TYPE) {
				return new Boolean(n.byteValue() != 0);
			}
			if ((enumClass != null) && enumClass.isAssignableFrom(destClass)) {
				try {
					Object o = getEnumConstants
							.invoke(destClass, new Object[0]);
					return Array.get(o, n.intValue());
				} catch (Throwable e) {
					return null;
				}
			}
			return n;
		}

		public static Object cast(String s, Class<?> destClass, String charset) {
			if (destClass == char[].class) {
				return s.toCharArray();
			}
			if (destClass == byte[].class) {
				return getBytes(s, charset);
			}
			if (destClass == StringBuffer.class) {
				return new StringBuffer(s);
			}
			if (destClass == Character.class || destClass == Character.TYPE) {
				return new Character(s.charAt(0));
			}
			if (destClass == Byte.class || destClass == Byte.TYPE) {
				return new Byte(s);
			}
			if (destClass == Short.class || destClass == Short.TYPE) {
				return new Short(s);
			}
			if (destClass == Integer.class || destClass == Integer.TYPE) {
				return new Integer(s);
			}
			if (destClass == Long.class || destClass == Long.TYPE) {
				return new Long(s);
			}
			if (destClass == Float.class || destClass == Float.TYPE) {
				return new Float(s);
			}
			if (destClass == Double.class || destClass == Double.TYPE) {
				return new Double(s);
			}
			if (destClass == Boolean.class || destClass == Boolean.TYPE) {
				return new Boolean(!(s.equals("") || s.equals("0") || s
						.toLowerCase().equals("false")));
			}
			if (destClass == BigInteger.class) {
				return new BigInteger(s);
			}
			if (destClass == BigDecimal.class || destClass == Number.class) {
				return new BigDecimal(s);
			}
			if (destClass == Boolean.class || destClass == Boolean.TYPE) {
				return new Boolean(!(s.equals("") || s.equals("0") || s
						.toLowerCase().equals("false")));
			}
			return s;
		}

		public static Object cast(String s, Class<?> destClass) {
			return cast(s, destClass, "utf-8");
		}

		public static Object cast(AssocArray obj, Class<?> destClass,
				String charset) {
			if (destClass == AssocArray.class) {
				return obj;
			}
			if (destClass == ArrayList.class || destClass == List.class
					|| destClass == Collection.class) {
				return obj.toArrayList();
			}
			if (destClass == Set.class) {
				return new HashSet<Object>(obj.toArrayList());
			}
			if (destClass == HashMap.class || destClass == Map.class) {
				return obj.toHashMap();
			}
			if (destClass == LinkedHashMap.class) {
				return obj.toLinkedHashMap();
			}
			if (destClass.isArray()) {
				return toArray(obj.toArrayList(), destClass.getComponentType(),
						charset);
			}
			if (Collection.class.isAssignableFrom(destClass)) {
				try {
					Method addAll = destClass.getMethod("addAll",
							new Class[] { Collection.class });
					Object o = PHPSerializer.newInstance(destClass);
					if (o != null) {
						addAll.setAccessible(true);
						addAll.invoke(o, new Object[] { obj.toArrayList() });
					}
					return o;
				} catch (Throwable e) {
					return null;
				}
			}
			if (Map.class.isAssignableFrom(destClass)) {
				try {
					Method putAll = destClass.getMethod("putAll",
							new Class[] { Map.class });
					Object o = PHPSerializer.newInstance(destClass);
					if (o != null) {
						putAll.setAccessible(true);
						putAll.invoke(o, new Object[] { obj.toHashMap() });
					}
					return o;
				} catch (Throwable e) {
					return null;
				}
			}
			return cast(obj.toHashMap(), destClass, charset);
		}

		private static Object cast(HashMap<Object, Object> obj,
				Class<?> destClass, String charset) {
			try {
				PHPSerializer.getClassName(destClass);
				Object o = PHPSerializer.newInstance(destClass);
				for (Iterator<Object> keys = obj.keySet().iterator(); keys
						.hasNext();) {
					Object key = keys.next();
					String name = key.toString();
					Object value = obj.get(key);
					Field f = PHPSerializer.getField(o, name);
					if (f != null) {
						f.setAccessible(true);
						f.set(o, Cast.cast(value, f.getType(), charset));
					}
				}
				return o;
			} catch (Throwable e) {
				return null;
			}
		}

		@SuppressWarnings("unchecked")
		public static Object cast(Object obj, Class<?> destClass, String charset) {
			if (obj == null || destClass == null || destClass == Void.class
					|| destClass == Void.TYPE) {
				return null;
			}
			if (destClass.isInstance(obj)) {
				return obj;
			}
			if (obj instanceof byte[]) {
				return cast(toString(obj, charset), destClass, charset);
			}
			if (obj instanceof char[]) {
				return cast(new String((char[]) obj), destClass, charset);
			}
			if (obj instanceof StringBuffer) {
				return cast(obj.toString(), destClass, charset);
			}
			if (obj instanceof String) {
				return cast((String) obj, destClass, charset);
			}
			if (destClass == Character.class || destClass == Character.TYPE) {
				return new Character(obj.toString().charAt(0));
			}
			if ((obj instanceof Calendar)
					&& Date.class.isAssignableFrom(destClass)) {
				return toDate(((Calendar) obj).getTimeInMillis(), destClass);
			}
			if (obj instanceof AssocArray) {
				return cast((AssocArray) obj, destClass, charset);
			}
			if (obj instanceof HashMap) {
				return cast((HashMap<Object, Object>) obj, destClass, charset);
			}
			if ((obj instanceof Boolean)
					&& Number.class.isAssignableFrom(destClass)) {
				return cast(new Integer(
						(((Boolean) obj).booleanValue() == true) ? 1 : 0),
						destClass);
			}
			if (destClass == String.class) {
				return obj.toString();
			}
			if (destClass == StringBuffer.class) {
				return new StringBuffer(obj.toString());
			}
			if (!obj.getClass().isArray() && destClass == byte[].class) {
				return getBytes(obj);
			}
			if (!obj.getClass().isArray() && destClass == char[].class) {
				return obj.toString().toCharArray();
			}
			if (obj instanceof Number) {
				return cast((Number) obj, destClass);
			}
			return obj;
		}

		public static Object cast(Object obj, Class<?> destClass) {
			return cast(obj, destClass, "utf-8");
		}

		public static Object toArray(ArrayList<Object> obj,
				Class<?> componentType, String charset) {
			int n = obj.size();
			Object a = Array.newInstance(componentType, n);

			for (int i = 0; i < n; i++) {
				Array.set(a, i, cast(obj.get(i), componentType, charset));
			}
			return a;
		}

		public static Date toDate(long time, Class<?> destClass) {
			if (destClass == Date.class) {
				return new Date(time);
			}
			if (destClass == java.sql.Date.class) {
				return new java.sql.Date(time);
			}
			if (destClass == java.sql.Time.class) {
				return new java.sql.Time(time);
			}
			if (destClass == java.sql.Timestamp.class) {
				return new java.sql.Timestamp(time);
			} else {
				try {
					return (Date) (destClass
							.getConstructor(new Class[] { Long.TYPE })
							.newInstance(new Object[] { new Long(time) }));

				} catch (Throwable e) {
					return null;
				}
			}
		}
	};

	private int writeObject(ByteArrayOutputStream stream, Object obj,
			HashMap<Object, Integer> ht, int hv) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Class<?> cls = obj.getClass();
		byte[] className = getBytes(getClassName(cls));
		byte[] classNameLen = getAsciiBytes(new Integer(className.length));
		if (obj instanceof Serializable) {
			byte[] cs = ((Serializable) obj).serialize();
			byte[] cslen = getAsciiBytes(new Integer(cs.length));
			stream.write(__C);
			stream.write(__Colon);
			stream.write(classNameLen, 0, classNameLen.length);
			stream.write(__Colon);
			stream.write(__Quote);
			stream.write(className, 0, className.length);
			stream.write(__Quote);
			stream.write(__Colon);
			stream.write(cslen, 0, cslen.length);
			stream.write(__Colon);
			stream.write(__LeftB);
			stream.write(cs, 0, cs.length);
			stream.write(__RightB);
		} else {
			HashMap<String, Field> f;
			Method __sleep = null;
			if (fieldcache.containsKey(cls)) {
				f = (HashMap<String, Field>) fieldcache.get(cls);
				// __sleep need to run
				if (__sleepcache.containsKey(cls)) {
					__sleep = (Method) __sleepcache.get(cls);
					__sleep.invoke(obj, new Object[0]);
				}
			} else {
				try {
					__sleep = cls.getMethod("__sleep", new Class[0]);
					__sleep.setAccessible(true);
					__sleepcache.put(cls, __sleep);
				} catch (Exception e) {
				}
				if (__sleep != null) {
					String[] fieldNames = (String[]) __sleep.invoke(obj,
							new Object[0]);
					f = getFields(obj, fieldNames);
				} else {
					f = getFields(obj);
				}
				fieldcache.put(cls, f);
			}
			byte[] flen = getAsciiBytes(new Integer(f.size()));
			stream.write(__O);
			stream.write(__Colon);
			stream.write(classNameLen, 0, classNameLen.length);
			stream.write(__Colon);
			stream.write(__Quote);
			stream.write(className, 0, className.length);
			stream.write(__Quote);
			stream.write(__Colon);
			stream.write(flen, 0, flen.length);
			stream.write(__Colon);
			stream.write(__LeftB);
			for (Iterator<String> keys = f.keySet().iterator(); keys.hasNext();) {
				String key = (String) keys.next();
				Object o = ((Field) f.get(key)).get(obj);
				writeString(stream, getBytes(key));
				hv = serialize(stream, o, ht, hv);
			}
			stream.write(__RightB);
		}
		return hv;
	}

	private byte[] getBytes(Object obj) {
		try {
			return obj.toString().getBytes(charset);
		} catch (Exception e) {
			return obj.toString().getBytes();
		}
	}

	private byte[] getAsciiBytes(Object obj) {
		try {
			return obj.toString().getBytes("US-ASCII");
		} catch (Exception e) {
			return null;
		}
	}

	private String getString(byte[] b) {
		try {
			return new String(b, charset);
		} catch (Exception e) {
			return new String(b);
		}
	}

	private Class<?> getInnerClass(StringBuffer className, int[] pos, int i,
			char c) {
		if (i < pos.length) {
			int p = pos[i];
			className.setCharAt(p, c);
			Class<?> cls = getInnerClass(className, pos, i + 1, '_');
			if (i + 1 < pos.length && cls == null) {
				cls = getInnerClass(className, pos, i + 1, '$');
			}
			return cls;
		} else {
			try {
				return Class.forName(className.toString());
			} catch (Exception e) {
				return null;
			}
		}
	}

	private Class<?> getClass(StringBuffer className, int[] pos, int i, char c) {
		if (i < pos.length) {
			int p = pos[i];
			className.setCharAt(p, c);
			Class<?> cls = getClass(className, pos, i + 1, '.');
			if (i + 1 < pos.length) {
				if (cls == null) {
					cls = getClass(className, pos, i + 1, '_');
				}
				if (cls == null) {
					cls = getInnerClass(className, pos, i + 1, '$');
				}
			}
			return cls;
		} else {
			try {
				return Class.forName(className.toString());
			} catch (Exception e) {
				return null;
			}
		}
	}

	public Class<?> getClass(String className) {
		if (clscache.containsKey(className)) {
			return (Class<?>) clscache.get(className);
		}
		StringBuffer cn = new StringBuffer(className);
		ArrayList<Object> al = new ArrayList<Object>();
		int p = cn.indexOf("_");
		while (p > -1) {
			al.add(new Integer(p));
			p = cn.indexOf("_", p + 1);
		}
		Class<?> cls = null;
		if (al.size() > 0) {
			try {
				int[] pos = (int[]) Cast.toArray(al, Integer.TYPE, charset);
				cls = getClass(cn, pos, 0, '.');
				if (cls == null) {
					cls = getClass(cn, pos, 0, '_');
				}
				if (cls == null) {
					cls = getInnerClass(cn, pos, 0, '$');
				}
			} catch (Exception e) {
			}
		} else {
			try {
				cls = Class.forName(className.toString());
			} catch (Exception e) {
			}
		}
		clscache.put(className, cls);
		return cls;
	}

	public static String getClassName(Class<?> cls) {
		String className = cls.getName().replace('.', '_').replace('$', '_');
		if (!clscache.containsKey(className)) {
			clscache.put(className, cls);
		}
		return className;
	}

	public static Field getField(Object obj, String fieldName) {
		for (Class<?> cls = obj.getClass(); cls != null; cls = cls
				.getSuperclass()) {
			try {
				Field field = cls.getDeclaredField(fieldName);
				int mod = field.getModifiers();
				if (Modifier.isTransient(mod) || Modifier.isStatic(mod)) {
					return null;
				}
				field.setAccessible(true);
				return field;
			} catch (Exception e) {
			}
		}
		return null;
	}

	private HashMap<String, Field> getFields(Object obj, String[] fieldNames) {
		if (fieldNames == null) {
			return getFields(obj);
		}
		int n = fieldNames.length;
		HashMap<String, Field> fields = new HashMap<String, Field>(n);
		for (int i = 0; i < n; i++) {
			Field f = getField(obj, fieldNames[i]);
			if (f != null) {
				fields.put(fieldNames[i], f);
			}
		}
		return fields;
	}

	private HashMap<String, Field> getFields(Object obj) {
		HashMap<String, Field> fields = new HashMap<String, Field>();
		for (Class<?> cls = obj.getClass(); cls != null; cls = cls
				.getSuperclass()) {
			Field[] fs = cls.getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				Field field = fs[i];
				int mod = fs[i].getModifiers();
				if (!Modifier.isTransient(mod) && !Modifier.isStatic(mod)) {
					field.setAccessible(true);
					if (fields.get(field.getName()) == null) {
						fields.put(field.getName(), field);
					}
				}
			}
		}
		return fields;
	}

	public static Object newInstance(Class<?> cls) {
		return newInstance(cls, true);
	}

	private static Object newInstance(Class<?> cls, boolean tryagain) {
		try {
			if (tryagain) {
				return cls.newInstance();
			}
			ObjectStreamClass desc = ObjectStreamClass.lookup(cls);
			Method m = ObjectStreamClass.class.getDeclaredMethod("newInstance",
					new Class[] {});
			m.setAccessible(true);
			return m.invoke(desc, new Object[] {});
		} catch (Exception e) {
			if (tryagain) {
				return newInstance(cls, false);
			} else {
				return null;
			}
		}
	}

	public Object unserialize(byte[] ss) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		return unserialize(ss, Object.class);
	}

	public Object unserialize(byte[] ss, Class<?> cls)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		ByteArrayInputStream stream = new ByteArrayInputStream(ss);
		Object result = unserialize(stream, new ArrayList<Object>());
		return Cast.cast(result, cls, charset);
	}

	private Object unserialize(ByteArrayInputStream stream,
			ArrayList<Object> objectContainer) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Object obj;
		switch (stream.read()) {
		case __N:
			obj = readNull(stream);
			objectContainer.add(obj);
			return obj;
		case __b:
			obj = readBoolean(stream);
			objectContainer.add(obj);
			return obj;
		case __i:
			obj = readInteger(stream);
			objectContainer.add(obj);
			return obj;
		case __d:
			obj = readDouble(stream);
			objectContainer.add(obj);
			return obj;
		case __s:
			obj = readString(stream);
			objectContainer.add(obj);
			return obj;
		case __S:
			obj = readEscapedString(stream);
			objectContainer.add(obj);
			return obj;
		case __U:
			obj = readUnicodeString(stream);
			objectContainer.add(obj);
			return obj;
		case __r:
			return readRef(stream, objectContainer);
		case __R:
			return readPointRef(stream, objectContainer);
		case __a:
			return readAssocArray(stream, objectContainer);
		case __O:
			return readObject(stream, objectContainer);
		case __C:
			return readCustomObject(stream, objectContainer);
		default:
			return null;
		}
	}

	private String readNumber(ByteArrayInputStream stream) {
		StringBuffer sb = new StringBuffer();
		int i = stream.read();
		while ((i != __Semicolon) && (i != __Colon)) {
			sb.append((char) i);
			i = stream.read();
		}
		return sb.toString();
	}

	private Object readNull(ByteArrayInputStream stream) {
		stream.skip(1);
		return null;
	}

	private Boolean readBoolean(ByteArrayInputStream stream) {
		stream.skip(1);
		Boolean b = new Boolean(stream.read() == __1);
		stream.skip(1);
		return b;
	}

	private Number readInteger(ByteArrayInputStream stream) {
		stream.skip(1);
		return new Integer(readNumber(stream));
	}

	private Number readDouble(ByteArrayInputStream stream) {
		stream.skip(1);
		String d = readNumber(stream);
		if (d.equals(__NAN)) {
			return new Double(Double.NaN);
		}
		if (d.equals(__INF)) {
			return new Double(Double.POSITIVE_INFINITY);
		}
		if (d.equals(__NINF)) {
			return new Double(Double.NEGATIVE_INFINITY);
		}
		if ((d.indexOf('.') > 0) || (d.indexOf('e') > 0)
				|| (d.indexOf('E') > 0)) {
			return new Double(d);
		}
		int len = d.length();
		char c = d.charAt(0);
		if ((len < 19) || ((c == '-') && (len < 20))) {
			return new Long(d);
		}
		if ((len > 20) || ((c != '-') && (len > 19))) {
			return new Double(d);
		}
		try {
			return new Long(d);
		} catch (Exception e) {
			return new Double(d);
		}
	}

	private byte[] readString(ByteArrayInputStream stream) {
		stream.skip(1);
		int len = Integer.parseInt(readNumber(stream));
		stream.skip(1);
		byte[] buf = new byte[len];
		stream.read(buf, 0, len);
		stream.skip(2);
		return buf;
	}

	private byte[] readEscapedString(ByteArrayInputStream stream) {
		stream.skip(1);
		int len = Integer.parseInt(readNumber(stream));
		stream.skip(1);
		byte[] buf = new byte[len];
		int c;
		for (int i = 0; i < len; i++) {
			if ((c = stream.read()) == __Slash) {
				char c1 = (char) stream.read();
				char c2 = (char) stream.read();
				buf[i] = (byte) (Integer.parseInt(new String(new char[] { c1,
						c2 }), 16) & 0xff);
			} else {
				buf[i] = (byte) (c & 0xff);
			}
		}
		stream.skip(2);
		return buf;
	}

	private String readUnicodeString(ByteArrayInputStream stream) {
		stream.skip(1);
		int len = Integer.parseInt(readNumber(stream));
		stream.skip(1);
		StringBuffer sb = new StringBuffer(len);
		int c;
		for (int i = 0; i < len; i++) {
			if ((c = stream.read()) == __Slash) {
				char c1 = (char) stream.read();
				char c2 = (char) stream.read();
				char c3 = (char) stream.read();
				char c4 = (char) stream.read();
				sb.append((char) (Integer.parseInt(new String(new char[] { c1,
						c2, c3, c4 }), 16)));
			} else {
				sb.append((char) c);
			}
		}
		stream.skip(2);
		return sb.toString();
	}

	private Object readRef(ByteArrayInputStream stream,
			ArrayList<Object> objectContainer) {
		stream.skip(1);
		Object obj = objectContainer
				.get(Integer.parseInt(readNumber(stream)) - 1);
		objectContainer.add(obj);
		return obj;
	}

	private Object readPointRef(ByteArrayInputStream stream,
			ArrayList<Object> objectContainer) {
		stream.skip(1);
		return objectContainer.get(Integer.parseInt(readNumber(stream)) - 1);
	}

	private AssocArray readAssocArray(ByteArrayInputStream stream,
			ArrayList<Object> objectContainer) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		stream.skip(1);
		int n = Integer.parseInt(readNumber(stream));
		stream.skip(1);
		AssocArray a = new AssocArray(n);
		objectContainer.add(a);
		for (int i = 0; i < n; i++) {
			Object key;
			switch (stream.read()) {
			case __i:
				key = new Integer(readInteger(stream).intValue());
				break;
			case __s:
				key = Cast.cast(readString(stream), String.class, charset);
				break;
			case __S:
				key = Cast.cast(readEscapedString(stream), String.class,
						charset);
				break;
			case __U:
				key = readUnicodeString(stream);
				break;
			default:
				return null;
			}
			Object result = unserialize(stream, objectContainer);
			if (key instanceof Integer) {
				a.set((Integer) key, result);
			} else {
				a.set((String) key, result);
			}
		}
		stream.skip(1);
		return a;
	}

	private Calendar readCalendar(ByteArrayInputStream stream,
			ArrayList<Object> objectContainer, int n) {
		HashMap<String, Object> dt = new HashMap<String, Object>(n);
		String key;
		for (int i = 0; i < n; i++) {
			switch (stream.read()) {
			case __s:
				key = getString(readString(stream));
				break;
			case __S:
				key = getString(readEscapedString(stream));
				break;
			case __U:
				key = readUnicodeString(stream);
				break;
			default:
				return null;
			}
			if (stream.read() == __i) {
				dt.put(key, Cast.cast(readInteger(stream), Integer.class));
			} else {
				return null;
			}
		}
		stream.skip(1);
		GregorianCalendar calendar = new GregorianCalendar(
				((Integer) dt.get("year")).intValue(),
				((Integer) dt.get("month")).intValue() - 1,
				((Integer) dt.get("day")).intValue(),
				((Integer) dt.get("hour")).intValue(),
				((Integer) dt.get("minute")).intValue(),
				((Integer) dt.get("second")).intValue());
		objectContainer.add(calendar);
		objectContainer.add(dt.get("year"));
		objectContainer.add(dt.get("month"));
		objectContainer.add(dt.get("day"));
		objectContainer.add(dt.get("hour"));
		objectContainer.add(dt.get("minute"));
		objectContainer.add(dt.get("second"));
		objectContainer.add(dt.get("millisecond"));
		return calendar;
	}

	@SuppressWarnings("unchecked")
	private Object readObject(ByteArrayInputStream stream,
			ArrayList<Object> objectContainer) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		stream.skip(1);
		int len = Integer.parseInt(readNumber(stream));
		stream.skip(1);
		byte[] buf = new byte[len];
		stream.read(buf, 0, len);
		String cn = getString(buf);
		stream.skip(2);
		int n = Integer.parseInt(readNumber(stream));
		stream.skip(1);
		if (cn.equals("PHPRPC_Date")) {
			return readCalendar(stream, objectContainer, n);
		}
		Class<?> cls = getClass(cn);
		Object o;
		HashMap<?, ?> fields = null;
		if (cls != null) {
			if ((o = newInstance(cls)) == null) {
				o = new HashMap<Object, Object>(n);
			} else {
				fields = (HashMap<?, ?>) fieldcache.get(cls);
			}
		} else {
			o = new HashMap<Object, Object>(n);
		}
		objectContainer.add(o);
		for (int i = 0; i < n; i++) {
			String key;
			switch (stream.read()) {
			case __s:
				key = getString(readString(stream));
				break;
			case __S:
				key = getString(readEscapedString(stream));
				break;
			case __U:
				key = readUnicodeString(stream);
				break;
			default:
				return null;
			}
			if (key.charAt(0) == (char) 0) {
				key = key.substring(key.indexOf("\0", 1) + 1);
			}
			Object result = unserialize(stream, objectContainer);
			if (o instanceof HashMap) {
				((HashMap<String, Object>) o).put(key, result);
			} else {
				Field f;
				if (fields == null) {
					f = getField(o, key);
				} else {
					f = (Field) fields.get(key);
				}
				if (f != null) {
					f.set(o, Cast.cast(result, f.getType(), charset));
				}
			}
		}
		stream.skip(1);
		if (!(o instanceof HashMap)) {
			Method __wakeup = null;
			if (__wakeupcache.containsKey(cls)) {
				__wakeup = (Method) __wakeupcache.get(cls);
			} else {
				try {
					__wakeup = cls.getMethod("__wakeup", new Class[0]);
					__wakeup.setAccessible(true);
				} catch (Exception e) {
				}
				__wakeupcache.put(cls, __wakeup);
			}
			if (__wakeup != null) {
				__wakeup.invoke(o, new Object[] {});
			}
		}
		return o;
	}

	private Object readCustomObject(ByteArrayInputStream stream,
			ArrayList<Object> objectContainer) {
		stream.skip(1);
		int len = Integer.parseInt(readNumber(stream));
		stream.skip(1);
		byte[] buf = new byte[len];
		stream.read(buf, 0, len);
		String cn = getString(buf);
		stream.skip(2);
		int n = Integer.parseInt(readNumber(stream));
		stream.skip(1);
		Class<?> cls = getClass(cn);
		Object o;
		if (cls != null) {
			o = newInstance(cls);
		} else {
			o = null;
		}
		objectContainer.add(o);
		if (o == null) {
			stream.skip(n);
		} else if (o instanceof Serializable) {
			byte[] b = new byte[n];
			stream.read(b, 0, n);
			((Serializable) o).unserialize(b);
		} else {
			stream.skip(n);
		}
		stream.skip(1);
		return o;
	}

	public class AssocArray implements Cloneable, java.io.Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1605562814551245006L;
		private ArrayList<Object> arrayList;
		private LinkedHashMap<Object, Object> hashMap;
		private int arrayLength;
		private int maxNumber;

		public AssocArray() {
			arrayList = new ArrayList<Object>();
			hashMap = new LinkedHashMap<Object, Object>();
			arrayLength = 0;
			maxNumber = -1;
		}

		public AssocArray(int initialCapacity) {
			arrayList = new ArrayList<Object>(initialCapacity);
			hashMap = new LinkedHashMap<Object, Object>(initialCapacity);
			arrayLength = 0;
			maxNumber = -1;
		}

		public AssocArray(int initialCapacity, float loadFactor) {
			arrayList = new ArrayList<Object>(initialCapacity);
			hashMap = new LinkedHashMap<Object, Object>(initialCapacity,
					loadFactor);
			arrayLength = 0;
			maxNumber = -1;
		}

		public AssocArray(Collection<?> c) {
			arrayList = new ArrayList<Object>(c);
			arrayLength = arrayList.size();
			maxNumber = arrayLength - 1;
			hashMap = new LinkedHashMap<Object, Object>(arrayLength);
			for (int i = 0; i < arrayLength; i++) {
				hashMap.put(new Integer(i), arrayList.get(i));
			}
		}

		public AssocArray(Map<?, ?> m) {
			int len = m.size();
			arrayList = new ArrayList<Object>(len);
			hashMap = new LinkedHashMap<Object, Object>(len);
			arrayLength = 0;
			maxNumber = -1;
			Iterator<?> keys = m.keySet().iterator();
			while (keys.hasNext()) {
				Object key = keys.next();
				if ((key instanceof Integer) || (key instanceof Short)
						|| (key instanceof Byte)) {
					int k = ((Number) key).intValue();
					if (k > -1) {
						arrayLength++;
						if (maxNumber < k) {
							maxNumber = k;
						}
						// assert (maxNumber + 1 >= arrayLength);
					}
					hashMap.put(new Integer(k), m.get(key));
				} else if (key instanceof String) {
					hashMap.put(key, m.get(key));
				}
			}
			setArrayList();
		}

		private void setArrayList() {
			int len = arrayList.size();
			// assert (len <= arrayLength);
			if (len < arrayLength) {
				if (maxNumber + 1 == arrayLength) {
					for (int i = len; i < arrayLength; i++) {
						arrayList.add(hashMap.get(new Integer(i)));
					}
				} else {
					Integer key = new Integer(len);
					while (hashMap.containsKey(key)) {
						arrayList.add(hashMap.get(key));
						key = new Integer(++len);
					}
				}
			}
		}

		public ArrayList<Object> toArrayList() {
			setArrayList();
			return arrayList;
		}

		public HashMap<Object, Object> toHashMap() {
			return hashMap;
		}

		public LinkedHashMap<Object, Object> toLinkedHashMap() {
			return hashMap;
		}

		public int size() {
			return hashMap.size();
		}

		public boolean isEmpty() {
			return hashMap.isEmpty();
		}

		public boolean add(Object element) {
			int index = arrayList.size();
			boolean result = arrayList.add(element);
			if (result) {
				Integer key = new Integer(index);
				if (!hashMap.containsKey(key)) {
					arrayLength++;
					if (maxNumber < index) {
						maxNumber = index;
					}
					// assert (maxNumber + 1 >= arrayLength);
				}
				hashMap.put(key, element);
			}
			return result;
		}

		public boolean addAll(Collection<?> c) {
			int len = c.size();
			int index = arrayList.size() - 1;
			boolean result = arrayList.addAll(c);
			if (result) {
				for (int i = 0; i < len; i++) {
					Integer key = new Integer(++index);
					if (!hashMap.containsKey(key)) {
						arrayLength++;
					}
					hashMap.put(key, arrayList.get(index));
				}
				if (maxNumber < index) {
					maxNumber = index;
				}
				// assert (maxNumber + 1 >= arrayLength);
			}
			return result;
		}

		public void putAll(Map<?, ?> m) {
			Iterator<?> keys = m.keySet().iterator();
			while (keys.hasNext()) {
				Object key = keys.next();
				if ((key instanceof Integer) || (key instanceof Short)
						|| (key instanceof Byte)) {
					int k = ((Number) key).intValue();
					key = new Integer(k);
					if (k > -1 && !hashMap.containsKey(key)) {
						arrayLength++;
						if (maxNumber < k) {
							maxNumber = k;
						}
						// assert (maxNumber + 1 >= arrayLength);
					}
					hashMap.put(key, m.get(key));
				} else if (key instanceof String) {
					hashMap.put(key, m.get(key));
				}
			}
			setArrayList();
		}

		public Object get(int index) {
			if (index < arrayList.size()) {
				return arrayList.get(index);
			} else {
				return hashMap.get(new Integer(index));
			}
		}

		public Object get(Byte key) {
			return get(key.intValue());
		}

		public Object get(Short key) {
			return get(key.intValue());
		}

		public Object get(Integer key) {
			return get(key.intValue());
		}

		public Object get(String key) {
			return hashMap.get(key);
		}

		public Object set(int index, Object element) {
			Integer key = new Integer(index);
			if (index > -1) {
				int size = arrayList.size();
				if (size > index) {
					arrayList.set(index, element);
				} else {
					if (size == index) {
						arrayList.add(element);
					}
					if (!hashMap.containsKey(key)) {
						arrayLength++;
						if (maxNumber < index) {
							maxNumber = index;
						}
						// assert (maxNumber + 1 >= arrayLength);
					}
				}
			}
			return hashMap.put(key, element);
		}

		public Object set(Byte key, Object element) {
			return set(key.intValue(), element);
		}

		public Object set(Short key, Object element) {
			return set(key.intValue(), element);
		}

		public Object set(Integer key, Object element) {
			return set(key.intValue(), element);
		}

		public Object set(String key, Object element) {
			return hashMap.put(key, element);
		}

		public Object remove(int index) {
			Integer key = new Integer(index);
			if (index > -1) {
				if (hashMap.containsKey(key)) {
					arrayLength--;
					int lastIndex = arrayList.size() - 1;
					if (index <= lastIndex) {
						for (int i = lastIndex; i >= index; i--) {
							arrayList.remove(i);
						}
						if (maxNumber == index) {
							maxNumber--;
						}
					} else if (maxNumber == index) {
						while ((--index > lastIndex)
								&& !hashMap.containsKey(new Integer(index)))
							;
						maxNumber = index;
					}
					// assert (maxNumber + 1 >= arrayLength);
				} else {
					return null;
				}
			}
			return hashMap.remove(key);
		}

		public Object remove(Byte key) {
			return remove(key.intValue());
		}

		public Object remove(Short key) {
			return remove(key.intValue());
		}

		public Object remove(Integer key) {
			return remove(key.intValue());
		}

		public Object remove(String key) {
			return hashMap.remove(key);
		}

		public void clear() {
			arrayList.clear();
			hashMap.clear();
			arrayLength = 0;
			maxNumber = -1;
		}

		@SuppressWarnings("unchecked")
		public Object clone() throws CloneNotSupportedException {
			AssocArray result = null;
			result = (AssocArray) super.clone();
			result.arrayList = (ArrayList<Object>) this.arrayList.clone();
			result.hashMap = (LinkedHashMap<Object, Object>) this.hashMap
					.clone();
			result.arrayLength = this.arrayLength;
			result.maxNumber = this.maxNumber;
			return result;
		}
	}
}