package com.grace.study.util.junithelper;

import java.io.Serializable;

public class Junit4Failure implements Serializable {
	private String message = null;

	private String testHeader = null;

	private String trace = null;


	public Junit4Failure(String message,String testHeader,String trace){

	this.message = message;

	this.testHeader = testHeader;

	this.trace = trace;

	}


	public Junit4Failure(){

	}


	public String getMessage(){

	return this.message;

	}


	public void setMessage(String message){

	this.message = message;

	}


	public String getTestHeader(){

	return this.testHeader;

	}


	public void setTestHeader(String testHeader){

	this.testHeader =testHeader;

	}


	public String getTrace(){

	return this.trace;

	}


	public void setTrace(String trace){

	this.trace = trace;

	}
}