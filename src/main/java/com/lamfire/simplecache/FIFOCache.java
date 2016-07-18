package com.lamfire.simplecache;

import com.lamfire.logger.Logger;

public class FIFOCache<K, V> extends AbstractCache<K, V> {

    static final Logger LOGGER = Logger.getLogger(FIFOCache.class);

    public FIFOCache(int maxElementsInCache, long timeToLiveMillis) {
        super(new FIFOHashMap<K, Item<K, V>>(maxElementsInCache), maxElementsInCache, timeToLiveMillis);
    }
}
