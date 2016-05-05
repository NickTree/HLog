package com.webproject.da.hlog.tool;

import java.lang.annotation.Annotation;

public class MyParameter {

	Annotation[] annotations;
	String name;
	String packageName;
	
	@SuppressWarnings("unchecked")
	public <T>  T getAnnotation(Class<T> annotation){
		if(annotations == null)
			return null;
		for(Annotation a: annotations){
			if(a.annotationType() ==  annotation){
				return (T) a;
			}
		}
		return null;
	}
}