package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.Response;

import com.google.common.collect.Maps;

/**
 * presents Response data to the OData4J framework
a * @author Ron
 */
public class ResponseModel extends OvationModelBase<String,Response> {
	static final Logger _log = Logger.getLogger(ResponseModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static { addResponse(_propertyTypeMap, _collectionTypeMap); }
	
	public ResponseModel() 					{ super(_propertyTypeMap, _collectionTypeMap); }
	public String			getTypeName()	{ return "Response"; }
	public String 			entityName() 	{ return "Responses"; }
	public Class<Response> 	getEntityType() { return Response.class; }
	public Class<String> 	getKeyType() 	{ return String.class; }
	
	public Iterable<?> 	getCollectionValue	(Object target, String collectionName)	{ return getCollection((Response)target, CollectionName.valueOf(collectionName)); }
	public Object 		getPropertyValue	(Object target, String propertyName) 	{ return getProperty((Response)target,   PropertyName.valueOf(propertyName)); }
	
	public Func<Iterable<Response>> allGetter() {
		return new Func<Iterable<Response>>() { 
			public Iterable<Response> apply() { 
				final Iterable<Response> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<Response,String> idGetter() {
		return new Func1<Response,String>() {
			public String apply(Response record) {
				return record.getUuid();
			}		
		};
	}
}