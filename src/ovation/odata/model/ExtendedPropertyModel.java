package ovation.odata.model;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;
import org.joda.time.DateTime;
import org.odata4j.core.OEntityKey;
import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.inmemory.PropertyModel;

import ovation.ExternalDevice;
import ovation.KeywordTag;
import ovation.NumericData;
import ovation.NumericDataType;
import ovation.Resource;
import ovation.User;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;
import ovation.odata.util.DataContextCache;

import com.google.common.collect.Maps;

/**
 * this class is an extension of the OData4J PropertyModel interface which allows us to (hopefully) package
 * everything an OData4J Producer implementation needs to serve up the object model entities.  in some
 * ways it replicates what is exposed via the JavaBean API but gives us finer-grained control over how
 * it's exposed and used (and addresses areas where the object model isn't 100% JavaBean compliant).
 *    
 * @author Ron
 *
 * @param <K> Key type - the type of object used to look up this type of Value
 * @param <V> Value type - the type this model is about
 */
public abstract class ExtendedPropertyModel<K,V> implements PropertyModel {
    public static final Logger _log = Logger.getLogger(ExtendedPropertyModel.class);

    public static final String GET_ALL_PQL = "true";	// apparently this PQL "query" returns all instances of a type
    private static final ThreadLocal<QueryInfo> _threadQueryInfo = new ThreadLocal<QueryInfo>();
    
    protected Map<String,Class<?>> _fieldTypes;
    protected Map<String,Class<?>> _collectionTypes;
    
    protected ExtendedPropertyModel(Map<String,Class<?>> fieldTypes, Map<String,Class<?>> collectionTypes) {
        _fieldTypes = fieldTypes;
        _collectionTypes = collectionTypes;
    }
    
    /** @return the type of the specified collection within the associated entity type */
    public Class<?> getCollectionElementType(String collectionName) {
        if (_collectionTypes.containsKey(collectionName) == false) {
            _log.error("Unrecognized collection - " + collectionName);
        }
        return _collectionTypes.get(collectionName); 
    }
    
    /** @return the names of collections within the associated entity type */
    public Iterable<String> getCollectionNames() { 
        return _collectionTypes.keySet(); 
    }
    
    /** @return the names of the properties of the associated entity type */
    public Iterable<String> getPropertyNames() { 
        return _fieldTypes.keySet(); 
    }
    
    /** @return the type of the specified property of the associated entity type */
    public Class<?> getPropertyType(String propertyName) { 
        if (_fieldTypes.containsKey(propertyName) == false) {
            _log.error("Unrecognized property - " + propertyName);
        }
        return _fieldTypes.get(propertyName); 
    }
    
    /** @return the name of the entity set this model represents and by which it is registered with OData4J */
    public abstract String entityName();
    /** @return the elements of the specified collection with the associated entity type */
    public abstract Iterable<?> getCollectionValue(Object target, String collectionName);
    /** @return the value of the specified property of the associated entity type */
    public abstract Object getPropertyValue(Object target, String propertyName);
    
    /** @return the type of Entity this Model is for */
    public abstract Class<V> getEntityType(); 
    /** @return the primary-key type for this model's entity type */
    public abstract Class<K> getKeyType();
    /** @return a Func object which, when apply()ed will return all top-level entities of this model's type */
    public abstract Func<Iterable<V>> allGetter();
    /** @return a Func1 object which, when apply()ed will return the primary key for the provided entity instance */
    public abstract Func1<V,K> idGetter();
    /** @return the name used to identify this type in the Ovation DB */
    public abstract String getTypeName();
    
    /** @return entity that matches key or null if none */
    public V getEntityByKey(OEntityKey key) {
        Iterator<V> itr = getEntityIterByUUID(key);
        if (itr.hasNext() == false) {
            _log.error("Unable to find Project with UUID " + key.toKeyStringWithoutParentheses());
            return null;
        }
        V v = itr.next(); // the object we want.
        if (itr.hasNext()) {
            _log.error("Found multiple " + getEntityType() + " with UUID " + key.toKeyStringWithoutParentheses());
        }
        return v;
    }
    
