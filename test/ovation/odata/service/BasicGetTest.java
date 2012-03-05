package ovation.odata.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.core4j.Enumerable;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.EntitySetInfo;
import org.odata4j.core.OClientBehaviors;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityGetRequest;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OProperty;
import org.odata4j.core.OQueryRequest;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
import ovation.odata.util.DataContextCache;
import ovation.odata.util.OvationDBTestHelper;

public class BasicGetTest {
	static final String SERVICE_URL 	= System.getProperty("ovodataUrl",      "http://localhost:8080/ovodata/Ovodata.svc/");
	static final String USERNAME 		= System.getProperty("ovationUser", 	"ron");
	static final String PASSWORD 		= System.getProperty("ovationPassword", "passpass1");
	static final String DB_CON_FILE		= System.getProperty("ovationDbFile",   "/var/lib/ovation/db/dev.connection");
	
	private static ODataConsumer 	_odataClient; 
	private static DataContext 		_dbContext;
	
	@BeforeClass 
	public static void beforeAllTests() throws Throwable {
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
		List<Experiment> allFromDb = getAllFromDB(Experiment.class);	
		Enumerable<OEntity> allFromService = getAllEntities(Entity.EXPERIMENT);
		
		Assert.assertEquals(allFromDb.size(), allFromService.count());

		for (OEntity entity : allFromService) {
			compareToDb(Entity.EXPERIMENT, entity);
		}
	}
	
	@Test
	public void testGetAllEpochGroups() throws Throwable {
		List<EpochGroup> allFromDb = getAllFromDB(EpochGroup.class);	
		Enumerable<OEntity> allFromService = getAllEntities(Entity.EPOCH_GROUP);

		// TODO - assert same length and for each in 1 group assert it exists in other group
		
	}


	@Test
	public void testGetAllAnalysisRecords() throws Throwable {
		List<AnalysisRecord> allFromDb = getAllFromDB(AnalysisRecord.class);	
		Enumerable<OEntity> allFromService = getAllEntities(Entity.ANALYSIS_RECORD);

		// TODO - assert same length and for each in 1 group assert it exists in other group
		
	}

	@Test
	public void testGetAllResponses() throws Throwable {
		List<Response> allFromDb = getAllFromDB(Response.class);	
		Enumerable<OEntity> allFromService = getAllEntities(Entity.RESPONSE);

		// TODO - assert same length and for each in 1 group assert it exists in other group
		
	}

	@Test
	public void testGetAllExternalDevices() throws Throwable {
		List<ExternalDevice> allFromDb = getAllFromDB(ExternalDevice.class);	
		Enumerable<OEntity> allFromService = getAllEntities(Entity.EXTERNAL_DEVICE);

		// TODO - assert same length and for each in 1 group assert it exists in other group
		
	}

	@Test
	public void testGetAllSources() throws Throwable {
		List<Source> allFromDb = getAllFromDB(Source.class);	
		Enumerable<OEntity> allFromService = getAllEntities(Entity.SOURCE);

		// TODO - assert same length and for each in 1 group assert it exists in other group
		
	}

	@Test
	public void testGetAllDerivedResponses() throws Throwable {
		List<DerivedResponse> allFromDb = getAllFromDB(DerivedResponse.class);	
		Enumerable<OEntity> allFromService = getAllEntities(Entity.DERIVED_RESPONSE);

		// TODO - assert same length and for each in 1 group assert it exists in other group
		
	}

	@Test
	public void testGetAllProjects() throws Throwable {
		List<Project> allFromDb = getAllFromDB(Project.class);	
		Enumerable<OEntity> allFromService = getAllEntities(Entity.PROJECT);

		// TODO - assert same length and for each in 1 group assert it exists in other group
		
	}

