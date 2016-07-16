package com.lamfire.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ObjectUtils {

	/**
	 * 设置对象属性
	 * 
	 * @param instance
	 * @param propertyValues
	 */
	public static void setPropertiesValues(Object instance, Map<String, Object> propertyValues) {
		if (instance==null || propertyValues == null || propertyValues.isEmpty())
			return;
		try {
			PropertyDescriptor[] pdArray = ClassUtils.getPropertyDescriptorsArray(instance.getClass());
			if(pdArray == null)return;
			for (PropertyDescriptor descriptor : pdArray) {
				String propertyName = descriptor.getName();
				Object value = propertyValues.get(propertyName);
				if (value != null) {
					setPropertyValue(instance, descriptor, value);
				}
			}
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 设置对象属性
	 * 
	 * @param instance
	 * @param name
	 * @param value
	 */
	public static void setPropertyValue(Object instance, String name, Object value) {
		PropertyDescriptor pd = ClassUtils.getPropertyDescriptor(instance.getClass(), name);
		if (pd == null){
            throw new RuntimeException("the property '" + name + "' not found - " + instance.getClass().getName());
        }
		setPropertyValue(instance, pd, value);
	}

	/**
	 * 获得属性值
	 * 
	 * @param instance
	 * @param propertyName
	 * @return
	 */
	public static Object getPropertyValue(Object instance, String propertyName) {
		PropertyDescriptor pd = ClassUtils.getPropertyDescriptor(instance.getClass(), propertyName);
		if (pd == null){
			throw new RuntimeException("the property '" + propertyName + "' not found");
		}
		return readPropertyValue(instance, pd);

	}

    public static Field getField(Object target, String fieldName) {
          return ClassUtils.getField(target.getClass(),fieldName);
    }

    public static Object getFieldValue(Object target, String fieldName)  {
        Field field = getField(target,fieldName);
        if(field == null){
            throw new RuntimeException("Field[" + fieldName +"] not found - " + target.getClass().getName());
        }
        return getFieldValue(field,target);
    }

    public static Object getFieldValue(Field field,Object bean)  {
        boolean hasChangeAccessibleFlag = false;
        if(!field.isAccessible()){
            field.setAccessible(true);
            hasChangeAccessibleFlag = true;
        }
        try {
            Object result = field.get(bean);
            return result;
        } catch (IllegalAccessException e) {

        } finally {
            if(hasChangeAccessibleFlag){
                field.setAccessible(false);
            }
        }
        return null;
    }

    public static void setFieldValue(Object instance, String fieldName,Object value)  {
        Field field = getField(instance,fieldName);
        if(field == null){
            throw new RuntimeException("Field[" + fieldName +"] not found - " + instance.getClass().getName());
        }
        setFieldValue(instance,field,value);
    }

    public static void setFieldValue(Object instance,Field field,Object value)  {
        boolean hasChangeAccessibleFlag = false;
        if(!field.isAccessible()){
            field.setAccessible(true);
            hasChangeAccessibleFlag = true;
        }
        try {
            field.set(instance,value);
        } catch (IllegalAccessException e) {

        } finally {
            if(hasChangeAccessibleFlag){
                field.setAccessible(false);
            }
        }
    }

    protected static Object readPropertyValue(Object objInstance , PropertyDescriptor descriptor){
		if (descriptor == null){
			return null;
        }

		Method getter = descriptor.getReadMethod();
		if (getter == null) {
			return getFieldValue(objInstance,descriptor.getName());
		}
		return invokeMethod(objInstance, getter);
	}
	
	protected static void writePropertyValue(Object objInstance , PropertyDescriptor descriptor,Object value){
		if (descriptor == null){
			return ;
		}
		Method setter = descriptor.getWriteMethod();
		if (setter == null) {
			setFieldValue(objInstance,descriptor.getName(),value);
            return;
		}
		invokeMethod(objInstance, setter,value);
	}

	/**
	 * 设置对象属性
	 * 
	 * @param instance
	 * @param descriptor
	 * @param value
	 */
	protected static void setPropertyValue(Object instance, PropertyDescriptor descriptor, Object value) {
		if (instance==null || descriptor == null){
			return;
		}
		
		if(value == null){
			//调用写入属性方法进行附值
            writePropertyValue(instance, descriptor, value);
			return ;
		}
		
		Class propType = descriptor.getPropertyType();
		Object desValue = value;
		//如果是复杂对象则先转换复杂对象，再附值
		if(!value.getClass().isAssignableFrom(propType) && value instanceof Map){
			desValue = toJavaObject((Map) value, propType);
            writePropertyValue(instance, descriptor, desValue);
			return ;
		}
		
		//如果不是目标类型的子类，则进行类型转换
		if(!value.getClass().isAssignableFrom(propType)){
			desValue = TypeConvertUtils.convert(desValue, propType);
		}
		
		//调用写入属性方法进行附值
        writePropertyValue(instance, descriptor, desValue);

	}
	
	public static <T>T toJavaObject(Map<String, Object> map,Class<T> claxx){
		try {
			T desObject = claxx.newInstance();
			setPropertiesValues(desObject, map);
			return desObject;
		} catch (Exception e) {
			throw new RuntimeException("the class '" +claxx.getName() + "' not cerate new instance," + e.getMessage());
		}
	}
	
	
	public static Object invokeMethod(Object obj, Method invokeMethod, Object... args) {
		boolean hasChangeAccessibleFlag = false;
		if (!invokeMethod.isAccessible()) {
			invokeMethod.setAccessible(true);
			hasChangeAccessibleFlag = true;
		}
		try {
			return invokeMethod.invoke(obj, args);
		} catch (Exception e) {
			throw new RuntimeException("cannot invoke " + invokeMethod.getName() + " for " + obj.getClass().getName(), e);
		} finally {
			if (hasChangeAccessibleFlag) {
				invokeMethod.setAccessible(false);
			}
		}
	}
	
	public static Method getMethod(Object obj,String name,Object[] args) throws SecurityException, NoSuchMethodException{
		Class<?>[] types = null;
		if(args != null){
			types = new Class<?>[args.length];
			for(int i=0;i<args.length;i++){
				types[i] = args[i].getClass();
			}
		}
		return obj.getClass().getMethod(name, types);
	}

	/**
	 * 拷贝相同的属性值到目标对象
	 * 需要类型相同
	 * @param from
	 * @param to
	 */

	public static void copy(Object from, Object to) {
        if( ! StringUtils.equals(from.getClass().getName(),to.getClass().getName()) ){
            throw new IllegalArgumentException(from.getClass() + " - " + to.getClass() +" was not same class");
        }
        PropertyDescriptor[] propertyDescriptors = null;
        try {
            propertyDescriptors = ClassUtils.getPropertyDescriptorsArray(from.getClass());
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        if(propertyDescriptors == null){
            return;
        }

        for (PropertyDescriptor descriptor : propertyDescriptors) {
			try {
                if(descriptor.getName().equals("class")){
                    continue;
                }
				Object value = getPropertyValue(from, descriptor.getName());
				setPropertyValue(to,descriptor,value);
			} catch (Exception e) {
                e.printStackTrace();
			}
		}
	}

    public static void copyTo(Object from, Object to) {
        Field[] fields = ClassUtils.getDeclaredFields(from.getClass());
        for (Field field : fields) {
            try {
                Object value = getFieldValue(from, field.getName());
                setFieldValue(to,field.getName(),value);
            } catch (Exception e) {

            }
        }
    }

    public static void empty(Object instance){
        Field[] fields = ClassUtils.getAllFields(instance.getClass());
        for (Field field : fields) {
            try {
                if(ClassUtils.isGenericNumberType(field.getType())){
                    setFieldValue(instance,field.getName(),0);
                }else{
                    setFieldValue(instance,field, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Object clone(Serializable instance){
        byte [] bytes = Bytes.toBytes(instance);
        try {
            return Bytes.toObject(bytes);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
