package com.webproject.da.hlog.base;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * 定义的基本格式
 * @author jiangww
 *
 */
class HLogFormatter extends Formatter {
	private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    @Override 
    public String format(LogRecord record) { 
    	StringBuffer sb  = new StringBuffer();
    	sb.append( format.format(new Date(record.getMillis()))).append(" ").append(record.getMessage()).append("\n");
         return sb.toString(); 
    } 
}