	@Test
	public void testGetAllURLResources() throws Throwable {
		List<URLResource> allFromDb = getAllFromDB(URLResource.class);	
		Enumerable<OEntity> allFromService = getAllEntities(Entity.URL_RESOURCE);

		// TODO - assert same length and for each in 1 group assert it exists in other group
		
	}

	@Test
	public void testGetAllStimuli() throws Throwable {
		List<Stimulus> allFromDb = getAllFromDB(Stimulus.class);	
		Enumerable<OEntity> allFromService = getAllEntities(Entity.STIMULUS);

		// TODO - assert same length and for each in 1 group assert it exists in other group
		
	}

	@Test
	public void testGetAllKeywordTags() throws Throwable {
		List<KeywordTag> allFromDb = getAllFromDB(KeywordTag.class);	
		Enumerable<OEntity> allFromService = getAllEntities(Entity.KEYWORD_TAG);

		// TODO - assert same length and for each in 1 group assert it exists in other group
		
	}

	@Test
	public void testGetAllEpochs() throws Throwable {
		List<Epoch> allFromDb = getAllFromDB(Epoch.class);	
		Enumerable<OEntity> allFromService = getAllEntities(Entity.EPOCH);

		// TODO - assert same length and for each in 1 group assert it exists in other group
		
	}

	@Test
	public void testGetAllResources() throws Throwable {
		List<Resource> allFromDb = getAllFromDB(Resource.class);	
		Enumerable<OEntity> allFromService = getAllEntities(Entity.RESOURCE);

		// TODO - assert same length and for each in 1 group assert it exists in other group
		
	}

	
	static OEntity getEntity(Entity type, String entityId) {
		OEntityGetRequest<OEntity> req = _odataClient.getEntity(type._setName, OEntityKey.create(entityId));
		return req.execute();
	}
	
	static Enumerable<OEntity> getAllEntities(Entity type) {
		OQueryRequest<OEntity> req = _odataClient.getEntities(type._setName);
		return req.execute();
	}
	
	static <T> List<T> getAllFromDB(Class<T> type) {
		return Lists.newArrayList(_dbContext.query((Class)type, "true"));
	}
	
	static <T> T getFromDB(String uri) {
    	return (T)_dbContext.objectWithURI(uri);		
	}
	static String getStringProperty(OEntity entity, String name) 		{ return  entity.getProperty(name, String.class).getValue(); }
	static DateTime getDateTimeProperty(OEntity entity, String name) 	{ return  entity.getProperty(name, DateTime.class).getValue(); }
	
	static void compareToDb(Entity type, OEntity entity) {
		// TODO
		switch(type) {
			case EXPERIMENT :
				String uri = getStringProperty(entity, "URIString");
				Experiment fromDb = getFromDB(uri);
				Assert.assertNotNull(fromDb);
				Assert.assertEquals(fromDb.getNotes(), 				getStringProperty(entity, "Notes"));
				Assert.assertEquals(fromDb.getSerializedName(), 	getStringProperty(entity, "SerializedName"));
				Assert.assertEquals(fromDb.getUuid(), 				getStringProperty(entity, "UUID"));
				Assert.assertEquals(fromDb.getSerializedLocation(), getStringProperty(entity, "SerializedLocation"));
				Assert.assertEquals(fromDb.getStartTime(), 			getDateTimeProperty(entity, "StartTime"));
				Assert.assertEquals(fromDb.getEndTime(), 			getDateTimeProperty(entity, "EndTime"));
				Assert.assertEquals(fromDb.getPurpose(), 			getStringProperty(entity, "Purpose"));
				//fromDb.getAnnotationGroupTags()... sub-entities
				break;
			case ANALYSIS_RECORD :
			case DERIVED_RESPONSE :
			case EPOCH :
			case EPOCH_GROUP :
			case EXTERNAL_DEVICE :
			case KEYWORD_TAG :
			case PROJECT :
			case RESOURCE :
			case RESPONSE :
			case SOURCE :
			case STIMULUS :
			case URL_RESOURCE :
		}
	}
}
