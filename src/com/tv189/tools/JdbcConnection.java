package com.tv189.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class JdbcConnection { 
	private PreparedStatement stmt ;
	public PreparedStatement createStatement(String sql) {
		try {
		      Class.forName("com.mysql.jdbc.Driver");        
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		    }
		    try {
		      Connection connect = DriverManager.getConnection(
		      //"jdbc:mysql://localhost:3306/test","root","mysqlxys");
		      "jdbc:mysql://192.168.99.83:3306/ChannelAndProgram?characterEncoding=utf-8&allowMultiQueries=true&autoReconnect=true","root","fXL2bO$RQgaRS^lH");

		      //Statement stmt = connect.createStatement(); 
		      stmt =connect.prepareStatement(sql);
		    }
		    catch (Exception e) {
		      System.out.print("get data error!");
		      e.printStackTrace();
		    }
			return stmt;
	}
	public Statement getStmt() {
		return stmt;
	}
	public void setStmt(PreparedStatement stmt) {
		this.stmt = stmt;
	}
}
