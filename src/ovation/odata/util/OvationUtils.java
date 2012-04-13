package ovation.odata.util;

import java.util.List;

import ovation.DataContext;

import com.google.common.collect.Lists;

public class OvationUtils {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> getAllFromDB(DataContext dbContext, Class<T> type) {
		return Lists.newArrayList(dbContext.query((Class)type, "true"));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getFromDB(DataContext dbContext, String uri) {
    	return (T)dbContext.objectWithURI(uri);		
	}

}
