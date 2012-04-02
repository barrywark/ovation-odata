package ovation.odata.service;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.core4j.Enumerable;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.EntitySetInfo;
import org.odata4j.core.OClientBehaviors;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityGetRequest;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OQueryRequest;
import org.odata4j.core.ORelatedEntitiesLink;
import org.odata4j.core.ORelatedEntityLink;

import ovation.AnalysisRecord;
import ovation.DataContext;
import ovation.DerivedResponse;
import ovation.Epoch;
import ovation.EpochGroup;
import ovation.Experiment;
import ovation.ExternalDevice;
import ovation.IAnnotation;
import ovation.ITaggableEntityBase;
import ovation.KeywordTag;
import ovation.NumericDataType;
import ovation.Project;
import ovation.Resource;
import ovation.Response;
import ovation.Source;
import ovation.Stimulus;
import ovation.URLResource;
import ovation.User;
import ovation.odata.model.dao.Property;
import ovation.odata.util.JUnitUtils;
import ovation.odata.util.OData4JClientUtils;
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
		USER("Users",						User.class),
		
		_STRING("_Strings",              	String.class),
		_DOUBLE("_Doubles",                 Double.class),
		_FLOAT("_Floats",                 	Float.class),
		_LONG("_Longs",                 	Long.class),
		_INTEGER("_Integers",               Integer.class),
		
		_PROPERTY("_Properties",			Property.class),
		_ANNOTATION("_Annotations",			IAnnotation.class),
		
		_TaggableEntityBase("_ITaggableEntityBases",ITaggableEntityBase.class),
		_NumericDataTypes("_NumericDataTypes",		NumericDataType.class),
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

	static OEntity getSubEntity(Entity type, OEntity entity, String name) {
		ORelatedEntityLink link = entity.getLink(name, ORelatedEntityLink.class);
		OEntityGetRequest<OEntity> req = _odataClient.getEntity(link);
		return req.execute();
	}	
	
	static Enumerable<OEntity> getSubEntities(Entity type, OEntity entity, String name) {
		ORelatedEntitiesLink link = entity.getLink(name, ORelatedEntitiesLink.class);
		OQueryRequest<OEntity> req = _odataClient.getEntities(link);
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


	
	static void compareToDb(Entity type, OEntity entity) {
		switch(type) {
			case EXPERIMENT : {
				Experiment fromDb = getFromDB(OData4JClientUtils.getStringProperty(entity, "URI"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getNotes(), 				OData4JClientUtils.getStringProperty(entity, "Notes"));
//				Assert.assertEquals(fromDb.getSerializedName(), 	OData4JClientUtils.getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(), OData4JClientUtils.getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getStartTime(), 			OData4JClientUtils.getDateTimeProperty(entity, "StartTime"));
				Assert.assertEquals(fromDb.getEndTime(), 			OData4JClientUtils.getDateTimeProperty(entity, "EndTime"));
				Assert.assertEquals(fromDb.getPurpose(), 			OData4JClientUtils.getStringProperty(entity, "Purpose"));

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
				AnalysisRecord fromDb = getFromDB(OData4JClientUtils.getStringProperty(entity, "URI"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getNotes(), 				OData4JClientUtils.getStringProperty(entity, "Notes"));
				Assert.assertEquals(fromDb.getName(), 				OData4JClientUtils.getStringProperty(entity, "Name"));
//				Assert.assertEquals(fromDb.getSerializedName(), 	OData4JClientUtils.getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(),	OData4JClientUtils.getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getScmRevision(), 		OData4JClientUtils.getIntegerProperty(entity, "ScmRevision", Integer.MIN_VALUE));
				Assert.assertEquals(fromDb.getEntryFunctionName(), 	OData4JClientUtils.getStringProperty(entity, "EntryFunctionName"));
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
				DerivedResponse fromDb = getFromDB(OData4JClientUtils.getStringProperty(entity, "URI"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getDescription(), 		OData4JClientUtils.getStringProperty(entity, "Description"));
				Assert.assertEquals(fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(), OData4JClientUtils.getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getName(), 				OData4JClientUtils.getStringProperty(entity, "Name"));
				JUnitUtils.assertEquals(fromDb.getDataBytes(), 		OData4JClientUtils.getByteArrayProperty(entity, "DataBytes"));
//				Assert.assertEquals(fromDb.getSerializedName(), 	OData4JClientUtils.getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUnits(), 				OData4JClientUtils.getStringProperty(entity, "Units"));
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
				Epoch fromDb = getFromDB(OData4JClientUtils.getStringProperty(entity, "URI"));
				Assert.assertNotNull(fromDb);
//				Assert.assertEquals(fromDb.getSerializedName(), 						OData4JClientUtils.getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getDuration(), 								OData4JClientUtils.getDoubleProperty(entity, "Duration"));
				Assert.assertEquals(fromDb.getUuid(), 									OData4JClientUtils.getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(), 					OData4JClientUtils.getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(Boolean.valueOf(fromDb.getExcludeFromAnalysis()),	OData4JClientUtils.getBooleanProperty(entity, "ExcludeFromAnalysis"));
				Assert.assertEquals(fromDb.getProtocolID(),								OData4JClientUtils.getStringProperty(entity, "ProtocolID"));
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
				EpochGroup fromDb = getFromDB(OData4JClientUtils.getStringProperty(entity, "URI"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getEpochCount(),			OData4JClientUtils.getIntegerProperty(entity, "EpochCount", -1));
				Assert.assertEquals(fromDb.getSerializedLocation(), OData4JClientUtils.getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(entity, "UUID"));
//				Assert.assertEquals(fromDb.getSerializedName(), 	OData4JClientUtils.getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getLabel(), 				OData4JClientUtils.getStringProperty(entity, "Label"));
				Assert.assertEquals(fromDb.getStartTime(), 			OData4JClientUtils.getDateTimeProperty(entity, "StartTime"));
				Assert.assertEquals(fromDb.getEndTime(), 			OData4JClientUtils.getDateTimeProperty(entity, "EndTime"));

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
				ExternalDevice fromDb = getFromDB(OData4JClientUtils.getStringProperty(entity, "URI"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getName(), 				OData4JClientUtils.getStringProperty(entity, "Name"));
				Assert.assertEquals(fromDb.getManufacturer(), 		OData4JClientUtils.getStringProperty(entity, "Manufacturer"));
//				Assert.assertEquals(fromDb.getSerializedName(), 	OData4JClientUtils.getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(),	OData4JClientUtils.getStringProperty(entity, "SerializedLocation"));

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
				KeywordTag fromDb = getFromDB(OData4JClientUtils.getStringProperty(entity, "URI"));
				Assert.assertNotNull(fromDb);
//				Assert.assertEquals(fromDb.getSerializedName(), 	OData4JClientUtils.getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getTag(), 				OData4JClientUtils.getStringProperty(entity, "Tag"));
				Assert.assertEquals(fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(entity, "UUID"));
//				Assert.assertEquals(fromDb.getSerializedLocation(),	OData4JClientUtils.getStringProperty(entity, "SerializedLocation"));
				
/*					
					<NavigationProperty Name="MyProperties" Relationship="Ovodata.FK_KeywordTags__MapEntries" FromRole="KeywordTags" ToRole="_MapEntries"/>
					<NavigationProperty Name="Tagged" Relationship="Ovodata.FK_KeywordTags__Strings" FromRole="KeywordTags" ToRole="_Strings"/>
					<NavigationProperty Name="Resources" Relationship="Ovodata.FK_KeywordTags_Resources" FromRole="KeywordTags" ToRole="Resources"/>
					<NavigationProperty Name="Properties" Relationship="Ovodata.FK_KeywordTags__MapEntries" FromRole="KeywordTags" ToRole="_MapEntries"/>
*/
				break;
			}
			case PROJECT : {
				String URI = OData4JClientUtils.getStringProperty(entity, "URI");
				Project fromDb = getFromDB(URI);
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(URI, fromDb.getNotes(), 				OData4JClientUtils.getStringProperty(entity, "Notes"));
				Assert.assertEquals(URI, fromDb.getName(), 				OData4JClientUtils.getStringProperty(entity, "Name"));
//				Assert.assertEquals(URI, fromDb.getSerializedName(), 		OData4JClientUtils.getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(URI, fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(entity, "UUID"));
				Assert.assertEquals(URI, fromDb.getSerializedLocation(), 	OData4JClientUtils.getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(URI, fromDb.getPurpose(), 			OData4JClientUtils.getStringProperty(entity, "Purpose"));
				Assert.assertEquals(URI, fromDb.getStartTime(), 			OData4JClientUtils.getDateTimeProperty(entity, "StartTime"));
				Assert.assertEquals(URI, fromDb.getEndTime(), 			OData4JClientUtils.getDateTimeProperty(entity, "EndTime"));

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
				Resource fromDb = getFromDB(OData4JClientUtils.getStringProperty(entity, "URI"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getNotes(), 				OData4JClientUtils.getStringProperty(entity, "Notes"));
				Assert.assertEquals(fromDb.getName(), 				OData4JClientUtils.getStringProperty(entity, "Name"));
				JUnitUtils.assertEquals(fromDb.getData(), 			OData4JClientUtils.getByteArrayProperty(entity, "Data"));
//				Assert.assertEquals(fromDb.getSerializedName(), 	OData4JClientUtils.getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(), OData4JClientUtils.getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getUti(), 				OData4JClientUtils.getStringProperty(entity, "UTI"));
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
				Response fromDb = getFromDB(OData4JClientUtils.getStringProperty(entity, "URI"));
				Assert.assertNotNull(fromDb);
				
//				System.out.println("links - " + entity.getLinks());
/*				
links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Owner], 
			ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/NumericDataType,title=NumericDataType,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/NumericDataType], 
			ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ExternalDevice,title=ExternalDevice,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/ExternalDevice], 
			ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Epoch,title=Epoch,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Epoch], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/SamplingUnits,title=SamplingUnits,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/SamplingUnits], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/ResourceNames], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/SamplingRates,title=SamplingRates,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/SamplingRates], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/FloatingPointData,title=FloatingPointData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/FloatingPointData], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyTags], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/FloatData,title=FloatData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/FloatData], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DeviceParameters,title=DeviceParameters,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/DeviceParameters], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyAnnotationGroupTags], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Properties], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Tags], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyAnnotations], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Shape,title=Shape,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Shape], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyProperties], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyKeywordTags],
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MatlabShape,title=MatlabShape,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MatlabShape], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/IntData,title=IntData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/IntData], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/AnnotationGroupTags], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/IntegerData,title=IntegerData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/IntegerData], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DoubleData,title=DoubleData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/DoubleData], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/KeywordTags], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DimensionLabels,title=DimensionLabels,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/DimensionLabels], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Annotations], 
			ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Resources]]
*/
				
				// Properties
				Assert.assertEquals(fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(), OData4JClientUtils.getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getUnits(), 				OData4JClientUtils.getStringProperty(entity, "Units"));
				Assert.assertEquals(fromDb.getUTI(), 				OData4JClientUtils.getStringProperty(entity, "UTI"));
				Assert.assertEquals(fromDb.isIncomplete(), 			OData4JClientUtils.getBooleanProperty(entity, "IsIncomplete") == Boolean.TRUE);
				Assert.assertEquals(fromDb.getURIString(), 			OData4JClientUtils.getStringProperty(entity, "URI"));
				JUnitUtils.assertEquals(fromDb.getDataBytes(), 		OData4JClientUtils.getByteArrayProperty(entity, "Data"));
//				Assert.assertEquals(fromDb.getSerializedName(), 	OData4JClientUtils.getStringProperty(entity, "SerializedName"));
			
//				User owner2 = 
//				fromDb.getData();
//				fromDb.getDoubleData();
//				fromDb.getFloatData();
//				fromDb.getFloatingPointData();
//				fromDb.getIntegerData();

				// Associated objects
				Epoch 			epoch 	= fromDb.getEpoch();
				OEntity svcEpoch = getSubEntity(Entity.EPOCH, entity, "Epoch"); //[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Epoch,title=Epoch,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Epoch],
				System.out.println("epoch    = " + epoch);
				System.out.println("svcEpoch = " + svcEpoch);
				
				ExternalDevice 	dev 	= fromDb.getExternalDevice();
				User			owner 	= fromDb.getOwner(); 
				long[] mshape = fromDb.getMatlabShape();
				NumericDataType ndtype = fromDb.getNumericDataType();
				long[] shape = fromDb.getShape();
				
				
				// Collections
//				JUnitUtils.assertEquals(fromDb.getSamplingUnits(), 	OData4JClientUtils.getStringArrayProperty(entity, "SamplingUnits"));
//				JUnitUtils.assertEquals(fromDb.getSamplingRates(), 	OData4JClientUtils.getDoubleArrayProperty(entity, "SamplingRates"));
				fromDb.getDeviceParameters();
	        // TaggableEntityBase
				fromDb.getKeywordTags();
				fromDb.getMyKeywordTags();
				fromDb.getMyTags();
				fromDb.getTags();
	        // EntityBase
				fromDb.getMyProperties();		// String,Object
				fromDb.getProperties();			// String,Object[]
				fromDb.getResourcesIterable();

				break;
			}
			case SOURCE : {
				Source fromDb = getFromDB(OData4JClientUtils.getStringProperty(entity, "URI"));
				Assert.assertNotNull(fromDb);
//				Assert.assertEquals(fromDb.getSerializedName(), 	OData4JClientUtils.getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(),	OData4JClientUtils.getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getLabel(), 				OData4JClientUtils.getStringProperty(entity, "Label"));
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
				Stimulus fromDb = getFromDB(OData4JClientUtils.getStringProperty(entity, "URI"));
				Assert.assertNotNull(fromDb);
//				Assert.assertEquals(fromDb.getSerializedName(), 	OData4JClientUtils.getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUnits(), 				OData4JClientUtils.getStringProperty(entity, "Units"));
				Assert.assertEquals(fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(), OData4JClientUtils.getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getPluginID(), 			OData4JClientUtils.getStringProperty(entity, "PluginID"));
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
				URLResource fromDb = getFromDB(OData4JClientUtils.getStringProperty(entity, "URI"));
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getNotes(), 				OData4JClientUtils.getStringProperty(entity, "Notes"));
				Assert.assertEquals(fromDb.getName(), 				OData4JClientUtils.getStringProperty(entity, "Name"));
//				Assert.assertEquals(fromDb.getSerializedName(), 	OData4JClientUtils.getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(),	OData4JClientUtils.getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getUti(), 				OData4JClientUtils.getStringProperty(entity, "UTI"));
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
*/
		}
	}
}
