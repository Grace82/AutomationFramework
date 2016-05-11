package com.grace.study.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import net.sourceforge.argparse4j.inf.ArgumentParserException;

import com.grace.study.run;
import com.grace.study.configuration.Config;
import com.grace.study.util.ReflectUtil;
import com.grace.study.util.filehelper.FileUtil;
import com.grace.study.util.filehelper.YamlUtil;


public class Application {
	private String rootDir;
	private String projectDir;
	private String logDir;
	private String caseFile;//
	private String modulePattern;//文件模式，用于查找指定路径下匹配的文件
	private String reportOut;//存放子在本地的报告
	private Config config = null;
	private Map<Object,Object> caseConfig = new LinkedHashMap<Object,Object>();
	
	public Application(String[] args) throws FileNotFoundException, ArgumentParserException{
		config = new Config(args);
		this.rootDir = config.getCWD();
		this.projectDir = Paths.get(this.rootDir,File.separator,"com",File.separator,"grace",File.separator,"study",File.separator,"projects",config.getCurrentProject()).toString();
		this.logDir = Paths.get(this.projectDir,File.separator,"log").toString();
		this.caseFile = Paths.get(this.projectDir,File.separator,"conf",File.separator,"case.yaml").toString();
		this.modulePattern = "glob:**/test/Test*";
		this.reportOut = Paths.get(this.rootDir,File.separator,config.getCurrentConfig().get("report_output").toString()).toString();
		if(new File(this.caseFile).exists()){
			Path path = Paths.get(this.projectDir,File.separator,"conf",File.separator,"case.yaml");
			this.caseConfig = (Map<Object, Object>)YamlUtil.loadYaml(path.toString());
			System.out.println("this.caseConfig is : " + this.caseConfig);
		}
	}
	
	//获取需要测试的指定project中的所有测试集
	public List<String> loadModulesList() throws IOException{
		List<String> allModuleList = new ArrayList<String>();
		List<String> testFileList = FileUtil.getPathPatternMatchFile(this.modulePattern, this.projectDir);
		Iterator it = testFileList.iterator();
		
		//获取指定Project下面所有的测试集（Test1.java）的名字（Test1）
		while(it.hasNext()){
			String filePath = (String) it.next();
			System.out.println("In loadModulesList the filePath is " + filePath);
			allModuleList.add(filePath.substring(filePath.lastIndexOf(File.separator)+1,filePath.lastIndexOf(".")));
		}
		
		//如果测试集不为空
		if(!allModuleList.isEmpty()){
			//去除在conf。case。yaml文件中，指定需要排除的测试集
			if(new File(this.caseFile).exists()){
				List<String> excludeTestFileList = new ArrayList<String>();
				excludeTestFileList = (List<String>) this.caseConfig.get("exclude_test_file");
				System.out.println("In loadModulesList the excludeTestFileList is " + excludeTestFileList);
				if(!excludeTestFileList.isEmpty()){
					allModuleList.removeAll(excludeTestFileList);
				}
			}
		}
		return allModuleList;
	}
	
	//获取需要测试的指定project中的所有测试集
	public List<String> loadModulesListExceptExcludeTestFile() throws IOException{
		List<String> allModuleList = new ArrayList<String>();
		List<String> testFileList = FileUtil.getPathPatternMatchFile(this.modulePattern, this.projectDir);
		Iterator it = testFileList.iterator();
		
		//获取指定Project下面所有的测试集（Test1.java）的名字（Test1）
		while(it.hasNext()){
			String filePath = (String) it.next();
			System.out.println("In loadModulesList the filePath is " + filePath);
			allModuleList.add(filePath.substring(filePath.lastIndexOf(File.separator)+1,filePath.lastIndexOf(".")));
		}
		return allModuleList;
	}
	
