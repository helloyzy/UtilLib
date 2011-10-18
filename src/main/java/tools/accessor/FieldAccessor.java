package tools.accessor;

import java.lang.reflect.Field;

public class FieldAccessor {

	/**
	 * @param target
	 * @param propertyName
	 * @return Object
	 * @throws Exception
	 */
	public static Object getValue(Object target, String propertyName) throws Exception {
		return getValue(target.getClass(), target, propertyName);
	}
	
	/**
	 * @param target
	 * @param propertyName
	 * @param value
	 * @throws Exception
	 */
	public static void setValue(Object target, String propertyName, Object value) throws Exception {
		setValue(target.getClass(), target, propertyName, value);
	}
	
	@SuppressWarnings("unchecked")
	public static Object getStaticValue(Class targetClass, String propertyName) throws Exception {
		return getValue(targetClass, null, propertyName);
	}
	
	@SuppressWarnings("unchecked")
	public static void setStaticValue(Class targetClass, String propertyName, Object value) throws Exception {
		setValue(targetClass, null, propertyName, value);
	}
	
	@SuppressWarnings("unchecked")
	public static Object getValue(Class targetClass, Object target, String propertyName) throws Exception {
		Field field = getField(targetClass, propertyName);
		// Essentially, a child class should holds all variables its super class holds, only that
		// some fields are "hidden" by its modifier, by setting accessible to true, these "hidden" fields will become visible to child class  
		field.setAccessible(true);
		return field.get(target);
	}	
	
	@SuppressWarnings("unchecked")
	public static void setValue(Class targetClass, Object target, String propertyName, Object value) throws Exception {
		Field field = getField(targetClass, propertyName);
		// Essentially, a child class should holds all variables its super class holds, only that
		// some fields are "hidden" by its modifier, by setting accessible to true, these "hidden" fields will become visible to child class
		field.setAccessible(true);
		field.set(target, value);
	}
	
	/**
	 * Try to get a given field through the name in the "targetClass" and all its super classes
	 * @param targetClass
	 * @param propertyName
	 * @return Field 
	 * @throws NoSuchFieldException -- if we iterate to the Object class but still can not get the field, throw this exception out
	 */
	@SuppressWarnings("unchecked")
	static Field getField(Class targetClass, String propertyName) throws NoSuchFieldException {		
		try {
		    return targetClass.getDeclaredField(propertyName);
		} catch(NoSuchFieldException e) {
			// if we've come to the Object class, just throw exception out indicating we can not find the field
			if (targetClass.getName().equals("java.lang.Object")) {
			    throw e;
			} else { // continue to search the field in the super classes
			    return getField(targetClass.getSuperclass(), propertyName);
			}
		}
	}
	
}
