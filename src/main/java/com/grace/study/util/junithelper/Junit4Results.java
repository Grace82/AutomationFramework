package com.grace.study.util.junithelper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Junit4Results implements Serializable {
	private int sucessCount = 0;

	private int ignoreCount = 0;

	private int failureCount = 0;

	private int  runCount = 0; 

	private long runTime = 0;

	private List<Junit4Failure> failureList = null;

	//private Map<String,Long> runModuleCaseAmountTimeDict = null;


	public Junit4Results(int sucessCount, int ignoreCount, int failureCount, int  runCount, long runTime,List<Junit4Failure>  failureList ){

	this.sucessCount = sucessCount;

	this.ignoreCount = ignoreCount;

	this.failureCount = failureCount;

	this.runCount = runCount;

	this.runTime = runTime;

	this.failureList = failureList;

	//this.runModuleCaseAmountTimeDict = runModuleCaseAmountTimeDict;

	}


	public Junit4Results(){

	}


//	public Map<String,Long> getRunModuleCaseAmountTimeDict(){
//
//	return this.runModuleCaseAmountTimeDict;
//
//	}


//	public void setRunModuleCaseAmountTimeDict(Map<String,Long> runModuleCaseAmountTimeDict){
//
//	this.runModuleCaseAmountTimeDict =runModuleCaseAmountTimeDict;
//
//	}


	public int getSucessCount(){

	return this.sucessCount;

	}


	public void setSucessCount(int sucessCount){

	this.sucessCount = sucessCount;

	}


	public int getIgnoreCount(){

	return this.ignoreCount;

	}


	public void setIgnoreCount(int ignoreCount){

	this.ignoreCount = ignoreCount;

	}


	public int getFailureCount(){

	return this.failureCount;

	}


	public void setFailureCount(int failureCount){

	this.failureCount = failureCount;

	}


	public List<Junit4Failure> getFailureList(){

	return this.failureList;

	}


	public void setFailureList(List<Junit4Failure> failureList){

	this.failureList = failureList;

	}


	public int getRunCount(){

	return this.runCount;

	}


	public void setRunCount(int runCount){

	this.runCount = runCount;

	}


	public long getRunTime(){

	return this.runTime;

	}


	public void setRunTime(long runTime){

	this.runTime = runTime;

	}

	}


