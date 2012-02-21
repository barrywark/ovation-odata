package ovation.odata.service;

import java.util.Properties;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;

import org.apache.log4j.Logger;
import org.odata4j.repack.org.apache.commons.codec.binary.Base64;

import ovation.DataContext;
import ovation.OvationException;
import ovation.UserAuthenticationException;
import ovation.odata.util.DataContextCache;
import ovation.odata.util.PropertyManager;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * this pre and post filter is responsible for making sure the user is authenticated
 * and that the proper DataContext is attached to the processing thread on the way in
 * and detached from it on the way out.
 * NOT USED AT THIS TIME - JerseyAuthenticator is used instead for stand-alone servers
 * @author Ron
 *
 */
public class AuthenticationFilter implements ContainerRequestFilter, ContainerResponseFilter {
	public static final Logger _log = Logger.getLogger(AuthenticationFilter.class);
	static final Properties _props = PropertyManager.getProperties(AuthenticationFilter.class);
	
	static final String TEST_USER			= _props.getProperty("ovodata.Auth.dbTestUser");
	static final String TEST_PASSWORD		= _props.getProperty("ovodata.Auth.dbTestPassword");
	static final String AUTH_COOKIE_NAME	= _props.getProperty("ovodata.Auth.cookieName", "authToken");
	static final String AUTH_COOKIE_PATH	= _props.getProperty("ovodata.Auth.cookiePath", "/");
	static final String AUTH_COOKIE_DOMAIN  = _props.getProperty("ovodata.Auth.cookieDomain");
	static final int	AUTH_COOKIE_EXP_SEC = Integer.parseInt(_props.getProperty("ovodata.Auth.cookieExpSec", "1800"));	// 1800 sec = 30 min
	
	static final String AUTH_STATE_KEY		= "AuthState";
	enum AuthState {
		UNAUTHENTICATED, AUTHENTICATED, AUTH_FAILURE, SERVER_FAILURE
	}
	
