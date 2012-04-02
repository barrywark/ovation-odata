package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;
import org.odata4j.core.OEntityKey;

import ovation.IAnnotation;
import ovation.NumericDataType;
import ovation.odata.model.OvationModelBase.CollectionName;
import ovation.odata.model.OvationModelBase.PropertyName;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

public class NumericDataTypeModel extends ExtendedPropertyModel<String,NumericDataType> {
	static final Logger _log = Logger.getLogger(NumericDataTypeModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		OvationModelBase.addProperties (_propertyTypeMap,   PropertyName.ByteOrder, PropertyName.Format, PropertyName.NumericByteOrder, PropertyName.SampleBytes);
	}
	
	public NumericDataTypeModel() { super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 				getTypeName()	{ return "NumericDataType"; }
	public String 				entityName() 	{ return "_NumericDataTypes"; }
	public Class<NumericDataType>	getEntityType() { return NumericDataType.class; }
	public Class<String> 		getKeyType() 	{ return String.class; }
	
	public Iterable<?> 	getCollectionValue(Object target, String collectionName) 	{
		_log.error("unrecognized col " + collectionName + " for " + target); 
		return CollectionUtils.makeEmptyIterable();
	}
	
	public Object getPropertyValue(Object target, String propertyName) {
		NumericDataType obj = (NumericDataType)target;
		PropertyName prop = PropertyName.valueOf(propertyName);
		
    	switch (prop) {
    		// everything is Strings until i can convert them to something meaningful
	    	case ByteOrder:			return String.valueOf(obj.getByteOrder());
	    	case Format:			return String.valueOf(obj.getFormat());
	    	case NumericByteOrder:	return String.valueOf(obj.getNumericByteOrder());
	    	case SampleBytes:		return obj.getSampleBytes();
	    	default: 				_log.error("unrecognized prop " + prop + " for " + obj); return null; 
    	}
	}

	public Func<Iterable<NumericDataType>> allGetter() {
		return new Func<Iterable<NumericDataType>>() {  public Iterable<NumericDataType> apply() { return CollectionUtils.makeEmptyIterable(); } };
	}

	public Func1<NumericDataType,String> idGetter() { 
		return new Func1<NumericDataType,String>() { public String apply(NumericDataType obj) { return obj != null ? obj.toString() : null; } };
	}
	@Override
	public NumericDataType getEntityByKey(OEntityKey key) {
		// TODO Auto-generated method stub
		return null;
	}
}
