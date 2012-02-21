package ovation.odata.service;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import ovation.OvationException;
import ovation.UserAuthenticationException;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

/**
 * this class ensures the user is authenticated, authenticates them if not, and 
 * ensures that a DataContext is on the thread for the authenticated users that
 * matches their credentials.  Used by stand-alone Jersey servers only.
 * 
 * @author Ron
 */
public class JerseyAuthenticator extends Authenticator {
	public static final Logger _log = Logger.getLogger(JerseyAuthenticator.class);
	
	public Result authenticate(HttpExchange xchng) {
		try {
			Headers requestHeaders = xchng.getRequestHeaders();
			String authHeader = requestHeaders.getFirst(HttpHeaders.AUTHORIZATION);
			if (authHeader != null) {
				// auth attempt
				_log.debug("attempt to authenticate '" + authHeader + "'");
				try {
					String[] usernamePasswordArray = AuthUtil.parseBasicAuthHeader(authHeader);
					String userName = usernamePasswordArray[0];
					String password = usernamePasswordArray[1];
					try {
						if (AuthUtil.authenticateUser(userName, password)) {
							return new Authenticator.Success(new HttpPrincipal(userName, "ovodata"));
						}
					} catch (UserAuthenticationException uax) {
					} catch (OvationException ox) {
						// FIXME - special-case for change in auth failure in DataContext.authenticateUser()
						if ("Incorrect password".equals(ox.getMessage()) == false) {
							throw ox;
						}
					}
					_log.info("failed to authenticate user '" + userName + "'");
				} catch (IllegalArgumentException iax) {
					_log.error(iax.toString());
				}
			} else {
				_log.debug("no auth header provided");
			}
			xchng.getResponseHeaders().add(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"ovodata\"");
			return new Authenticator.Failure(Response.Status.UNAUTHORIZED.getStatusCode());
		} catch (Exception ex) {
			_log.error(ex, ex);
			xchng.getResponseHeaders().add("Warning", ex.toString());
			return new Authenticator.Failure(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
	}
}