	public List<List<String>> splitTestSuites(){
		List<List<String>> runTestCasesTotalList = new ArrayList<List<String>>();
		if(new File(this.caseFile).exists()){
			int runSetNum = (int) (config.getEnvConfig().get("process_num"));
			int runSetNumByUsers = 0;
			Map<Object,Object> usersMap = new HashMap<Object,Object>();
			usersMap = (Map<Object, Object>) config.getEnvConfig().get("users");
			System.out.println("usersMap is : " + usersMap);
			
			if(!usersMap.isEmpty()){
//				Iterator<Map.Entry<Object, Object>> usersMapEntries = usersMap.entrySet().iterator();
//				while(usersMapEntries.hasNext()){
//					Map.Entry<Object, Object> userEntry = usersMapEntries.next();
//					runSetNumByUsers++;
//				}
				runSetNumByUsers = usersMap.size();
			}
			
			
			if(runSetNum == 1 || runSetNumByUsers ==1 ){
				return runTestCasesTotalList;
			}
			
			if(runSetNum > runSetNumByUsers )
				runSetNum = runSetNumByUsers;
			
		}
		return runTestCasesTotalList;
	}
	
	private Map<String,Integer> getTotalCaseStatistics(List<String> testList){
		System.out.println("In the getTotalCaseStatistics the testList is " + testList);
		Map<String,Integer> totalCaseStatistics = new HashMap<String,Integer>();
		for(String className : testList){
			List<Method> allMethodsList = new ArrayList<Method>();
			allMethodsList = ReflectUtil.getAllMethods((new StringBuffer(
					"com.grace.study.projects.").append(
							config.getCurrentProject()).append(".test.")
							.append(className).toString()));
			int methodCount = 0;
			if(allMethodsList != null){
				for(Method method : allMethodsList){
					if(method.getAnnotation(Test.class) != null){
						methodCount++;
					}
				}
			}
			totalCaseStatistics.put(className, methodCount);
		}
		return totalCaseStatistics;
	}
	
	protected void executeRunningTest(String[] args,List<String> cmdString,List<List<String>> testCases){
		List<String> cmdList = new ArrayList<String>();
		String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";  
	    String classpath = System.getProperty("java.class.path"); 
	    System.out.println(classpath);
		cmdList.add(java);  
		cmdList.add("-classpath");  
		cmdList.add(classpath); 
		cmdList.add(run.class.getName());
		Iterator<String> it = cmdString.iterator();
		while(it.hasNext()){
			cmdList.add((String) it.next());
		}
		
		it = testCases.get(0).iterator();
		while(it.hasNext()){
			cmdList.add((String) it.next());
		}
		
		System.out.println(cmdList);
		//cmdList.add(MyTest.class.getName());
	}
	
