package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;
import org.odata4j.core.OEntityKey;

import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

public class IntegerModel extends ExtendedPropertyModel<Integer,Integer> {
	static final Logger _log = Logger.getLogger(IntegerModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		_propertyTypeMap.put("value", Integer.class);
	}
	
	public IntegerModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 			getTypeName()	{ return "Integer"; }
	public String 			entityName() 	{ return "_Integers"; }
	public Class<Integer>	getEntityType() { return Integer.class; }
	public Class<Integer> 	getKeyType() 	{ return Integer.class; }	// FIXME - TECHNICALLY this type has no key (it just adapts String so it can be returned as a collection-type)
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		_log.error("unrecognized collection name '" + collectionName + "'");
		return null;
	}

	public Object getPropertyValue(Object target, String propertyName) {
		if ("value".equals(propertyName)) return (Integer)target;
		_log.error("unrecognized property name '" + propertyName + "'");
		return null;
	}

	public Func<Iterable<Integer>> allGetter() {
		return new Func<Iterable<Integer>>() {  public Iterable<Integer> apply() { return CollectionUtils.makeEmptyIterable(); } };
	}

	public Func1<Integer,Integer> idGetter() { 
		return new Func1<Integer,Integer>() { public Integer apply(Integer record) { _log.error("trying to get key out of '" + record + "'"); return null; } };
	}
	@Override
	public Integer getEntityByKey(OEntityKey key) {
		// TODO Auto-generated method stub
		return null;
	}
}