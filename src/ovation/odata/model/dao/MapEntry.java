package ovation.odata.model.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.odata4j.edm.EdmComplexType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.edm.EdmType;

import ovation.odata.util.StringUtils;

import com.google.common.collect.Lists;

/**
 * the purpose of this class is to dumb-down the generic Map.Entry<K,V> to a String-String pair
 * to make it easier for OData4J to render it into JSON/XML 
 * @author Ron
 *
 */
public class MapEntry {
	private final static EdmType _type = new EdmComplexType("Ovodata", "MapEntry", 
		Lists.newArrayList(new EdmProperty[]{
			new EdmProperty("key", 	 EdmSimpleType.STRING, true),	
			new EdmProperty("value", EdmSimpleType.STRING, true),	
		}
	)); 

	private final String _key;
	private final String _value;

	public MapEntry(Object key, Object value) { 
		_key 	= StringUtils.toString(key); 
		_value 	= StringUtils.toString(value); 
	}
	
	public String getKey() 	 { return _key; }
	public String getValue() { return _value; }
	public String toString() { return super.toString() + "{key:'" + _key + "', val:'" + _value + "'}"; }
	
	public static EdmType getType() { return _type; }

	
	public static <K,V> Iterable<MapEntry> makeIterable(Map<K,V> map) {
		final List<MapEntry> entryList = Lists.newArrayList();
		
		for (Map.Entry<K,V> entry : map.entrySet()) {
			entryList.add(new MapEntry(entry.getKey(), entry.getValue()));
		}
		
		return new Iterable<MapEntry>() {
			public Iterator<MapEntry> iterator() {
				return entryList.iterator();
			}
		};
	}
	
}
