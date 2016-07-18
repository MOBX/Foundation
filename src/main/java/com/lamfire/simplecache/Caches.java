package com.lamfire.simplecache;

public class Caches {

    public static <K, V> Cache<K, V> makeFifoCache(int maxElementsInCache, long timeToLiveMillis) {
        return new FIFOCache<K, V>(maxElementsInCache, timeToLiveMillis);
    }

    public static <K, V> Cache<K, V> makeLruCache(int maxElementsInCache, long timeToLiveMillis) {
        return new LRUCache<K, V>(maxElementsInCache, timeToLiveMillis);
    }
}
