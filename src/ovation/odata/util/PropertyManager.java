package ovation.odata.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;

public class PropertyManager {
	public static final Logger _log = Logger.getLogger(PropertyManager.class);
	private static final HashMap<Class<?>, Properties> _propertiesMap = Maps.newHashMap();
	static {
		setProperties(null, System.getProperties());	// start with system props
	}
	
	/**
	 * didn't see much point in synchronizing this method - granted, it's wasted work if you have
	 * 2 threads requesting the same Properties for the first time but they should both arrive at
	 * the same result and once it's generated it won't be again.
	 * @param clazz
	 * @return the Properties object for the specified class
	 */
	public static Properties getProperties(Class<?> clazz) {
		Properties props = _propertiesMap.get(clazz);
		if (props == null) {
			props = _propertiesMap.get(null);	// start with system (or default) props
			// build property keys starting from "props" until the full class-name is reached
			// this allows for stacking/overwriting of props at the package and class level
			// note, it has the down-side of probably storing many Properties objects with identical values
			// since each is linked at the leaf level (rather than building a more complex hierarchy
			// with data points at the interior nodes like Log4j does)
			String className = clazz.getName() + ".";	// last dot added to make code below simpler
			String propName = "props";
			int nextDot = 0;
			while (true) {
				_log.debug(propName);
				String propFile = System.getProperty(propName, null);
				if (propFile != null) {
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(propFile);
						props.load(fis);
					} catch (IOException iox) {
						_log.warn("failed to load properties from '" + propFile + "' (" + propName + ") - " + iox, iox);
					} finally {
						try { fis.close(); } catch (Exception ignore) {}
					}
				}
				nextDot = className.indexOf('.', nextDot + 1);
				if (nextDot == -1) {
					break;
				}
				propName = className.substring(0, nextDot) + ".props";
			}
			setProperties(clazz, props);
		}
		return props;
	}
	
	public static void setProperties(Class<?> clazz, Properties props) {
		_propertiesMap.put(clazz, props);
	}
}
