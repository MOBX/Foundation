package com.lamfire.utils;

import java.util.Collection;
import java.util.Map;

public class Printers {

	public static void print(Object object){
		System.out.println(object);
	}

	public static void print(Collection<?> col){
		for(Object o : col){
			print(o);
		}
	}

	public static void print(Map<?,?> map){
		for(Map.Entry<?, ?> e : map.entrySet()){
			System.out.println(e.getKey() +"="+e.getValue());
		}
	}

	public static void print(Object[] objects){
		for(Object obj :objects){
			System.out.println(obj);
		}
	}
	
}
