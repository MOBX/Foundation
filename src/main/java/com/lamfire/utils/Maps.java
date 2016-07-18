package com.lamfire.utils;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Maps {

    public static <K, V> List<Entry<K, V>> getEntriesOrderByValue(Map<K, V> source) {
        return getEntriesOrderByValue(source, false);
    }

    public static <K, V> List<Entry<K, V>> getEntriesOrderByValue(Map<K, V> source, final boolean descOrder) {
        List<Entry<K, V>> entries = Lists.newLinkedList(source.entrySet());
        Lists.sort(entries, new Comparator<Entry<K, V>>() {

            @Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                V v1 = o1.getValue();
                V v2 = o2.getValue();
                if (v1.equals(v2)) {
                    return 0;
                }
                Object[] objects = new Object[] { v1, v2 };
                Arrays.sort(objects);
                if (v1 == objects[0]) {
                    return descOrder ? 1 : -1;
                }
                return descOrder ? -1 : 1;
            }
        });
        return entries;
    }

    public static <K, V> List<Entry<K, V>> sortEntries(Map<K, V> source, Comparator<Entry<K, V>> comparator) {
        List<Entry<K, V>> entries = Lists.newLinkedList(source.entrySet());
        Lists.sort(entries, comparator);
        return entries;
    }

    public static <K, V> List<Entry<K, V>> getEntriesOrderByKey(Map<K, V> source) {
        return getEntriesOrderByKey(source, false);
    }

    public static <K, V> List<Entry<K, V>> getEntriesOrderByKey(Map<K, V> source, final boolean descOrder) {
        List<Entry<K, V>> entries = Lists.newLinkedList(source.entrySet());
        Lists.sort(entries, new Comparator<Entry<K, V>>() {

            @Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                K v1 = o1.getKey();
                K v2 = o2.getKey();
                if (v1.equals(v2)) {
                    return 0;
                }
                Object[] objects = new Object[] { v1, v2 };
                Arrays.sort(objects);
                if (v1 == objects[0]) {
                    return descOrder ? 1 : -1;
                }
                return descOrder ? -1 : 1;
            }
        });
        return entries;
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    public static <K, V> HashMap<K, V> newHashMap(int expectedSize) {
        return new HashMap<K, V>((expectedSize));
    }

    public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> map) {
        return new HashMap<K, V>(map);
    }

    public static <K, V> HashMap<K, V> newHashMap(Iterable<Entry<K, V>> entries) {
        HashMap<K, V> map = newHashMap();
        fill(entries, map);
        return map;
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<K, V>();
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> map) {
        return new LinkedHashMap<K, V>(map);
    }

    public static <K, V> HashMap<K, V> newLinkedHashMap(Iterable<Entry<K, V>> entries) {
        HashMap<K, V> map = newLinkedHashMap();
        fill(entries, map);
        return map;
    }

    public static <K, V> void fill(Iterable<Entry<K, V>> entries, Map<K, V> map) {
        for (Entry<K, V> e : entries) {
            map.put(e.getKey(), e.getValue());
        }
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        return new ConcurrentHashMap<K, V>();
    }

    public static <K, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<K, V>();
    }

    public static <K, V> TreeMap<K, V> newTreeMap(SortedMap<K, ? extends V> map) {
        return new TreeMap<K, V>(map);
    }

    public static <C, K extends C, V> TreeMap<K, V> newTreeMap(Comparator<C> comparator) {
        return new TreeMap<K, V>(comparator);
    }

    public static <K, V> TreeMap<K, V> newTreeMap(Iterable<Entry<K, V>> entries) {
        TreeMap<K, V> map = newTreeMap();
        fill(entries, map);
        return map;
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
        return new EnumMap<K, V>((type));
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Map<K, ? extends V> map) {
        return new EnumMap<K, V>(map);
    }

    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap<K, V>();
    }
}
