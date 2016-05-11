package com.grace.study.configuration;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class TestConfig {
	public static void main(String[] args) throws ArgumentParserException, FileNotFoundException{
		Config config = new Config(args);
//		Namespace res = config.getNamespace();
//		List<Object> list = new ArrayList<Object>();
//		list = res.getList("test");
//		
//		Map<Object,List<Object>> includedTestsMap = config.getIncludedTestsMap();
//		System.out.println("includedTestsMap is : "+ includedTestsMap);
//		System.out.println(includedTestsMap.get("study"));
//		
//		Iterator it = list.iterator();
//		while(it.hasNext()){
//			System.out.println(it.next());
//		}
//		System.out.println(res.getString("project"));
//		System.out.println(config.getCWD());
//		String testcase = "test.test1.dd";
//		int pos = testcase.lastIndexOf(".");
//		System.out.println(pos);
//		System.out.println(testcase.substring(pos+1,testcase.length()));
//		
//		Map<Object,List<Object>> testsMap = null;
//		testsMap = new HashMap<Object,List<Object>>();
//		
//		testsMap = config.getExcludedTestsMap();
//		System.out.println(testsMap.get("study"));
//		
//		System.out.println(config.getCurrentProject());
//		System.out.println(config.getLogFile());
//		System.out.println(config.getRunResult());
//		
//		System.out.println(config.getPublicConfig());
//		System.out.println(config.getProjectConfig());
		
	}
	
	private static void parseTest(Map<Object,List<Object>> testsMap,List<Object> testList){
		if(testList != null){
			Iterator it = testList.iterator();
			while(it.hasNext()){
				String testcase = (String) it.next();
				int dotPos = testcase.lastIndexOf(".");
				if(dotPos != -1){
					String className = testcase.substring(0,dotPos);
					String methodName = testcase.substring(dotPos+1,testcase.length());
					List<Object> caseList = new ArrayList<Object>();
					if(testsMap.containsKey(className)){
						caseList = testsMap.get(className);
						caseList.add(methodName);
						testsMap.put(className, caseList);
					}else{
						caseList.add(methodName);
						testsMap.put(className, caseList);
					}
				}else{
					List<Object> caseList = new ArrayList<Object>();
					caseList.add("");
					testsMap.put(testcase,caseList);
				}
			}
		}
	}
}
