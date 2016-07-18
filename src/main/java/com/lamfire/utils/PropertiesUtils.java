package com.lamfire.utils;

import java.io.*;
import java.util.*;

import com.lamfire.logger.Logger;

public class PropertiesUtils {

    private static final Logger LOGGER = Logger.getLogger(PropertiesUtils.class.getName());

    public static Map<String, String> loadAsMap(InputStream input) {
        if (input == null) {
            throw new IllegalArgumentException("input stream is null.");
        }
        return toMap(load(input));
    }

    public static Properties load(InputStream input) {
        if (input == null) {
            throw new IllegalArgumentException("input stream is null.");
        }
        Properties properties = new Properties();
        try {
            properties.load(input);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        return properties;
    }

    public static Properties load(String resource, Class<?> callingClass) {
        InputStream input = ClassLoaderUtils.getResourceAsStream(resource, callingClass);
        return load(input);
    }

    public static Properties load(File file) {
        try {
            return load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static Map<String, String> loadAsMap(File file) {
        try {
            return loadAsMap(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static Map<String, String> loadAsMap(String resource, Class<?> callingClass) {
        InputStream input = ClassLoaderUtils.getResourceAsStream(resource, callingClass);
        return loadAsMap(input);
    }

    public static Map<String, String> toMap(Properties properties) {
        Map<String, String> map = new HashMap<String, String>();
        if (properties == null) {
            return map;
        }
        Enumeration<?> e = properties.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String val = properties.getProperty(key.toString(), "");
            map.put(key, val);
        }
        return map;
    }
}
