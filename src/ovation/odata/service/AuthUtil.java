package ovation.odata.service;

import org.apache.log4j.Logger;
import org.odata4j.repack.org.apache.commons.codec.binary.Base64;

import ovation.DataContext;
import ovation.OvationException;
import ovation.UserAuthenticationException;
import ovation.odata.util.DataContextCache;

/**
 * a set of utility methods common to both Jersey and Filter authenticators
 * @author Ron
 *
 */
public final class AuthUtil {
	private static final Logger _log = Logger.getLogger(AuthUtil.class);
	
	private AuthUtil() {}
	
	/**
	 * authenticate the provided user credentials against the Ovation API (and thus the Objectivity-DB)
	 * @param user
	 * @param password
	 * @return
	 * @throws OvationException
	 * @throws UserAuthenticationException
	 */
	public static boolean authenticateUser(String user, String password) throws OvationException, UserAuthenticationException {
		DataContext ctx = DataContextCache.getDataContext(user, password);

		if (_log.isDebugEnabled()) {
			_log.debug("got context " + ctx + " for '" + user + "'");
		}
		
		return ctx != null;
	}
	
	/**
	 * parse a HTTP-Basic Auth header (base-64 encoded username:password pair)
	 * @param authHeader
	 * @return String[]{username, password} (never returns null or empty array)
	 * @throws IllegalArgumentException if the header is malformed (fails to base-64 decode or doesn't contain ':' separator
	 */
	public static String[] parseBasicAuthHeader(String authHeader) throws IllegalArgumentException {
		if (authHeader.startsWith("Basic ")) {
			authHeader = authHeader.substring("Basic ".length());
		}
		byte[] decodedBytes = Base64.decodeBase64(authHeader);
		if (decodedBytes != null) {
			String decodedString = new String(decodedBytes);
			_log.info("decoded '" + decodedString + "'");
			int delim = decodedString.indexOf(':');
			if (delim != -1) {
				String userName = decodedString.substring(0, delim);
				String password = decodedString.substring(delim + 1);
				return new String[]{userName, password};
			} else {
				throw new IllegalArgumentException("failed to find delimiter in '" + decodedString + "'");
			}
		} else {
			throw new IllegalArgumentException("failed to decode '" + authHeader + "'");
		}
	}
}
