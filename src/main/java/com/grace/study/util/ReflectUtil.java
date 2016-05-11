package com.grace.study.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

public class ReflectUtil {
	public ReflectUtil(){
	}
	
	private static Class getClass(String className){
		Class c = null;
		if (className != null) {
			try {
				c = Class.forName(className);
				System.out.println(c);
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(ReflectUtil.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
		return c;
	}
	
	private static Object getInstance(String className, Class[] classParas,
			Object[] paras) {
		Object o = null;
		if (className != null) {
			try {
				Class c = getClass(className);
				if (c != null) {
					Constructor con = c.getConstructor(classParas);
					if (con != null) {
						try {
							o = con.newInstance(paras);
						} catch (InstantiationException e) {
							Logger.getLogger(ReflectUtil.class.getName()).log(
									Level.SEVERE, null, e);
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							Logger.getLogger(ReflectUtil.class.getName()).log(
									Level.SEVERE, null, e);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							Logger.getLogger(ReflectUtil.class.getName()).log(
									Level.SEVERE, null, e);
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							Logger.getLogger(ReflectUtil.class.getName()).log(
									Level.SEVERE, null, e);
						}
					}
				}

			} catch (NoSuchMethodException ex) {
				Logger.getLogger(ReflectUtil.class.getName()).log(
						Level.SEVERE, null, ex);
			} catch (SecurityException ex) {
				Logger.getLogger(ReflectUtil.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
		return o;
	}
	
	private static Class[] getValueClass(Object ... parames){
		ArrayList<Object> objectList = new ArrayList<Object>();
		ArrayList<Class> classList = new ArrayList<Class>();
		if (parames != null) {
			for (Object o : parames) {
				objectList.add(o);
			}
			Iterator it = objectList.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				classList.add(obj.getClass());
			}
			Class[] classParams = new Class[classList.size()];
			classList.toArray(classParams);
			return classParams;
		}
		return null;
	}
	
	public static Object getInstance(String className,Object []paras){
		Object obj = null;
		if(paras != null) {
			Class[] classParas = getValueClass(paras);
			if( classParas != null && className != null){
				obj = getInstance(className,classParas,paras);	
			}
		}
		return obj;
	}
	
	public static Object getInstance(String className){
		Object obj = null;
		if(className != null){
			Class c = getClass(className);
			if(c != null){
				try {
					Constructor con = c.getConstructor();
					if(con != null){
						try {
							obj = con.newInstance();
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							Logger.getLogger(ReflectUtil.class.getName()).log(
									Level.SEVERE, null, e);
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							Logger.getLogger(ReflectUtil.class.getName()).log(
									Level.SEVERE, null, e);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							Logger.getLogger(ReflectUtil.class.getName()).log(
									Level.SEVERE, null, e);
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							Logger.getLogger(ReflectUtil.class.getName()).log(
									Level.SEVERE, null, e);
						}
					}
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					Logger.getLogger(ReflectUtil.class.getName()).log(
							Level.SEVERE, null, e);
				}
				catch (SecurityException e){
					// TODO Auto-generated catch block
					Logger.getLogger(ReflectUtil.class.getName()).log(
							Level.SEVERE, null, e);
				}
			}
		}
		return obj;
	}
	
	public static List<Method> getAllMethods(String className){
		List<Method> methodsList = new ArrayList<Method>();
		Object o = getInstance(className);
		if(o != null){
			Method[] methods = o.getClass().getDeclaredMethods();
			for(int i = 0;i<methods.length;i++){
				methodsList.add(methods[i]);
			}
		}
		return methodsList;
	}
	
	
	public static List<String> getAllMethodsName(String className){
		List<String> methodsNameList = new ArrayList<String>();
		Object o = getInstance(className);
		if(o != null){
			Method[] methods = o.getClass().getDeclaredMethods();
			for(int i = 0;i<methods.length;i++){
				methodsNameList.add(methods[i].getName());
			}
		}
		System.out.println("getAllMethodsName is " + methodsNameList);
		return methodsNameList;
	}
	
	public static List<String> getAllTestCasesMethodsName(String className){
		List<String> methodsNameList = new ArrayList<String>();
		Class<?> classFromName = null; 
		try {
			classFromName = Class.forName(className);
			for (Method method : classFromName.getMethods()) { 
				 if (method.getAnnotation(Test.class) != null){
					 methodsNameList.add(method.getName());
				 }
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(" $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$   methodsNameList is : " + methodsNameList);
		return methodsNameList;
	}
}
