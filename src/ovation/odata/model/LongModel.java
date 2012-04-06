package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;
import org.odata4j.core.OEntityKey;

import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

public class LongModel extends ExtendedPropertyModel<Long,Long> {
	static final Logger _log = Logger.getLogger(LongModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		_propertyTypeMap.put("value", Long.class);
	}
	
	public LongModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 		getTypeName()	{ return "Long"; }
	public String 		entityName() 	{ return "_Longs"; }
	public Class<Long>	getEntityType() { return Long.class; }
	public Class<Long> 	getKeyType() 		{ return Long.class; }	// FIXME - TECHNICALLY this type has no key (it just adapts String so it can be returned as a collection-type)
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		_log.error("unrecognized collection name '" + collectionName + "'");
		return null;
	}

	public Object getPropertyValue(Object target, String propertyName) {
		if ("value".equals(propertyName)) return (Long)target;
		_log.error("unrecognized property name '" + propertyName + "'");
		return null;
	}

	public Func<Iterable<Long>> allGetter() {
		return new Func<Iterable<Long>>() {  public Iterable<Long> apply() { return CollectionUtils.makeEmptyIterable(); } };
	}

	public Func1<Long,Long> idGetter() { 
		return new Func1<Long,Long>() { public Long apply(Long record) { return record; } };
	}
	@Override
	public Long getEntityByKey(OEntityKey key) {
		// TODO Auto-generated method stub
		return null;
	}
}