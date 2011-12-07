package ovation.odata.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * adapts java.util.logging (JUL)-using code to pipe log data into Log4J
 * 
 * to use add this to cmd-line: -Djava.util.logging.manager=ovation.odata.util.JULLog4JBridge
 * 
 * @author Ron
 */
public class JULLog4JBridge extends LogManager {
	public static void register() {
		System.setProperty("java.util.logging.manager", JULLog4JBridge.class.getName());
	}

    /** "proxy" for a JUL Logger which pipes all log events through Log4J Logger*/
    private static class BridgeLogger extends Logger {
        final org.apache.log4j.Logger _log4jLogger;
        
        BridgeLogger(Logger replaced) {
            super(replaced.getName(), null);
            _log4jLogger = org.apache.log4j.Logger.getLogger(replaced.getName());
        }
        
        public void fine(String msg)    { _log4jLogger.debug(msg); }
        public void finer(String msg)   { _log4jLogger.trace(msg); }
        public void finest(String msg)  { _log4jLogger.trace(msg); }
        public void info(String msg)    { _log4jLogger.info(msg); }
        public void warning(String msg) { _log4jLogger.warn(msg); }
        public void severe(String msg)  { _log4jLogger.error(msg); }

        public void log(Level level, String msg) {
            int val = level.intValue();
            if (val <= Level.FINER.intValue())  _log4jLogger.trace(msg); else
            if (val <= Level.FINE.intValue())   _log4jLogger.debug(msg); else
            if (val <= Level.INFO.intValue())   _log4jLogger.info(msg);  else
            if (val <= Level.WARNING.intValue())_log4jLogger.warn(msg);  else
            if (val <= Level.SEVERE.intValue()) _log4jLogger.error(msg);
        }
        public void log(Level level, String msg, Throwable thrown)  {
            int val = level.intValue();
            if (val <= Level.FINER.intValue())  _log4jLogger.trace(msg, thrown); else
            if (val <= Level.FINE.intValue())   _log4jLogger.debug(msg, thrown); else
            if (val <= Level.INFO.intValue())   _log4jLogger.info(msg, thrown);  else
            if (val <= Level.WARNING.intValue())_log4jLogger.warn(msg, thrown);  else
            if (val <= Level.SEVERE.intValue()) _log4jLogger.error(msg, thrown);
        }
        
        public void log(Level l, String msg, Object p)                                  { log(l, msg); }
        public void log(Level l, String msg, Object[] p)                                { log(l, msg); }
        public void logp(Level l, String c, String m, String msg)                       { log(l, msg); }
        public void logp(Level l, String c, String m, String msg, Object p)             { log(l, msg); }
        public void logp(Level l, String c, String m, String msg, Object[] p)           { log(l, msg); }
        public void logp(Level l, String c, String m, String msg, Throwable t)          { log(l, msg, t); }
        public void logrb(Level l, String c, String m, String b, String msg)            { log(l, msg); }
        public void logrb(Level l, String c, String m, String b, String msg, Object p)  { log(l, msg); }
        public void logrb(Level l, String c, String m, String b, String msg, Object[] p){ log(l, msg); }
        public void logrb(Level l, String c, String m, String b, String msg, Throwable t){ log(l, msg, t); }
        
        public void log(LogRecord record) {
            Throwable t = record.getThrown();
            if (t != null)  log(record.getLevel(), record.getMessage(), t);
            else            log(record.getLevel(), record.getMessage());
        }
        
        /* not really fast because of JCL abstraction on top of level */
        @Override
        public Level getLevel() {
            if (_log4jLogger.isTraceEnabled()) return Level.FINEST;
            if (_log4jLogger.isEnabledFor(org.apache.log4j.Priority.DEBUG)) return Level.FINE;
            if (_log4jLogger.isEnabledFor(org.apache.log4j.Priority.INFO))  return Level.INFO;
            if (_log4jLogger.isEnabledFor(org.apache.log4j.Priority.WARN))  return Level.WARNING;
            if (_log4jLogger.isEnabledFor(org.apache.log4j.Priority.ERROR)) return Level.SEVERE;
            if (_log4jLogger.isEnabledFor(org.apache.log4j.Priority.FATAL)) return Level.SEVERE;
            return Level.OFF;
        }

        /** not really fast because Level is not an enum (and JCL hides logging level) */
        @Override
        public boolean isLoggable(Level level) {
            int val = level.intValue();
            if (val <= Level.FINER.intValue())  return _log4jLogger.isTraceEnabled();
            if (val <= Level.FINE.intValue())   return _log4jLogger.isDebugEnabled();
            if (val <= Level.INFO.intValue())   return _log4jLogger.isInfoEnabled();
            if (val <= Level.WARNING.intValue())return _log4jLogger.isEnabledFor(org.apache.log4j.Priority.WARN);
            if (val <= Level.SEVERE.intValue()) return _log4jLogger.isEnabledFor(org.apache.log4j.Priority.ERROR);
            return false;
        }
    }
    
    public synchronized boolean addLogger(Logger logger) {
        String loggerName = logger.getName();
        Logger oldLogger = super.getLogger(loggerName);
        if (oldLogger != null) {
            return false;   // already got that one
        }
        // how we replace the JUL impl
        return super.addLogger(new BridgeLogger(logger));
    }

    public void readConfiguration() throws IOException, SecurityException {}
    public void readConfiguration(InputStream ins) throws IOException, SecurityException {}   
    public void reset() throws SecurityException {}
}
