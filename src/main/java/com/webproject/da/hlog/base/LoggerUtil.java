/*
 * @(#) Config.java 2014-4-24
 * 
 * Copyright 2010 NetEase.com, Inc. All rights reserved.
 */
package com.webproject.da.hlog.base;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 类Config
 *
 * @author hzwenweijiang
 * @version 2014-4-24
 */
public class LoggerUtil {
	static Logger logger;
	static Level HLevel = Level.INFO;

	static {
		String name = System.getProperty("da.hlog.name");
		String filePath = System.getProperty("da.hlog.filePath");
		String filesize = System.getProperty("da.hlog.filesize");
		String filenum = System.getProperty("da.hlog.filenum");
		String hlevel = System.getProperty("da.hlog.level");
		
		if ("debug".equalsIgnoreCase(hlevel))
			HLevel = Level.FINE;
		if ("error".equalsIgnoreCase(hlevel))
			HLevel = Level.SEVERE;
		String name2 = name == null ? "syslog" : name;
		load(name2, filePath == null ? name2 + "_%g.log" : filePath,
				filesize == null ? 10240000 : Integer.parseInt(filesize) <= 0  ? Integer.MAX_VALUE : Integer.parseInt(filesize),
				filenum == null ? 2 :Integer.parseInt(filesize) <= 0 ? 500 :  Integer.parseInt(filenum));
	}

	/**
	 * 初始化加载
	 * @param name
	 * @param filePath
	 * @param filesize
	 * @param filenum
	 */
	private static void load(String name, String filePath, int filesize, int filenum) {
		System.out.println("init log :" + name + " with path :" + filePath);

		logger = Logger.getLogger(name);
		Handler handler = null;
		try {
			if (filePath.startsWith("hdfs")) {
				//HDFS 默认安日期记录所有日志
				handler = new HdfsHandler(filePath);
			} else {
				handler = new FileHandler(filePath, filesize, filenum);
			}
			if (handler != null) {
				handler.setLevel(HLevel);
				handler.setFormatter(new HLogFormatter());
				logger.addHandler(handler);
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			LoggerUtil.error(e.toString() + " # " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LoggerUtil.error(e.toString() + " # " + e.getMessage());
		}

	}
	/**
	 * 基础信息
	 * @param s
	 * @return
	 */
	private static String getinfo(String s) {
		StackTraceElement[] temp = Thread.currentThread().getStackTrace();
		StackTraceElement b = (StackTraceElement) temp[2];
		StackTraceElement a = (StackTraceElement) temp[3];
		StringBuffer sb = new StringBuffer();
		sb.append(b.getMethodName()).append(" [").append(a.getClassName()).append("->").append(a.getMethodName())
				.append(":").append(a.getLineNumber()).append("]: ").append(s);
		return sb.toString();
	}

	/**
	 * 可直接使用的 debug级别 日志
	 * @param s
	 */
	public static void debug(String s) {
		logger.fine(getinfo(s));
	}

	/**
	 * 可直接使用的 info级别 日志
	 * @param s
	 */
	public static void info(String s) {
		logger.info(getinfo(s));
	}

	/**
	 * 可直接使用的 error级别 日志
	 * @param s
	 */
	public static void error(String s) {
		logger.severe(getinfo(s));
	}

	/**
	 * 方便使用的 error级别 日志 不推荐
	 * @param e
	 */
	public static void error(Throwable e) {
		logger.severe(getinfo(e.toString()));
	}
}
