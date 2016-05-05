### 这个是干嘛的
答: 超级轻量的日志包，web 或普通jar 通用， 标准化格式。支持文件和dfs，作为打印日志的补充。

### 需要配置的内容

1. 基础配置：
    使用 System.setProperty() 设置参数
    
    包含以下：
    
        da.hlog.name ： log标识，打印时前面做区分，相当于项目ID
        
		da.hlog.filePath ： 日志输出路径 （hdfs 打头表示存入Hdfs 系统）
		
		da.hlog.filesize ： 每个文件大小  (<=0 不设置文件系统最大2G ，hdfs 无上限 ,默认100m)
		
		da.hlog.filenum ： 最多几个文件  （<=0  无限文件 文件系统 500 个 hdfs 无上线，默认2个）
		
		da.hlog.level ： 日志级别 （debug ， inf ，error）
		
    Web 或者需要任何需要使用到 代理切面日志的：
    
        da.hlog.thread.num ： 日志打印类线程数 （0~10）
        
        da.hlog.print.all ： 是否默认打印，默认是false 既 带有注解的对象和方法打印，改成true 既注解配置 ignore 的不打印其他全部打印。
        

2. Web端需要实现的类：
     继承 LogFormat 重写 String getUserName(HttpSession session) 获取用户名
     


### 使用的范例
Luopa 系统对所有访问进行监控。


1. 打包后加入Mave 仓库：
``` xml
    <dependency>
		<groupId>da.hlog</groupId>
		<artifactId>da.hlog</artifactId>
			<version>1.0.4</version>
	</dependency>
```

2. 普通使用：

   LoggerUtil 下的static 方法。

  包含几个级别的日志打印。格式化些基础信息。

  LogFormat  对标有注解（或者系统配置成all 之后所有传入值）的对象，进行格式化。 


#### WEB 使用

3. 重写用户获取类
``` java
    public class LuopanLog extends LogFormat{

	@Override
	protected String getUserName(HttpSession session){
		if(session == null )return "unlogin user";
		User sessionUser = (User) session.getAttribute(User.SESSION_USER);
		if(sessionUser == null){
			return "unlogin user";
		}
		return sessionUser.getUsername();
	}
}
```

4. 在Spring 里配置：
``` xml
     <!-- 初始化自定义log类 如果不需要自定义也可以不加 -->
     <bean id="logformat" class="com.xxx.xx.xxx.log.LuopanLog"  />
	 <!-- 初始化切面类 把自定义加入 -->
	  <bean id="logAspect" class="com.webproject.da.hlog.aop.LogAspect" >
	 		 <property name="logFormat" ref="logformat"></property>  
	  </bean>
	  <!-- 监控所有 controller 包下的方法 -->
	<aop:config>
		<aop:pointcut id="logPoint" expression="execution(public * com.xxxx.xx.xxxx.controller.*.*(..)) " />
		<aop:advisor pointcut-ref="logPoint" advice-ref="logAspect" />
	</aop:config> 
```

5. 需要打印日志的方法前加注解。
``` java
    @RequestMapping(value = "/admin", method = {RequestMethod.GET, RequestMethod.POST})
    @HLog
    public String admin(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        final String basePath = getBasePath(request);
        logger.info(basePath);
        String login = loginService.getRedirectUrl(session, basePath);
        return "redirect:" + login;
    }



```
输出文件
2016-04-20 16:30:42 info [com.webproject.da.hlog.tool.LogQueue$LogRecordRun->run:74]: Call Class:com.xxx.xxx.xxx.xxx.xxxx-> Method:summary ->paramer:{user:xxx@gmail.com ,Request :/overview/summary ,from:127.0.0.1 ,refer from:null ,User-Agent:Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36 ,Parameter:{}}
2016-04-20 16:30:43 info [com.xxx.xxx.xxx.xxx.xxx$LogRecordRun->run:74]: Call Class:com.xxx.xxx.xxx.xxx.LoginController-> Method:getUserName ->paramer:{user:xxx@gmail.com ,Request :/getUserName ,from:127.0.0.1 ,refer from:http://127.0.0.1:8080/overview/summary ,User-Agent:Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36 ,Parameter:{}}
```


### 常见问题

1. 方法中的对象无法打印日志怎么办? A: 方法中的非JAVA自带类（除了 java原生包里的int，String，map, Exception等），默认不打印，要打印请在对象前加 @HLog ep:public String admin(@Hlog User user)

2. Controller 方法都记录了啥？ A:  HttpServletRequest 的会记录 访问用户的IP ，请求地址，头信息，refer 其request 只记录访问IP

3. 待补充



### 欢迎提供代码

RT.
欢迎贡献更多代码。