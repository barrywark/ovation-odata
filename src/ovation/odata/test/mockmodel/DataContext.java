package ovation.odata.test.mockmodel;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;


/** mock DataContext (unfortunately can't extend from REAL DataContext - need to FIXME */
public class DataContext {
	public static final Logger _log = Logger.getLogger(DataContext.class);
	
	private HashMap<String, List<Project>> _projectsByName  = Maps.newHashMap();
	private HashMap<String, List<Source>>	_sourcesByLabel = Maps.newHashMap();

	// made-up data
	private Project[] _projects = new Project[] {
		new Project("fred"), 
		new Project("barney"), 
		new Project("wilma"), 
		new Project("betty"), 
	};
	
	private Source[] _sources = new Source[] {
			new Source(), new Source(), new Source(),
	};
	private KeywordTag[] _tags = new KeywordTag[] {
			new KeywordTag("key1", "val1"), new KeywordTag("key2", "val2"),
	};
	
	/** Gets all of the objects in the Ovation database tagged with the given tag. */
	public TaggableEntityBase[]	getAllTaggedObjects(String tag) {
		return null; // TODO
	}
	
 /** Gets all the KeywordTags available in the database */ 
	public KeywordTag[]	getAllTags() { return _tags; }
	
 /** Searches the authenticated user's database for a query with the given name. */
	public SavedQuery getMyQuery(String name) {
		return null;	// TODO
	}
	
 /** Gets the Projects in the Ovation database, sorted by startTime. */
	public Project[] getProjects() { return _projects; }
	
 /** Gets Projects in the Ovation database with the given name, sorted by startTime. */
	public Project[] getProjects(String projectName) {
		List<Project> projects = _projectsByName.get(projectName);
		return projects != null ? projects.toArray(new Project[projects.size()]) : new Project[0];
	}
	/** Gets all SavedQueries with the given name. */
	public SavedQuery[]	getQueries(String name) {
		return null;	// TODO
	}
  
 /** Gets the Sources in the Ovation database, sorted by their labels. */
	public Source[]	getSources() {
		return _sources; 
	}
	
	/** Gets the Sources in the Ovation database with the given name, sorted by their labels. */
	public Source[]	getSources(String sourceLabel) { 
		return _sources; // TODO
	} 
  
	/** Gets all of the objects in the Ovation database tagged by the current user with the given tag. */ 
	public TaggableEntityBase[]	getTaggedObjects(String tag) {
		return null;	// TODO
	}
 
	/** Returns an array of the unique tags owned by the current User. */
	public KeywordTag[]	getTags() {
		return _tags;
	}
	
	public void close() { _log.debug("DataContext.close()"); }
}