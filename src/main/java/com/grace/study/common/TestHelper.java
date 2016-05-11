package com.grace.study.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import net.sourceforge.argparse4j.inf.ArgumentParserException;

import com.grace.study.configuration.Config;
import com.grace.study.util.ReflectUtil;
import com.grace.study.util.filehelper.FileUtil;
import com.grace.study.util.filehelper.YamlUtil;
import com.grace.study.util.junithelper.JunitUtil;

public class TestHelper {
	private Config config = null;
	private String rootDir;
	private String projectDir;
	private String modulePattern;
	private String caseFile;
	private Map<Object, Object> caseConfig = new LinkedHashMap<Object, Object>();
	private List<String> inputPara = new ArrayList<String>();

	public TestHelper(String[] args) throws ArgumentParserException, IOException {
		
		 File file = new File("C:\\Users\\Administrator\\Desktop\\test111111111dddddddd.txt");
		   if(!file.exists()){
			   file.createNewFile();
		   }
		   
		config = new Config(args);
		this.rootDir = config.getCWD();
		this.projectDir = Paths.get(this.rootDir, File.separator, "com",File.separator, "grace", File.separator, "study",
				File.separator, "projects", config.getCurrentProject()).toString();
		this.modulePattern = "glob:**/test/Test*";
		this.caseFile = Paths.get(this.projectDir, File.separator, "conf",File.separator, "case.yaml").toString();
		
		if (new File(this.caseFile).exists()) {
			Path path = Paths.get(this.projectDir, File.separator, "conf",File.separator, "case.yaml");
			this.caseConfig = (Map<Object, Object>) YamlUtil.loadYaml(path.toString());
			System.out.println("this.caseConfig is : " + this.caseConfig);
		}
		
		for(int i = 0;i<args.length;i++){
			this.inputPara.add(args[i]);
		}
		
	}

	public void executeTest() {

	}

	private List<String> loadAllModulesList() throws IOException {
		List<String> tmpAllModuleList = new ArrayList<String>();
		List<String> allModuleList = new ArrayList<String>();
		
		List<String> testFileList = FileUtil.getPathPatternMatchFile(this.modulePattern, this.projectDir);
		Iterator it = testFileList.iterator();
		// 获取指定Project下面所有的测试集（Test1.java）的名字（Test1）
		while(it.hasNext()){
			String filePath = (String) it.next();
			System.out.println("In loadModulesList the filePath is " + filePath);
			tmpAllModuleList.add(filePath.substring(filePath.lastIndexOf(File.separator)+1,filePath.lastIndexOf(".")));
		}
		//如果测试集不为空
		if(!tmpAllModuleList.isEmpty()){
			//去除在conf。case。yaml文件中，指定需要排除的测试集
			if(new File(this.caseFile).exists()){
				List<String> excludeTestFileList = new ArrayList<String>();
				excludeTestFileList = (List<String>) this.caseConfig.get("exclude_test_file");
				System.out.println("In loadModulesList the excludeTestFileList is " + excludeTestFileList);
				if((!excludeTestFileList.isEmpty()) && (this.inputPara.contains("-et"))){
					Iterator iter = excludeTestFileList.iterator();
					while(iter.hasNext()){
						String moduleName = (String) iter.next();
						Iterator inputParaIter = this.inputPara.iterator();
						while(inputParaIter.hasNext()){
							String paraModuleName = (String) inputParaIter.next();
							if(paraModuleName.startsWith(moduleName)){
								iter.remove();
							}
						}
					}
					tmpAllModuleList.removeAll(excludeTestFileList);
					System.out.println("tmpAllModuleList is : " + tmpAllModuleList);
				}
			}
		}
		
		Iterator allModuleListIter = tmpAllModuleList.iterator();
		while (allModuleListIter.hasNext()) {
			String filePath = (String) allModuleListIter.next();
			System.out.println(filePath);
			StringBuffer testModulesClassName = new StringBuffer();
			testModulesClassName.append("com.grace.study.projects.");
			testModulesClassName.append(config.getCurrentProject());
			testModulesClassName.append(".test.");
			testModulesClassName.append(filePath);
			allModuleList.add(testModulesClassName.toString());
		}
		
		return allModuleList;
	}

	public List<String> loadModulesClassNameList() throws IOException {
		List<String> allModuleList = new ArrayList<String>();
		List<String> testFileList = FileUtil.getPathPatternMatchFile(
				this.modulePattern, this.projectDir);
		Iterator<String> it = testFileList.iterator();

		// 获取指定Project下面所有的测试集（Test1.java）的名字（Test1）
		while (it.hasNext()) {
			String filePath = (String) it.next();
			System.out.println("In loadModulesList the filePath is " + filePath);
			allModuleList.add(filePath.substring(
					filePath.lastIndexOf(File.separator) + 1,
					filePath.lastIndexOf(".")));
		}
		System.out.println("loadModulesClassNameList is " + allModuleList);
		return allModuleList;
	}
	
