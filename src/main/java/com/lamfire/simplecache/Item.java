package com.lamfire.simplecache;

import java.io.Serializable;

public final class Item<K, V> implements Serializable, Cloneable {

    private static final long serialVersionUID = -7483621274299424047L;
    private final K           key;
    private V                 value;
    private long              version;
    private long              creationTime;
    private long              lastAccessTime;
    private long              hitCount;
    private long              lastUpdateTime;

    public Item(K key) {
        this.key = key;
        this.version = 0;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessTime = -1;
        this.lastUpdateTime = -1;
        this.hitCount = 0;
    }

    public Item(K key, V value) {
        this(key);
        setValue(value);
    }

    public V getValue() {
        this.hitCount++;
        this.lastAccessTime = System.currentTimeMillis();
        return value;
    }

    public void setValue(V value) {
        this.value = value;
        this.version++;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public long getVersion() {
        return version;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public long getHitCount() {
        return hitCount;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public K getKey() {
        return key;
    }

    public boolean isExpired(long timeToLiveMillis) {
        if (timeToLiveMillis <= 0) {
            return false;
        }
        long now = System.currentTimeMillis();
        long expirationTime = getCreationTime() + timeToLiveMillis;
        return now > expirationTime;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("key=" + key);
        buffer.append(",value=" + value);
        buffer.append(",version=" + version);
        buffer.append(",creationTime=" + creationTime);
        buffer.append(",lastUpdateTime=" + lastUpdateTime);
        buffer.append(",lastAccessTime=" + lastAccessTime);
        buffer.append(",hitCount=" + hitCount);

        return buffer.toString();
    }
}
