package com.lamfire.simplecache;

import java.util.Collection;
import java.util.Map;

public interface Cache<K, V> {

    public Map<K, V> asMap();

    public void clear();

    public int getMaxElementsInCache();

    public long getTimeToLiveMillis();

    public void set(K key, V val);

    public boolean isExpired(Item<K, V> item);

    public V get(K key);

    public Item<K, V> remove(K key);

    public int size();

    public Collection<K> keys();

    public Collection<V> values();

    public Collection<Item<K, V>> items();

    public boolean isEmpty();
}