	public Map<String,Object> getTestCasesSuit() throws IOException {
		Map<String,Object> testCasesSuit = new HashMap<String,Object>();
		if(this.inputPara.contains("-t")){
			testCasesSuit.put("tag", "-t");
			testCasesSuit.put("cases",getTestSuit());
		}else{
			testCasesSuit.put("tag", "-et");
			testCasesSuit.put("cases",getTestSuit());
		}
		return testCasesSuit;
	}

	
	private Map<String, List<String>> getTestSuit() throws IOException {
		List<String> allModuleList = loadAllModulesList();
		System.out.println("###################################################");
		System.out.println();
		System.out.println("allModuleList is : " + allModuleList);
		System.out.println();
		System.out.println("###################################################");
		Map<String, List<String>> suitCases = new HashMap<String, List<String>>();
		List<String> excluedCases = new ArrayList<String>();

		if ((config.getIncludedTestsMap().isEmpty()) && (config.getExcludedTestsMap().isEmpty())) {
			System.out.println("************************** !!!!!!!!!!!!!!!!!"
					+ allModuleList);
			Iterator allModuleListIterator = allModuleList.iterator();
			while (allModuleListIterator.hasNext()) {
				String className = (String) allModuleListIterator.next();
				suitCases.put(className, excluedCases);
			}
			System.out.println("suitCases is " + suitCases);
			return suitCases;
		}

		if (!config.getIncludedTestsMap().isEmpty()) {
			List<String> tempIncludedList = new ArrayList<String>();
			Map<Object, List<Object>> incluedTestList = new HashMap<Object, List<Object>>();

			incluedTestList = config.getIncludedTestsMap(); // includedTestList
															// is
															// {TestWeatherSoapWebservice0=[testSoap01,
															// testSoap02]}
			System.out.println("incluedTestList is " + incluedTestList);

			Iterator allModuleListIterator = allModuleList.iterator();
			while (allModuleListIterator.hasNext()) {
				String className = allModuleListIterator.next().toString();
				String includeClassName = className.substring(className.lastIndexOf(".") + 1, className.length());
				if (incluedTestList.containsKey(className.substring(
						className.lastIndexOf(".") + 1, className.length()))) {

					String tempIncludedClassName = new StringBuffer(
							"com.grace.study.projects.")
							.append(config.getCurrentProject())
							.append(".test.")
							.append(className.substring(
									className.lastIndexOf(".") + 1,
									className.length())).toString();
					
					List<String> includeMethodList = new ArrayList<String>();
					List<String> allMethodsNameList = new ArrayList<String>();
					
					//allMethodsNameList = ReflectUtil.getAllTestCasesMethodsName(tempIncludedClassName);
					allMethodsNameList = ReflectUtil.getAllMethodsName(tempIncludedClassName);
					System.out.println("#########################################################################################");
					System.out.println();
					System.out.println(allMethodsNameList);
					System.out.println();
					System.out.println("#########################################################################################");
					
					List<Object> includeMethodNameList = new ArrayList<Object>();
					includeMethodNameList = incluedTestList.get(includeClassName);
					System.out.println("includeMethodNameList is " + includeMethodNameList);
					
					if(!includeMethodNameList.contains("")){
						System.out.println("includeMethodNameList " + includeMethodNameList);
						System.out.println("allMethodsNameList " + allMethodsNameList);
						for(Object methodName : includeMethodNameList){
							if(allMethodsNameList.contains(methodName)){
								includeMethodList.add((String) methodName);
							}
						}
					}else if(includeMethodNameList.contains("")){
						Iterator allMethodsNameListIter = allMethodsNameList.iterator();
						while(allMethodsNameListIter.hasNext()){
							String methodName = (String) allMethodsNameListIter.next();
							includeMethodList.add(methodName);
						}
					}
					System.out.println("includeMethodList is " + includeMethodList);
					suitCases.put(tempIncludedClassName,includeMethodList);
				}
				
			}
			System.out.println("suitCases is " + suitCases);
			return suitCases;

		} else if (!config.getExcludedTestsMap().isEmpty()) {
			List<String> tempExcludeList = new ArrayList<String>();
			Map<Object, List<Object>> excludeTestList = new HashMap<Object, List<Object>>();
			excludeTestList = config.getExcludedTestsMap();
			System.out.println("###################################################");
			System.out.println();
			System.out.println("excludeTestList is " + excludeTestList);
			System.out.println();
			System.out.println("###################################################");
			
			Iterator<String> allModuleListIterator = allModuleList.iterator();
			while (allModuleListIterator.hasNext()) {
				String className = allModuleListIterator.next().toString();
				System.out.println(className);
				String exlucdedclassName = className.substring(className.lastIndexOf(".") + 1, className.length());
				if (excludeTestList.containsKey(exlucdedclassName)) {
					tempExcludeList.add((new StringBuffer(
							"com.grace.study.projects.").append(
							config.getCurrentProject()).append(".test.")
							.append(exlucdedclassName).toString()));
					
					if(!excludeTestList.get(className.substring(className.lastIndexOf(".") + 1, className.length())).contains("")){
						List<Object> methodsNameList = new ArrayList<Object>();
						methodsNameList = excludeTestList.get(exlucdedclassName);
						
						List<String> excludeMethodList = new ArrayList<String>();
						List<String> allMethodsNameList = new ArrayList<String>();
						
						allMethodsNameList = ReflectUtil.getAllMethodsName((new StringBuffer(
								"com.grace.study.projects.").append(
										config.getCurrentProject()).append(".test.")
										.append(exlucdedclassName).toString()));

						Iterator<Object> methodsNameListIterator = methodsNameList.iterator();
						while(methodsNameListIterator.hasNext()){
							String methodName = (String) methodsNameListIterator.next();
							if(allMethodsNameList.contains(methodName)){
								excludeMethodList.add(methodName);
							}
						}
							suitCases.put((new StringBuffer(
									"com.grace.study.projects.").append(
											config.getCurrentProject()).append(".test.")
											.append(exlucdedclassName).toString()), excludeMethodList);
						
					}
//					else{
//						List<String> allMethodsNameList = new ArrayList<String>();
//						
//						allMethodsNameList = ReflectClass.getAllMethodsName((new StringBuffer(
//								"com.grace.study.projects.").append(
//										config.getCurrentProject()).append(".test.")
//										.append(exlucdedclassName).toString()));
//							suitCases.put((new StringBuffer(
//									"com.grace.study.projects.").append(
//											config.getCurrentProject()).append(".test.")
//											.append(exlucdedclassName).toString()), allMethodsNameList);
//					}
				}
			}

			System.out.println("tempExcludeList is " + tempExcludeList);
			Iterator<String> tempExcludeListIterator = tempExcludeList.iterator();
			while (tempExcludeListIterator.hasNext()) {
				String tmpExcludeModule = (String) tempExcludeListIterator
						.next();
				if (allModuleList.contains(tmpExcludeModule)) {
					allModuleList.remove(tmpExcludeModule);
				}
			}
			System.out.println("After execute exclude cases the allModuleList is "+ allModuleList);
			// return allModuleList;
			allModuleListIterator = allModuleList.iterator();
			List<String> excludeMethodList = new ArrayList<String>();
			while(allModuleListIterator.hasNext()){
				suitCases.put((String) allModuleListIterator.next(), excludeMethodList);
			}
			System.out.println("suitCases is " + suitCases);
		}
		return suitCases;
	}

