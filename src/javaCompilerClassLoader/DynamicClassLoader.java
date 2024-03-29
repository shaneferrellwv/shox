package edu.ufl.cise.plcsp23.javaCompilerClassLoader;

import java.lang.reflect.Method;

public class DynamicClassLoader extends ClassLoader {
	public DynamicClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	/**
	 * Executes indicated method defined in bytecode and returns the result. args is
	 * an Object[] containing the parameters of the method, or may be null if the
	 * method does not have parameters.
	 * 
	 * Requires that the given method is not overloaded in the class file.
	 * 
	 * @param bytecode
	 * @param className
	 * @param methodName
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public static Object loadClassAndRunMethod(byte[] bytecode, String className, String methodName, Object[] args) throws Exception {
		Class<?> testClass = getClass(bytecode, className);
		return runMethod(testClass,methodName, args);
	}

	private static Method findMethod(String name, Method[] methods) {
		for (Method m : methods) {
			String methodName = m.getName();
			if (name.equals(methodName))
				return m;
		}
		throw new RuntimeException("Method " + name + " not found in generated bytecode");
	}

	static Class<?> getClass(byte[] bytecode, String className) throws Exception {
		DynamicClassLoader loader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
		Class<?> testClass = loader.define(className, bytecode);
		return testClass;
	}

	public Class<?> define(String className, byte[] bytecode) {
		return super.defineClass(className, bytecode, 0, bytecode.length);
	}

	static Object runMethod(Class<?> testClass, String methodName, Object[] args) throws Exception {
		Method[] methods = testClass.getDeclaredMethods();
		Method m = findMethod(methodName, methods);
		return m.invoke(null, args);
	}

	
}
