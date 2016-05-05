package com.webproject.da.hlog.tool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

 /**
  * 这个注解用在class 上 field 上，指定log
  * @author jiangww
  *
  */
@Retention(RetentionPolicy.RUNTIME) 
@Target({ElementType.TYPE,ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER}) 
@Inherited
public @interface HLog {  

    public boolean ignore() default false;  
}  

