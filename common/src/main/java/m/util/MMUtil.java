package m.util;

import java.util.HashMap;
import java.util.Map;

public class MMUtil {
	public static <X, K, V> Map<K, V> getIfNotExitHashMap(X key, Map<X, Map<K, V>> mm) {
		Map<K, V> m = mm.get(key);
		if (m == null) {
			m = new HashMap<K, V>();
			mm.put(key, m);
		}
		return m;
	}
}
