package com.lamfire.utils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class Sets {

	public static <E extends Enum<E>> EnumSet<E> newEnumSet(Iterable<E> iterable, Class<E> elementType) {
		EnumSet<E> set = EnumSet.noneOf(elementType);
		Iterator<E> it = iterable.iterator();
		while (it.hasNext()) {
			set.add(it.next());
		}
		return set;
	}

	public static <E> HashSet<E> newHashSet() {
		return new HashSet<E>();
	}

	public static <E> HashSet<E> newHashSet(E[] elements) {
		HashSet<E> set = newHashSet(elements.length);
		Collections.addAll(set, elements);
		return set;
	}

	public static <E> HashSet<E> newHashSet(int expectedSize) {
		return new HashSet<E>((expectedSize));
	}

	static <T> Collection<T> cast(Iterable<T> iterable) {
		return (Collection<T>) iterable;
	}

	public static <E> HashSet<E> newHashSet(Iterable<? extends E> elements) {
		return (elements instanceof Collection<?>) ? new HashSet<E>(cast(elements)) : newHashSet(elements.iterator());
	}

	public static <E> HashSet<E> newHashSet(Iterator<? extends E> elements) {
		HashSet<E> set = newHashSet();
		while (elements.hasNext()) {
			set.add(elements.next());
		}
		return set;
	}

	public static <E> LinkedHashSet<E> newLinkedHashSet() {
		return new LinkedHashSet<E>();
	}

	public static <E> LinkedHashSet<E> newLinkedHashSet(int expectedSize) {
		return new LinkedHashSet<E>((expectedSize));
	}

	public static <E> LinkedHashSet<E> newLinkedHashSet(Iterable<? extends E> elements) {
		if ((elements instanceof Collection<?>)) {
			return new LinkedHashSet<E>(cast(elements));
		}
		LinkedHashSet<E> set = newLinkedHashSet();
		for (E e : elements) {
			set.add(e);
		}
		return set;
	}

	public static <E extends Comparable<E>> TreeSet<E> newTreeSet() {
		return new TreeSet<E>();
	}

	public static <E extends Comparable<E>> TreeSet<E> newTreeSet(Iterable<? extends E> elements) {
		TreeSet<E> set = newTreeSet();
		for (E e : elements) {
			set.add(e);
		}
		return set;
	}

	public static <E> TreeSet<E> newTreeSet(Comparator<? super E> comparator) {
		return new TreeSet<E>((comparator));
	}

	public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet() {
		return new CopyOnWriteArraySet<E>();
	}

	public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet(Iterable<? extends E> elements) {
		Collection<? extends E> elementsCollection = (elements instanceof Collection<?>) ? cast(elements) : Lists.newArrayList(elements);

		return new CopyOnWriteArraySet<E>(elementsCollection);
	}

	public static <T> T[] toArray(Collection<? extends T> elements,Class<T> type){
		T[] array = ArrayUtils.newArray(type, elements.size());
		int i=0;
		for (T t :elements) {
			array[i++] = t;
		}
		return array;
	}

    public static <T> void addAll(Set<T> set,T[] array){
        for (T t :array) {
            set.add(t);
        }
    }
}
