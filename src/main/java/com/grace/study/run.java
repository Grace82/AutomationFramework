package com.grace.study;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import net.sourceforge.argparse4j.inf.ArgumentParserException;

import com.grace.study.common.TestHelper;
import com.grace.study.configuration.Config;
import com.grace.study.util.filehelper.FileUtil;
import com.grace.study.util.junithelper.Junit4Failure;
import com.grace.study.util.junithelper.Junit4Results;


public class run {
	private static Config config = null;
	private static String rootDir = null;
	private static String projectDir = null;
	private static String logDir = null;
	
	
	
	public static void main(String[] args) throws IOException,
			ArgumentParserException, ClassNotFoundException {
		config = new Config(args);
		rootDir = config.getCWD();
		projectDir = Paths.get(rootDir,File.separator,"com",File.separator,"grace",File.separator,"study",File.separator,"projects",config.getCurrentProject()).toString();
		logDir = Paths.get(projectDir,File.separator,"logs").toString();
		
		TestHelper th = new TestHelper(args);
		Map<String, Object> testCases = new HashMap<String, Object>();
		testCases = th.getTestCasesSuit();
		System.out.println(testCases.get("tag"));
		System.out.print(testCases.get("cases"));
		
		String logPath = Paths.get(logDir,File.separator,"RunResult1.log").toString();
		FileUtil.createNewFile(logPath);
		System.out.println();
		System.out.println("logPath is : " + logPath);

		Map<String, Result> results = new HashMap<String, Result>();
		results = th.executeTestCases(testCases);
		Iterator<Map.Entry<String, Result>> entriesResults = results.entrySet().iterator();
		Map<String,Junit4Results> Junit4ResultsMap = new HashMap<String,Junit4Results>();
		while(entriesResults.hasNext()){
			Map.Entry<String, Result> entry = entriesResults.next();
			Junit4Results junit4results = new Junit4Results();
			List<Junit4Failure> junit4FailureList = new ArrayList<Junit4Failure>();
			if(entry.getValue() != null){
				if(entry.getValue().getFailures() != null){
					List<Failure> failureList = new ArrayList<Failure>();
					failureList = entry.getValue().getFailures();
					for(Failure failure:failureList){
						Junit4Failure junit4Failure = new Junit4Failure();
						junit4Failure.setMessage(failure.getMessage());
						junit4Failure.setTestHeader(failure.getTestHeader());
						junit4Failure.setTrace(failure.getTrace());
						junit4FailureList.add(junit4Failure);
					}
				}
				junit4results.setFailureList(junit4FailureList);
				junit4results.setFailureCount(entry.getValue().getFailureCount());
				junit4results.setIgnoreCount(entry.getValue().getIgnoreCount());
				junit4results.setSucessCount(entry.getValue().getRunCount() - entry.getValue().getFailureCount() - entry.getValue().getIgnoreCount());
				junit4results.setRunCount(entry.getValue().getRunCount());
				junit4results.setRunTime(entry.getValue().getRunTime());
			}else {
				junit4results.setFailureList(junit4FailureList);
				junit4results.setFailureCount(0);
				junit4results.setIgnoreCount(0);
				junit4results.setRunCount(0);
				junit4results.setSucessCount(0);
				junit4results.setRunTime(0);
			}
			Junit4ResultsMap.put(entry.getKey(), junit4results);
		}
		ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(new File(logPath)));
		oo.writeObject(Junit4ResultsMap);
		oo.close();
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(logPath)));
		Map<String,Junit4Results> Junit4ResultsMapObject = new HashMap<String,Junit4Results>();
		Junit4ResultsMapObject = (Map<String,Junit4Results>) ois.readObject();
		Iterator<Map.Entry<String,Junit4Results>> entriesObject = Junit4ResultsMap.entrySet().iterator();
		while(entriesObject.hasNext()){
			Map.Entry<String, Junit4Results> entry = entriesObject.next();
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+entry.getKey());
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+entry.getValue().getFailureCount());
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+entry.getValue().getIgnoreCount());
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+entry.getValue().getRunCount());
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+entry.getValue().getRunTime());
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+entry.getValue().getSucessCount());
			List<Junit4Failure> failureList = entry.getValue().getFailureList();
			for(Junit4Failure failure : failureList){
				System.out.println();
				System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+failure.getMessage());
				System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+failure.getTestHeader());
				System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+failure.getTrace());
			}
		}
	}

}