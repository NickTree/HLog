package com.webproject.da.hlog.tool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.webproject.da.hlog.base.LoggerUtil;
import com.webproject.da.hlog.bean.LogBean;
import com.webproject.da.hlog.bean.MethodLogBean;

public  class LogFormat {

	protected static Gson gson  =new Gson();
	private static  boolean isAll =  "true".equals(System.getProperty("da.hlog.print.all"));

	/**
	 * 打印request 的标准日志格式
	 * @param request
	 * @return
	 */
	public  String requestFormat(HttpServletRequest request){
		StringBuffer sb = new StringBuffer();
		
		HttpSession session = null;
		try{
			session = request.getSession();
		}catch(Exception e){
			
		}
		sb.append("user:").append(getUserName(session))
		.append(" ,Request :").append( request.getRequestURI())
		.append(" ,from:").append(request.getRemoteAddr()).append(" ,refer from:")
		.append(request.getHeader("Referer")).append(" ,User-Agent:")
		.append(request.getHeader("User-Agent")).append(" ,Parameter:")
		.append(gson.toJson(request.getParameterMap()));

		return sb.toString();
	}
	
	public  String requestFormat(ServletRequest request){
		StringBuffer sb = new StringBuffer();
		
		sb.append(" from:").append(request.getRemoteAddr())
		.append(gson.toJson(request.getParameterMap()));

		return sb.toString();
	}
	/**
	 * 对象格式化
	 * @param o
	 * @return
	 */
	public  String ObjectFormat(Object o){
		HLog h = Java17Tool.getDeclaredAnnotation(o, HLog.class);
		if(h != null || isAll){
			return gson.toJson(o);
		}
		boolean loged = false;
		Map<String ,Object> logmap = new HashMap<String ,Object>();
		for(Field f : o.getClass().getDeclaredFields()){
			f.setAccessible(true);
			HLog h2 =  Java17Tool.getDeclaredAnnotation(f, HLog.class);
			if(h2!= null){
				try {
					logmap.put(f.getName(), f.get(o));
					loged = false;
				} catch (Exception e) {
					LoggerUtil.error(e.getMessage());
				}
			}
		}
		return loged? gson.toJson(logmap) : null;
	}
	
	public  String CommonFormat(Object parameter[]){
		if( parameter == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for(Object o :parameter ){
			if(o.getClass().getPackage() == null){
				sb.append(" prarameter:").append(String.valueOf(o));
				continue;
			}
			if(o.getClass().getPackage().toString().contains("Java Platform API ")){
				sb.append(" prarameter:").append(gson.toJson(o));
				continue;
			}
			String olog = ObjectFormat(o);
			if(olog == null) {
					 olog = o.toString();
			 }
			sb.append(olog);
		}
		return sb.toString();
	}
	
	
	public  String	 Format(LogBean lb){
		if(lb instanceof MethodLogBean) {
			MethodLogBean mlb = (MethodLogBean) lb;
			return MethodFormat(mlb.getMethod() ,mlb.getArgs());
		} else {
			return CommonFormat(lb.getArgs());
		}
	}


	/**
	 * 切面方法日志 主要面对web工程
	 * @param m
	 * @param parameter
	 * @return
	 */
	@HLog
	public  String MethodFormat(Method m ,Object parameter[]){
		if(m == null){
			return null;
		}
		HLog h = Java17Tool.getDeclaredAnnotation(m, HLog.class);
		if(h == null && !isAll){
			return null;
		}
		MyParameter[] ps = Java17Tool.getParameter(m);
		
		
		if (parameter == null || parameter.length != ps.length){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("Call Class:").append(m.getDeclaringClass().getName()).append("-> Method:")
		.append(m.getName()).append(" ->paramer:{");
		
		for(int i = 0; i < parameter.length ; i++){
			MyParameter p = ps[i];
			Object o = parameter[i];
			HLog h2 = p.getAnnotation(HLog.class);
			if(o instanceof HttpServletRequest){
				sb.append(requestFormat((HttpServletRequest)o));
				continue;
			}
			if(p.packageName== null){
				sb.append(" name:").append(p.name).append(" value:").append(String.valueOf(o));
				continue;
			}
			if(p.packageName.contains("Java Platform API ")){
				sb.append(" name:").append(p.name).append(" value:").append(gson.toJson(o));
				continue;
			}
			if(h2 != null || isAll){
				 String olog = ObjectFormat(o);
				 if(olog == null) {
					 olog = o.toString();
				 }
				 sb.append(olog);
			}
		}
		sb.append("}");
		return sb.toString()
				;

	}
	
	
	/**
	 * //需要继承然后重写 get user name 方法，来获得日志
	 * @param session
	 * @return
	 */
	protected String getUserName(HttpSession session){
		if(session == null )return "unlogin user";
		return 	session.getId();
	}
	
	@HLog
	String getUserName(){
		return "";
	}
}
