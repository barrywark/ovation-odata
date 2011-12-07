package ovation.odata.test;

import org.apache.log4j.Logger;

import ovation.AnalysisRecord;
import ovation.DataContext;
import ovation.Experiment;
import ovation.Ovation;
import ovation.OvationException;
import ovation.Project;
import ovation.SavedQuery;
import ovation.SerializedObject;
import ovation.UserAuthenticationException;

 
//From there, you're off to the races. You can add a Project with context.insertProject(...), query the database with context.query(...), etc. The Ovation Guide.pdf (in C:\Program Files\Physion\Ovation\docs) is the place to start.

public class Basic {
	public static final Logger logger = Logger.getLogger(Basic.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final String dbFilePath = "C:/dev/ovation-db/dev.connection";
		final String dbUser 	= "ron";
		final String dbPassword =  "password"; 

		DataContext context = null;
		try {
			context = Ovation.connect(dbFilePath, dbUser, dbPassword);
			{
				String queryName = null;
				SavedQuery query = context.getMyQuery(queryName);
			}
			{
				context.getAllTaggedObjects("");
				context.getAllTags();
				context.getProjects("");
				context.getQueries("");
				context.getSources();
				context.getSources("");
				context.getTaggedObjects("");
				context.getTags();

				Project[] projects = context.getProjects();
				for (Project project : projects) {
					Experiment[] experiments = project.getExperiments();
					for (Experiment e : experiments) {
						SerializedObject obj = e.tohdf5();
					}
				}
			}
		} catch (OvationException e) {
			logger.fatal(e.toString(), e);
			e.printStackTrace();
		} catch (UserAuthenticationException e) {
			logger.fatal(e.toString(), e);
		} finally {
			try { context.close(); } catch (Exception ignore) {}
		}
	}
}
