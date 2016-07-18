package com.lamfire.utils;

import java.io.Closeable;
import java.util.Set;

import com.lamfire.logger.Logger;

/**
 * CloseOnJvmShutdownHook
 */
public class CloseOnJvmShutdownHook implements Runnable {

    private static final Logger           LOGGER   = Logger.getLogger(CloseOnJvmShutdownHook.class);
    private static CloseOnJvmShutdownHook instance = new CloseOnJvmShutdownHook();

    public static CloseOnJvmShutdownHook getInstance() {
        return instance;
    }

    private CloseOnJvmShutdownHook() {
        Thread t = new Thread(this);
        t.setName("CloseOnJvmShutdownHook");
        Runtime.getRuntime().addShutdownHook(t);
    }

    private final Set<Closeable> closeableSet = Sets.newHashSet();

    public void addJvmShutdownHook(Closeable closeable) {
        closeableSet.add(closeable);
    }

    public void removeJvmShutdownHook(Closeable closeable) {
        closeableSet.remove(closeable);
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
            LOGGER.info("[CloseOnJvmShutdown] : " + closeable.getClass().getName() + " - " + closeable);
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
    }

    @Override
    public void run() {
        for (Closeable closeable : closeableSet) {
            closeQuietly(closeable);
        }
    }
}
