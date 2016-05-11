package com.grace.study;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.argparse4j.inf.ArgumentParserException;

import com.grace.study.common.Application;
import com.grace.study.configuration.Config;
import com.grace.study.util.StreamWatchUtil;

public class Main extends Application{
	private static String logFile=null;
	private static Config config = null;
	private static String rootDir = null;
	private static String projectDir = null;
	private static String logDir = null;
	
	public Main(String[] args) throws FileNotFoundException, ArgumentParserException{
		super(args);
	}
	
	@Override
	protected void executeRunningTest(String[] args,List<String> cmdString,List<List<String>> testCases){
		try {  
		System.out.println("*************************");
		List<String> cmdList = new ArrayList<String>();
	    ProcessBuilder pb = null;  
	    Process p = null; 
		String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";  
	    String classpath = System.getProperty("java.class.path"); 
		cmdList.add(java);  
		cmdList.add("-classpath");  
		cmdList.add(classpath); 
		cmdList.add(run.class.getName());
		Iterator<String> it = cmdString.iterator();
		while(it.hasNext()){
			cmdList.add((String) it.next());
		}
		
		if (!testCases.isEmpty()) {
			it = testCases.get(0).iterator();
			while (it.hasNext()) {
				cmdList.add((String) it.next());
			}
		}
		
		System.out.println(cmdList);
		
	    pb = new ProcessBuilder(cmdList);  
	    p = pb.start();  
	     
	    System.out.println(pb.command()); 
	    
	    StreamWatchUtil outputWatch = new StreamWatchUtil(p.getInputStream(),"OUTPUT"); 
	    outputWatch.start();
	    
	    //wait for exit  
	    int exitVal = p.waitFor();  
	    
	    for(int i = 0 ;i <outputWatch.getOutput().size();i++ ){
			 System.out.println("OUTPUT: " + outputWatch.getOutput().get(i));
		 }
	    //print the content from ERROR and OUTPUT  
	    System.out.println("the return code is " + exitVal);  
	   } catch (Throwable t) {  
	    t.printStackTrace();  
	   }  
	}
	
	public static void main(String[] args) throws ArgumentParserException, IOException{
		config = new Config(args);
		rootDir = config.getCWD();
		projectDir = Paths.get(rootDir,File.separator,"com",File.separator,"grace",File.separator,"study",File.separator,"projects",config.getCurrentProject()).toString();
		logDir = Paths.get(projectDir,File.separator,"logs").toString();
		logFile = Paths.get(logDir, File.separator,"RunCaseTimeResult.log").toString();
		
		
		Main ma = new Main(args);
		List<List<String>> testCasesList = new ArrayList<List<String>>();
		if((int)(config.getEnvConfig().get("process_num"))>= 1){
			testCasesList = ma.splitTestSuites();
			System.out.println("In main the testCasesList is "+ testCasesList);
		}else{
			//自定义异常，提示 “No process or users can not be run, please check process_num in case.yaml file.”
			throw new IllegalArgumentException("No process or users can not be run, please check process_num in case.yaml file.");   
		}
		
		// add java获取当前时间 格式“2015-10-26 17:36:22.093000”
		ma.runCase(args, testCasesList);
		System.out.println(config.getCurrentTestResultTitle());
		
		File file = new File(logFile);
		if(file.exists())
			file.delete();
	}
}
