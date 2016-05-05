package com.webproject.da.hlog.bean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.webproject.da.hlog.base.LoggerUtil;

public class LogBean {	
	private Object[] args;
	private CountDownLatch countDown = new CountDownLatch(1);
	
	public LogBean(Object args) {
		this.args = new Object[1];
		this.args[1] = args;
	}
	
	public LogBean(Object[] args) {
		this.args = args;
	}

	
	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	
	public void done() {
		countDown.countDown();
	}
	
	public void join() {
		try {
			countDown.await(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LoggerUtil.error(e);
		}
	}
	
}
