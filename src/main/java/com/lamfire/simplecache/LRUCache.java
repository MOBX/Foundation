package com.lamfire.simplecache;

import com.lamfire.logger.Logger;

public class LRUCache<K, V> extends AbstractCache<K, V> {

    static final Logger LOGGER = Logger.getLogger(LRUCache.class);

    public LRUCache(int maxElementsInCache, long timeToLiveMillis) {
        super(new LRUHashMap<K, Item<K, V>>(maxElementsInCache), maxElementsInCache, timeToLiveMillis);
    }
}
