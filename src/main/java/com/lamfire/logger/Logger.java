package com.lamfire.logger;

public abstract class Logger {

	public abstract void debug(String msg);
    public abstract void debug(String msg, Throwable cause);
    public abstract void debug(Throwable cause);
    
    public abstract void error(String msg) ;
    public abstract void error(String msg, Throwable cause);
    public abstract void error(Throwable cause);

    public abstract void info(String msg);
    public abstract void info(String msg, Throwable cause);
    public abstract void info(Throwable cause);
    
    public abstract void warn(String msg);
    public abstract void warn(String msg, Throwable cause);
    public abstract void warn(Throwable cause);
    
    public abstract boolean isDebugEnabled();
    public abstract boolean isErrorEnabled();
    public abstract boolean isInfoEnabled();
    public abstract boolean isWarnEnabled();

    
    
    public static final Logger getLogger(String name){
    	return LoggerFactory.getLogger(name);
    }
    
    
	public static final Logger getLogger(Class<?> cls){
    	return LoggerFactory.getLogger(cls);
    }
}
