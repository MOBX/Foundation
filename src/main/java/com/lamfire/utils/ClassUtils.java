package com.lamfire.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class ClassUtils {
	public static final char PACKAGE_SEPARATOR_CHAR = '.';
	public static final String PACKAGE_SEPARATOR = String.valueOf('.');
	public static final char INNER_CLASS_SEPARATOR_CHAR = '$';
	public static final String INNER_CLASS_SEPARATOR = String.valueOf('$');

	private static final Map<Class<?>, Map<String, PropertyDescriptor>> propertyDescriptorMapCache = new HashMap<Class<?>, Map<String, PropertyDescriptor>>();
	private static final Map<Class<?>, PropertyDescriptor[]> propertyDescriptorArrayCache = new HashMap<Class<?>, PropertyDescriptor[]>();
	private static final Map<Class<?>, Field[]> fieldCache = new HashMap<Class<?>,  Field[]>();
	private static final Map<Object, Class<?>> primitiveWrapperMap = new HashMap<Object, Class<?>>();
	private static final Map<String, Class<?>> genericActualTypeCacheMap = new HashMap<String, Class<?>>();
	private static final Map<String, String> abbreviationMap = new HashMap<String, String>();
    private static final Map<Class<?>, List<Class<?>>> allSupperClassMap = new HashMap<Class<?>,List<Class<?>>>();
    private static final Map<Class<?>, List<Class<?>>> allInterfacesMap = new HashMap<Class<?>,List<Class<?>>>();

	public static String getShortClassName(Object object, String valueIfNull) {
		if (object == null) {
			return valueIfNull;
		}
		return getShortClassName(object.getClass().getName());
	}

	public static String getShortClassName(Class<?> cls) {
		if (cls == null) {
			return "";
		}
		return getShortClassName(cls.getName());
	}

	public static String getShortClassName(String className) {
		if (className == null) {
			return "";
		}
		if (className.length() == 0) {
			return "";
		}
		char[] chars = className.toCharArray();
		int lastDot = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '.')
				lastDot = i + 1;
			else if (chars[i] == '$') {
				chars[i] = '.';
			}
		}
		return new String(chars, lastDot, chars.length - lastDot);
	}

	public static String getPackageName(Object object, String valueIfNull) {
		if (object == null) {
			return valueIfNull;
		}
		return getPackageName(object.getClass().getName());
	}

	public static String getPackageName(Class<?> cls) {
		if (cls == null) {
			return "";
		}
		return getPackageName(cls.getName());
	}

	public static String getPackageName(String className) {
		if (className == null) {
			return "";
		}
		int i = className.lastIndexOf('.');
		if (i == -1) {
			return "";
		}
		return className.substring(0, i);
	}

	public static Annotation[] getAnnotations(Class<?> cls) {
		return cls.getAnnotations();
	}

	public static <T extends Annotation> T getAnnotation(Class<?> target, Class<T> annotationClass) {
		if (annotationClass == null || target == null)
			throw new NullPointerException();

		T t = target.getAnnotation(annotationClass);
		if (t != null) {
			return t;
		}
		Class<?> superclass = target.getSuperclass();
		while (superclass != null) {
			t = superclass.getAnnotation(annotationClass);
			if (t != null) {
				return t;
			}
			superclass = superclass.getSuperclass();
		}
		return null;
	}

	public static Field getAnnotationField(Class<?> target, Class<? extends Annotation> annotationClass) {
		if (annotationClass == null || target == null){
			throw new NullPointerException();
		}
		Map<String, Field> fields = ClassUtils.getAllFieldsAsMap(target);
		for (Map.Entry<String, Field> e : fields.entrySet()) {
			Field field = e.getValue();
			Annotation ann = field.getAnnotation(annotationClass);
			if (ann != null) {
				return field;
			}
		}
		return null;
	}

	public static List<Class<?>> getAllSuperclasses(Class<?> cls) {
		if (cls == null) {
			return null;
		}
        List<Class<?>> classes = allSupperClassMap.get(cls);
        if(classes != null){
            return classes;
        }
		classes = new ArrayList<Class<?>>();
		Class<?> superclass = cls.getSuperclass();
		while (superclass != null) {
			classes.add(superclass);
			superclass = superclass.getSuperclass();
		}
        allSupperClassMap.put(cls,classes);
		return classes;
	}

	public static List<Class<?>> getAllInterfaces(Class<?> cls) {
		if (cls == null) {
			return null;
		}
        List<Class<?>> list = allInterfacesMap.get(cls);
        if(list != null){
            return list;
        }
		list = new ArrayList<Class<?>>();
		while (cls != null) {
			Class<?>[] interfaces = cls.getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				if (!list.contains(interfaces[i])) {
					list.add(interfaces[i]);
				}
				List<Class<?>> superInterfaces = getAllInterfaces(interfaces[i]);
				for (Iterator<Class<?>> it = superInterfaces.iterator(); it.hasNext();) {
					Class<?> intface = (Class<?>) it.next();
					if (!list.contains(intface)) {
						list.add(intface);
					}
				}
			}
			cls = cls.getSuperclass();
		}
        allInterfacesMap.put(cls,list);
		return list;
	}

	public static List<Class<?>> toClasses(List<String> classNames) {
		if (classNames == null) {
			return null;
		}
		List<Class<?>> classes = new ArrayList<Class<?>>(classNames.size());
		for (String className : classNames) {
			try {
				classes.add(Class.forName(className));
			} catch (Exception ex) {
				classes.add(null);
			}
		}
		return classes;
	}

	public static List<String> toClassNames(List<Class<?>> classes) {
		if (classes == null) {
			return null;
		}
		List<String> classNames = new ArrayList<String>(classes.size());
		for (Iterator<Class<?>> it = classes.iterator(); it.hasNext();) {
			Class<?> cls = (Class<?>) it.next();
			if (cls == null)
				classNames.add(null);
			else {
				classNames.add(cls.getName());
			}
		}
		return classNames;
	}

	public static boolean isAssignable(Class<?>[] classArray, Class<?>[] toClassArray) {
		if (!ArrayUtils.isSameLength(classArray, toClassArray)) {
			return false;
		}
		if (classArray == null) {
			classArray = ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		if (toClassArray == null) {
			toClassArray = ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		for (int i = 0; i < classArray.length; i++) {
			if (!isAssignable(classArray[i], toClassArray[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean isAssignable(Class<?> cls, Class<?> toClass) {
		if (toClass == null) {
			return false;
		}

		if (cls == null) {
			return !toClass.isPrimitive();
		}
		if (cls.equals(toClass)) {
			return true;
		}
		if (cls.isPrimitive()) {
			if (!toClass.isPrimitive()) {
				return false;
			}
			if (Integer.TYPE.equals(cls)) {
				return (Long.TYPE.equals(toClass)) || (Float.TYPE.equals(toClass)) || (Double.TYPE.equals(toClass));
			}

			if (Long.TYPE.equals(cls)) {
				return (Float.TYPE.equals(toClass)) || (Double.TYPE.equals(toClass));
			}

			if (Boolean.TYPE.equals(cls)) {
				return false;
			}
			if (Double.TYPE.equals(cls)) {
				return false;
			}
			if (Float.TYPE.equals(cls)) {
				return Double.TYPE.equals(toClass);
			}
			if (Character.TYPE.equals(cls)) {
				return (Integer.TYPE.equals(toClass)) || (Long.TYPE.equals(toClass)) || (Float.TYPE.equals(toClass)) || (Double.TYPE.equals(toClass));
			}

			if (Short.TYPE.equals(cls)) {
				return (Integer.TYPE.equals(toClass)) || (Long.TYPE.equals(toClass)) || (Float.TYPE.equals(toClass)) || (Double.TYPE.equals(toClass));
			}

			if (Byte.TYPE.equals(cls)) {
				return (Short.TYPE.equals(toClass)) || (Integer.TYPE.equals(toClass)) || (Long.TYPE.equals(toClass)) || (Float.TYPE.equals(toClass))
						|| (Double.TYPE.equals(toClass));
			}

			return false;
		}
		return toClass.isAssignableFrom(cls);
	}

	public static Class<?> primitiveToWrapper(Class<?> cls) {
		Class<?> convertedClass = cls;
		if ((cls != null) && (cls.isPrimitive())) {
			convertedClass = (Class<?>) primitiveWrapperMap.get(cls);
		}
		return convertedClass;
	}

	public static Class<?>[] primitivesToWrappers(Class<?>[] classes) {
		if (classes == null) {
			return null;
		}

		if (classes.length == 0) {
			return classes;
		}

		Class<?>[] convertedClasses = new Class<?>[classes.length];
		for (int i = 0; i < classes.length; i++) {
			convertedClasses[i] = primitiveToWrapper(classes[i]);
		}
		return convertedClasses;
	}

	public static boolean isInnerClass(Class<?> cls) {
		if (cls == null) {
			return false;
		}
		return cls.getName().indexOf('$') >= 0;
	}

	public static Class<?> getClass(ClassLoader classLoader, String className, boolean initialize) throws ClassNotFoundException {
		Class<?> clazz;
		if (abbreviationMap.containsKey(className)) {
			String clsName = "[" + abbreviationMap.get(className);
			clazz = Class.forName(clsName, initialize, classLoader).getComponentType();
		} else {
			clazz = Class.forName(toProperClassName(className), initialize, classLoader);
		}
		return clazz;
	}

	public static Class<?> getClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
		return getClass(classLoader, className, true);
	}

	public static Class<?> getClass(String className) throws ClassNotFoundException {
		return getClass(className, true);
	}

	public static Class<?> getClass(String className, boolean initialize) throws ClassNotFoundException {
		ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
		ClassLoader loader = contextCL == null ? ClassUtils.class.getClassLoader() : contextCL;
		return getClass(loader, className, initialize);
	}

	public static Method getPublicMethod(Class<?> cls, String methodName, Class<?>[] parameterTypes) throws SecurityException, NoSuchMethodException {
		Method declaredMethod = cls.getMethod(methodName, parameterTypes);
		if (Modifier.isPublic(declaredMethod.getDeclaringClass().getModifiers())) {
			return declaredMethod;
		}

		List<Class<?>> candidateClasses = new ArrayList<Class<?>>();
		candidateClasses.addAll(getAllInterfaces(cls));
		candidateClasses.addAll(getAllSuperclasses(cls));

		for (Iterator<Class<?>> it = candidateClasses.iterator(); it.hasNext();) {
			Class<?> candidateClass = (Class<?>) it.next();
			if (!Modifier.isPublic(candidateClass.getModifiers()))
				continue;
			Method candidateMethod;
			try {
				candidateMethod = candidateClass.getMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException ex) {
				continue;
			}
			if (Modifier.isPublic(candidateMethod.getDeclaringClass().getModifiers())) {
				return candidateMethod;
			}
		}

		throw new NoSuchMethodException("Can't find a public method for " + methodName);
	}

	private static String toProperClassName(String className) {
		className = StringUtils.deleteWhitespace(className);
		if (className == null)
			throw new NullPointerException("className");
		if (className.endsWith("[]")) {
			StringBuffer classNameBuffer = new StringBuffer();
			while (className.endsWith("[]")) {
				className = className.substring(0, className.length() - 2);
				classNameBuffer.append("[");
			}
			String abbreviation = (String) abbreviationMap.get(className);
			if (abbreviation != null)
				classNameBuffer.append(abbreviation);
			else {
				classNameBuffer.append("L").append(className).append(";");
			}
			className = classNameBuffer.toString();
		}
		return className;
	}

	static {
		primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
		primitiveWrapperMap.put(Byte.TYPE, Byte.class);
		primitiveWrapperMap.put(Character.TYPE, Character.class);
		primitiveWrapperMap.put(Short.TYPE, Short.class);
		primitiveWrapperMap.put(Integer.TYPE, Integer.class);
		primitiveWrapperMap.put(Long.TYPE, Long.class);
		primitiveWrapperMap.put(Double.TYPE, Double.class);
		primitiveWrapperMap.put(Float.TYPE, Float.class);
		primitiveWrapperMap.put(Void.TYPE, Void.TYPE);

		abbreviationMap.put("int", "I");
		abbreviationMap.put("boolean", "Z");
		abbreviationMap.put("float", "F");
		abbreviationMap.put("long", "J");
		abbreviationMap.put("short", "S");
		abbreviationMap.put("byte", "B");
		abbreviationMap.put("double", "D");
		abbreviationMap.put("char", "C");
	}

	public static PropertyDescriptor[] getPropertyDescriptorsArray(Class<?> targetClass) throws IntrospectionException {
		PropertyDescriptor[] result = null;
		if (targetClass != null) {
			synchronized (propertyDescriptorArrayCache) {
				if ((result = (PropertyDescriptor[]) propertyDescriptorArrayCache.get(targetClass)) == null) {
					propertyDescriptorArrayCache.put(targetClass, result = Introspector.getBeanInfo(targetClass).getPropertyDescriptors());
				}
			}
		}
		return result;
	}

	public static Map<String, PropertyDescriptor> getPropertyDescriptorsMap(Class<?> targetClass) throws IntrospectionException {
		Map<String, PropertyDescriptor> result = null;
		if (targetClass == null)
			return null;

		synchronized (propertyDescriptorMapCache) {
			if ((result = propertyDescriptorMapCache.get(targetClass)) == null) {
				result = new HashMap<String, PropertyDescriptor>();

				PropertyDescriptor[] pds;
				pds = getPropertyDescriptorsArray(targetClass);
				for (PropertyDescriptor pd : pds) {
					result.put(pd.getName(), pd);
				}
				propertyDescriptorMapCache.put(targetClass, result);

			}
		}

		return result;
	}

	public static PropertyDescriptor getPropertyDescriptor(Class<?> targetClass, String name) {

		Map<String, PropertyDescriptor> pdMap;
		try {
			pdMap = getPropertyDescriptorsMap(targetClass);
			if (pdMap == null || pdMap.isEmpty()) {
				return null;
			}

			return pdMap.get(name);
		} catch (IntrospectionException e) {
			return null;
		}
	}

	public static Method getReadMethod(Class<?> targetClass, String propertyName) {
		PropertyDescriptor descriptor = getPropertyDescriptor(targetClass, propertyName);
		return descriptor.getReadMethod();
	}

	public static Method getWriteMethod(Class<?> targetClass, String propertyName) {
		PropertyDescriptor descriptor = getPropertyDescriptor(targetClass, propertyName);
		return descriptor.getWriteMethod();
	}

	public static Map<String, Field> getDeclaredFieldsAsMap(Class<?> targetClass) {
		Map<String, Field> result = new HashMap<String, Field>();
		Field[] fa = targetClass.getDeclaredFields();
		for (int i = 0; i < fa.length; i++) {
			result.put(fa[i].getName(), fa[i]);
		}

		return result;
	}

    public static Field[] getDeclaredFields(Class<?> targetClass) {
        Field[] result = targetClass.getDeclaredFields();
        return result;
    }

	public static Field getField(Class<?> inClass, String name) {
		Field result = null;
		for (Class<?> cls = inClass; (cls != null); cls = inClass.getSuperclass()) {
			Map<String, Field> fields = getDeclaredFieldsAsMap(inClass);
			result = fields.get(name);
			if (result != null || cls == Object.class){
				break;
            }
		}

		return result;
	}

	public synchronized static Map<String, Field> getAllFieldsAsMap(Class<?> inClass) {
		Map<String, Field> result = new HashMap<String, Field>();
		result.putAll(getDeclaredFieldsAsMap(inClass));

        Field[] fields = getAllFields(inClass);
        for (Field f: fields) {
            result.put(f.getName(),f);
		}
		return result;
	}

    public synchronized static Field[] getAllFields(Class<?> inClass) {
        Field[] result = fieldCache.get(inClass);
        if (result != null) {
            return result;
        }
        List<Field> list = Lists.newArrayList();
        Collections.addAll(list,getDeclaredFields(inClass));

        List<Class<?>> allClass = getAllSuperclasses(inClass);

        for (Class<?> cls : allClass) {
            Collections.addAll(list,getDeclaredFields(cls));
        }
        result = Lists.toArray(list,Field.class);
        fieldCache.put(inClass, result);
        return result;
    }

	public static Method getMethod(Class<?> inClass, String name, Class<?>[] argsTypes) {
		try {
			return inClass.getMethod(name, argsTypes);
		} catch (Exception e) {

		}
		return null;
	}

	public static Class<?> getGenericActualType(Class<?> inClass, int index) {
		String key = inClass.getName() + "_" + index;
		Class<?> result = genericActualTypeCacheMap.get(key);
		if (result != null) {
			return result;
		}
		Type genType = inClass.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (index >= params.length || index < 0) {
			throw new RuntimeException("Index out of bounds");
		}
		if (!(params[index] instanceof Class<?>)) {
			return Object.class;
		}
		result = (Class<?>) params[index];
		genericActualTypeCacheMap.put(key, result);
		return result;
	}

    public static boolean isGenericNumberType(Class<?> inClass){
        if(int.class == inClass){
            return true;
        }
        if(long.class == inClass){
            return true;
        }
        if(float.class == inClass){
            return true;
        }
        if(double.class == inClass){
            return true;
        }
        if(short.class == inClass){
            return true;
        }
        if(byte.class == inClass){
            return true;
        }
        if(char.class == inClass){
            return true;
        }
        return false;
    }

    public static boolean isObjectNumberType(Class<?> inClass){
        if(Integer.class == inClass){
            return true;
        }
        if(Long.class == inClass){
            return true;
        }
        if(Float.class == inClass){
            return true;
        }
        if(Double.class == inClass){
            return true;
        }
        if(Short.class == inClass){
            return true;
        }
        if(Byte.class == inClass){
            return true;
        }
        return false;
    }

    public static boolean isArrayType(Class<?> inClass){
        return inClass.isArray();
    }

    public static boolean isCollectionType(Class<?> inClass){
        return Collection.class.isAssignableFrom(inClass);
    }

    public static boolean isMapType(Class<?> inClass){
        return Map.class.isAssignableFrom(inClass);
    }

    public static boolean isListType(Class<?> inClass){
        return List.class.isAssignableFrom(inClass);
    }

    public static boolean isSetType(Class<?> inClass){
        return Set.class.isAssignableFrom(inClass);
    }

    public static <T> T newInstance(Class<T> typeClass) throws IllegalAccessException, InstantiationException {
        return typeClass.newInstance();
    }

    public static <T> T newInstance(Class<T> typeClass,Object... args) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?>[] argsClasses = new Class[args.length];
        int i=0;
        for(Object arg : args){
            argsClasses[i++] = arg.getClass();
        }
        return newInstance(typeClass,argsClasses,args);
    }

    public static <T> T newInstance(Class<T> typeClass,Class<?>[] argsClasses, Object[] constructorArgs) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException  {
        Constructor<T>  cons = typeClass.getConstructor(argsClasses);
        return cons.newInstance(constructorArgs);
    }
}
