package com.webproject.da.hlog.tool;

import java.util.concurrent.LinkedBlockingQueue;

import com.webproject.da.hlog.base.LoggerUtil;
import com.webproject.da.hlog.bean.LogBean;

public class LogQueue {
		private static LinkedBlockingQueue<LogBean> queue = new LinkedBlockingQueue<LogBean>();
		private static  int logThreadNum = 1;
		
		private static LogFormat format = new LogFormat();
		
		public static void add(Object[] args){
			queue.add(new LogBean(args));
		}
		public static void add(LogBean bean){
			queue.add(bean);
		}

		public static void setLogFormat(LogFormat format){
			LogQueue.format = format;
		}
		
		
		/**
		 * 禁止外部new
		 */
		private LogQueue(){
			//只有一个地方可以调用，不存在线程安全问题
			String threadnnum = System.getProperty("da.hlog.thread.num");
			if(threadnnum != null){
				try {
					int num = Integer.parseInt(threadnnum);
					if(num > 0 && num < 10)
						LogQueue.logThreadNum = num ;
				}catch(Exception e) {
					System.err.println(e.getMessage());
				}
			}

		}
		/**
		 * 静态启动内部class
		 */
		static {
			LogQueue logQueue = new LogQueue();
			//根据配置启动多线程
			for(int i =0;i < logThreadNum ;i ++){
				System.out.println("LogQueue start   :" + i );
				logQueue.run();
			}
		}
		
		//	//只有一个地方可以调用，不存在线程安全问题
		public void run(){
			LogRecordRun lr = new LogRecordRun();
			lr.start();
		}
		
		/**
		 * 日志进程
		 * @author jiangww
		 *
		 */
		class LogRecordRun extends Thread{
			@Override
			public void run(){
				while (true) {
					LogBean mb = null;
					try{
						mb = queue.take();
						String u = LogQueue.format.Format(mb);
						if(u != null)
							LoggerUtil.info(u);
					}catch(Exception e){
						System.out.println(e.getMessage());
					}finally{
						if(mb != null){
							mb.done();
						}
					}
				}
			}
		}
		
}
