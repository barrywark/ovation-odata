package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * minor hack to work around OData4J limitation that all collection types must be registered 
 * via model (and thus appear as top-level types in the service - that seems like a bug to me)
 * @author Ron
 */
public class MapEntryModel extends ExtendedPropertyModel<String,MapEntry> {
	static final Logger _log = Logger.getLogger(MapEntryModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		_propertyTypeMap.put("key", 	Object.class);
		_propertyTypeMap.put("value", 	Object.class);
	}
	
	public MapEntryModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 			getTypeName()	{ return "MapEntry"; }
	public String 			entityName() 	{ return "_MapEntries"; }
	public Class<MapEntry>	getEntityType() { return MapEntry.class; }
	public Class<String> 	getKeyType() 	{ return String.class; }	// FIXME - TECHNICALLY this type has no key (it just adapts MapEntry so it can be returned as a collection-type)
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		_log.error("unrecognized collection name '" + collectionName + "'");
		return null;
	}

	public Object getPropertyValue(Object target, String propertyName) {
		MapEntry obj = (MapEntry)target;
		if ("key".equals(propertyName)) return obj.getKey();
		if ("value".equals(propertyName)) return obj.getValue();
		_log.error("unrecognized property name '" + propertyName + "'");
		return null;
	}

	public Func<Iterable<MapEntry>> allGetter() {
		return new Func<Iterable<MapEntry>>() {  public Iterable<MapEntry> apply() { return CollectionUtils.makeEmptyIterable(); } };
	}

	public Func1<MapEntry,String> idGetter() { 
		return new Func1<MapEntry,String>() { public String apply(MapEntry record) { _log.error("trying to get key out of '" + record + "'"); return ""; } };
	}
}