package ovation.odata.service.servlet;

import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import ovation.odata.util.CollectionUtils;
import ovation.odata.util.DataContextCache;
import ovation.odata.util.PropertyManager;
import ovation.odata.util.Props;

/**
 * an instance of this class ensures proper setup and tear-down of the web-application's 
 * properties and other singletons needed to function.
 * @author Ron
 */
public class WebappListener implements ServletContextListener {
	public static final Logger _log = Logger.getLogger(WebappListener.class);
	
	public void contextInitialized(ServletContextEvent event) {
		_log.info("context started");
		ServletContext ctx = event.getServletContext();
		Properties rootProps = PropertyManager.getProperties(null);
		for (String name : CollectionUtils.makeIterable(ctx.getInitParameterNames())) {
			rootProps.setProperty(name, ctx.getInitParameter(name));
		}
		// check for props file prop and load that as well
		String propFilePath = rootProps.getProperty("ovodata.props");
		if (propFilePath != null) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(propFilePath);
				rootProps.load(fis);
			} catch (Exception ex) {
				_log.warn("failed to load '" + propFilePath + "'");
			} finally {
				try { fis.close(); } catch (Exception ignore) {}
			}
		}
		
		String dbFile = System.getenv("OVODATA_CONNECTION_FILE");
		if (dbFile != null) {
			rootProps.setProperty(Props.DC_FILE_DEFAULT, dbFile);
		}
		if (_log.isDebugEnabled()) {
			_log.debug("root props : " + rootProps);
		}
	}
	public void contextDestroyed(ServletContextEvent event) {
		_log.info("context stopped");
	}
}