	public void runCase(String[] args,List<List<String>> testCases) throws IOException{
		if(args.length > 1){
			List<String> inputPara = new ArrayList<String>();
			List<String> caseList = new ArrayList<String>();
			List<String> cmdString = new ArrayList<String>();
			for(int i = 0;i<args.length;i++){
				inputPara.add(args[i]);
			}
			
			List<String> tTag = new ArrayList<String>();
			tTag.add("-t");
			
			List<String> testList = new ArrayList<String>();
			testList = loadModulesListExceptExcludeTestFile();
		
			System.out.println("In run case the inputPara is " + inputPara);
			System.out.println("In run case the tTag is " + tTag);
			System.out.println("In run case the testList is " + testList);
			
			if(inputPara.contains("-t") || inputPara.contains("-et")){
				int index1 = 0;
				List<String> inputTestCaseList = new ArrayList<String>();
				List<String> inputTestList = new ArrayList<String>();
				Iterator<String> inputParaIterator = inputPara.iterator();
				while(inputParaIterator.hasNext()){
					String test = (String) inputParaIterator.next();
					if(test.startsWith("Test")){
						inputTestList.add(test);
						if(test.contains(".")){
							inputTestCaseList.add(test.substring(0,test.indexOf('.')));
						}else{
							inputTestCaseList.add(test);
						}
					}
				}
				
				if(inputPara.contains("-et")){
					Iterator<String> inputTestListIterator = inputTestList.iterator();
					while(inputTestListIterator.hasNext()){
						String inputTest = (String) inputTestListIterator.next();
							if(testList.contains(inputTest)){
								testList.remove(inputTest);
							}
					}
					
					index1 = inputPara.indexOf("-et");
					System.out.println("index1 is " + index1);
					
					if(((int)(config.getEnvConfig().get("process_num")) == 1) || (((Map<Object, Object>)(config.getEnvConfig().get("users"))).size() == 1)){
						tTag.add(0,"-et");
					}
					
				}else{
					System.out.println("In run case the inputTestCaseList is " + inputTestCaseList);
					System.out.println("In run case the testList is " + testList);
					List<String> tmpCaseList = new ArrayList<String>();
					for(int i = 0;i<inputTestCaseList.size();i++){
						String testCase = inputTestCaseList.get(i);
						if(testList.contains(testCase)){
							tmpCaseList.add(testCase);
						}
					}
					testList = tmpCaseList;
					index1 = inputPara.indexOf("-t");
					System.out.println("index1 is -t " + index1);
					
					System.out.println("In run case the tmpCaseList is " + tmpCaseList);
					System.out.println("In run case the inputTestList is " + inputTestList);
				}
				System.out.println("In run case the inputTestList is " + inputTestList);
				System.out.println("In run case the testList is " + testList);
				
				int index2 = inputPara.indexOf(inputTestList.get(inputTestList.size()-1));
				System.out.println("inputTestList.get(inputTestList.size()-1)" + (inputTestList.get(inputTestList.size()-1)));
				System.out.println("index2 is " + index2);

				for(int i=0;i<index1;i++){
					cmdString.add(inputPara.get(i));
				}
				for(int i=index2+1;i<inputPara.size();i++){
					cmdString.add(inputPara.get(i));
				}
				cmdString.add(tTag.get(0));
				
				for(int i=index1+1;i<index2+1;i++){
					caseList.add(inputPara.get(i));
				}
				System.out.println("cmdString is " + cmdString);
				System.out.println("caseList is " + caseList);
				
				if(testCases != null && testCases.isEmpty() && !caseList.isEmpty()){
					testCases.add(caseList);
				}
				System.out.println("testCases is " + testCases);
			}else{
				cmdString = inputPara;
				System.out.println(cmdString);
			}
			executeRunningTest(args,cmdString,testCases);
			Map<String,Integer> totalModuleCaseamountDict = new HashMap<String,Integer>();
			totalModuleCaseamountDict = getTotalCaseStatistics(testList);
			Iterator<Map.Entry<String, Integer>> entries = totalModuleCaseamountDict.entrySet().iterator();
			while(entries.hasNext()){
				Entry<String, Integer> entry = entries.next();
				System.out.println(entry.getKey());
				System.out.println(entry.getValue());
			}
			System.out.println(totalModuleCaseamountDict.size());
		}
			
	}
	
	public static void main(String[] args) throws ArgumentParserException, IOException{
		Application a = new Application(args);
		System.out.println(a.rootDir);
		System.out.println(a.projectDir);
		System.out.println(a.logDir);
		System.out.println(a.caseFile);
		System.out.println(a.modulePattern);
		List<String> list = new ArrayList<String>();
		list = FileUtil.getPathPatternMatchFile(a.modulePattern, a.projectDir);
		System.out.println("***********************    getPathPatternMatchFile list is " + list);
		List<String> allModuleList = a.loadModulesList();
		System.out.println("allModuleList is " + allModuleList);
		//a.splitTestSuites();
		List<List<String>> testCases = new ArrayList<List<String>>();
		a.runCase(args, testCases);
		a.executeRunningTest(args, allModuleList, testCases);
	}
	
}
