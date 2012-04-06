package ovation.odata.util;

import java.io.IOException;

import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.resources.CrossDomainResourceConfig;
import org.odata4j.producer.resources.ODataResourceConfig;
import org.odata4j.producer.server.JerseyServer;
/* FIXME 0.6
import org.odata4j.producer.resources.RootApplication;
import org.odata4j.jersey.producer.resources.ODataApplication;
import org.odata4j.jersey.producer.server.JerseyServer;
*/
import com.sun.jersey.api.container.filter.LoggingFilter;

/**
 * a set of utility methods useful to OData servers
 * @author Ron
 */
public class OData4JServerUtils {

    public static void hostODataServer(String baseUri) {
       
        JerseyServer server= startODataServer(baseUri);
        try {
	        System.out.println("Press CR to exit");
	        try { System.in.read(); } catch (IOException iox) {}
        } finally {
        	server.stop();
        }
    }
    
    public static JerseyServer startODataServer(String baseUri) {
        JerseyServer server = new JerseyServer(baseUri);
//FIXME 0.6        server.setODataApplication(ODataApplication.class);
//FIXME 0.6        server.setRootApplication(RootApplication.class);
        server.addAppResourceClasses(new ODataResourceConfig().getClasses());
        server.addRootResourceClasses(new CrossDomainResourceConfig().getClasses());

        server.addJerseyRequestFilter(LoggingFilter.class); // log all requests

        // server.addHttpServerFilter(new WhitelistFilter("127.0.0.1","0:0:0:0:0:0:0:1%0")); // only allow local requests
        server.start();
        
        return server;
        
    }
    
    public static String toString(QueryInfo query) {
    	return "{inlineCnt:" + query.inlineCount + ", top:" + query.top + ", skip:" + query.skip + ", filter:" + query.filter
    			+ ", orderBy:" + query.orderBy + ", skipToken:" + query.skipToken + ", customOptions:" + query.customOptions 
    			+ ", expand:" + query.expand + ", select:" + query.select + "}";
    }
}
