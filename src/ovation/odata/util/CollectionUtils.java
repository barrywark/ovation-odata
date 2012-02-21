package ovation.odata.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Lists;

/**
 * a few utils they probably should add to google Maps.
 * @author Ron
 *
 */
public class CollectionUtils {
	public static class BasicMapEntry<K,V>  implements Map.Entry<K, V> {
		private K _key;
		private V _val;
		public BasicMapEntry(K key, V val) { _key = key; _val = val; }
		public K getKey() 					{ return _key; }
		public V getValue() 				{ return _val; }
		public V setValue(V value) 			{ V oldVal = _val; _val = value; return oldVal; }
		@SuppressWarnings("unchecked")
		public boolean equals(Object obj) 	{ return _key != null ? _key.equals((K)obj) : obj == null; }
		public int hashCode() 				{ return _key != null ? _key.hashCode() : 0;  }
	}
	
	public static <K,V> Map.Entry<K, V> newEntry(final K key, final V val) { 
		return new BasicMapEntry<K,V>(key, val); 
	}
	public static <K,V> Map<K,V> addAll(Map<K,V> map, Map.Entry<K, V>... entries) {
		for (Map.Entry<K,V> entry : entries) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	/** wrap array in Collection and return Iterable that returns its iterator */
	public static <T> Iterable<T> makeIterable(final T... array) {
		if (array == null || array.length == 0) {
			return makeEmptyIterable();
		}
		return makeIterable(Lists.newArrayList(array));
	}

	/** convert Map to Set and return Iterable that returns its iterator */
	public static <K,V> Iterable<Map.Entry<K,V>> makeIterable(final Map<K,V> map) {
		if (map == null || map.isEmpty()) {
			return makeEmptyIterable();
		}
		return makeIterable(map.entrySet());
	}

	/** consume Enumeration into Collection and return Iterable wrapped around its iterator */
	public static <T> Iterable<T> makeIterable(final Enumeration<T> e) {
		if (e == null) {
			return makeEmptyIterable();
		}
		return makeIterable(
			new Iterator<T>() {
				public boolean 	hasNext() 	{ return e.hasMoreElements(); }
				public T 		next()		{ return e.nextElement(); }
				public void 	remove() 	{ throw new UnsupportedOperationException("remove not allowed"); }
			}
		);
	}

	public static <T> Iterable<T> makeIterable(final Iterator<T> i) {
		if (i == null) {
			return makeEmptyIterable();
		}
		// consume iterator and store such that we can iterate again
		return makeIterable(Lists.newArrayList( new Iterable<T>() { public Iterator<T> iterator() { return i; } } ));
	}

	/** Iterable is a iterator factory, not a wrapper */
	public static <T> Iterable<T> makeIterable(final Collection<T> col) {
		return new Iterable<T>() { public Iterator<T> iterator() { return col.iterator(); } };
	}
	
	public static <T> Iterable<T> makeEmptyIterable() {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return new Iterator<T>() {
					public boolean 	hasNext() 	{ return false; }
					public T 		next()		{ return null; }
					public void 	remove() 	{ throw new UnsupportedOperationException("remove not allowed"); }
				};
			}			
		};
	}
}