	public Map<String, Result> executeTestCases(Map<String,Object> suitCases) throws ClassNotFoundException{
		Map<String, Result> results = new HashMap<String, Result>();
		if(suitCases!=null && !suitCases.isEmpty()){
			Map<String, List<String>> cases = new HashMap<String, List<String>>();
			cases = (Map<String, List<String>>) suitCases.get("cases");
			if(suitCases.get("tag").equals("-t")){
				results = JunitUtil.executeIncludedMethods(cases);
			}else{
				results = JunitUtil.executeExcluedMethods(cases);
			}
		}
		return results;
	}
	
	public static void main(String[] args) throws ArgumentParserException,IOException, ClassNotFoundException{
		TestHelper th = new TestHelper(args);
		Map<String, Object> testCases = new HashMap<String,Object>();
		testCases = th.getTestCasesSuit();
		System.out.println(testCases.get("tag"));
		System.out.print(testCases.get("cases"));
		Map<String, Result> results = new HashMap<String, Result>();
		results = th.executeTestCases(testCases);
		Iterator<Map.Entry<String, Result>> entries = results.entrySet().iterator();
		while(entries.hasNext()){
			Map.Entry<String, Result> entry = entries.next();
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());//不能直接引用entry.getValue()的值，可能是null。由于可能未执行任何的用例，因此使用前需要先判断是否会null，
			if(entry.getValue() != null){
				List<Failure> failureList = new ArrayList<Failure>();
				failureList = entry.getValue().getFailures();
				if(!failureList.isEmpty()){
					for(Failure failure:failureList){
					System.out.println(failure.getMessage());
					System.out.println(failure.getTrace());
					System.out.println(failure.getDescription().toString());
				}
				}
			}else{
				
			}
		}
	}
}