    @SuppressWarnings("unchecked")
    protected Iterator<V> getEntityIterByUUID(OEntityKey key) {
        String typeName = getTypeName();
        String query     = "uuid == " + key.toKeyStringWithoutParentheses();
        _log.info("executing type:'" + typeName + "', query:'" + query + "'");
        return (Iterator<V>)DataContextCache.getThreadContext().query(typeName, query); 
    }
    
    // ExtendedPropertyModel instances keyed by value type
    private static final HashMap<Class<?>, ExtendedPropertyModel<?,?>> _modelByTypeMap = Maps.newHashMap();
    private static final HashMap<String, ExtendedPropertyModel<?,?>>   _modelByNameMap = Maps.newHashMap();
    
    public static ExtendedPropertyModel<?,?> getPropertyModel(Class<?> type) {
        return _modelByTypeMap.get(type);
    }
    public static ExtendedPropertyModel<?,?> getPropertyModel(String name) {
        return _modelByNameMap.get(name);
    }
    public static void setPropertyModel(Class<?> type, ExtendedPropertyModel<?,?> model) {
        _modelByTypeMap.put(type, model);
        _modelByNameMap.put(model.entityName(), model);
    }
    public static void addPropertyModel(ExtendedPropertyModel<?,?> model) {
        setPropertyModel(model.getEntityType(), model);
    }
    public static Set<Class<?>> getEntityTypes() { 
        return _modelByTypeMap.keySet(); 
    }
    public static Set<String> getEntityNames() {
        return _modelByNameMap.keySet();
    }
    
    public static void 		setQueryInfo(QueryInfo info) { if (info != null) { _threadQueryInfo.set(info); } else { _threadQueryInfo.remove(); } }
    public static QueryInfo getQueryInfo()             	 { return _threadQueryInfo.get(); }
    
    /** register model handlers for all standard ovation classes */
    public static void registerOvationModel() {
        ExtendedPropertyModel.addPropertyModel(new ProjectModel());
        ExtendedPropertyModel.addPropertyModel(new AnalysisRecordModel());
        ExtendedPropertyModel.addPropertyModel(new ExperimentModel());
        ExtendedPropertyModel.addPropertyModel(new SourceModel());
        ExtendedPropertyModel.addPropertyModel(new ResourceModel());
        ExtendedPropertyModel.addPropertyModel(new URLResourceModel());
        ExtendedPropertyModel.addPropertyModel(new KeywordTagModel());
        ExtendedPropertyModel.addPropertyModel(new EpochGroupModel());
        ExtendedPropertyModel.addPropertyModel(new EpochModel());
        ExtendedPropertyModel.addPropertyModel(new ExternalDeviceModel());
        ExtendedPropertyModel.addPropertyModel(new StimulusModel());
        ExtendedPropertyModel.addPropertyModel(new ResponseModel());
        ExtendedPropertyModel.addPropertyModel(new DerivedResponseModel());
        ExtendedPropertyModel.addPropertyModel(new MapEntryModel());
        ExtendedPropertyModel.addPropertyModel(new StringModel());
        // TODO Query?
    }
    
