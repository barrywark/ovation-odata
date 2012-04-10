package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;
import org.odata4j.core.OEntityKey;

import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

public class DoubleModel extends ExtendedPropertyModel<Double,Double> {
	static final Logger _log = Logger.getLogger(StringModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		_propertyTypeMap.put("value", Double.class);
	}
	
	public DoubleModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 			getTypeName()	{ return "Double"; }
	public String 			entityName() 	{ return "_Doubles"; }
	public Class<Double>	getEntityType() { return Double.class; }
	public Class<Double> 	getKeyType() 	{ return Double.class; }	// FIXME - TECHNICALLY this type has no key (it just adapts String so it can be returned as a collection-type)
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		_log.error("unrecognized collection name '" + collectionName + "'");
		return null;
	}

	public Object getPropertyValue(Object target, String propertyName) {
		if ("value".equals(propertyName)) return String.valueOf(target);
		_log.error("unrecognized property name '" + propertyName + "'");
		return null;
	}

	public Func<Iterable<Double>> allGetter() {
		return new Func<Iterable<Double>>() {  public Iterable<Double> apply() { return CollectionUtils.makeEmptyIterable(); } };
	}

	public Func1<Double, Double> idGetter() { 
		return new Func1<Double, Double>() { public Double apply(Double record) { _log.error("trying to get key out of '" + record + "'"); return null; } };
	}
	@Override
	public Double getEntityByKey(OEntityKey key) {
		// TODO Auto-generated method stub
		return null;
	}
}
