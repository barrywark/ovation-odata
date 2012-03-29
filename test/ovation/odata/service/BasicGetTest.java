package ovation.odata.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.core4j.Enumerable;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
//FIXME 0.6 - import org.odata4j.consumer.behaviors.OClientBehaviors;
import org.odata4j.core.EntitySetInfo;
import org.odata4j.core.OClientBehaviors;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityGetRequest;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OProperty;
import org.odata4j.core.OQueryRequest;
//FIXME 0.6 - import org.odata4j.jersey.consumer.ODataJerseyConsumer;

import ovation.AnalysisRecord;
import ovation.DataContext;
import ovation.DerivedResponse;
import ovation.Epoch;
import ovation.EpochGroup;
import ovation.Experiment;
import ovation.ExternalDevice;
import ovation.KeywordTag;
import ovation.Project;
import ovation.Resource;
import ovation.Response;
import ovation.Source;
import ovation.Stimulus;
import ovation.URLResource;
import ovation.odata.util.OvationDBTestHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class BasicGetTest {
	static final String SERVICE_URL 	= System.getProperty("ovodataUrl",      "http://localhost:8080/ovodata/Ovodata.svc/");
	static final String USERNAME 		= System.getProperty("ovationUser", 	"ron");
	static final String PASSWORD 		= System.getProperty("ovationPassword", "passpass1");
	static final String DB_CON_FILE		= System.getProperty("ovationDbFile",   "/var/lib/ovation/db/dev.connection");
	
	private static ODataConsumer 	_odataClient; 
	private static DataContext 		_dbContext;
	
	@BeforeClass 
	public static void beforeAllTests() throws Throwable {
//FIXME 0.6 - _odataClient = ODataJerseyConsumer.newBuilder(SERVICE_URL).setClientBehaviors(OClientBehaviors.basicAuth(USERNAME, PASSWORD)).build();
		_odataClient = ODataConsumer.create(SERVICE_URL, OClientBehaviors.basicAuth(USERNAME, PASSWORD)); 
		_dbContext = OvationDBTestHelper.getContext(USERNAME, PASSWORD, DB_CON_FILE);
	}
	
	@AfterClass 
	public static void logout() {
		_odataClient = null; 
		_dbContext.logout();
		_dbContext.close();
		_dbContext = null;
	 }
	
	static enum Entity {
		EXPERIMENT("Experiments", 			Experiment.class), 
		EPOCH_GROUP("EpochGroups", 			EpochGroup.class),
		ANALYSIS_RECORD("AnalysisRecords", 	AnalysisRecord.class), 
		RESPONSE("Responses", 				Response.class),
		EXTERNAL_DEVICE("ExternalDevices", 	ExternalDevice.class), 
		SOURCE("Sources", 					Source.class),
		DERIVED_RESPONSE("DerivedResponses",DerivedResponse.class), 
		PROJECT("Projects", 				Project.class),
		URL_RESOURCE("URLResources", 		URLResource.class), 
		STIMULUS("Stimuli", 				Stimulus.class),
		KEYWORD_TAG("KeywordTags", 			KeywordTag.class), 
		EPOCH("Epochs", 					Epoch.class),
		RESOURCE("Resources", 				Resource.class),
		_MAP_ENTRY("_MapEntries", 			Map.Entry.class),
		_STRING("_Strings",                 String.class),
		;

		private final String 	_setName;
		
		Entity(String setName, Class<?> type) { _setName = setName; }
	}
	
	private static final Set<String> _knownEntities = Sets.newHashSet();
	static {
		for(Entity e : Entity.values()) {
			_knownEntities.add(e._setName);
		}
	}

	/**
	 * assert that the server recognizes all entity types we expect it to recognize
	 */
	@Test
	public void testBasics() throws Throwable {
		// assert that service exposes all known entity types (and no more or less)
		Enumerable<EntitySetInfo> serviceEntities = _odataClient.getEntitySets();
		Assert.assertEquals(_knownEntities.size(), serviceEntities.count());
		for (EntitySetInfo info : serviceEntities) {
			Assert.assertTrue(_knownEntities.contains(info.getTitle()));
		}
	}

	@Test
	public void testGetAllExperiments() throws Throwable {
		Entity 				type 			= Entity.EXPERIMENT;
		List<Experiment> 	allFromDb 		= getAllFromDB(Experiment.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		
		// assert that the count returned by the DB match the count returned by the service
		// and that every element from the service matches the same element in the DB
		Assert.assertEquals(allFromDb.size(), allFromService.count());
		for (OEntity entity : allFromService) {
			compareToDb(type, entity);
		}
	}
	
	@Test
	public void testGetAllEpochGroups() throws Throwable {
		Entity 				type 			= Entity.EPOCH_GROUP;
		List<EpochGroup> 	allFromDb 		= getAllFromDB(EpochGroup.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		
		// assert that the count returned by the DB match the count returned by the service
		// and that every element from the service matches the same element in the DB
		Assert.assertEquals(allFromDb.size(), allFromService.count());
		for (OEntity entity : allFromService) {
			compareToDb(type, entity);
		}
	}


	@Test
	public void testGetAllAnalysisRecords() throws Throwable {
		Entity 					type 			= Entity.ANALYSIS_RECORD;
		List<AnalysisRecord> 	allFromDb 		= getAllFromDB(AnalysisRecord.class);	
		Enumerable<OEntity> 	allFromService 	= getAllEntities(type);
		
		// assert that the count returned by the DB match the count returned by the service
		// and that every element from the service matches the same element in the DB
		Assert.assertEquals(allFromDb.size(), allFromService.count());
		for (OEntity entity : allFromService) {
			compareToDb(type, entity);
		}
	}

	@Test
	public void testGetAllResponses() throws Throwable {
		Entity 				type 			= Entity.RESPONSE;
		List<Response> 		allFromDb 		= getAllFromDB(Response.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		
		// assert that the count returned by the DB match the count returned by the service
		// and that every element from the service matches the same element in the DB
		Assert.assertEquals(allFromDb.size(), allFromService.count());
		for (OEntity entity : allFromService) {
			compareToDb(type, entity);
		}
	}

	@Test
	public void testGetAllExternalDevices() throws Throwable {
		Entity 					type 			= Entity.EXTERNAL_DEVICE;
		List<ExternalDevice> 	allFromDb 		= getAllFromDB(ExternalDevice.class);	
		Enumerable<OEntity> 	allFromService 	= getAllEntities(type);
		
		// assert that the count returned by the DB match the count returned by the service
		// and that every element from the service matches the same element in the DB
		Assert.assertEquals(allFromDb.size(), allFromService.count());
		for (OEntity entity : allFromService) {
			compareToDb(type, entity);
		}
	}

	@Test
	public void testGetAllSources() throws Throwable {
		Entity 				type 			= Entity.SOURCE;
		List<Source> 		allFromDb 		= getAllFromDB(Source.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		
		// assert that the count returned by the DB match the count returned by the service
		// and that every element from the service matches the same element in the DB
		Assert.assertEquals(allFromDb.size(), allFromService.count());
		for (OEntity entity : allFromService) {
			compareToDb(type, entity);
		}
	}

	@Test
	public void testGetAllDerivedResponses() throws Throwable {
		Entity 					type 			= Entity.DERIVED_RESPONSE;
		List<DerivedResponse>	allFromDb 		= getAllFromDB(DerivedResponse.class);	
		Enumerable<OEntity> 	allFromService 	= getAllEntities(type);
		
		// assert that the count returned by the DB match the count returned by the service
		// and that every element from the service matches the same element in the DB
		Assert.assertEquals(allFromDb.size(), allFromService.count());
		for (OEntity entity : allFromService) {
			compareToDb(type, entity);
		}
	}

	@Test
	public void testGetAllProjects() throws Throwable {
		Entity 				type 			= Entity.PROJECT;
		List<Project> 		allFromDb 		= getAllFromDB(Project.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		
		// assert that the count returned by the DB match the count returned by the service
		// and that every element from the service matches the same element in the DB
		Assert.assertEquals(allFromDb.size(), allFromService.count());
		for (OEntity entity : allFromService) {
			compareToDb(type, entity);
		}
	}

	@Test
	public void testGetAllURLResources() throws Throwable {
		Entity 				type 			= Entity.URL_RESOURCE;
		List<URLResource> 	allFromDb 		= getAllFromDB(URLResource.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		
		// assert that the count returned by the DB match the count returned by the service
		// and that every element from the service matches the same element in the DB
		Assert.assertEquals(allFromDb.size(), allFromService.count());
		for (OEntity entity : allFromService) {
			compareToDb(type, entity);
		}
	}

	@Test
	public void testGetAllStimuli() throws Throwable {
		Entity 				type 			= Entity.STIMULUS;
		List<Stimulus> 		allFromDb 		= getAllFromDB(Stimulus.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		
		// assert that the count returned by the DB match the count returned by the service
		// and that every element from the service matches the same element in the DB
		Assert.assertEquals(allFromDb.size(), allFromService.count());
		for (OEntity entity : allFromService) {
			compareToDb(type, entity);
		}
	}

	@Test
	public void testGetAllKeywordTags() throws Throwable {
		Entity 				type 			= Entity.KEYWORD_TAG;
		List<KeywordTag> 	allFromDb 		= getAllFromDB(KeywordTag.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		
		// assert that the count returned by the DB match the count returned by the service
		// and that every element from the service matches the same element in the DB
		Assert.assertEquals(allFromDb.size(), allFromService.count());
		for (OEntity entity : allFromService) {
			compareToDb(type, entity);
		}
	}

	@Test
	public void testGetAllEpochs() throws Throwable {
		Entity 				type 			= Entity.EPOCH;
		List<Epoch> 		allFromDb 		= getAllFromDB(Epoch.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		
		// assert that the count returned by the DB match the count returned by the service
		// and that every element from the service matches the same element in the DB
		Assert.assertEquals(allFromDb.size(), allFromService.count());
		for (OEntity entity : allFromService) {
			compareToDb(type, entity);
		}
	}

	@Test
	public void testGetAllResources() throws Throwable {
		Entity 				type 			= Entity.RESOURCE;
		List<Resource> 		allFromDb 		= getAllFromDB(Resource.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		
		// assert that the count returned by the DB match the count returned by the service
		// and that every element from the service matches the same element in the DB
		Assert.assertEquals(allFromDb.size(), allFromService.count());
		for (OEntity entity : allFromService) {
			compareToDb(type, entity);
		}
	}

	// ------------ utility methods --------------
	//
	static OEntity getEntity(Entity type, String entityId) {
		OEntityGetRequest<OEntity> req = _odataClient.getEntity(type._setName, OEntityKey.create(entityId));
		return req.execute();
	}
	
	static Enumerable<OEntity> getAllEntities(Entity type) {
		OQueryRequest<OEntity> req = _odataClient.getEntities(type._setName);
		return req.execute();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static <T> List<T> getAllFromDB(Class<T> type) {
		return Lists.newArrayList(_dbContext.query((Class)type, "true"));
	}
	
	@SuppressWarnings("unchecked")
	static <T> T getFromDB(String uri) {
    	return (T)_dbContext.objectWithURI(uri);		
	}
	static String 	getStringProperty(OEntity entity, String name) { 
		OProperty<String> prop = entity.getProperty(name, String.class);
		return prop != null ? prop.getValue() : null; 
	}
	static String[] getStringArrayProperty(OEntity entity, String name) { 
		OProperty<String[]> prop = entity.getProperty(name, String[].class);
		return prop != null ? prop.getValue() : null; 
	}
	static double[] getDoubleArrayProperty(OEntity entity, String name) { 
		OProperty<double[]> prop = entity.getProperty(name, double[].class);
		return prop != null ? prop.getValue() : null; 
	}

	static DateTime getDateTimeProperty(OEntity entity, String name) { 
		OProperty<LocalDateTime> prop = entity.getProperty(name, LocalDateTime.class);
		return prop != null && prop.getValue() != null ? prop.getValue().toDateTime() : null; 
	}
	static Integer  getIntegerProperty(OEntity entity, String name) { 
		OProperty<Integer> prop = entity.getProperty(name, Integer.class);
		return prop != null ? prop.getValue() : null; 
	}
	static int		getIntegerProperty(OEntity entity, String name, int def) {
		Integer val = getIntegerProperty(entity, name);
		return val != null ? val.intValue() : def;
	}
	static byte[]	getByteArrayProperty(OEntity entity, String name) { 
		OProperty<byte[]> prop = entity.getProperty(name, byte[].class);
		return prop != null ? prop.getValue() : null; 
	}
	static Double	getDoubleProperty(OEntity entity, String name) { 
		OProperty<Double> prop = entity.getProperty(name, Double.class);
		return prop != null ? prop.getValue() : null; 
	}
	static Boolean	getBooleanProperty(OEntity entity, String name) { 
		OProperty<Boolean> prop = entity.getProperty(name, Boolean.class);
		return prop != null ? prop.getValue() : null; 
	}

	
	static void assertEquals(byte[] expected, byte[] actual) {
		if (expected == actual) return;	// handles identical arrays and both null
		Assert.assertEquals(Arrays.toString(expected), Arrays.toString(actual));
	}
	static void assertEquals(double[] expected, double[] actual) {
		if (expected == actual) return;	// handles identical arrays and both null
		Assert.assertEquals(Arrays.toString(expected), Arrays.toString(actual));
	}
	static <T> void assertEquals(T[] expected, T[] actual) {
		if (expected == actual) return;	// handles identical arrays and both null
		Assert.assertEquals(Arrays.toString(expected), Arrays.toString(actual));
	}
	
	static void compareToDb(Entity type, OEntity entity) {
		// TODO
		switch(type) {
			case EXPERIMENT : {
				Experiment fromDb = getFromDB(getStringProperty(entity, "URIString"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getNotes(), 				getStringProperty(entity, "Notes"));
				Assert.assertEquals(fromDb.getSerializedName(), 	getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUuid(), 				getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(), getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getStartTime(), 			getDateTimeProperty(entity, "StartTime"));
				Assert.assertEquals(fromDb.getEndTime(), 			getDateTimeProperty(entity, "EndTime"));
				Assert.assertEquals(fromDb.getPurpose(), 			getStringProperty(entity, "Purpose"));

				// TODO sub-entities - Set<String> ss  = fromDb.getAnnotationGroupTagSet();
/*				
				<NavigationProperty Name="Projects" 		Relationship="Ovodata.FK_Experiments_Projects" 			FromRole="Experiments" ToRole="Projects"/>
				<NavigationProperty Name="Tags" 			Relationship="Ovodata.FK_Experiments__Strings" 			FromRole="Experiments" ToRole="_Strings"/>
				<NavigationProperty Name="MyProperties" 	Relationship="Ovodata.FK_Experiments__MapEntries" 		FromRole="Experiments" ToRole="_MapEntries"/>
				<NavigationProperty Name="MyKeywordTags" 	Relationship="Ovodata.FK_Experiments_KeywordTags" 		FromRole="Experiments" ToRole="KeywordTags"/>
				<NavigationProperty Name="EpochGroups" 		Relationship="Ovodata.FK_Experiments_EpochGroups" 		FromRole="Experiments" ToRole="EpochGroups"/>
				<NavigationProperty Name="MyTags" 			Relationship="Ovodata.FK_Experiments__Strings" 			FromRole="Experiments" ToRole="_Strings"/>
				<NavigationProperty Name="ExternalDevices" 	Relationship="Ovodata.FK_Experiments_ExternalDevices" 	FromRole="Experiments" ToRole="ExternalDevices"/>
				<NavigationProperty Name="Sources" 			Relationship="Ovodata.FK_Experiments_Sources" 			FromRole="Experiments" ToRole="Sources"/>
				<NavigationProperty Name="KeywordTags" 		Relationship="Ovodata.FK_Experiments_KeywordTags" 		FromRole="Experiments" ToRole="KeywordTags"/>
				<NavigationProperty Name="Epochs" 			Relationship="Ovodata.FK_Experiments_Epochs" 			FromRole="Experiments" ToRole="Epochs"/>
				<NavigationProperty Name="Resources" 		Relationship="Ovodata.FK_Experiments_Resources" 		FromRole="Experiments" ToRole="Resources"/>
				<NavigationProperty Name="Properties" 		Relationship="Ovodata.FK_Experiments__MapEntries" 		FromRole="Experiments" ToRole="_MapEntries"/>
*/				

				break;
			}
			case ANALYSIS_RECORD : {
				AnalysisRecord fromDb = getFromDB(getStringProperty(entity, "URIString"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getNotes(), 				getStringProperty(entity, "Notes"));
				Assert.assertEquals(fromDb.getName(), 				getStringProperty(entity, "Name"));
				Assert.assertEquals(fromDb.getSerializedName(), 	getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUuid(), 				getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(),	getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getScmRevision(), 		getIntegerProperty(entity, "ScmRevision", Integer.MIN_VALUE));
				Assert.assertEquals(fromDb.getEntryFunctionName(), 	getStringProperty(entity, "EntryFunctionName"));
/*					
					<NavigationProperty Name="Project" Relationship="Ovodata.FK_AnalysisRecords_Projects" FromRole="AnalysisRecords" ToRole="Projects"/>
					<NavigationProperty Name="Tags" Relationship="Ovodata.FK_AnalysisRecords__Strings" FromRole="AnalysisRecords" ToRole="_Strings"/>
					<NavigationProperty Name="MyProperties" Relationship="Ovodata.FK_AnalysisRecords__MapEntries" FromRole="AnalysisRecords" ToRole="_MapEntries"/>
					<NavigationProperty Name="MyKeywordTags" Relationship="Ovodata.FK_AnalysisRecords_KeywordTags" FromRole="AnalysisRecords" ToRole="KeywordTags"/>
					<NavigationProperty Name="MyTags" Relationship="Ovodata.FK_AnalysisRecords__Strings" FromRole="AnalysisRecords" ToRole="_Strings"/>
					<NavigationProperty Name="KeywordTags" Relationship="Ovodata.FK_AnalysisRecords_KeywordTags" FromRole="AnalysisRecords" ToRole="KeywordTags"/>
					<NavigationProperty Name="Epochs" Relationship="Ovodata.FK_AnalysisRecords_Epochs" FromRole="AnalysisRecords" ToRole="Epochs"/>
					<NavigationProperty Name="Resources" Relationship="Ovodata.FK_AnalysisRecords_Resources" FromRole="AnalysisRecords" ToRole="Resources"/>
					<NavigationProperty Name="Properties" Relationship="Ovodata.FK_AnalysisRecords__MapEntries" FromRole="AnalysisRecords" ToRole="_MapEntries"/>
					<NavigationProperty Name="AnalysisParameters" Relationship="Ovodata.FK_AnalysisRecords__MapEntries" FromRole="AnalysisRecords" ToRole="_MapEntries"/>
*/
				break;
			}
			case DERIVED_RESPONSE : {
				DerivedResponse fromDb = getFromDB(getStringProperty(entity, "URIString"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getDescription(), 		getStringProperty(entity, "Description"));
				Assert.assertEquals(fromDb.getUuid(), 				getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(), getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getName(), 				getStringProperty(entity, "Name"));
				assertEquals(fromDb.getDataBytes(), 				getByteArrayProperty(entity, "DataBytes"));
				Assert.assertEquals(fromDb.getSerializedName(), 	getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUnits(), 				getStringProperty(entity, "Units"));
/*					
					<NavigationProperty Name="ExternalDevice" Relationship="Ovodata.FK_DerivedResponses_ExternalDevices" FromRole="DerivedResponses" ToRole="ExternalDevices"/>
					<NavigationProperty Name="Epoch" Relationship="Ovodata.FK_DerivedResponses_Epochs" FromRole="DerivedResponses" ToRole="Epochs"/>
					<NavigationProperty Name="Tags" Relationship="Ovodata.FK_DerivedResponses__Strings" FromRole="DerivedResponses" ToRole="_Strings"/>
					<NavigationProperty Name="MyProperties" Relationship="Ovodata.FK_DerivedResponses__MapEntries" FromRole="DerivedResponses" ToRole="_MapEntries"/>
					<NavigationProperty Name="MyKeywordTags" Relationship="Ovodata.FK_DerivedResponses_KeywordTags" FromRole="DerivedResponses" ToRole="KeywordTags"/>
					<NavigationProperty Name="DerivationParameters" Relationship="Ovodata.FK_DerivedResponses__MapEntries" FromRole="DerivedResponses" ToRole="_MapEntries"/>
					<NavigationProperty Name="MyTags" Relationship="Ovodata.FK_DerivedResponses__Strings" FromRole="DerivedResponses" ToRole="_Strings"/>
					<NavigationProperty Name="DeviceParameters" Relationship="Ovodata.FK_DerivedResponses__MapEntries" FromRole="DerivedResponses" ToRole="_MapEntries"/>
					<NavigationProperty Name="KeywordTags" Relationship="Ovodata.FK_DerivedResponses_KeywordTags" FromRole="DerivedResponses" ToRole="KeywordTags"/>
					<NavigationProperty Name="Resources" Relationship="Ovodata.FK_DerivedResponses_Resources" FromRole="DerivedResponses" ToRole="Resources"/>
					<NavigationProperty Name="Properties" Relationship="Ovodata.FK_DerivedResponses__MapEntries" FromRole="DerivedResponses" ToRole="_MapEntries"/>
*/
				break;
			}
			case EPOCH : {
				Epoch fromDb = getFromDB(getStringProperty(entity, "URIString"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getSerializedName(), 						getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getDuration(), 								getDoubleProperty(entity, "Duration"));
				Assert.assertEquals(fromDb.getUuid(), 									getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(), 					getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(Boolean.valueOf(fromDb.getExcludeFromAnalysis()),	getBooleanProperty(entity, "ExcludeFromAnalysis"));
				Assert.assertEquals(fromDb.getProtocolID(),								getStringProperty(entity, "ProtocolID"));
/*					
					<NavigationProperty Name="EpochGroup" Relationship="Ovodata.FK_Epochs_EpochGroups" FromRole="Epochs" ToRole="EpochGroups"/>
					<NavigationProperty Name="PreviousEpoch" Relationship="Ovodata.FK_Epochs_Epochs" FromRole="Epochs" ToRole="Epochs1"/>
					<NavigationProperty Name="NextEpoch" Relationship="Ovodata.FK_Epochs_Epochs" FromRole="Epochs" ToRole="Epochs1"/>
					<NavigationProperty Name="AnalysisRecords" Relationship="Ovodata.FK_AnalysisRecords_Epochs" FromRole="Epochs" ToRole="AnalysisRecords"/>
					<NavigationProperty Name="Responses" Relationship="Ovodata.FK_Responses_Epochs" FromRole="Epochs" ToRole="Responses"/>
					<NavigationProperty Name="MyTags" Relationship="Ovodata.FK_Epochs__Strings" FromRole="Epochs" ToRole="_Strings"/>
					<NavigationProperty Name="DerivedResponses" Relationship="Ovodata.FK_DerivedResponses_Epochs" FromRole="Epochs" ToRole="DerivedResponses"/>
					<NavigationProperty Name="Properties" Relationship="Ovodata.FK_Epochs__MapEntries" FromRole="Epochs" ToRole="_MapEntries"/>
					<NavigationProperty Name="Tags" Relationship="Ovodata.FK_Epochs__Strings" FromRole="Epochs" ToRole="_Strings"/>
					<NavigationProperty Name="MyProperties" Relationship="Ovodata.FK_Epochs__MapEntries" FromRole="Epochs" ToRole="_MapEntries"/>
					<NavigationProperty Name="MyKeywordTags" Relationship="Ovodata.FK_Epochs_KeywordTags" FromRole="Epochs" ToRole="KeywordTags"/>
					<NavigationProperty Name="Stimuli" Relationship="Ovodata.FK_Stimuli_Epochs" FromRole="Epochs" ToRole="Stimuli"/>
					<NavigationProperty Name="MyDerivedResponses" Relationship="Ovodata.FK_DerivedResponses_Epochs" FromRole="Epochs" ToRole="DerivedResponses"/>
					<NavigationProperty Name="ProtocolParameters" Relationship="Ovodata.FK_Epochs__MapEntries" FromRole="Epochs" ToRole="_MapEntries"/>
					<NavigationProperty Name="KeywordTags" Relationship="Ovodata.FK_Epochs_KeywordTags" FromRole="Epochs" ToRole="KeywordTags"/>
					<NavigationProperty Name="Resources" Relationship="Ovodata.FK_Epochs_Resources" FromRole="Epochs" ToRole="Resources"/>
*/
				break;
			}
			case EPOCH_GROUP : {
				EpochGroup fromDb = getFromDB(getStringProperty(entity, "URIString"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getEpochCount(),			getIntegerProperty(entity, "EpochCount", -1));
				Assert.assertEquals(fromDb.getSerializedLocation(), getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getUuid(), 				getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedName(), 	getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getLabel(), 				getStringProperty(entity, "Label"));
				Assert.assertEquals(fromDb.getStartTime(), 			getDateTimeProperty(entity, "StartTime"));
				Assert.assertEquals(fromDb.getEndTime(), 			getDateTimeProperty(entity, "EndTime"));

/*				
				<NavigationProperty Name="Parent" Relationship="Ovodata.FK_EpochGroups_EpochGroups" FromRole="EpochGroups" ToRole="EpochGroups1"/>
				<NavigationProperty Name="Source" Relationship="Ovodata.FK_EpochGroups_Sources" FromRole="EpochGroups" ToRole="Sources"/>
				<NavigationProperty Name="Experiment" Relationship="Ovodata.FK_EpochGroups_Experiments" FromRole="EpochGroups" ToRole="Experiments"/>
				<NavigationProperty Name="Tags" Relationship="Ovodata.FK_EpochGroups__Strings" FromRole="EpochGroups" ToRole="_Strings"/>
				<NavigationProperty Name="MyProperties" Relationship="Ovodata.FK_EpochGroups__MapEntries" FromRole="EpochGroups" ToRole="_MapEntries"/>
				<NavigationProperty Name="MyKeywordTags" Relationship="Ovodata.FK_EpochGroups_KeywordTags" FromRole="EpochGroups" ToRole="KeywordTags"/>
				<NavigationProperty Name="MyTags" Relationship="Ovodata.FK_EpochGroups__Strings" FromRole="EpochGroups" ToRole="_Strings"/>
				<NavigationProperty Name="KeywordTags" Relationship="Ovodata.FK_EpochGroups_KeywordTags" FromRole="EpochGroups" ToRole="KeywordTags"/>
				<NavigationProperty Name="Children" Relationship="Ovodata.FK_EpochGroups_EpochGroups" FromRole="EpochGroups1" ToRole="EpochGroups"/>
				<NavigationProperty Name="EpochsUnsorted" Relationship="Ovodata.FK_EpochGroups_Epochs" FromRole="EpochGroups" ToRole="Epochs"/>
				<NavigationProperty Name="Epochs" Relationship="Ovodata.FK_EpochGroups_Epochs" FromRole="EpochGroups" ToRole="Epochs"/>
				<NavigationProperty Name="Resources" Relationship="Ovodata.FK_EpochGroups_Resources" FromRole="EpochGroups" ToRole="Resources"/>
				<NavigationProperty Name="Properties" Relationship="Ovodata.FK_EpochGroups__MapEntries" FromRole="EpochGroups" ToRole="_MapEntries"/>
*/
				break;
			}
			case EXTERNAL_DEVICE : {
				ExternalDevice fromDb = getFromDB(getStringProperty(entity, "URIString"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getName(), 				getStringProperty(entity, "Name"));
				Assert.assertEquals(fromDb.getManufacturer(), 		getStringProperty(entity, "Manufacturer"));
				Assert.assertEquals(fromDb.getSerializedName(), 	getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUuid(), 				getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(),	getStringProperty(entity, "SerializedLocation"));

/*					
					<NavigationProperty Name="Experiment" Relationship="Ovodata.FK_ExternalDevices_Experiments" FromRole="ExternalDevices" ToRole="Experiments"/>
					<NavigationProperty Name="Tags" Relationship="Ovodata.FK_ExternalDevices__Strings" FromRole="ExternalDevices" ToRole="_Strings"/>
					<NavigationProperty Name="MyProperties" Relationship="Ovodata.FK_ExternalDevices__MapEntries" FromRole="ExternalDevices" ToRole="_MapEntries"/>
					<NavigationProperty Name="MyKeywordTags" Relationship="Ovodata.FK_ExternalDevices_KeywordTags" FromRole="ExternalDevices" ToRole="KeywordTags"/>
					<NavigationProperty Name="MyTags" Relationship="Ovodata.FK_ExternalDevices__Strings" FromRole="ExternalDevices" ToRole="_Strings"/>
					<NavigationProperty Name="KeywordTags" Relationship="Ovodata.FK_ExternalDevices_KeywordTags" FromRole="ExternalDevices" ToRole="KeywordTags"/>
					<NavigationProperty Name="Resources" Relationship="Ovodata.FK_ExternalDevices_Resources" FromRole="ExternalDevices" ToRole="Resources"/>
					<NavigationProperty Name="Properties" Relationship="Ovodata.FK_ExternalDevices__MapEntries" FromRole="ExternalDevices" ToRole="_MapEntries"/>
*/
				break;
			}
			case KEYWORD_TAG : {
				KeywordTag fromDb = getFromDB(getStringProperty(entity, "URIString"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getSerializedName(), 	getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getTag(), 				getStringProperty(entity, "Tag"));
				Assert.assertEquals(fromDb.getUuid(), 				getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(),	getStringProperty(entity, "SerializedLocation"));
				
/*					
					<NavigationProperty Name="MyProperties" Relationship="Ovodata.FK_KeywordTags__MapEntries" FromRole="KeywordTags" ToRole="_MapEntries"/>
					<NavigationProperty Name="Tagged" Relationship="Ovodata.FK_KeywordTags__Strings" FromRole="KeywordTags" ToRole="_Strings"/>
					<NavigationProperty Name="Resources" Relationship="Ovodata.FK_KeywordTags_Resources" FromRole="KeywordTags" ToRole="Resources"/>
					<NavigationProperty Name="Properties" Relationship="Ovodata.FK_KeywordTags__MapEntries" FromRole="KeywordTags" ToRole="_MapEntries"/>
*/
				break;
			}
			case PROJECT : {
				String uriString = getStringProperty(entity, "URIString");
				Project fromDb = getFromDB(uriString);
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(uriString, fromDb.getNotes(), 				getStringProperty(entity, "Notes"));
				Assert.assertEquals(uriString, fromDb.getName(), 				getStringProperty(entity, "Name"));
				Assert.assertEquals(uriString, fromDb.getSerializedName(), 		getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(uriString, fromDb.getUuid(), 				getStringProperty(entity, "UUID"));
				Assert.assertEquals(uriString, fromDb.getSerializedLocation(), 	getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(uriString, fromDb.getPurpose(), 			getStringProperty(entity, "Purpose"));
				Assert.assertEquals(uriString, fromDb.getStartTime(), 			getDateTimeProperty(entity, "StartTime"));
				Assert.assertEquals(uriString, fromDb.getEndTime(), 			getDateTimeProperty(entity, "EndTime"));

/*				
				<NavigationProperty Name="Experiments" Relationship="Ovodata.FK_Experiments_Projects" FromRole="Projects" ToRole="Experiments"/>
				<NavigationProperty Name="Tags" Relationship="Ovodata.FK_Projects__Strings" FromRole="Projects" ToRole="_Strings"/>
				<NavigationProperty Name="MyProperties" Relationship="Ovodata.FK_Projects__MapEntries" FromRole="Projects" ToRole="_MapEntries"/>
				<NavigationProperty Name="MyKeywordTags" Relationship="Ovodata.FK_Projects_KeywordTags" FromRole="Projects" ToRole="KeywordTags"/>
				<NavigationProperty Name="AnalysisRecords" Relationship="Ovodata.FK_AnalysisRecords_Projects" FromRole="Projects" ToRole="AnalysisRecords"/>
				<NavigationProperty Name="MyTags" Relationship="Ovodata.FK_Projects__Strings" FromRole="Projects" ToRole="_Strings"/>
				<NavigationProperty Name="MyAnalysisRecords" Relationship="Ovodata.FK_AnalysisRecords_Projects" FromRole="Projects" ToRole="AnalysisRecords"/>
				<NavigationProperty Name="KeywordTags" Relationship="Ovodata.FK_Projects_KeywordTags" FromRole="Projects" ToRole="KeywordTags"/>
				<NavigationProperty Name="Resources" Relationship="Ovodata.FK_Projects_Resources" FromRole="Projects" ToRole="Resources"/>
				<NavigationProperty Name="Properties" Relationship="Ovodata.FK_Projects__MapEntries" FromRole="Projects" ToRole="_MapEntries"/>
*/
				break;
			}
			case RESOURCE : {
				Resource fromDb = getFromDB(getStringProperty(entity, "URIString"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getNotes(), 				getStringProperty(entity, "Notes"));
				Assert.assertEquals(fromDb.getName(), 				getStringProperty(entity, "Name"));
				assertEquals(fromDb.getData(), 						getByteArrayProperty(entity, "Data"));
				Assert.assertEquals(fromDb.getSerializedName(), 	getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUuid(), 				getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(), getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getUti(), 				getStringProperty(entity, "Uti"));
/*					
					<NavigationProperty Name="Tags" Relationship="Ovodata.FK_Resources__Strings" FromRole="Resources" ToRole="_Strings"/>
					<NavigationProperty Name="MyProperties" Relationship="Ovodata.FK_Resources__MapEntries" FromRole="Resources" ToRole="_MapEntries"/>
					<NavigationProperty Name="MyKeywordTags" Relationship="Ovodata.FK_KeywordTags_Resources" FromRole="Resources" ToRole="KeywordTags"/>
					<NavigationProperty Name="MyTags" Relationship="Ovodata.FK_Resources__Strings" FromRole="Resources" ToRole="_Strings"/>
					<NavigationProperty Name="KeywordTags" Relationship="Ovodata.FK_KeywordTags_Resources" FromRole="Resources" ToRole="KeywordTags"/>
					<NavigationProperty Name="Resources" Relationship="Ovodata.FK_Resources_Resources" FromRole="Resources" ToRole="Resources1"/>
					<NavigationProperty Name="Properties" Relationship="Ovodata.FK_Resources__MapEntries" FromRole="Resources" ToRole="_MapEntries"/>
*/
				break;
			}
			case RESPONSE : {
				Response fromDb = getFromDB(getStringProperty(entity, "URIString"));
				Assert.assertNotNull(fromDb);
				
//				fromDb.getAnnotationGroupTags()
/*
getAnnotationGroupTags()
getAnnotationGroupTagSet()
getAnnotations()
getAnnotations(String)
getAnnotationsIterable()
getAnnotationsIterable(String)
getMyAnnotationGroupTags()
getMyAnnotationGroupTagSet()
getMyAnnotations()
getMyAnnotations(String)
getMyAnnotationsIterable()
getMyAnnotationsIterable(String)
getMyNoteAnnotations(String)
getMyNoteAnnotationsIterable(String)
getMyTimelineAnnotations(String)
getMyTimelineAnnotationsIterable(String)
getNoteAnnotations(String)
getNoteAnnotationsIterable(String)
getTimelineAnnotations(String)
getTimelineAnnotationsIterable(String)
 */
/*
getData()
getDataBytes()
getDoubleData()
getFloatData()
getFloatingPointData()
getIntData()
getIntegerData()
getMatlabShape()
getNumericDataType()
getShape()
 */
/*
getDeviceParameters()
getDimensionLabels()
getExternalDevice()
getUnits()
 */
/*
getEpoch()
getSamplingRates()
getSamplingUnits()
getSerializedLocation()
getUTI()
 */
/*
getKeywordTags()
getMyKeywordTags()
getMyTags()
getTags()
getTagSet()
 */
/*
getMyProperties()
getMyProperty(String)
getOwner()
getProperties()
getProperty(String)
getResource(String)
getResourceNames()
getResourcesIterable()
getSerializedLocation()
getSerializedName()
getURI()
getURIString()
getUuid()
 */
//				assertEquals(fromDb.getSamplingUnits(), 		getStringArrayProperty(entity, "SamplingUnits"));
//				assertEquals(fromDb.getSamplingRates(), 		getDoubleArrayProperty(entity, "SamplingRates"));	// FIXME - this will fail (it's double[] in DB but Double in svc)
				Assert.assertEquals(fromDb.getUuid(), 				getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(), getStringProperty(entity, "SerializedLocation"));
				assertEquals(fromDb.getDataBytes(), 				getByteArrayProperty(entity, "DataBytes"));
				Assert.assertEquals(fromDb.getSerializedName(), 	getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUnits(), 				getStringProperty(entity, "Units"));

/*					
					<NavigationProperty Name="ExternalDevice" Relationship="Ovodata.FK_Responses_ExternalDevices" FromRole="Responses" ToRole="ExternalDevices"/>
					<NavigationProperty Name="Epoch" Relationship="Ovodata.FK_Responses_Epochs" FromRole="Responses" ToRole="Epochs"/>
					<NavigationProperty Name="Tags" Relationship="Ovodata.FK_Responses__Strings" FromRole="Responses" ToRole="_Strings"/>
					<NavigationProperty Name="MyProperties" Relationship="Ovodata.FK_Responses__MapEntries" FromRole="Responses" ToRole="_MapEntries"/>
					<NavigationProperty Name="MyKeywordTags" Relationship="Ovodata.FK_Responses_KeywordTags" FromRole="Responses" ToRole="KeywordTags"/>
					<NavigationProperty Name="MyTags" Relationship="Ovodata.FK_Responses__Strings" FromRole="Responses" ToRole="_Strings"/>
					<NavigationProperty Name="DeviceParameters" Relationship="Ovodata.FK_Responses__MapEntries" FromRole="Responses" ToRole="_MapEntries"/>
					<NavigationProperty Name="KeywordTags" Relationship="Ovodata.FK_Responses_KeywordTags" FromRole="Responses" ToRole="KeywordTags"/>
					<NavigationProperty Name="Resources" Relationship="Ovodata.FK_Responses_Resources" FromRole="Responses" ToRole="Resources"/>
					<NavigationProperty Name="Properties" Relationship="Ovodata.FK_Responses__MapEntries" FromRole="Responses" ToRole="_MapEntries"/>
*/
				break;
			}
			case SOURCE : {
				Source fromDb = getFromDB(getStringProperty(entity, "URIString"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getSerializedName(), 	getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUuid(), 				getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(),	getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getLabel(), 				getStringProperty(entity, "Label"));
/*					
					<NavigationProperty Name="Parent" Relationship="Ovodata.FK_Sources_Sources" FromRole="Sources" ToRole="Sources1"/>
					<NavigationProperty Name="ParentRoot" Relationship="Ovodata.FK_Sources_Sources" FromRole="Sources" ToRole="Sources1"/>
					<NavigationProperty Name="Experiments" Relationship="Ovodata.FK_Experiments_Sources" FromRole="Sources" ToRole="Experiments"/>
					<NavigationProperty Name="Tags" Relationship="Ovodata.FK_Sources__Strings" FromRole="Sources" ToRole="_Strings"/>
					<NavigationProperty Name="MyProperties" Relationship="Ovodata.FK_Sources__MapEntries" FromRole="Sources" ToRole="_MapEntries"/>
					<NavigationProperty Name="AllEpochGroups" Relationship="Ovodata.FK_EpochGroups_Sources" FromRole="Sources" ToRole="EpochGroups"/>
					<NavigationProperty Name="MyKeywordTags" Relationship="Ovodata.FK_Sources_KeywordTags" FromRole="Sources" ToRole="KeywordTags"/>
					<NavigationProperty Name="EpochGroups" Relationship="Ovodata.FK_EpochGroups_Sources" FromRole="Sources" ToRole="EpochGroups"/>
					<NavigationProperty Name="AllExperiments" Relationship="Ovodata.FK_Experiments_Sources" FromRole="Sources" ToRole="Experiments"/>
					<NavigationProperty Name="MyTags" Relationship="Ovodata.FK_Sources__Strings" FromRole="Sources" ToRole="_Strings"/>
					<NavigationProperty Name="KeywordTags" Relationship="Ovodata.FK_Sources_KeywordTags" FromRole="Sources" ToRole="KeywordTags"/>
					<NavigationProperty Name="Children" Relationship="Ovodata.FK_Sources_Sources" FromRole="Sources1" ToRole="Sources"/>
					<NavigationProperty Name="Resources" Relationship="Ovodata.FK_Sources_Resources" FromRole="Sources" ToRole="Resources"/>
					<NavigationProperty Name="Properties" Relationship="Ovodata.FK_Sources__MapEntries" FromRole="Sources" ToRole="_MapEntries"/>
*/
				break;
			}
			case STIMULUS : {
				Stimulus fromDb = getFromDB(getStringProperty(entity, "URIString"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getSerializedName(), 	getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUnits(), 				getStringProperty(entity, "Units"));
				Assert.assertEquals(fromDb.getUuid(), 				getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(), getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getPluginID(), 			getStringProperty(entity, "PluginID"));
/*					
					<NavigationProperty Name="ExternalDevice" Relationship="Ovodata.FK_Stimuli_ExternalDevices" FromRole="Stimuli" ToRole="ExternalDevices"/>
					<NavigationProperty Name="Epoch" Relationship="Ovodata.FK_Stimuli_Epochs" FromRole="Stimuli" ToRole="Epochs"/>
					<NavigationProperty Name="Tags" Relationship="Ovodata.FK_Stimuli__Strings" FromRole="Stimuli" ToRole="_Strings"/>
					<NavigationProperty Name="MyProperties" Relationship="Ovodata.FK_Stimuli__MapEntries" FromRole="Stimuli" ToRole="_MapEntries"/>
					<NavigationProperty Name="MyKeywordTags" Relationship="Ovodata.FK_Stimuli_KeywordTags" FromRole="Stimuli" ToRole="KeywordTags"/>
					<NavigationProperty Name="StimulusParameters" Relationship="Ovodata.FK_Stimuli__MapEntries" FromRole="Stimuli" ToRole="_MapEntries"/>
					<NavigationProperty Name="MyTags" Relationship="Ovodata.FK_Stimuli__Strings" FromRole="Stimuli" ToRole="_Strings"/>
					<NavigationProperty Name="DeviceParameters" Relationship="Ovodata.FK_Stimuli__MapEntries" FromRole="Stimuli" ToRole="_MapEntries"/>
					<NavigationProperty Name="KeywordTags" Relationship="Ovodata.FK_Stimuli_KeywordTags" FromRole="Stimuli" ToRole="KeywordTags"/>
					<NavigationProperty Name="Resources" Relationship="Ovodata.FK_Stimuli_Resources" FromRole="Stimuli" ToRole="Resources"/>
					<NavigationProperty Name="Properties" Relationship="Ovodata.FK_Stimuli__MapEntries" FromRole="Stimuli" ToRole="_MapEntries"/>
*/
				break;
			}
			case URL_RESOURCE : {
				URLResource fromDb = getFromDB(getStringProperty(entity, "URIString"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getNotes(), 				getStringProperty(entity, "Notes"));
				Assert.assertEquals(fromDb.getName(), 				getStringProperty(entity, "Name"));
				Assert.assertEquals(fromDb.getSerializedName(), 	getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUuid(), 				getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(),	getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getUti(), 				getStringProperty(entity, "Uti"));
/*					
					<NavigationProperty Name="Tags" Relationship="Ovodata.FK_URLResources__Strings" FromRole="URLResources" ToRole="_Strings"/>
					<NavigationProperty Name="MyProperties" Relationship="Ovodata.FK_URLResources__MapEntries" FromRole="URLResources" ToRole="_MapEntries"/>
					<NavigationProperty Name="MyKeywordTags" Relationship="Ovodata.FK_URLResources_KeywordTags" FromRole="URLResources" ToRole="KeywordTags"/>
					<NavigationProperty Name="MyTags" Relationship="Ovodata.FK_URLResources__Strings" FromRole="URLResources" ToRole="_Strings"/>
					<NavigationProperty Name="KeywordTags" Relationship="Ovodata.FK_URLResources_KeywordTags" FromRole="URLResources" ToRole="KeywordTags"/>
					<NavigationProperty Name="Resources" Relationship="Ovodata.FK_URLResources_Resources" FromRole="URLResources" ToRole="Resources"/>
					<NavigationProperty Name="Properties" Relationship="Ovodata.FK_URLResources__MapEntries" FromRole="URLResources" ToRole="_MapEntries"/>
*/
				break;
			}

/*
<EntityType Name="_MapEntries">
	<Key><PropertyRef Name="EntityId"/></Key>
	<Property Name="EntityId" 	Type="Edm.String" Nullable="false"/>
	<Property Name="value" 		Type="Edm.String" Nullable="true"/>
	<Property Name="key" 		Type="Edm.String" Nullable="true"/>
</EntityType>
<EntityType Name="_Strings">
	<Property Name="EntityId" Type="Edm.String" Nullable="false"/>
	<Property Name="value" Type="Edm.String" Nullable="true"/>
</EntityType>
* /

<Association Name="FK_Experiments_Projects"><End Role="Experiments" Type="Ovodata.Experiments" Multiplicity="*"/><End Role="Projects" Type="Ovodata.Projects" Multiplicity="*"/></Association>
<Association Name="FK_Experiments__Strings"><End Role="Experiments" Type="Ovodata.Experiments" Multiplicity="0..1"/><End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/></Association>
<Association Name="FK_Experiments__MapEntries"><End Role="Experiments" Type="Ovodata.Experiments" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_Experiments_KeywordTags"><End Role="Experiments" Type="Ovodata.Experiments" Multiplicity="0..1"/><End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/></Association>
<Association Name="FK_Experiments_EpochGroups"><End Role="Experiments" Type="Ovodata.Experiments" Multiplicity="0..1"/><End Role="EpochGroups" Type="Ovodata.EpochGroups" Multiplicity="*"/></Association>
<Association Name="FK_Experiments__Strings"><End Role="Experiments" Type="Ovodata.Experiments" Multiplicity="0..1"/><End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/></Association>
<Association Name="FK_Experiments_ExternalDevices"><End Role="Experiments" Type="Ovodata.Experiments" Multiplicity="0..1"/><End Role="ExternalDevices" Type="Ovodata.ExternalDevices" Multiplicity="*"/></Association>
<Association Name="FK_Experiments_Sources"><End Role="Experiments" Type="Ovodata.Experiments" Multiplicity="*"/><End Role="Sources" Type="Ovodata.Sources" Multiplicity="*"/></Association>
<Association Name="FK_Experiments_KeywordTags"><End Role="Experiments" Type="Ovodata.Experiments" Multiplicity="0..1"/><End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/></Association>
<Association Name="FK_Experiments_Epochs"><End Role="Experiments" Type="Ovodata.Experiments" Multiplicity="0..1"/><End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="*"/></Association>
<Association Name="FK_Experiments_Resources"><End Role="Experiments" Type="Ovodata.Experiments" Multiplicity="0..1"/><End Role="Resources" Type="Ovodata.Resources" Multiplicity="*"/></Association>
<Association Name="FK_Experiments__MapEntries"><End Role="Experiments" Type="Ovodata.Experiments" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_EpochGroups_EpochGroups"><End Role="EpochGroups" Type="Ovodata.EpochGroups" Multiplicity="*"/><End Role="EpochGroups1" Type="Ovodata.EpochGroups" Multiplicity="1"/></Association>
<Association Name="FK_EpochGroups_Sources"><End Role="EpochGroups" Type="Ovodata.EpochGroups" Multiplicity="*"/><End Role="Sources" Type="Ovodata.Sources" Multiplicity="1"/></Association>
<Association Name="FK_EpochGroups_Experiments"><End Role="EpochGroups" Type="Ovodata.EpochGroups" Multiplicity="*"/><End Role="Experiments" Type="Ovodata.Experiments" Multiplicity="1"/></Association>
<Association Name="FK_EpochGroups__Strings"><End Role="EpochGroups" Type="Ovodata.EpochGroups" Multiplicity="0..1"/><End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/></Association>
<Association Name="FK_EpochGroups__MapEntries"><End Role="EpochGroups" Type="Ovodata.EpochGroups" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_EpochGroups_KeywordTags"><End Role="EpochGroups" Type="Ovodata.EpochGroups" Multiplicity="0..1"/><End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/></Association>
<Association Name="FK_EpochGroups__Strings"><End Role="EpochGroups" Type="Ovodata.EpochGroups" Multiplicity="0..1"/><End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/></Association>
<Association Name="FK_EpochGroups_KeywordTags"><End Role="EpochGroups" Type="Ovodata.EpochGroups" Multiplicity="0..1"/><End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/></Association>
<Association Name="FK_EpochGroups_Epochs"><End Role="EpochGroups" Type="Ovodata.EpochGroups" Multiplicity="0..1"/><End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="*"/></Association>
<Association Name="FK_EpochGroups_Epochs"><End Role="EpochGroups" Type="Ovodata.EpochGroups" Multiplicity="0..1"/><End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="*"/></Association>
<Association Name="FK_EpochGroups_Resources"><End Role="EpochGroups" Type="Ovodata.EpochGroups" Multiplicity="0..1"/><End Role="Resources" Type="Ovodata.Resources" Multiplicity="*"/></Association>
<Association Name="FK_EpochGroups__MapEntries"><End Role="EpochGroups" Type="Ovodata.EpochGroups" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_AnalysisRecords_Projects"><End Role="AnalysisRecords" Type="Ovodata.AnalysisRecords" Multiplicity="*"/><End Role="Projects" Type="Ovodata.Projects" Multiplicity="1"/></Association>
<Association Name="FK_AnalysisRecords__Strings"><End Role="AnalysisRecords" Type="Ovodata.AnalysisRecords" Multiplicity="0..1"/><End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/></Association>
<Association Name="FK_AnalysisRecords__MapEntries"><End Role="AnalysisRecords" Type="Ovodata.AnalysisRecords" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_AnalysisRecords_KeywordTags"><End Role="AnalysisRecords" Type="Ovodata.AnalysisRecords" Multiplicity="0..1"/><End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/></Association>
<Association Name="FK_AnalysisRecords__Strings"><End Role="AnalysisRecords" Type="Ovodata.AnalysisRecords" Multiplicity="0..1"/><End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/></Association>
<Association Name="FK_AnalysisRecords_KeywordTags"><End Role="AnalysisRecords" Type="Ovodata.AnalysisRecords" Multiplicity="0..1"/><End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/></Association>
<Association Name="FK_AnalysisRecords_Epochs"><End Role="AnalysisRecords" Type="Ovodata.AnalysisRecords" Multiplicity="*"/><End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="*"/></Association>
<Association Name="FK_AnalysisRecords_Resources"><End Role="AnalysisRecords" Type="Ovodata.AnalysisRecords" Multiplicity="0..1"/><End Role="Resources" Type="Ovodata.Resources" Multiplicity="*"/></Association>
<Association Name="FK_AnalysisRecords__MapEntries"><End Role="AnalysisRecords" Type="Ovodata.AnalysisRecords" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_AnalysisRecords__MapEntries"><End Role="AnalysisRecords" Type="Ovodata.AnalysisRecords" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_Responses_ExternalDevices"><End Role="Responses" Type="Ovodata.Responses" Multiplicity="*"/><End Role="ExternalDevices" Type="Ovodata.ExternalDevices" Multiplicity="1"/></Association>
<Association Name="FK_Responses_Epochs"><End Role="Responses" Type="Ovodata.Responses" Multiplicity="*"/><End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="1"/></Association>
<Association Name="FK_Responses__Strings"><End Role="Responses" Type="Ovodata.Responses" Multiplicity="0..1"/><End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/></Association>
<Association Name="FK_Responses__MapEntries"><End Role="Responses" Type="Ovodata.Responses" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_Responses_KeywordTags"><End Role="Responses" Type="Ovodata.Responses" Multiplicity="0..1"/><End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/></Association>
<Association Name="FK_Responses__Strings"><End Role="Responses" Type="Ovodata.Responses" Multiplicity="0..1"/><End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/></Association>
<Association Name="FK_Responses__MapEntries"><End Role="Responses" Type="Ovodata.Responses" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_Responses_KeywordTags"><End Role="Responses" Type="Ovodata.Responses" Multiplicity="0..1"/><End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/></Association>
<Association Name="FK_Responses_Resources"><End Role="Responses" Type="Ovodata.Responses" Multiplicity="0..1"/><End Role="Resources" Type="Ovodata.Resources" Multiplicity="*"/></Association>
<Association Name="FK_Responses__MapEntries"><End Role="Responses" Type="Ovodata.Responses" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_ExternalDevices_Experiments"><End Role="ExternalDevices" Type="Ovodata.ExternalDevices" Multiplicity="*"/><End Role="Experiments" Type="Ovodata.Experiments" Multiplicity="1"/></Association>
<Association Name="FK_ExternalDevices__Strings"><End Role="ExternalDevices" Type="Ovodata.ExternalDevices" Multiplicity="0..1"/><End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/></Association>
<Association Name="FK_ExternalDevices__MapEntries"><End Role="ExternalDevices" Type="Ovodata.ExternalDevices" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_ExternalDevices_KeywordTags"><End Role="ExternalDevices" Type="Ovodata.ExternalDevices" Multiplicity="0..1"/><End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/></Association>
<Association Name="FK_ExternalDevices__Strings"><End Role="ExternalDevices" Type="Ovodata.ExternalDevices" Multiplicity="0..1"/><End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/></Association>
<Association Name="FK_ExternalDevices_KeywordTags"><End Role="ExternalDevices" Type="Ovodata.ExternalDevices" Multiplicity="0..1"/><End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/></Association>
<Association Name="FK_ExternalDevices_Resources"><End Role="ExternalDevices" Type="Ovodata.ExternalDevices" Multiplicity="0..1"/><End Role="Resources" Type="Ovodata.Resources" Multiplicity="*"/></Association>
<Association Name="FK_ExternalDevices__MapEntries"><End Role="ExternalDevices" Type="Ovodata.ExternalDevices" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_Sources_Sources"><End Role="Sources" Type="Ovodata.Sources" Multiplicity="*"/><End Role="Sources1" Type="Ovodata.Sources" Multiplicity="1"/></Association>
<Association Name="FK_Sources_Sources"><End Role="Sources" Type="Ovodata.Sources" Multiplicity="*"/><End Role="Sources1" Type="Ovodata.Sources" Multiplicity="1"/></Association>
<Association Name="FK_Sources__Strings"><End Role="Sources" Type="Ovodata.Sources" Multiplicity="0..1"/><End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/></Association>
<Association Name="FK_Sources__MapEntries"><End Role="Sources" Type="Ovodata.Sources" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_Sources_KeywordTags"><End Role="Sources" Type="Ovodata.Sources" Multiplicity="0..1"/><End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/></Association>
<Association Name="FK_Sources__Strings"><End Role="Sources" Type="Ovodata.Sources" Multiplicity="0..1"/><End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/></Association>
<Association Name="FK_Sources_KeywordTags"><End Role="Sources" Type="Ovodata.Sources" Multiplicity="0..1"/><End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/></Association>
<Association Name="FK_Sources_Resources"><End Role="Sources" Type="Ovodata.Sources" Multiplicity="0..1"/><End Role="Resources" Type="Ovodata.Resources" Multiplicity="*"/></Association>
<Association Name="FK_Sources__MapEntries"><End Role="Sources" Type="Ovodata.Sources" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_DerivedResponses_ExternalDevices"><End Role="DerivedResponses" Type="Ovodata.DerivedResponses" Multiplicity="*"/><End Role="ExternalDevices" Type="Ovodata.ExternalDevices" Multiplicity="1"/></Association>
<Association Name="FK_DerivedResponses_Epochs"><End Role="DerivedResponses" Type="Ovodata.DerivedResponses" Multiplicity="*"/><End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="1"/></Association>
<Association Name="FK_DerivedResponses__Strings"><End Role="DerivedResponses" Type="Ovodata.DerivedResponses" Multiplicity="0..1"/><End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/></Association>
<Association Name="FK_DerivedResponses__MapEntries"><End Role="DerivedResponses" Type="Ovodata.DerivedResponses" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_DerivedResponses_KeywordTags"><End Role="DerivedResponses" Type="Ovodata.DerivedResponses" Multiplicity="0..1"/><End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/></Association>
<Association Name="FK_DerivedResponses__MapEntries"><End Role="DerivedResponses" Type="Ovodata.DerivedResponses" Multiplicity="0..1"/><End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/></Association>
<Association Name="FK_DerivedResponses__Strings">
<End Role="DerivedResponses" Type="Ovodata.DerivedResponses" Multiplicity="0..1"/>
<End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/>
</Association>
<Association Name="FK_DerivedResponses__MapEntries">
<End Role="DerivedResponses" Type="Ovodata.DerivedResponses" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_DerivedResponses_KeywordTags">
<End Role="DerivedResponses" Type="Ovodata.DerivedResponses" Multiplicity="0..1"/>
<End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/>
</Association>
<Association Name="FK_DerivedResponses_Resources">
<End Role="DerivedResponses" Type="Ovodata.DerivedResponses" Multiplicity="0..1"/>
<End Role="Resources" Type="Ovodata.Resources" Multiplicity="*"/>
</Association>
<Association Name="FK_DerivedResponses__MapEntries">
<End Role="DerivedResponses" Type="Ovodata.DerivedResponses" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_Projects__Strings">
<End Role="Projects" Type="Ovodata.Projects" Multiplicity="0..1"/>
<End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/>
</Association>
<Association Name="FK_Projects__MapEntries">
<End Role="Projects" Type="Ovodata.Projects" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_Projects_KeywordTags">
<End Role="Projects" Type="Ovodata.Projects" Multiplicity="0..1"/>
<End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/>
</Association>
<Association Name="FK_Projects__Strings">
<End Role="Projects" Type="Ovodata.Projects" Multiplicity="0..1"/>
<End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/>
</Association>
<Association Name="FK_Projects_KeywordTags">
<End Role="Projects" Type="Ovodata.Projects" Multiplicity="0..1"/>
<End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/>
</Association>
<Association Name="FK_Projects_Resources">
<End Role="Projects" Type="Ovodata.Projects" Multiplicity="0..1"/>
<End Role="Resources" Type="Ovodata.Resources" Multiplicity="*"/>
</Association>
<Association Name="FK_Projects__MapEntries">
<End Role="Projects" Type="Ovodata.Projects" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_URLResources__Strings">
<End Role="URLResources" Type="Ovodata.URLResources" Multiplicity="0..1"/>
<End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/>
</Association>
<Association Name="FK_URLResources__MapEntries">
<End Role="URLResources" Type="Ovodata.URLResources" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_URLResources_KeywordTags">
<End Role="URLResources" Type="Ovodata.URLResources" Multiplicity="0..1"/>
<End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/>
</Association>
<Association Name="FK_URLResources__Strings">
<End Role="URLResources" Type="Ovodata.URLResources" Multiplicity="0..1"/>
<End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/>
</Association>
<Association Name="FK_URLResources_KeywordTags">
<End Role="URLResources" Type="Ovodata.URLResources" Multiplicity="0..1"/>
<End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/>
</Association>
<Association Name="FK_URLResources_Resources">
<End Role="URLResources" Type="Ovodata.URLResources" Multiplicity="0..1"/>
<End Role="Resources" Type="Ovodata.Resources" Multiplicity="*"/>
</Association>
<Association Name="FK_URLResources__MapEntries">
<End Role="URLResources" Type="Ovodata.URLResources" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_Stimuli_ExternalDevices">
<End Role="Stimuli" Type="Ovodata.Stimuli" Multiplicity="*"/>
<End Role="ExternalDevices" Type="Ovodata.ExternalDevices" Multiplicity="1"/>
</Association>
<Association Name="FK_Stimuli_Epochs">
<End Role="Stimuli" Type="Ovodata.Stimuli" Multiplicity="*"/>
<End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="1"/>
</Association>
<Association Name="FK_Stimuli__Strings">
<End Role="Stimuli" Type="Ovodata.Stimuli" Multiplicity="0..1"/>
<End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/>
</Association>
<Association Name="FK_Stimuli__MapEntries">
<End Role="Stimuli" Type="Ovodata.Stimuli" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_Stimuli_KeywordTags">
<End Role="Stimuli" Type="Ovodata.Stimuli" Multiplicity="0..1"/>
<End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/>
</Association>
<Association Name="FK_Stimuli__MapEntries">
<End Role="Stimuli" Type="Ovodata.Stimuli" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_Stimuli__Strings">
<End Role="Stimuli" Type="Ovodata.Stimuli" Multiplicity="0..1"/>
<End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/>
</Association>
<Association Name="FK_Stimuli__MapEntries">
<End Role="Stimuli" Type="Ovodata.Stimuli" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_Stimuli_KeywordTags">
<End Role="Stimuli" Type="Ovodata.Stimuli" Multiplicity="0..1"/>
<End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/>
</Association>
<Association Name="FK_Stimuli_Resources">
<End Role="Stimuli" Type="Ovodata.Stimuli" Multiplicity="0..1"/>
<End Role="Resources" Type="Ovodata.Resources" Multiplicity="*"/>
</Association>
<Association Name="FK_Stimuli__MapEntries">
<End Role="Stimuli" Type="Ovodata.Stimuli" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_KeywordTags__MapEntries">
<End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_KeywordTags__Strings">
<End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="0..1"/>
<End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/>
</Association>
<Association Name="FK_KeywordTags_Resources">
<End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/>
<End Role="Resources" Type="Ovodata.Resources" Multiplicity="*"/>
</Association>
<Association Name="FK_KeywordTags__MapEntries">
<End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_Epochs_EpochGroups">
<End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="*"/>
<End Role="EpochGroups" Type="Ovodata.EpochGroups" Multiplicity="1"/>
</Association>
<Association Name="FK_Epochs_Epochs">
<End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="*"/>
<End Role="Epochs1" Type="Ovodata.Epochs" Multiplicity="1"/>
</Association>
<Association Name="FK_Epochs_Epochs">
<End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="*"/>
<End Role="Epochs1" Type="Ovodata.Epochs" Multiplicity="1"/>
</Association>
<Association Name="FK_Epochs__Strings">
<End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="0..1"/>
<End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/>
</Association>
<Association Name="FK_Epochs__MapEntries">
<End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_Epochs__Strings">
<End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="0..1"/>
<End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/>
</Association>
<Association Name="FK_Epochs__MapEntries">
<End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_Epochs_KeywordTags">
<End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="0..1"/>
<End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/>
</Association>
<Association Name="FK_Epochs__MapEntries">
<End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_Epochs_KeywordTags">
<End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="0..1"/>
<End Role="KeywordTags" Type="Ovodata.KeywordTags" Multiplicity="*"/>
</Association>
<Association Name="FK_Epochs_Resources">
<End Role="Epochs" Type="Ovodata.Epochs" Multiplicity="0..1"/>
<End Role="Resources" Type="Ovodata.Resources" Multiplicity="*"/>
</Association>
<Association Name="FK_Resources__Strings">
<End Role="Resources" Type="Ovodata.Resources" Multiplicity="0..1"/>
<End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/>
</Association>
<Association Name="FK_Resources__MapEntries">
<End Role="Resources" Type="Ovodata.Resources" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>
<Association Name="FK_Resources__Strings">
<End Role="Resources" Type="Ovodata.Resources" Multiplicity="0..1"/>
<End Role="_Strings" Type="Ovodata._Strings" Multiplicity="*"/>
</Association>
<Association Name="FK_Resources_Resources">
<End Role="Resources" Type="Ovodata.Resources" Multiplicity="*"/>
<End Role="Resources1" Type="Ovodata.Resources" Multiplicity="*"/>
</Association>
<Association Name="FK_Resources__MapEntries">
<End Role="Resources" Type="Ovodata.Resources" Multiplicity="0..1"/>
<End Role="_MapEntries" Type="Ovodata._MapEntries" Multiplicity="*"/>
</Association>

<EntityContainer Name="Container" m:IsDefaultEntityContainer="true">
<EntitySet Name="Experiments" EntityType="Ovodata.Experiments"/>
<EntitySet Name="EpochGroups" EntityType="Ovodata.EpochGroups"/>
<EntitySet Name="_MapEntries" EntityType="Ovodata._MapEntries"/>
<EntitySet Name="AnalysisRecords" EntityType="Ovodata.AnalysisRecords"/>
<EntitySet Name="Responses" EntityType="Ovodata.Responses"/>
<EntitySet Name="ExternalDevices" EntityType="Ovodata.ExternalDevices"/>
<EntitySet Name="Sources" EntityType="Ovodata.Sources"/>
<EntitySet Name="DerivedResponses" EntityType="Ovodata.DerivedResponses"/>
<EntitySet Name="Projects" EntityType="Ovodata.Projects"/>
<EntitySet Name="URLResources" EntityType="Ovodata.URLResources"/>
<EntitySet Name="_Strings" EntityType="Ovodata._Strings"/>
<EntitySet Name="Stimuli" EntityType="Ovodata.Stimuli"/>
<EntitySet Name="KeywordTags" EntityType="Ovodata.KeywordTags"/>
<EntitySet Name="Epochs" EntityType="Ovodata.Epochs"/>
<EntitySet Name="Resources" EntityType="Ovodata.Resources"/>

<AssociationSet Name="FK_Experiments_Projects" Association="Ovodata.FK_Experiments_Projects">
<End Role="Experiments" EntitySet="Experiments"/>
<End Role="Projects" EntitySet="Projects"/>
</AssociationSet>
<AssociationSet Name="FK_Experiments__Strings" Association="Ovodata.FK_Experiments__Strings">
<End Role="Experiments" EntitySet="Experiments"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_Experiments__MapEntries" Association="Ovodata.FK_Experiments__MapEntries">
<End Role="Experiments" EntitySet="Experiments"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Experiments_KeywordTags" Association="Ovodata.FK_Experiments_KeywordTags">
<End Role="Experiments" EntitySet="Experiments"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_Experiments_EpochGroups" Association="Ovodata.FK_Experiments_EpochGroups">
<End Role="Experiments" EntitySet="Experiments"/>
<End Role="EpochGroups" EntitySet="EpochGroups"/>
</AssociationSet>
<AssociationSet Name="FK_Experiments__Strings" Association="Ovodata.FK_Experiments__Strings">
<End Role="Experiments" EntitySet="Experiments"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_Experiments_ExternalDevices" Association="Ovodata.FK_Experiments_ExternalDevices">
<End Role="Experiments" EntitySet="Experiments"/>
<End Role="ExternalDevices" EntitySet="ExternalDevices"/>
</AssociationSet>
<AssociationSet Name="FK_Experiments_Sources" Association="Ovodata.FK_Experiments_Sources">
<End Role="Experiments" EntitySet="Experiments"/>
<End Role="Sources" EntitySet="Sources"/>
</AssociationSet>
<AssociationSet Name="FK_Experiments_KeywordTags" Association="Ovodata.FK_Experiments_KeywordTags">
<End Role="Experiments" EntitySet="Experiments"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_Experiments_Epochs" Association="Ovodata.FK_Experiments_Epochs">
<End Role="Experiments" EntitySet="Experiments"/>
<End Role="Epochs" EntitySet="Epochs"/>
</AssociationSet>
<AssociationSet Name="FK_Experiments_Resources" Association="Ovodata.FK_Experiments_Resources">
<End Role="Experiments" EntitySet="Experiments"/>
<End Role="Resources" EntitySet="Resources"/>
</AssociationSet>
<AssociationSet Name="FK_Experiments__MapEntries" Association="Ovodata.FK_Experiments__MapEntries">
<End Role="Experiments" EntitySet="Experiments"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_EpochGroups_EpochGroups" Association="Ovodata.FK_EpochGroups_EpochGroups">
<End Role="EpochGroups" EntitySet="EpochGroups"/>
<End Role="EpochGroups1" EntitySet="EpochGroups"/>
</AssociationSet>
<AssociationSet Name="FK_EpochGroups_Sources" Association="Ovodata.FK_EpochGroups_Sources">
<End Role="EpochGroups" EntitySet="EpochGroups"/>
<End Role="Sources" EntitySet="Sources"/>
</AssociationSet>
<AssociationSet Name="FK_EpochGroups_Experiments" Association="Ovodata.FK_EpochGroups_Experiments">
<End Role="EpochGroups" EntitySet="EpochGroups"/>
<End Role="Experiments" EntitySet="Experiments"/>
</AssociationSet>
<AssociationSet Name="FK_EpochGroups__Strings" Association="Ovodata.FK_EpochGroups__Strings">
<End Role="EpochGroups" EntitySet="EpochGroups"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_EpochGroups__MapEntries" Association="Ovodata.FK_EpochGroups__MapEntries">
<End Role="EpochGroups" EntitySet="EpochGroups"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_EpochGroups_KeywordTags" Association="Ovodata.FK_EpochGroups_KeywordTags">
<End Role="EpochGroups" EntitySet="EpochGroups"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_EpochGroups__Strings" Association="Ovodata.FK_EpochGroups__Strings">
<End Role="EpochGroups" EntitySet="EpochGroups"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_EpochGroups_KeywordTags" Association="Ovodata.FK_EpochGroups_KeywordTags">
<End Role="EpochGroups" EntitySet="EpochGroups"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_EpochGroups_Epochs" Association="Ovodata.FK_EpochGroups_Epochs">
<End Role="EpochGroups" EntitySet="EpochGroups"/>
<End Role="Epochs" EntitySet="Epochs"/>
</AssociationSet>
<AssociationSet Name="FK_EpochGroups_Epochs" Association="Ovodata.FK_EpochGroups_Epochs">
<End Role="EpochGroups" EntitySet="EpochGroups"/>
<End Role="Epochs" EntitySet="Epochs"/>
</AssociationSet>
<AssociationSet Name="FK_EpochGroups_Resources" Association="Ovodata.FK_EpochGroups_Resources">
<End Role="EpochGroups" EntitySet="EpochGroups"/>
<End Role="Resources" EntitySet="Resources"/>
</AssociationSet>
<AssociationSet Name="FK_EpochGroups__MapEntries" Association="Ovodata.FK_EpochGroups__MapEntries">
<End Role="EpochGroups" EntitySet="EpochGroups"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_AnalysisRecords_Projects" Association="Ovodata.FK_AnalysisRecords_Projects">
<End Role="AnalysisRecords" EntitySet="AnalysisRecords"/>
<End Role="Projects" EntitySet="Projects"/>
</AssociationSet>
<AssociationSet Name="FK_AnalysisRecords__Strings" Association="Ovodata.FK_AnalysisRecords__Strings">
<End Role="AnalysisRecords" EntitySet="AnalysisRecords"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_AnalysisRecords__MapEntries" Association="Ovodata.FK_AnalysisRecords__MapEntries">
<End Role="AnalysisRecords" EntitySet="AnalysisRecords"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_AnalysisRecords_KeywordTags" Association="Ovodata.FK_AnalysisRecords_KeywordTags">
<End Role="AnalysisRecords" EntitySet="AnalysisRecords"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_AnalysisRecords__Strings" Association="Ovodata.FK_AnalysisRecords__Strings">
<End Role="AnalysisRecords" EntitySet="AnalysisRecords"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_AnalysisRecords_KeywordTags" Association="Ovodata.FK_AnalysisRecords_KeywordTags">
<End Role="AnalysisRecords" EntitySet="AnalysisRecords"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_AnalysisRecords_Epochs" Association="Ovodata.FK_AnalysisRecords_Epochs">
<End Role="AnalysisRecords" EntitySet="AnalysisRecords"/>
<End Role="Epochs" EntitySet="Epochs"/>
</AssociationSet>
<AssociationSet Name="FK_AnalysisRecords_Resources" Association="Ovodata.FK_AnalysisRecords_Resources">
<End Role="AnalysisRecords" EntitySet="AnalysisRecords"/>
<End Role="Resources" EntitySet="Resources"/>
</AssociationSet>
<AssociationSet Name="FK_AnalysisRecords__MapEntries" Association="Ovodata.FK_AnalysisRecords__MapEntries">
<End Role="AnalysisRecords" EntitySet="AnalysisRecords"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_AnalysisRecords__MapEntries" Association="Ovodata.FK_AnalysisRecords__MapEntries">
<End Role="AnalysisRecords" EntitySet="AnalysisRecords"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Responses_ExternalDevices" Association="Ovodata.FK_Responses_ExternalDevices">
<End Role="Responses" EntitySet="Responses"/>
<End Role="ExternalDevices" EntitySet="ExternalDevices"/>
</AssociationSet>
<AssociationSet Name="FK_Responses_Epochs" Association="Ovodata.FK_Responses_Epochs">
<End Role="Responses" EntitySet="Responses"/>
<End Role="Epochs" EntitySet="Epochs"/>
</AssociationSet>
<AssociationSet Name="FK_Responses__Strings" Association="Ovodata.FK_Responses__Strings">
<End Role="Responses" EntitySet="Responses"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_Responses__MapEntries" Association="Ovodata.FK_Responses__MapEntries">
<End Role="Responses" EntitySet="Responses"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Responses_KeywordTags" Association="Ovodata.FK_Responses_KeywordTags">
<End Role="Responses" EntitySet="Responses"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_Responses__Strings" Association="Ovodata.FK_Responses__Strings">
<End Role="Responses" EntitySet="Responses"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_Responses__MapEntries" Association="Ovodata.FK_Responses__MapEntries">
<End Role="Responses" EntitySet="Responses"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Responses_KeywordTags" Association="Ovodata.FK_Responses_KeywordTags">
<End Role="Responses" EntitySet="Responses"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_Responses_Resources" Association="Ovodata.FK_Responses_Resources">
<End Role="Responses" EntitySet="Responses"/>
<End Role="Resources" EntitySet="Resources"/>
</AssociationSet>
<AssociationSet Name="FK_Responses__MapEntries" Association="Ovodata.FK_Responses__MapEntries">
<End Role="Responses" EntitySet="Responses"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_ExternalDevices_Experiments" Association="Ovodata.FK_ExternalDevices_Experiments">
<End Role="ExternalDevices" EntitySet="ExternalDevices"/>
<End Role="Experiments" EntitySet="Experiments"/>
</AssociationSet>
<AssociationSet Name="FK_ExternalDevices__Strings" Association="Ovodata.FK_ExternalDevices__Strings">
<End Role="ExternalDevices" EntitySet="ExternalDevices"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_ExternalDevices__MapEntries" Association="Ovodata.FK_ExternalDevices__MapEntries">
<End Role="ExternalDevices" EntitySet="ExternalDevices"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_ExternalDevices_KeywordTags" Association="Ovodata.FK_ExternalDevices_KeywordTags">
<End Role="ExternalDevices" EntitySet="ExternalDevices"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_ExternalDevices__Strings" Association="Ovodata.FK_ExternalDevices__Strings">
<End Role="ExternalDevices" EntitySet="ExternalDevices"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_ExternalDevices_KeywordTags" Association="Ovodata.FK_ExternalDevices_KeywordTags">
<End Role="ExternalDevices" EntitySet="ExternalDevices"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_ExternalDevices_Resources" Association="Ovodata.FK_ExternalDevices_Resources">
<End Role="ExternalDevices" EntitySet="ExternalDevices"/>
<End Role="Resources" EntitySet="Resources"/>
</AssociationSet>
<AssociationSet Name="FK_ExternalDevices__MapEntries" Association="Ovodata.FK_ExternalDevices__MapEntries">
<End Role="ExternalDevices" EntitySet="ExternalDevices"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Sources_Sources" Association="Ovodata.FK_Sources_Sources">
<End Role="Sources" EntitySet="Sources"/>
<End Role="Sources1" EntitySet="Sources"/>
</AssociationSet>
<AssociationSet Name="FK_Sources_Sources" Association="Ovodata.FK_Sources_Sources">
<End Role="Sources" EntitySet="Sources"/>
<End Role="Sources1" EntitySet="Sources"/>
</AssociationSet>
<AssociationSet Name="FK_Sources__Strings" Association="Ovodata.FK_Sources__Strings">
<End Role="Sources" EntitySet="Sources"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_Sources__MapEntries" Association="Ovodata.FK_Sources__MapEntries">
<End Role="Sources" EntitySet="Sources"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Sources_KeywordTags" Association="Ovodata.FK_Sources_KeywordTags">
<End Role="Sources" EntitySet="Sources"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_Sources__Strings" Association="Ovodata.FK_Sources__Strings">
<End Role="Sources" EntitySet="Sources"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_Sources_KeywordTags" Association="Ovodata.FK_Sources_KeywordTags">
<End Role="Sources" EntitySet="Sources"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_Sources_Resources" Association="Ovodata.FK_Sources_Resources">
<End Role="Sources" EntitySet="Sources"/>
<End Role="Resources" EntitySet="Resources"/>
</AssociationSet>
<AssociationSet Name="FK_Sources__MapEntries" Association="Ovodata.FK_Sources__MapEntries">
<End Role="Sources" EntitySet="Sources"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_DerivedResponses_ExternalDevices" Association="Ovodata.FK_DerivedResponses_ExternalDevices">
<End Role="DerivedResponses" EntitySet="DerivedResponses"/>
<End Role="ExternalDevices" EntitySet="ExternalDevices"/>
</AssociationSet>
<AssociationSet Name="FK_DerivedResponses_Epochs" Association="Ovodata.FK_DerivedResponses_Epochs">
<End Role="DerivedResponses" EntitySet="DerivedResponses"/>
<End Role="Epochs" EntitySet="Epochs"/>
</AssociationSet>
<AssociationSet Name="FK_DerivedResponses__Strings" Association="Ovodata.FK_DerivedResponses__Strings">
<End Role="DerivedResponses" EntitySet="DerivedResponses"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_DerivedResponses__MapEntries" Association="Ovodata.FK_DerivedResponses__MapEntries">
<End Role="DerivedResponses" EntitySet="DerivedResponses"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_DerivedResponses_KeywordTags" Association="Ovodata.FK_DerivedResponses_KeywordTags">
<End Role="DerivedResponses" EntitySet="DerivedResponses"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_DerivedResponses__MapEntries" Association="Ovodata.FK_DerivedResponses__MapEntries">
<End Role="DerivedResponses" EntitySet="DerivedResponses"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_DerivedResponses__Strings" Association="Ovodata.FK_DerivedResponses__Strings">
<End Role="DerivedResponses" EntitySet="DerivedResponses"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_DerivedResponses__MapEntries" Association="Ovodata.FK_DerivedResponses__MapEntries">
<End Role="DerivedResponses" EntitySet="DerivedResponses"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_DerivedResponses_KeywordTags" Association="Ovodata.FK_DerivedResponses_KeywordTags">
<End Role="DerivedResponses" EntitySet="DerivedResponses"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_DerivedResponses_Resources" Association="Ovodata.FK_DerivedResponses_Resources">
<End Role="DerivedResponses" EntitySet="DerivedResponses"/>
<End Role="Resources" EntitySet="Resources"/>
</AssociationSet>
<AssociationSet Name="FK_DerivedResponses__MapEntries" Association="Ovodata.FK_DerivedResponses__MapEntries">
<End Role="DerivedResponses" EntitySet="DerivedResponses"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Projects__Strings" Association="Ovodata.FK_Projects__Strings">
<End Role="Projects" EntitySet="Projects"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_Projects__MapEntries" Association="Ovodata.FK_Projects__MapEntries">
<End Role="Projects" EntitySet="Projects"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Projects_KeywordTags" Association="Ovodata.FK_Projects_KeywordTags">
<End Role="Projects" EntitySet="Projects"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_Projects__Strings" Association="Ovodata.FK_Projects__Strings">
<End Role="Projects" EntitySet="Projects"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_Projects_KeywordTags" Association="Ovodata.FK_Projects_KeywordTags">
<End Role="Projects" EntitySet="Projects"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_Projects_Resources" Association="Ovodata.FK_Projects_Resources">
<End Role="Projects" EntitySet="Projects"/>
<End Role="Resources" EntitySet="Resources"/>
</AssociationSet>
<AssociationSet Name="FK_Projects__MapEntries" Association="Ovodata.FK_Projects__MapEntries">
<End Role="Projects" EntitySet="Projects"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_URLResources__Strings" Association="Ovodata.FK_URLResources__Strings">
<End Role="URLResources" EntitySet="URLResources"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_URLResources__MapEntries" Association="Ovodata.FK_URLResources__MapEntries">
<End Role="URLResources" EntitySet="URLResources"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_URLResources_KeywordTags" Association="Ovodata.FK_URLResources_KeywordTags">
<End Role="URLResources" EntitySet="URLResources"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_URLResources__Strings" Association="Ovodata.FK_URLResources__Strings">
<End Role="URLResources" EntitySet="URLResources"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_URLResources_KeywordTags" Association="Ovodata.FK_URLResources_KeywordTags">
<End Role="URLResources" EntitySet="URLResources"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_URLResources_Resources" Association="Ovodata.FK_URLResources_Resources">
<End Role="URLResources" EntitySet="URLResources"/>
<End Role="Resources" EntitySet="Resources"/>
</AssociationSet>
<AssociationSet Name="FK_URLResources__MapEntries" Association="Ovodata.FK_URLResources__MapEntries">
<End Role="URLResources" EntitySet="URLResources"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Stimuli_ExternalDevices" Association="Ovodata.FK_Stimuli_ExternalDevices">
<End Role="Stimuli" EntitySet="Stimuli"/>
<End Role="ExternalDevices" EntitySet="ExternalDevices"/>
</AssociationSet>
<AssociationSet Name="FK_Stimuli_Epochs" Association="Ovodata.FK_Stimuli_Epochs">
<End Role="Stimuli" EntitySet="Stimuli"/>
<End Role="Epochs" EntitySet="Epochs"/>
</AssociationSet>
<AssociationSet Name="FK_Stimuli__Strings" Association="Ovodata.FK_Stimuli__Strings">
<End Role="Stimuli" EntitySet="Stimuli"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_Stimuli__MapEntries" Association="Ovodata.FK_Stimuli__MapEntries">
<End Role="Stimuli" EntitySet="Stimuli"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Stimuli_KeywordTags" Association="Ovodata.FK_Stimuli_KeywordTags">
<End Role="Stimuli" EntitySet="Stimuli"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_Stimuli__MapEntries" Association="Ovodata.FK_Stimuli__MapEntries">
<End Role="Stimuli" EntitySet="Stimuli"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Stimuli__Strings" Association="Ovodata.FK_Stimuli__Strings">
<End Role="Stimuli" EntitySet="Stimuli"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_Stimuli__MapEntries" Association="Ovodata.FK_Stimuli__MapEntries">
<End Role="Stimuli" EntitySet="Stimuli"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Stimuli_KeywordTags" Association="Ovodata.FK_Stimuli_KeywordTags">
<End Role="Stimuli" EntitySet="Stimuli"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_Stimuli_Resources" Association="Ovodata.FK_Stimuli_Resources">
<End Role="Stimuli" EntitySet="Stimuli"/>
<End Role="Resources" EntitySet="Resources"/>
</AssociationSet>
<AssociationSet Name="FK_Stimuli__MapEntries" Association="Ovodata.FK_Stimuli__MapEntries">
<End Role="Stimuli" EntitySet="Stimuli"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_KeywordTags__MapEntries" Association="Ovodata.FK_KeywordTags__MapEntries">
<End Role="KeywordTags" EntitySet="KeywordTags"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_KeywordTags__Strings" Association="Ovodata.FK_KeywordTags__Strings">
<End Role="KeywordTags" EntitySet="KeywordTags"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_KeywordTags_Resources" Association="Ovodata.FK_KeywordTags_Resources">
<End Role="KeywordTags" EntitySet="KeywordTags"/>
<End Role="Resources" EntitySet="Resources"/>
</AssociationSet>
<AssociationSet Name="FK_KeywordTags__MapEntries" Association="Ovodata.FK_KeywordTags__MapEntries">
<End Role="KeywordTags" EntitySet="KeywordTags"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Epochs_EpochGroups" Association="Ovodata.FK_Epochs_EpochGroups">
<End Role="Epochs" EntitySet="Epochs"/>
<End Role="EpochGroups" EntitySet="EpochGroups"/>
</AssociationSet>
<AssociationSet Name="FK_Epochs_Epochs" Association="Ovodata.FK_Epochs_Epochs">
<End Role="Epochs" EntitySet="Epochs"/>
<End Role="Epochs1" EntitySet="Epochs"/>
</AssociationSet>
<AssociationSet Name="FK_Epochs_Epochs" Association="Ovodata.FK_Epochs_Epochs">
<End Role="Epochs" EntitySet="Epochs"/>
<End Role="Epochs1" EntitySet="Epochs"/>
</AssociationSet>
<AssociationSet Name="FK_Epochs__Strings" Association="Ovodata.FK_Epochs__Strings">
<End Role="Epochs" EntitySet="Epochs"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_Epochs__MapEntries" Association="Ovodata.FK_Epochs__MapEntries">
<End Role="Epochs" EntitySet="Epochs"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Epochs__Strings" Association="Ovodata.FK_Epochs__Strings">
<End Role="Epochs" EntitySet="Epochs"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_Epochs__MapEntries" Association="Ovodata.FK_Epochs__MapEntries">
<End Role="Epochs" EntitySet="Epochs"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Epochs_KeywordTags" Association="Ovodata.FK_Epochs_KeywordTags">
<End Role="Epochs" EntitySet="Epochs"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_Epochs__MapEntries" Association="Ovodata.FK_Epochs__MapEntries">
<End Role="Epochs" EntitySet="Epochs"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Epochs_KeywordTags" Association="Ovodata.FK_Epochs_KeywordTags">
<End Role="Epochs" EntitySet="Epochs"/>
<End Role="KeywordTags" EntitySet="KeywordTags"/>
</AssociationSet>
<AssociationSet Name="FK_Epochs_Resources" Association="Ovodata.FK_Epochs_Resources">
<End Role="Epochs" EntitySet="Epochs"/>
<End Role="Resources" EntitySet="Resources"/>
</AssociationSet>
<AssociationSet Name="FK_Resources__Strings" Association="Ovodata.FK_Resources__Strings">
<End Role="Resources" EntitySet="Resources"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_Resources__MapEntries" Association="Ovodata.FK_Resources__MapEntries">
<End Role="Resources" EntitySet="Resources"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
<AssociationSet Name="FK_Resources__Strings" Association="Ovodata.FK_Resources__Strings">
<End Role="Resources" EntitySet="Resources"/>
<End Role="_Strings" EntitySet="_Strings"/>
</AssociationSet>
<AssociationSet Name="FK_Resources_Resources" Association="Ovodata.FK_Resources_Resources">
<End Role="Resources" EntitySet="Resources"/>
<End Role="Resources1" EntitySet="Resources"/>
</AssociationSet>
<AssociationSet Name="FK_Resources__MapEntries" Association="Ovodata.FK_Resources__MapEntries">
<End Role="Resources" EntitySet="Resources"/>
<End Role="_MapEntries" EntitySet="_MapEntries"/>
</AssociationSet>
</EntityContainer>
</Schema>
</edmx:DataServices>
</edmx:Edmx>
 */
		}
	}
}