    /** every property of every child-type - ensures consistent naming */
    protected enum PropertyName     {
        SamplingRate, SamplingUnits,
        ParentRoot,
        PluginID,
        Url,
        Uti,
        Tag,
        Manufacturer,
        Duration, EpochGroup, ExcludeFromAnalysis, NextEpoch, PreviousEpoch, ProtocolID,
        EpochCount, Experiment, Label, Parent, Source,
        Description, Epoch,  
        Data, DataBytes, DoubleData, FloatData, FloatingPointData, IntegerData, MatlabShape, NumericDataType, Shape, 
        ExternalDevice, Units, 
        EntryFunctionName, Name, Notes, Project, ScmRevision, ScmURL,   
        Purpose,                                                                    
        EndTime, StartTime,
        Owner, SerializedLocation, SerializedName, URI, URIString, UUID
    };
    /** every collection (association) of every child-type - ensures consistent naming */
    protected enum CollectionName {
        AllEpochGroups, AllExperiments,   
        StimulusParameters,
        Experiments, MyAnalysisRecords,
        Tagged,
        AnalysisRecords, DerivedResponses, MyDerivedResponses, ProtocolParameters, Responses, Stimuli,   
        Children, EpochsUnsorted,
        DerivationParameters, 
        DeviceParameters, 
        AnalysisParameters,  
        EpochGroups, Epochs, ExternalDevices, Projects, Sources, 
        KeywordTags, MyKeywordTags, MyTags, Tags, 
        MyProperties, MyResources, Properties, Resources 
    };
    
    protected static void addPurposeAndNotesEntity(Map<String,Class<?>> propertyTypeMap, Map<String,Class<?>> collectionTypeMap) {
        propertyTypeMap.put(PropertyName.Notes.name(),        			String.class);
        propertyTypeMap.put(PropertyName.Purpose.name(),     			String.class);
        // base-type data
        addTimelineElement(propertyTypeMap, collectionTypeMap);
    }
    
    protected static void addTimelineElement(Map<String,Class<?>> propertyTypeMap, Map<String,Class<?>> collectionTypeMap) {
        propertyTypeMap.put(PropertyName.EndTime.name(),    			DateTime.class);
        propertyTypeMap.put(PropertyName.StartTime.name(),    			DateTime.class);
        // base-type data
        addTaggableEntityBase(propertyTypeMap, collectionTypeMap);
    }

    protected static void addResponseDataBase(Map<String,Class<?>> propertyTypeMap, Map<String,Class<?>> collectionTypeMap) {
        propertyTypeMap.put(PropertyName.Data.name(),					NumericData.class);
        propertyTypeMap.put(PropertyName.DataBytes.name(),          	byte[].class);
        propertyTypeMap.put(PropertyName.DoubleData.name(),         	double[].class);
        propertyTypeMap.put(PropertyName.FloatData.name(),          	float[].class);
        propertyTypeMap.put(PropertyName.FloatingPointData.name(),  	double[].class);
        propertyTypeMap.put(PropertyName.IntegerData.name(),        	int[].class);
        propertyTypeMap.put(PropertyName.MatlabShape.name(),        	long[].class);
        propertyTypeMap.put(PropertyName.NumericDataType.name(),    	NumericDataType.class);
        propertyTypeMap.put(PropertyName.Shape.name(),              	long[].class);
        // base-type data
        addIOBase(propertyTypeMap, collectionTypeMap);
    }
    protected static void addIOBase(Map<String,Class<?>> propertyTypeMap, Map<String,Class<?>> collectionTypeMap) {
        propertyTypeMap.put(PropertyName.ExternalDevice.name(),     	ExternalDevice.class);
        propertyTypeMap.put(PropertyName.Units.name(),              	String.class);
        collectionTypeMap.put(CollectionName.DeviceParameters.name(),   MapEntry.class);
        // base-type data
        addTaggableEntityBase(propertyTypeMap, collectionTypeMap);
    }
    
    protected static void addResource(Map<String,Class<?>> propertyTypeMap, Map<String,Class<?>> collectionTypeMap) {
        propertyTypeMap.put(PropertyName.Data.name(),               	byte[].class);
        propertyTypeMap.put(PropertyName.Name.name(),               	String.class);
        propertyTypeMap.put(PropertyName.Notes.name(),              	String.class);
        propertyTypeMap.put(PropertyName.Uti.name(),                	String.class);
        // base-type data
        addTaggableEntityBase(propertyTypeMap, collectionTypeMap);
    }
    
