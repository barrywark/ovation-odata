package ovation.odata.util;

import org.apache.log4j.Logger;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class Log4JJerseyLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
	public static final Logger _log = Logger.getLogger(Log4JJerseyLoggingFilter.class);
	public ContainerRequest filter(ContainerRequest req) {
		_log.info(toString(req));
		return req;
	}
	public ContainerResponse filter(ContainerRequest req, ContainerResponse res) {
		_log.info(toString(res));
		return null;
	}
	
	public static String toString(ContainerRequest req) {
		return "req - " + req;
	}
	public static String toString(ContainerResponse res) {
		return "res - " + res;
	}
}
