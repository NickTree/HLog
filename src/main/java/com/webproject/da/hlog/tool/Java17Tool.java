package com.webproject.da.hlog.tool;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 支持jdk1.7 如果是1.8 就不需要支持
 * @author jiangww
 *
 */
public class Java17Tool {
	private static boolean isJava8 = false;

	public static boolean isJava8() {
		return isJava8;
	}

	static {
		String javaVersion = System.getProperty("java.version");
		System.out.println("javaVersion=" + javaVersion);
		if (javaVersion.contains("1.8.")) {
			isJava8 = true;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T>  T getDeclaredAnnotation(Method m,Class<T> annotation){
		if(m == null || annotation == null )
			return null;
		
		
		/*if(isJava8)
			return m.getDeclaredAnnotation(annotation);*/
		
		
		if(m.getDeclaredAnnotations()!= null){
		for(Annotation a:  m.getDeclaredAnnotations()){
			if(a.annotationType() ==  annotation){
				return (T) a;
			}
		}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T>  T getDeclaredAnnotation(Object  o ,Class<T> annotation){
		if(o == null || annotation == null )
			return null;
		
		/*if(isJava8)
			return o.getClass().getDeclaredAnnotation(annotation);*/
		
		
		if(o.getClass().getDeclaredAnnotations()!= null){
		for(Annotation a:  o.getClass().getDeclaredAnnotations()){
			if(a.annotationType() ==  annotation){
				return (T) a;
			}
		}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T>  T getDeclaredAnnotation(Field  f ,Class<T> annotation){
		if(f == null || annotation == null )
			return null;
		
		/*if(isJava8)
			return f.getDeclaredAnnotation(annotation);*/
		
		if(f.getDeclaredAnnotations()!= null){
		for(Annotation a: f.getDeclaredAnnotations()){
			if(a.annotationType() ==  annotation){
				return (T) a;
			}
		}
		}
		return null;
	}

	public static MyParameter[]  getParameter(Method m){
		
		if(m == null )
			return new MyParameter[0] ;
		
		MyParameter[] p;
	/*	if(isJava8) {
			Parameter17[] p = new Parameter17[m.getParameters().length];
			for( ){
				Parameter
			}
		}*/
		
		p = new MyParameter[m.getParameterTypes().length];
		for(int i = 0 ;i< p.length; i++){
			MyParameter p1 = new MyParameter();
			p1.annotations = m.getParameterAnnotations()[i];
			p1.name = "arg" + i;
			Package pack =  m.getParameterTypes()[i].getPackage();
			p1.packageName = pack == null ? null : pack.toString();
			p[i] = p1;
		}
		
		return p;
	}
	
	
	
	public static void main(String args[]) throws NoSuchMethodException, SecurityException{
		Method m = LogFormat.class.getDeclaredMethod("getUserName");
		System.out.println(getDeclaredAnnotation(m,HLog.class));
		for(MyParameter mp : getParameter(m)){
			System.out.println(mp.name + " -- " + mp.packageName + " ----" +mp.getAnnotation(HLog.class));
		}
		
	}
	
	
	
}
