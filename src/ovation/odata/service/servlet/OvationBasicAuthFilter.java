package ovation.odata.service.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.apache.log4j.Logger;

import ovation.OvationException;
import ovation.UserAuthenticationException;
import ovation.odata.service.AuthUtil;
import ovation.odata.util.DataContextCache;

/**
 * Basic HTTP-Auth filter (old-school authentication layer) which works in this case because:
 * <ul>
 * <li>Ovation doesn't have any user-roles</li>
 * <li>We can package this into a war without requiring a JAAS LoginModule or custom Realm (both of which have complications)</li>
 * <li>We only do HTTP-Auth</li>
 * </ul>
 * 
 * @author Ron
 */
public class OvationBasicAuthFilter implements Filter {
	public static final Logger _log = Logger.getLogger(OvationBasicAuthFilter.class);
	public static final String REALM = "ovodata";
	
	public void init(FilterConfig arg0) throws ServletException {
		_log.debug("init()");
	}
	public void destroy() {
		_log.debug("destroy()");
	}
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest  httpReq = (HttpServletRequest)req;
        final HttpServletResponse httpRes = (HttpServletResponse)res;
        
		String authHeader = httpReq.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader != null) {
			// auth attempt
			_log.debug("attempt to authenticate '" + authHeader + "'");
			try {
				String[] userPasswordArray = AuthUtil.parseBasicAuthHeader(authHeader);
				String userName = userPasswordArray[0];
				String password = userPasswordArray[1];
				if (AuthUtil.authenticateUser(userName, password)) {
					try {
						chain.doFilter(req, res);	// success!
					} finally {
						// detach the DataContext from the thread
						DataContextCache.closeThreadContext();
					}
					// don't return unauthorized response headers/error
					return;
				}
			} catch (UserAuthenticationException uax) {
				_log.warn("failed to authenticate '" + authHeader + "'");
			} catch (OvationException ox) {
				// DataContext.authenticateUser actually throws one of these when auth fails 
				_log.warn("failed to authenticate '" + authHeader + "'");
			}
		} else {
			_log.debug("no auth header provided");
		}
		httpRes.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"" + REALM + "\"");
		httpRes.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}
}