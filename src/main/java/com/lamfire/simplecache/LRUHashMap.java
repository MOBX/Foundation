package com.lamfire.simplecache;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class LRUHashMap<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = 5464565678538L;
	private static final float LOAD_FACTOR = 1.0f;
	private int capacity;

	public LRUHashMap(int lruCapacity) {
		super(lruCapacity,LOAD_FACTOR,true);
		this.capacity = lruCapacity;
	}

	protected boolean removeEldestEntry(Entry<K, V> eldest) {
		if (size() > capacity) {
			return true;
		}
		return false;
	}
}
