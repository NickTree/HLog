package com.webproject.da.hlog.bean;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

/**
 * 切面类
 * @author jiangww
 *
 */
public class MethodLogBean extends LogBean {
	private Method method;
	
	public MethodLogBean(Method method, Object[] args) {
		super(args);
		this.method = method;
	}

	public MethodLogBean(MethodInvocation invocation) {
		super(invocation.getArguments());
		this.method = invocation.getMethod();
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	
}
