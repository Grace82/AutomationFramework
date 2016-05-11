package com.grace.study.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class StreamWatchUtil extends Thread {
	InputStream is;  
	String type; 
	List<String> output = new ArrayList<String>();
	boolean debug = false;  
	

	public StreamWatchUtil(InputStream is, String type) {  
	    this(is, type, false);  
	}  


	public StreamWatchUtil(InputStream is, String type, boolean debug) {  
	    this.is = is;  
	    this.type = type;  
	    this.debug = debug;  
	    }  

	public void run() {

		   try {  
		    PrintWriter pw = null;  
		     InputStreamReader isr = new InputStreamReader(is);  
		     BufferedReader br = new BufferedReader(isr);  
		     String line = null;  
		    while ((line = br.readLine()) != null) {  
		     output.add(line);  
		      if (debug)  
		       System.out.println(type + ">" + line);  
		     }  
		     if (pw != null)  
		      pw.flush();  
		    } catch (IOException ioe) {  
		     ioe.printStackTrace();  
		    }  
		 }  
		   
		 public List<String> getOutput() {  
		    return output;  
		 }  
}
