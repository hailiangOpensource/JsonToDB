package com.tv189.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.FileAppender;

import tools.DynamicResource;

import com.google.gson.Gson;
import com.tv189.domain.Channel;
import com.tv189.domain.JProgram;
import com.tv189.domain.ResponseObject;
import com.tv189.interfac.ChannelCRUDInterface;
import com.tv189.interfac.JsonToChannelInterface;
import com.tv189.interfac.JsonToProgramInterface;
import com.tv189.interfac.ProgramCRUDInterface;
import com.tv189.interfacImpl.ChannelCRUDInterfaceImpl;
import com.tv189.interfacImpl.JsonToChannelInterfacImpl;
import com.tv189.interfacImpl.JsonToProgramInterfacImpl;
import com.tv189.interfacImpl.ProgramCRUDInterfaceImpl;
import com.tv189.log.LogConfigHelper;
import com.tv189.log.MyLogger;
import com.tv189.log.MyLoggerManager;

/**
 * Servlet implementation class ChannelAndProgram
 */
public class ChannelAndProgramServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JsonToProgramInterface jsonToProgramInterface;
	private ProgramCRUDInterface programCRUDInterface;
	private JsonToChannelInterface jsonToChannelInterface;
	private ChannelCRUDInterface channelCRUDInterface;
	
	 @Override
		public void init() throws ServletException {
			super.init();
			this.jsonToProgramInterface = new JsonToProgramInterfacImpl();
			this.programCRUDInterface = new ProgramCRUDInterfaceImpl();
			this.jsonToChannelInterface = new JsonToChannelInterfacImpl();
			this.channelCRUDInterface = new ChannelCRUDInterfaceImpl();
			
			
			MyLoggerManager myLoggerManager = new MyLoggerManager();
			myLoggerManager.initThread();
		}
       
     @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {
    		request.setCharacterEncoding("UTF-8");
    	 Gson gson = new Gson();
         PrintWriter pwriter = response.getWriter();
         String flag ="0";
         String msg = "操作成功！！！";
         ResponseObject responseObj = new ResponseObject(flag, msg, null);
		/*
		 * 获取synctype、    method、content参数	 
		 */
    	String synctype = request.getParameter("synctype");
    	String method = request.getParameter("method");
    	//1:通过getParameter获取content
    	String content=request.getParameter("content");
//    	String content = new String(request.getParameter("content").getBytes("UTF-8"),"UTF-8");
    	System.out.println(content);
    	
    	//2:通过getParameterMap获取content
    	if(content==null || content.length()==0){
    		Map<String,String[]> map=request.getParameterMap();
    		String[] s = map.get("content");
        	if(s!=null && s.length>0){
        		content = s[0];
             	}
        	}
    	
	    //3:通过post输入流获取content
		if(content==null || content.length()==0){
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF-8"));  
	     	String line = null;  
	     	StringBuilder sb = new StringBuilder();  
	     	while ((line = br.readLine()) != null) {  
	     	    sb.append(line);  
	     	} 
	     	content = sb.toString();
		   }
		
		MyLogger log = new MyLogger(LogConfigHelper.getHttpInfoLoggerName(), "一条新请求："+request.getProtocol()+request.getLocalPort()+request.getRemoteAddr()+request.getRequestURI()+request.getQueryString());
		MyLoggerManager.addLogger(log);
		
    	/*
		 * 根据synctype、    method的不同值执行四种相应不同处理
		 */
     	List<JProgram> jProgramList = new ArrayList<JProgram>();
     	List<JProgram> programs = new ArrayList<JProgram>();
     	Channel channel = new Channel();
     	Channel channelResult = new Channel();
     	if(content!=null && content.length()!=0 && synctype!=null && synctype.length()!=0 && method!=null&& method.length()!=0 ){
     	   String jsonSrc = content;
     	
	    	 if(synctype.equals("liveProgram")){
	    		 //json转为Program
	    		 try {
					jProgramList = jsonToProgramInterface.jsonToProgram(jsonSrc);
					MyLogger log1 = new MyLogger(LogConfigHelper.getHttpInfoLoggerName(), "转换前json数据:"+content+"转换为节目单列表(List<JProgram>)格式成功，节目单条数为:"+jProgramList.size());
					MyLoggerManager.addLogger(log1);
				} catch (SQLException e1) {
					e1.printStackTrace();
					MyLoggerManager.printInfo("json转为节目单对象出错"+e1);
				}
	    		//1、直播节目单的发布处理：
	    		 if(method.equals("publish")){
	    		 try {
	    			for(JProgram jprogram:jProgramList){
	    			programs =programCRUDInterface.findProByLiveIdAndDate(jprogram.getLiveId(), jprogram.getProgramDate());
	    			}
	    			if(programs!=null){
	    				for(JProgram jpro:programs){
	    				programCRUDInterface.delProByLiveIdAndCreateTime(jpro.getLiveId(), jpro.getProgramDate());
	    				 MyLogger log3 = new MyLogger(LogConfigHelper.getHttpInfoLoggerName(), "插入前，数据库中查询到了"+programs.size()+"条的节目单记录，并将其成功删除");
						 MyLoggerManager.addLogger(log3);
	    				}
	    			}
	    			for(JProgram jpro:jProgramList){
	    				programCRUDInterface.insertPro(jpro);

	    				}
				} catch (SQLException e) {
					flag="1";
		 			msg="直播节目单发布失败！！！";
					e.printStackTrace();
					
				}
	    		 }
	    		 //2、直播节目单的下线处理：
	    		 if(method.equals("cancel")){
	    			 try {
	    				// jProgramList = jsonToProgramInterface.jsonToProgram(jsonSrc);
	 	    				for(JProgram jpro:jProgramList){
	 	    				programCRUDInterface.delProByLiveIdAndCreateTime(jpro.getLiveId(), jpro.getProgramDate());
	 	    				MyLogger log3 = new MyLogger(LogConfigHelper.getHttpInfoLoggerName(), "成功删除"+jProgramList.size()+"条的节目单记录");
	 						 MyLoggerManager.addLogger(log3);
	 	    				}
	    		 } catch (SQLException e) {
	    			    flag="1";
			 			msg="直播节目单下线失败！！！";
						e.printStackTrace();
					}
				}
	    	 }
    	
    	 if(synctype.equals("liveContent")){
    		 //json转换为Channel
    		 try {
    			 channel = jsonToChannelInterface.jsonToChannel(jsonSrc);
    			 MyLogger log2 = new MyLogger(LogConfigHelper.getHttpInfoLoggerName(), "转换前json数据:"+content+"\njson转换为频道(Channel)对象格式成功，频道的LiveId为："+channel.getLiveId());
					MyLoggerManager.addLogger(log2);
				} catch (SQLException e1) {
					e1.printStackTrace();
					MyLoggerManager.printInfo("json转为频道对象出错"+e1);
				}
    		//3、直播频道的发布处理：
    		 if(method.equals("publish")){
	    		 try {
	    			 //channel = jsonToChannelInterface.jsonToChannel(jsonSrc);
	    			 channelResult =channelCRUDInterface.findChannelByLiveId(channel.getLiveId());
	    			if(channelResult!=null){
	    				channelCRUDInterface.delChannelByLiveId(channelResult.getLiveId());
	    				 MyLogger log3 = new MyLogger(LogConfigHelper.getHttpInfoLoggerName(), "插入前，数据库中查询到了LiveId为："+channelResult.getLiveId()+"的频道记录，并将其成功删除");
						 MyLoggerManager.addLogger(log3);
	    			}
	    			channelCRUDInterface.insertChannel(channel);
	    			 MyLogger log3 = new MyLogger(LogConfigHelper.getHttpInfoLoggerName(), "channel插入数据库成功");
					 MyLoggerManager.addLogger(log3);
	    			
				} catch (SQLException e) {
					 flag="1";
			 	     msg="直播频道发布失败！！！";
					e.printStackTrace();
				}}
    		//4、直播频道的下线处理：
    		 if(method.equals("cancel")){
    			 try {
    				 //channel = jsonToChannelInterface.jsonToChannel(jsonSrc);
	    			 channelCRUDInterface.delChannelByLiveId(channel.getLiveId());
	    			 MyLogger log3 = new MyLogger(LogConfigHelper.getHttpInfoLoggerName(), "成功删除LiveId为"+channel.getLiveId()+"的频道记录");
					 MyLoggerManager.addLogger(log3);
    		 } catch (SQLException e) {
    			    flag="1";
		 			msg="直播频道下线失败！！！";
					e.printStackTrace();
				}
			}
    	 }
    	
     	}
     	pwriter.write(gson.toJson(responseObj));
    }

}