	public ContainerRequest filter(ContainerRequest req) {
		_log.debug("in-coming - " + req);
		
/*

there are 3 cases to handle - the unauthenticated user, the user attempting authentication, and the authorized user.

unauthenticated would mean no token cookie and no Authorization header which means the response should be a 401 with WWW-Authenticate header 
(probably Basic because i don't know the user's password - it's the Ovation.create() method's responsibility to authenticate (and authorize)
 the user (at least as i understand things)).  

the user attempting authentication then re-requests the URI with Authorization header 
(a base64-encoded user + ":" + password string which is why to make this even reasonably secure must be run over https).  
the server decodes this and attempts to create the DataContext for the user/password combo.  
if that succeeds then the user is authorized and a nonce key is issued (basically a random number 
tho it can certainly be a deterministic MD5 if you like, but that doesn't make it any more random).  
this key is then stored in a cookie and used to look up the DataContext going forward (until it times out in 30 minutes).  
there's currently no logout planned but that's certainly easy to implement (force the expiration of the DataContext from the cache).
 */
		Cookie authKeyCookie = req.getCookies().get(AUTH_COOKIE_NAME);
		String authKey 		= authKeyCookie != null ? authKeyCookie.getValue() : null;
		if (authKey != null) {
			_log.debug("received auth key '" + authKey + "'");
		}
		DataContext ctx 	= null; // FIXME DataContextCache.getDataContext(authKey);
		if (ctx == null) {
			_log.info("User not yet authenticated (auth-token:" + authKey + ")");

			// check for auth attempt
			String authHeader 	= req.getHeaderValue(HttpHeaders.WWW_AUTHENTICATE);
			String userName 	= TEST_USER;
			String password 	= TEST_PASSWORD;
			if (authHeader != null) {
				_log.debug("attempt to authenticate");
				req.getProperties().put(AUTH_STATE_KEY, AuthState.AUTH_FAILURE);
				
				byte[] decodedBytes = Base64.decodeBase64(authHeader);
				if (decodedBytes != null) {
					String decodedString = new String(decodedBytes);
					int delim = decodedString.indexOf(':');
					if (delim != -1) {
						userName = decodedString.substring(0, delim);
						password = decodedString.substring(delim + 1);
					} else {
						_log.error("failed to find delimiter in '" + decodedString + "'");
					}
				} else {
					_log.error("failed to decode '" + authHeader + "'");
				}
			} else {
				_log.debug("no auth header provided");
				req.getProperties().put(AUTH_STATE_KEY, AuthState.UNAUTHENTICATED);
			}
			
			if (userName == null || password == null) {
				_log.debug("sending auth challenge");
				req.getProperties().put(AUTH_STATE_KEY, AuthState.UNAUTHENTICATED);
				return null;	// prevent processing of this request
			}
			
			if (userName != null && password != null) {
				// FIXME - can't do this here - cookies must be in the response and Jersey requests don't have attributes. :-/
				authKey = userName + password;	// HACK - easy to figure out at response time, tho - could MD5 this...
				// this can't be random - it has to be deterministic to the user until auth is fully worked out - otherwise 1 user will create many contexts
				// FIXME - but if 1 user has 1 context then how to handle simultaneous requests by a single user?
				try {
					ctx = DataContextCache.getDataContext(userName, password, authKey);
					if (ctx != null) {
						// user authenticated
						req.getProperties().put(AUTH_STATE_KEY, AuthState.AUTHENTICATED);
						req.getProperties().put(AUTH_COOKIE_NAME, authKey);
					}
					_log.debug("got ctx " + ctx + " for user " + userName);
				} catch (UserAuthenticationException uax) {
					_log.error("auth failure (403?) - " + uax, uax);
					req.getProperties().put(AUTH_STATE_KEY, AuthState.AUTH_FAILURE);
				} catch (OvationException ox) {
					_log.error("some other Ovation error - " + ox, ox);
					req.getProperties().put(AUTH_STATE_KEY, AuthState.SERVER_FAILURE);
				}
			} else {
				_log.debug("userName '" + userName + "'/password '" + password + "' still null");
			}
		} else {
			_log.debug("found context with authKey '" + authKey + "'");
			req.getProperties().put(AUTH_STATE_KEY, AuthState.AUTHENTICATED);
		}
			
		if (ctx != null) {
			_log.debug("set context on thread");
// FIXME			DataContextCache.setThreadContext(ctx);
			// keep request going
			return req;
		} else {
			_log.debug("ctx == null - don't process request");
		}

		return null;
	}

	public ContainerResponse filter(ContainerRequest req, ContainerResponse res) {
		DataContext ctx = DataContextCache.getThreadContext();
		// auth needed - res.setStatus(401); w/ challenge of WWW-Auth: Basic
		_log.debug("out-going- " + req + ", " + res);
		// always detach the context from the thread to avoid leaks and other bad stuff
		DataContextCache.closeThreadContext();
		
		AuthState authState = (AuthState)req.getProperties().get(AUTH_STATE_KEY);
		switch (authState) {
			case AUTHENTICATED:
				_log.debug("authenticated req/res done.");
				String authKey = (String)req.getProperties().get(AUTH_COOKIE_NAME);
				if (authKey != null) {
					_log.debug("sending auth cookie");
					res.getHttpHeaders().add(HttpHeaders.SET_COOKIE, 
							AUTH_COOKIE_NAME + "=" + authKey);	// FIXME - for now it's a session cookie locked to this host + path
//					Set-Cookie: <name>=<value>[; <name>=<value>]... [; expires=<date>][; domain=<domain_name>] [; path=<some_path>][; secure][; httponly]
				}
				break;
			case AUTH_FAILURE:
				_log.debug("failed auth attempt");
				res.setStatus(403);
				break;
			case UNAUTHENTICATED:
				_log.debug("unauthenticated - sending challenge");
				res.setStatus(401);
				res.getHttpHeaders().add(HttpHeaders.WWW_AUTHENTICATE, "Basic");
				break;
			case SERVER_FAILURE:
				res.setStatus(500);
				break;
		}
		
		return res;
	}
}
