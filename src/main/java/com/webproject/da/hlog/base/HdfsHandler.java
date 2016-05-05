package com.webproject.da.hlog.base;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * 还未实现，很好实现的，需要加入hdfs jar 包 暂部支持
 * @author jiangww
 *
 */
public class HdfsHandler extends Handler{
	private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
	int day = -1;
	OutputStreamWriter writer;
	String hdfsPath;
	FileSystem fs;
	static final String LOCK  = "lock";
	private boolean close = true;
	public HdfsHandler(String hdfsPath){
		Configuration conf = new Configuration();  
		try {
			fs=FileSystem.get(conf);
			this.hdfsPath = hdfsPath;
			resetWriter();
			close = true;
		} catch (IOException e) {
			reportError("cat connect hdfs", e, 1);
			close = false;
			Date date = new Date();
			day = date.getDate();
			writer =  new OutputStreamWriter(System.out);
		}  

	}
	
	private void resetWriter() throws IllegalArgumentException, IOException{
		Date date = new Date();
		day = date.getDate();
		Path basepath = new Path(hdfsPath);
		if( !fs.exists(basepath)) {
			fs.mkdirs(basepath);
		}
		String filePath = hdfsPath + "/" + format.format(date);
		Path logPath = new Path(filePath);
		OutputStream out=fs.create(logPath);  
		writer = new OutputStreamWriter(out);
	}
	
	
	@Override
	public void publish(LogRecord record) {
		try {
			
			synchronized (LOCK) {
				Date date = new Date();
				if( day != date.getDate()){
					 close();
					 resetWriter();
				}
			}
			
			writer.write(getFormatter().format(record));
		} catch (IOException e) {
			reportError("write to  hdfs", e, 1);
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush() {
		try {
			writer.flush();
		} catch (IOException e) {
			reportError("flush to hdfs", e, 1);
		}
		
	}

	@Override
	public void close() throws SecurityException {
		try {
			if(close){
				writer.close();
			}
		} catch (IOException e) {
			reportError("close to hdfs", e, 1);
		}
	}

}
