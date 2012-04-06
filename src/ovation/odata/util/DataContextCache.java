package ovation.odata.util;

import java.util.Properties;

import org.apache.log4j.Logger;

import ovation.DataContext;
import ovation.DataStoreCoordinator;
import ovation.Ovation;
import ovation.OvationException;
import ovation.UserAuthenticationException;

import com.objy.db.DatabaseNotFoundException;
import com.objy.db.DatabaseOpenException;

/**
 * wrapper around Ovation DataContext management
 * 
 * configured via the props:<ul>
 *  <li>ovodata.DataContext.file       		- default Objectivity/DB data file</li>
 *  <li>ovodata.DataContext.file.[user]		- optional user-specific override for default data file</li>
 * </ul>
 * 
 * @author Ron
 */
public class DataContextCache {
	public  static final Logger 	_log = Logger.getLogger(DataContextCache.class);
	private static final Properties _props = PropertyManager.getProperties(DataContextCache.class);

	/** allows DataContexts to be associated with a specific (and single) Thread */
	private static final ThreadLocal<DataContext> _threadContext = new ThreadLocal<DataContext>();
	
	/**
	 * gets the DataContext associated with the calling thread (if any) - this allows the DataContext to be
	 * associated with a thread in the authentication layer but be retrievable by any code invoked by that
	 * thread without having to pass it as an argument.
	 * @return the calling Thread's DataContext or null if none is associated with the Thread
	 */
	public static DataContext getThreadContext() { 
		return _threadContext.get(); 
	}

	/**
	 * close the current context associated with this thread (replaces setThreadContext(null))	
	 */
	public static void closeThreadContext() {
		DataContext ctx = _threadContext.get();
		if (ctx != null) {
			if (_log.isDebugEnabled()) {
				_log.debug("closing context " + ctx);
			}
			ctx.logout();
			_threadContext.remove();
		} else {
			_log.debug("no context found to close on this thread");
		}
	}

	/**
	 * create a new DataContext, closing and overwriting the old if any, for the given key.
	 * 
	 * @param uid 		user ID
	 * @param pwd 		password
	 * @param filePath	path to Objectivity/DB database file
	 * @return newly-created DataContext
	 * @throws OvationException
	 * @throws UserAuthenticationException
	 */
	public synchronized static DataContext getDataContext(String uid, String pwd, String filePath) 
			throws OvationException, UserAuthenticationException {
		try {
			// get the DataStore coordinator for the specified database file (never returns null)
			DataStoreCoordinator dsCoordinator = DataStoreCoordinator.coordinatorWithConnectionFile(filePath);
			// get DataContext associated with this worker thread (never returns null)
			DataContext ctx = dsCoordinator.getContext();
			// returns boolean but always true; throws UserAuthException instead of returning false
			if (ctx.authenticateUser(uid, pwd) == false || ctx.currentAuthenticatedUser() == null) {	// check it anyway just to be safe
				throw new UserAuthenticationException("user " + uid + " failed to authenticate");
			}
			if (_log.isDebugEnabled()) {
				if (_threadContext.get() != ctx) {
					_log.debug("changing thread context from " + _threadContext.get() + " to " + ctx);
					// FIXME - should we log the previous context out?
				} else {
					_log.debug("thread context unchanged - " + ctx);
				}
			}
			_threadContext.set(ctx);
			return ctx;
		} catch (OvationException ox) {
			// FIXME - special-case for change in auth failure in DataContext.authenticateUser()
			if ("Incorrect password".equals(ox.getMessage())) {
				throw new UserAuthenticationException(ox.getMessage());
			}
			throw ox;
		} catch (DatabaseOpenException e) {
			throw new OvationException(e.getMessage());
		} catch (DatabaseNotFoundException e) {
			throw new OvationException(e.getMessage());
		}
	}

		
	/**
	 * create a DataContext and authenticate the provided user credentials
	 * @param uid
	 * @param pwd
	 * @return a DataContext authenticated with the provided user credentials
	 * @throws OvationException if any other error occurs while talking to Ovation
	 * @throws UserAuthenticationException if the user fails to authenticate with pwd
	 */
	public static DataContext getDataContext(String uid, String pwd) throws OvationException, UserAuthenticationException {
		String filePath = getODBFilePath(uid);
		if (filePath == null) {
			throw new OvationException("not properly configured - no default file path specified (ovodata.DataContext.file)");
		}
		return getDataContext(uid, pwd, filePath);
	}

	/**
	 * @param uid - user ID
	 * @return user-specific "ovodata.DataContext.file.<uid>" if found, otherwise "ovodata.DataContext.file" or null
	 */
	public static String getODBFilePath(String uid) {
		return _props.getProperty(Props.DC_FILE_BASE + uid, _props.getProperty(Props.DC_FILE_DEFAULT, null));
	}
	
	public static void close() {
		// there doesn't seem to be any methods/requirement for shutting down the Ovation API
	}
}
