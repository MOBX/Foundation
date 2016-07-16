package com.lamfire.utils;

import com.lamfire.logger.Logger;

import java.io.Closeable;
import java.util.List;
import java.util.Set;

/**
 * CloseOnJvmShutdownHook
 * User: linfan
 * Date: 15-7-17
 * Time: ÏÂÎç5:59
 * Auto close and release resource
 */
public class CloseOnJvmShutdownHook implements Runnable{
    private static final Logger LOGGER = Logger.getLogger(CloseOnJvmShutdownHook.class);
    private static CloseOnJvmShutdownHook instance = new CloseOnJvmShutdownHook();

    public static CloseOnJvmShutdownHook getInstance(){
        return instance;
    }

    private CloseOnJvmShutdownHook(){
        Thread t = new Thread(this);
        t.setName("CloseOnJvmShutdownHook");
        Runtime.getRuntime().addShutdownHook(t);
    }

    private final Set<Closeable> closeableSet = Sets.newHashSet();

    public void addJvmShutdownHook(Closeable closeable){
        closeableSet.add(closeable);
    }

    public void removeJvmShutdownHook(Closeable closeable){
        closeableSet.remove(closeable);
    }

    private void closeQuietly(Closeable closeable){
        if(closeable == null){
         return ;
        }
        try{
            closeable.close();
            LOGGER.info("[CloseOnJvmShutdown] : " + closeable.getClass().getName() +" - " + closeable);
        }catch (Throwable t){
            LOGGER.error(t.getMessage(),t);
        }
    }

    @Override
    public void run() {
        for(Closeable closeable : closeableSet){
            closeQuietly(closeable);
        }
    }
}
