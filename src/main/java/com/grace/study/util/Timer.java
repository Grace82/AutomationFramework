package com.grace.study.util;
import org.apache.commons.lang3.time.StopWatch;

public class Timer extends StopWatch {
	private String id=null;
	public Timer(){
		super();
		this.id = "";
	}
	
	public Timer(String id){
		super();
		this.id = id;
	}
	
	public String getId(){
		return this.id;
	}
}