    protected static void addTaggableEntityBase(Map<String,Class<?>> propertyTypeMap, Map<String,Class<?>> collectionTypeMap) {
        collectionTypeMap.put(CollectionName.KeywordTags.name(),    	KeywordTag.class);
        collectionTypeMap.put(CollectionName.MyKeywordTags.name(),     	KeywordTag.class);
        collectionTypeMap.put(CollectionName.MyTags.name(),     		String.class);
        collectionTypeMap.put(CollectionName.Tags.name(),       		String.class);
        // base-type data
        addEntityBase(propertyTypeMap, collectionTypeMap);
    }
    
    protected static void addEntityBase(Map<String,Class<?>> propertyTypeMap, Map<String,Class<?>> collectionTypeMap) {
        propertyTypeMap.put(PropertyName.Owner.name(),              	User.class);
        propertyTypeMap.put(PropertyName.SerializedLocation.name(), 	String.class);
        propertyTypeMap.put(PropertyName.SerializedName.name(),     	String.class);
        propertyTypeMap.put(PropertyName.URI.name(),                	URI.class);
        propertyTypeMap.put(PropertyName.URIString.name(),          	String.class);
        propertyTypeMap.put(PropertyName.UUID.name(),               	String.class);
        collectionTypeMap.put(CollectionName.MyProperties.name(),   	MapEntry.class);
        collectionTypeMap.put(CollectionName.MyResources.name(),    	Resource.class);
        collectionTypeMap.put(CollectionName.Properties.name(),     	MapEntry.class);
        collectionTypeMap.put(CollectionName.Resources.name(),			Resource.class);
        // no parent type within Ovation
    }
    
    
//    @SuppressWarnings("unchecked")
    protected Iterable<V> executeQueryInfo() {
        return (Iterable<V>)executeQueryInfo(getEntityType(), getQueryInfo());
    }
    
	protected Iterable<V> executeQuery(String query) {
    	return CollectionUtils.makeIterable(executeQuery(getEntityType(), query));
    }
    
//    @SuppressWarnings({ "rawtypes" })    // because EntityBase is package-private FIXME
    protected static Iterable<Object> executeQueryInfo(Class type, QueryInfo info) {    // Iterator<EntityBase> FIXME
        // http://win7-32:8080/ovodata/Ovodata.svc/Projects/?$format=json&pql=query%20goes%20here        
        // queryInfo:{{inlineCnt:null, top:null, skip:null, filter:null, orderBy:null, skipToken:null, customOptions:{pql=query goes here}, expand:[], select:[]}
        Map<String,String> customOptions = (info != null && info.customOptions != null) ? info.customOptions : null;
        if (customOptions != null) {
            String pqlQuery = customOptions.get("pql");
            String entityUrl = customOptions.get("url");
            if (pqlQuery != null) {
                Iterator<Object> iter = executeQuery(type, pqlQuery);    // should be Iterator<EntityBase> FIXME
                _log.info("query '" + pqlQuery + "' = " + iter);
                return CollectionUtils.makeIterable(iter);
            } else
            if (entityUrl != null) {
            	Object obj = getByURI(entityUrl);
                _log.info("url '" + entityUrl + "' = " + obj);
                return CollectionUtils.makeIterable(obj != null ? new Object[]{obj} : new Object[0]);
            } else {
                _log.info("both pql and url params are null");
            }
        } else {
            _log.info("customOptions is null - info:" + info);
        }
        return null;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })    // because EntityBase is package-private FIXME
	public static <T> Iterator<T> executeQuery(Class<T> type, String pqlQuery) {
    	return DataContextCache.getThreadContext().query((Class)type, pqlQuery); // should be Iterator<EntityBase> FIXME
    }

    public static Object getByURI(String uri) { 
    	return DataContextCache.getThreadContext().objectWithURI(uri);    // should be EntityBase FIXME
    }

}
