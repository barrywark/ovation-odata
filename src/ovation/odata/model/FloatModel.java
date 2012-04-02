package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;
import org.odata4j.core.OEntityKey;

import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

public class FloatModel extends ExtendedPropertyModel<Float,Float> {
	static final Logger _log = Logger.getLogger(FloatModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		_propertyTypeMap.put("value", Float.class);
	}
	
	public FloatModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 		getTypeName()	{ return "Float"; }
	public String 		entityName() 	{ return "_Floats"; }
	public Class<Float>	getEntityType() { return Float.class; }
	public Class<Float>	getKeyType() 	{ return Float.class; }	// FIXME - TECHNICALLY this type has no key (it just adapts String so it can be returned as a collection-type)
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		_log.error("unrecognized collection name '" + collectionName + "'");
		return null;
	}

	public Object getPropertyValue(Object target, String propertyName) {
		if ("value".equals(propertyName)) return (Float)target;
		_log.error("unrecognized property name '" + propertyName + "'");
		return null;
	}

	public Func<Iterable<Float>> allGetter() {
		return new Func<Iterable<Float>>() {  public Iterable<Float> apply() { return CollectionUtils.makeEmptyIterable(); } };
	}

	public Func1<Float,Float> idGetter() { 
		return new Func1<Float,Float>() { public Float apply(Float record) { _log.error("trying to get key out of '" + record + "'"); return null; } };
	}
	@Override
	public Float getEntityByKey(OEntityKey key) {
		// TODO Auto-generated method stub
		return null;
	}
}