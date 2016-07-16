package com.lamfire.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Lists {

	public static <E> ArrayList<E> newArrayList() {
		return new ArrayList<E>();
	}

	public static <E> ArrayList<E> newArrayList(E[] elements) {
		int capacity = (elements.length);
		ArrayList<E> list = new ArrayList<E>(capacity);
		Collections.addAll(list, elements);
		return list;
	}

	public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
		return (elements instanceof Collection<?>) ? new ArrayList<E>(cast(elements)) : newArrayList(elements.iterator());
	}

	static <T> Collection<T> cast(Iterable<T> iterable) {
		return (Collection<T>) iterable;
	}

	public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
		ArrayList<E> list = newArrayList();
		while (elements.hasNext()) {
			list.add(elements.next());
		}
		return list;
	}

	public static <E> ArrayList<E> newArrayList(int initialArraySize) {
		return new ArrayList<E>(initialArraySize);
	}

	public static <E> LinkedList<E> newLinkedList() {
		return new LinkedList<E>();
	}

	public static <E> LinkedList<E> newLinkedList(Iterable<? extends E> elements) {
		LinkedList<E> list = newLinkedList();
		for (Iterator<? extends E> it = elements.iterator(); it.hasNext();) {
			E element = it.next();
			list.add(element);
		}
		return list;
	}
	
	public static <T extends Comparable<? super T>> void sort(List<T> source) {
		Collections.sort(source);
	}
	
	public static <T> void sort(List<T> source,Comparator<? super T> comparator) {
		Collections.sort(source,comparator);
	}
	
	public static <T> T[] toArray(Collection<? extends T> elements,Class<T> type){
		T[] array = ArrayUtils.newArray(type, elements.size());
		int i=0;
		for (T t :elements) {
			array[i++] = t;
		}
		return array;
	}

    public static <T> void addAll(List<T> list,T[] array){
        for (T t :array) {
            list.add(t);
        }
    }

    public static List<String>  asStringList(List<Object> list){
        List<String> result = Lists.newArrayList();
        for(Object o : list){
            result.add(o.toString());
        }
        return result;
    }

    public static List<Integer>  asIntegerList(List<Object> list){
        List<Integer> result = Lists.newArrayList();
        for(Object o : list){
            Integer v = null;
            if(o instanceof Integer){
               v = (Integer)o;
            }else{
                v = Integer.valueOf(o.toString());
            }
            result.add(v);
        }
        return result;
    }

    public static List<Float>  asFloatList(List<Object> list){
        List<Float> result = Lists.newArrayList();
        for(Object o : list){
            Float v = null;
            if(o instanceof Float){
                v = (Float)o;
            }else{
                v = Float.valueOf(o.toString());
            }
            result.add(v);
        }
        return result;
    }

    public static List<Long>  asLongList(List<Object> list){
        List<Long> result = Lists.newArrayList();
        for(Object o : list){
            Long v = null;
            if(o instanceof Long){
                v = (Long)o;
            }else{
                v = Long.valueOf(o.toString());
            }
            result.add(v);
        }
        return result;
    }

    public static List<Double>  asDoubleList(List<Object> list){
        List<Double> result = Lists.newArrayList();
        for(Object o : list){
            Double v = null;
            if(o instanceof Double){
                v = (Double)o;
            }else{
                v = Double.valueOf(o.toString());
            }
            result.add(v);
        }
        return result;
    }

    public static List<Short>  asShortList(List<Object> list){
        List<Short> result = Lists.newArrayList();
        for(Object o : list){
            Short v = null;
            if(o instanceof Short){
                v = (Short)o;
            }else{
                v = Short.valueOf(o.toString());
            }
            result.add(v);
        }
        return result;
    }

    public static <E> List<E>  asList(Iterable<Object> elements){
        List<E> result = Lists.newArrayList();
        for(Object o : elements){
            E v = (E)o;
            result.add(v);
        }
        return result;
    }
}
