package com.tv189.log;

//import org.springframework.stereotype.Repository;
//import com.tv189.elipapi.constant.Constant;



//@Repository
public class MyLogger {
	private String logType;
	private String logContent;
	private String logNameType="1";
	//private String logNameType =Constant.DAILY;

	public MyLogger(){
		
	}
	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}

	public MyLogger(String logType,String logContent,String logNameType){
		this.logType = logType;
		this.logContent=logContent;
		this.logNameType = logNameType;
	}
	public MyLogger(String logType,String logContent){
		this.logType = logType;
		this.logContent=logContent;
	}

	public MyLogger(String resLogInfo) {
		// TODO Auto-generated constructor stub
		this.logContent=resLogInfo;
	}

	public String getLogNameType() {
		return logNameType;
	}

	public void setLogNameType(String logNameType) {
		this.logNameType = logNameType;
	}
	
}
