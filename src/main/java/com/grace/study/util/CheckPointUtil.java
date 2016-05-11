package com.grace.study.util;

import com.grace.study.util.loghelper.LoggedTestCase;


public class CheckPointUtil extends LoggedTestCase {
	private String id; //检查点的id
	private String description = ""; //对于检查点的描述
	private Throwable error = null;
	private String message = ""; // 与预期不一致时的描述
	private boolean result = false;//检查结果
	
	public CheckPointUtil(String id){
		if( id == null || id.trim().isEmpty()){
			throw new AutoException("CheckPoint ID cannot be <null> or an empty String!");
		}
		this.id = id;
		logger.info("CheckPoint: " + id + " created.");
	}
	
	public CheckPointUtil(String id, String description){
		this(id);
		if(description == null ){
			throw new AutoException("CheckPoint Description cannot be <null>!");
		}
	}

	public void validate(boolean condition,String failureMessage){
		if(condition){
			succeeded();
		}else{
			failed(failureMessage);
		}
	}
	
	public void validate(boolean condition){
		validate(condition,null);
	}
	
	public void failed(String errorMessage){
		result = false;
		message = prepareMessage(errorMessage, result);
		logger.error(errorMessage);
	}
	
	public void failed(String message,Throwable t){
		result = false;
		this.message = message;
		this.error = t;
		logger.error("CheckPoint:" + getId() + ":" + message + "failed");
	}
	
	public void succeeded(){
		succeeded(null);
	}
	
	public void succeeded(String successMessage){
		result = true;
		this.message = prepareMessage(successMessage,result);
		logger.info(this.message);
	}
	
	private String prepareMessage(String message,boolean success){
		StringBuilder text = new StringBuilder("CheckPoint: " + id);
		if( !description.trim().isEmpty()){
			text.append("(" + description + ")");
		}
		text.append(success ? " succeeded." : " failed!");
		if(!((message == null) || message.trim().isEmpty())){
			text.append(success ? " Message: " : " Reason: ");
			text.append(message);
		}
		return text.toString();
	}
	
	public String getId(){
		return this.id;
	}
	
	public boolean status(){
		return result;
	}
	
	public Throwable getError(){
		return this.error;
	}
	
	public String getStatusMessage(){
		return this.message;
	}
	
	public String getDescription(){
		return this.description;
	}
}
