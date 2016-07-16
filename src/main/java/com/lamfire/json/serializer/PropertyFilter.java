package com.lamfire.json.serializer;


public interface PropertyFilter {

    /**
     * @param source the owner of the property
     * @param name the name of the property
     * @param value the value of the property
     * @return true if the property will be filtered out, false otherwise
     */
    boolean apply(Object source, String name, Object value);
}
