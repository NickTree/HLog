package com.webproject.da.hlog.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.webproject.da.hlog.base.LoggerUtil;
import com.webproject.da.hlog.bean.LogBean;
import com.webproject.da.hlog.bean.MethodLogBean;
import com.webproject.da.hlog.tool.LogFormat;
import com.webproject.da.hlog.tool.LogQueue;

/**
 * 切面日志，用户spring 的web 工程
 * @author jiangww
 *
 */
public class LogAspect implements MethodInterceptor {

	private  LogFormat logFormat;
	
	public Object invoke(MethodInvocation invocation) throws Throwable {
		System.out.println("invoke start.");
		MethodLogBean logbean = new MethodLogBean(invocation);
		LogQueue.add(logbean);
		try{
		Object result =   invocation.proceed();
		return result;
		}catch(Throwable t){
			System.out.println("invoke error.");
			LoggerUtil.error("[" + Thread.currentThread().getName() + "] " + invocation.getMethod().getName() + " catch error >" + t.toString());
			throw t;
		}finally {
			logbean.join();
		}

	}

	public LogFormat getLogFormat() {
		return logFormat;
	}

	public void setLogFormat(LogFormat logFormat) {
		this.logFormat = logFormat;
		if(logFormat != null ){
			LogQueue.setLogFormat(logFormat);
		}
	}
	
}