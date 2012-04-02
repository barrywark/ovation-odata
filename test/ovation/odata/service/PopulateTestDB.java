package ovation.odata.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import org.joda.time.DateTime;

import ovation.DataContext;
import ovation.OvationException;
import ovation.Project;
import ovation.UserAuthenticationException;
import ovation.odata.util.OvationDBTestHelper;
import ovation.odata.util.OvationDBTestHelper.DeviceData;
import ovation.odata.util.OvationDBTestHelper.EpochData;
import ovation.odata.util.OvationDBTestHelper.EpochGroupData;
import ovation.odata.util.OvationDBTestHelper.ExperimentData;
import ovation.odata.util.OvationDBTestHelper.ProjectData;
import ovation.odata.util.OvationDBTestHelper.ResponseData;
import ovation.odata.util.OvationDBTestHelper.SourceData;
import ovation.odata.util.OvationDBTestHelper.StimulusData;

import com.google.common.collect.Lists;

public class PopulateTestDB {

	// disabled so it doesn't keep inserting new records every time
	public static void insertTestData() throws OvationException, UserAuthenticationException {
		// insert project into DB
		List<ProjectData> projects = Lists.newArrayList();
		projects.add(new ProjectData().name("Test Project 3").purpose("test OvOData service")
						.add(new ExperimentData().end(new DateTime())
								.add(new DeviceData().name("Device 1").manufacturer("Initech"))
								.add(new DeviceData().name("Device 42").manufacturer("Initrobe"))
								.add(new DeviceData().name("Probe 100").manufacturer("ProbieTech"))
								.add(new SourceData().label("Source 1")
									.add(new EpochGroupData().label("epoch group 1").end(new DateTime())
										.add(new EpochData()
												.protocolId("insertionStress")
												.param("key1", Double.valueOf(1.0))
												.param("epochNumber", Integer.valueOf(1))
												.tag("howdy!")
												.tag("snow is cold")
												.addPair("Device 1", new StimulusData(), new ResponseData())
												.addPair("Device 42",
														new StimulusData().devParam("theAnswer", Integer.valueOf(42)) .param("theQuestion", "Life, the Universe, and Everything"), 
														new ResponseData().data(new double[]{42}, "none"))
										)
									)
								)
						)
		);
		
		DataContext context = null;
		try {
			context = OvationDBTestHelper.getContext(BasicGetTest.USERNAME, BasicGetTest.PASSWORD, BasicGetTest.DB_CON_FILE);
			OvationDBTestHelper.insertFixture(context, projects);
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}
	
	public static void dumpDBToString(Writer writer) throws OvationException, UserAuthenticationException, IOException {
		DataContext context = null;
		try {
			context = OvationDBTestHelper.getContext(BasicGetTest.USERNAME, BasicGetTest.PASSWORD, BasicGetTest.DB_CON_FILE);
			Project[] projects = context.getProjects();
			for (Project project : projects) {
				writer.write(project + "\n");
			}
			
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			insertTestData();
			System.out.println("inserting done");
			PrintWriter outWriter = new PrintWriter(System.out);
			try {
				dumpDBToString(outWriter);
			} finally {
				outWriter.close();
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
