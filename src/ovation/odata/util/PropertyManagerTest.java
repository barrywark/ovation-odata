package ovation.odata.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import ovation.odata.util.PropertyManager;
import junit.framework.TestCase;

public class PropertyManagerTest extends TestCase {
	static final String BASE_PROPS 		= "prop1 = base-val1\n prop2 = base-val2\n prop3=base-val3\n prop4=base-val4\n prop5=base-val5\n prop6=base-val6\n";
	static final String OVATION_PROPS 	= "prop1 = ovat-val1\n prop2 = ovat-val2\n prop3=ovat-val3\n prop4=ovat-val4\n prop5=ovat-val5\n";
	static final String ODATA_PROPS 	= "prop1 = odat-val1\n prop2 = odat-val2\n prop3=odat-val3\n prop4=odat-val4\n";
	static final String UTIL_PROPS 		= "prop1 = util-val1\n prop2 = util-val2\n prop3=util-val3\n";
	static final String PROPMAN_PROPS 	= "prop1 = prop-val1\n prop2 = prop-val2\n";
	
	static File createTempFile(String prefix, String suffix, String content) throws IOException {
		File file = File.createTempFile(prefix, suffix);
		file.deleteOnExit();
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(content.toCharArray());
		} finally {
			try { writer.flush(); writer.close(); } catch (Exception ignore) {}
		} 
		return file;
	}
	
	public static void testPropLoading() throws Exception {
		File basePropFile 	= createTempFile("base", 	".props", BASE_PROPS);
		File ovationPropFile= createTempFile("ovation", ".props", OVATION_PROPS);
		File odataPropFile	= createTempFile("odata", 	".props", ODATA_PROPS);
		File utilPropFile	= createTempFile("util", 	".props", UTIL_PROPS);
		File propManPropFile= createTempFile("propMan", ".props", PROPMAN_PROPS);

		// set them so they can be picked up by PropertyManager
		System.setProperty("props", 										basePropFile.getAbsolutePath());
		System.setProperty("ovation.props", 								ovationPropFile.getAbsolutePath());
		System.setProperty("ovation.odata.props", 							odataPropFile.getAbsolutePath());
		System.setProperty("ovation.odata.util.props", 						utilPropFile.getAbsolutePath());
		System.setProperty("ovation.odata.util.PropertyManagerTest.props", 	propManPropFile.getAbsolutePath());

		Properties props = PropertyManager.getProperties(PropertyManagerTest.class);
		
		assertEquals("base-val6", props.getProperty("prop6"));
		assertEquals("ovat-val5", props.getProperty("prop5"));
		assertEquals("odat-val4", props.getProperty("prop4"));
		assertEquals("util-val3", props.getProperty("prop3"));
		assertEquals("prop-val2", props.getProperty("prop2"));
		assertEquals("prop-val1", props.getProperty("prop1"));
		
		System.out.println("combined props - " + props);
	}
}
