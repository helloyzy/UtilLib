package tools.accessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MethodAccessor {
	
	/**
	 * 
	 * Description -- Used to invoke a object's protected/private methods with no parameters(Given that the security policy
	 *                allows to invoke protected/private methods using reflection)          
	 * @param target
	 * @param methodName
	 * @return Object -- the value this protected/private method returns, can be ignored if the method returns nothing
	 */
	public static Object invokeMethod(Object target, String methodName) throws Exception {
		// if the method do not have any parameters, should pass an empty Object array
		return invokeMethod(target, methodName, new Object[] {});
	}
	
	/**
	 * Description -- Used when the method contains only one parameter
	 * @param target
	 * @param methodName
	 * @param parameter
	 * @return Object
	 * @throws Exception
	 */
	public static Object invokeMethod(Object target, String methodName, Object parameter) throws Exception {
		return invokeMethod(target, methodName, new Object[] { parameter } );
	}
	
	/**
	 * Description -- Used to invoke a object's protected/private methods(Given that the security policy
	 *                allows to invoke protected/private methods using reflection) 
	 * @param target
	 * @param methodName
	 * @param parameters
	 * @return Object -- the value this protected/private method returns, can be ignored if the method returns nothing
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Object invokeMethod(Object target, String methodName, Object[] parameters) throws Exception {
        // extract the types from the parameters
		Class[] paramsTypes = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			paramsTypes[i] = parameters[i].getClass();
		}
		return invokeMethod(target, methodName, paramsTypes, parameters);
	}
	
	/**
	 * @param target
	 * @param methodName
	 * @param parameterClasses -- the Type for all the parameters
	 * @param parameters
	 * @return Object -- the value this protected/private method returns, can be ignored if the method returns nothing
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Object invokeMethod(Object target, String methodName, Class[] parameterClasses, Object[] parameters) throws Exception {
		return invokeMethod(target, target.getClass(), methodName, parameterClasses, parameters);
	}
	
	@SuppressWarnings("unchecked")
	public static Object invokeStaticMethod(Class targetClass, String methodName, Class[] parameterClasses, Object[] parameters) throws Exception {
		return invokeMethod(null, targetClass, methodName, parameterClasses, parameters);
	}
	
	
	/**
	 * @param target -- this can be null if you want to invoke 
	 * @param targetClass
	 * @param methodName
	 * @param parameterClasses -- the Type for all the parameters
	 * @param parameters
	 * @return Object -- the value this protected/private method returns, can be ignored if the method returns nothing
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Object invokeMethod(Object target, Class targetClass, String methodName, Class[] parameterClasses, Object[] parameters) throws Exception {
		Method m = getMethod(targetClass, methodName, parameterClasses, parameters);
		// bypass the security check
		m.setAccessible(true);
		Object result = m.invoke(target, parameters);
		return result;
	}
	
	/**
	 * Find the specified method through targetClass and all its super classes according to "methodName" and "parameterClasses"
	 * @param targetClass
	 * @param methodName
	 * @param parameterClasses
	 * @return Method or Null if we can not find the method
	 * @throws Exception -- may throw SecurityException
	 */
	@SuppressWarnings("unchecked")
	static Method methodQuickMatch(Class targetClass, String methodName, Class[] parameterClasses) throws Exception {
		try {
			return targetClass.getDeclaredMethod(methodName, parameterClasses);
		} catch (NoSuchMethodException e) {
			// if we've come to the Object class, return null
			if (targetClass.getName().equals("java.lang.Object")) {
			    return null;
			} else { // continue to search the field in the super classes
			    return methodQuickMatch(targetClass.getSuperclass(), methodName, parameterClasses);
			}
		}
	}
	
	/**
	 * Get a list of methods with the name specified through targetClass and all its super classes
	 * @param targetClass
	 * @param methodName
	 * @return List<Method>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static List<Method> getMethodThroughName(Class targetClass, String methodName) throws Exception {
		List<Method> result = new ArrayList<Method>();
		Method[] declaredMethods = targetClass.getDeclaredMethods();
		for (int i = 0; i < declaredMethods.length; i++) {
			Method method = declaredMethods[i];
			if (method.getName().equals(methodName)) {
				result.add(method);
			}
		}
		// if it is not the Object class, we just iterate through all its super classes
		if (!targetClass.getName().equals("java.lang.Object")) {
			result.addAll(getMethodThroughName(targetClass.getSuperclass(), methodName));            
		}
		return result;
	}
	
	/**
	 * @param original
	 * @param numOfParameters
	 * @return List<Method>
	 */
	private static List<Method> filterMethodThroughNumOfParameters(List<Method> original, int numOfParameters) {
		List<Method> result = new ArrayList<Method>();
		for (Iterator<Method> iterator = original.iterator(); iterator.hasNext();) {
			Method method = iterator.next();
			if (method.getParameterTypes().length == numOfParameters) {
				result.add(method);
			}
		}
		return result;
	}
	
	/**
	 * Prerequisite -- primitiveClass should be one class of the following:int,long,boolean etc.
	 * <Br> Description -- determine whether the "boxingClass" is the container class of the "primitiveClass"
	 * @param primitiveClass
	 * @param boxingClass
	 * @return boolean -- true if the "boxingClass" is just the container class of the "primitiveClass", false otherwise
	 */
	@SuppressWarnings("unchecked")
	private static boolean isPrimitiveClassMatchBoxingClass(Class primitiveClass, Class boxingClass) {
		try {
			// all boxing classes(Integer,Boolean etc.) contains a static field "TYPE" which references to its related primitive type
			Class primitiveClassOfBoxingClass = (Class)FieldAccessor.getStaticValue(boxingClass, "TYPE");
			return primitiveClass.equals(primitiveClassOfBoxingClass);
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Description -- determine whether the "parameter" is an instance of the "parameterTypeToMatch"
	 * @param type
	 * @param parameter
	 * @return boolean -- true if the "parameter" is an instance of the "parameterTypeToMatch" 
	 * including the auto-boxing technique(means that if the "parameter" is an Integer, 
	 * but the "parameterTypeToMatch" is int, we still can get a match), false otherwise
	 */
	@SuppressWarnings("unchecked")
	private static boolean isParameterMatchType(Class type, Object parameter) {
		// For special types, we only consider primitive type(int,long,byte etc.), ignore void,array,interface,enum...
		if (type.isPrimitive()) {					
			return isPrimitiveClassMatchBoxingClass(type, parameter.getClass());
		} else {
			// see whether the "parameter" can be casted to type
			return type.isInstance(parameter);
		}
	}
	
	/**
	 * filter method through "parameters", the method picked up should have the same signature as defined by the "parameters"
	 * @param methodCandidates
	 * @param parameters
	 * @return Method
	 * @throws NoSuchMethodException
	 * Note -- make sure every method in "methodCandidates" has the same number parameters as "parameters"
	 */
	private static Method filterMethodThroughParamters(List<Method> methodCandidates, Object[] parameters) throws NoSuchMethodException {
		for (Iterator<Method> iterator = methodCandidates.iterator(); iterator
				.hasNext();) {
			Method methodCandidate = iterator.next();
			boolean allMatchFlag = true;
			for (int i = 0; i < methodCandidate.getParameterTypes().length; i++) {
				// the length of methodCandidate.getParameterTypes() and the parameters should be the same
				if (!isParameterMatchType(methodCandidate.getParameterTypes()[i], parameters[i])) {
					allMatchFlag = false;
					break;
				}
			}
			// if all parameters are matched, return this method
			if (allMatchFlag) {
				return methodCandidate;
			}
		}
        // if no method can be found, throw an exception out
		throw new NoSuchMethodException("No such method");
	}
	
	@SuppressWarnings("unchecked")
	static Method methodAdvancedMatch(Class targetClass, String methodName, Object[] parameters) throws Exception {
		List<Method> candidates_ThroughName = getMethodThroughName(targetClass, methodName);
		List<Method> candidates_Filtered = filterMethodThroughNumOfParameters(candidates_ThroughName, parameters.length);
		return filterMethodThroughParamters(candidates_Filtered, parameters);
		
	}
	
	@SuppressWarnings("unchecked")
	static Method getMethod(Class targetClass, String methodName, Class[] parameterClasses, Object[] parameters) throws Exception {
		Method result;
		// first quick search the method 
		result = methodQuickMatch(targetClass, methodName, parameterClasses);
		if (result != null) {
			return result;
		}
		// if we can not find it through quickMatch, using advanced ways to find it
		return methodAdvancedMatch(targetClass, methodName, parameters);
	}
}
