package com.grace.study.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.grace.study.util.filehelper.YamlUtil;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;

public class Config {
	private Namespace res = null;
	private volatile static Config config;
	//-t参数的测试用例
	private Map<Object,List<Object>> includedTestsMap = new HashMap<Object,List<Object>>();
	//-et参数的测试用例
	private Map<Object,List<Object>> excludedTestsMap = new HashMap<Object,List<Object>>();
	//configuration下 "public_config.yaml"文件的参数
	private Map<Object,Object> publicConfig = new LinkedHashMap<Object,Object>();
	// 指定project下conf下"project_config.yaml"文件的参数
	private Map<Object,Object> projectConfig = new LinkedHashMap<Object,Object>();
	// 合并configuration下 "public_config.yaml"和指定project下conf下"project_config.yaml"文件中关于邮件的参数
	private Map<Object,Object> currentConfig = new LinkedHashMap<Object,Object>();
	// 读取当前project下的“project_config.yaml”文件中的env参数
	private Map<Object,Object> envConfig = new LinkedHashMap<Object,Object>();
	
	
	private String cwd = null;
	
	
	public String getCurrentTestResultTitle(){
		return MessageFormat.format("Test Result on {0}",this.res.getString("environment"));
	}
	
	public String getCWD(){
		return this.cwd;
	}
	
	public Namespace getNamespace(){
		return this.res;
	}
	
	public Map<Object,List<Object>> getIncludedTestsMap(){
		return this.includedTestsMap;
	}
	
	public Map<Object,List<Object>> getExcludedTestsMap(){
		return this.excludedTestsMap;
	}
	
	public List<String> getProfile(){
		return this.res.getList("profile");
	}
	
	public String getCurrentProject(){
		return this.res.getString("project");
	}
	
	public String getRunResult(){
		return this.res.getString("runresult");
	}
	
	public String getLogFile(){
		return this.res.getString("logfile");
	}
	
	public String getTag(){
		return this.res.getString("tag");
	}
	
	public String getEnvironment(){
		return this.res.getString("environment");
	}
	
	public Map<Object,Object> getPublicConfig(){
		return this.publicConfig;
	}
	
	public Map<Object,Object> getProjectConfig(){
		return this.projectConfig;
	}
	
	public Map<Object,Object> getCurrentConfig(){
		return this.currentConfig;
	}
	
	public Map<Object,Object> getEnvConfig(){
		return this.envConfig;
	}
	
	
//	private Config(String[] args) throws ArgumentParserException, FileNotFoundException{
//		this.setCommandParamenters(args);
//		this.setCWD();
//		this.setTest();
//		this.setPublicConfig();
//	}
//	
//	public static Config getConfig(String[] args) throws ArgumentParserException, FileNotFoundException{
//		if(config == null){
//			synchronized (Config.class){
//				if(config == null){
//					config = new Config(args);
//				}
//			}
//		}
//		return config;
//	}
	
	public Config(String[] args) throws ArgumentParserException, FileNotFoundException{
		//处理命令行参数
		this.setCommandParamenters(args);
		//获取当前项目“/target/classes/”目录的绝对路径
		this.setCWD();
		//将测试用例组织成map形式
		this.setTest();
		// 读取"configuration"下的public_config.yaml配置文件的内容
		this.setPublicConfig();
		// 读取当前project下的conf文件夹下的project_config.yaml配置文件的内容
		this.setProjectConfig();
		// 合并configuration下 "public_config.yaml"和指定project下conf下"project_config.yaml"文件中关于邮件的参数
		this.setCurrenConfig();
		// 读取当前project下的“project_config.yaml”文件中的env参数
		this.setEnvConfig();
	}
	
	private void setCWD(){
		String tempPath = Config.class.getProtectionDomain().getCodeSource().getLocation().toString();
		System.out.println("tempPath is : " + tempPath);
		Pattern pattern = Pattern.compile("^file:/.*$");
		Matcher matcherDrive = pattern.matcher(tempPath);
		Boolean drive = matcherDrive.find();
		if(drive == true){
			int pos = tempPath.indexOf("/");
			if(System.getProperty("os.name").equals("Mac OS X")){
			this.cwd = tempPath.substring(pos,tempPath.length());
			System.out.println("cwd is : " + this.cwd);
		}else{
			this.cwd = tempPath.substring(pos+1,tempPath.length());
			System.out.println("cwd is : " + this.cwd);
		}
		}
	}
	
	
	private void setTest(){
		System.out.println("##############################################################");
		System.out.println("this.getIncludedTestsList() is : " + this.getIncludedTestsList());
		System.out.println("##############################################################");
		this.parseTest(includedTestsMap,this.getIncludedTestsList());
		this.parseTest(excludedTestsMap, this.getExcludedTestsList());
		System.out.println("includedTestsMap is : "+ this.includedTestsMap);
		System.out.println("excludedTestsMap is : "+ this.excludedTestsMap);
	}
	
	private void parseTest(Map<Object,List<Object>> testsMap,List<Object> testList){
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
	
	private List<Object> getIncludedTestsList(){
		List<Object> includedTestList = new ArrayList<Object>();
		includedTestList = this.res.getList("test");
		return includedTestList;
	}
	
	private List<Object> getExcludedTestsList(){
		List<Object> excludedTestsList = new ArrayList<Object>();
		excludedTestsList = this.res.getList("exclude_test");
		return excludedTestsList;
	}
	
	
	private void setPublicConfig() throws FileNotFoundException{
		Path path = Paths.get(this.getCWD(),File.separator,"com",File.separator,"grace",
				File.separator,"study",File.separator,"configuration",File.separator,"public_config.yaml");
		System.out.println(path.toString());
		publicConfig = (Map<Object, Object>) YamlUtil.loadYaml(path.toString());
	}
	
	private void setProjectConfig() throws FileNotFoundException{
		Path path = Paths.get(this.getCWD(),File.separator,"com",File.separator,"grace",
				File.separator,"study",File.separator,"projects",File.separator,this.getCurrentProject(),
				File.separator,"conf",File.separator,"project_config.yaml");
		System.out.println(path.toString());
		projectConfig = (Map<Object, Object>)YamlUtil.loadYaml(path.toString());
		System.out.println("projectConfig is : " + this.projectConfig);
	}
	
	private void setCurrenConfig() {
		Map<Object,Object> currentTempConfig = new HashMap<Object,Object>();
		if(!this.projectConfig.isEmpty()){
			
			Iterator<Map.Entry<Object, Object>> projectEntries = this.projectConfig.entrySet().iterator();
			while (projectEntries.hasNext()) {
				Map.Entry<Object, Object> entry = projectEntries.next();
				if (!entry.getKey().equals("env")){
					if (entry.getKey().equals("email_receiver")) {
						Map<Object,Object> emailMap = new HashMap<Object,Object>();
						emailMap = (Map<Object, Object>) entry.getValue();
						System.out.println("###################################");
						System.out.println();
						System.out.println("emailMap is : " + emailMap);
						System.out.println();
						System.out.println("###################################");
						Map<Object,Object> tmpEmailMap = new HashMap<Object,Object>();
						List<String> profilesList = this.getProfile();
						System.out.println("profilesList is : " + profilesList);
						Iterator<String> iter = profilesList.iterator();
						while(iter.hasNext()){
							String name = (String) iter.next();
							Iterator<Map.Entry<Object, Object>> emailEntries = emailMap.entrySet().iterator();
							while(emailEntries.hasNext()){
								Map.Entry<Object, Object> emailEntry = emailEntries.next();
								if(emailEntry.getKey().equals(name)){
									tmpEmailMap.put(emailEntry.getKey(), emailEntry.getValue());
								}
							}
							
						}
						currentTempConfig.put(entry.getKey(),tmpEmailMap);
					}else{
						currentTempConfig.put(entry.getKey(),entry.getValue());
					}
				}
			}
			
			System.out.println("currentTempConfig: " + currentTempConfig);
			
			Iterator<Map.Entry<Object, Object>> currentTempConfigEntries = currentTempConfig.entrySet().iterator();
			while(currentTempConfigEntries.hasNext()){
				Map.Entry<Object, Object> entry = currentTempConfigEntries.next();
				Map<Object ,Object> valueMap = new HashMap<Object,Object>();
				valueMap = (Map<Object, Object>) entry.getValue();
				Iterator<Map.Entry<Object, Object>> valueMapEntries = valueMap.entrySet().iterator();
				while(valueMapEntries.hasNext()){
					Map.Entry<Object, Object> valueEntry = valueMapEntries.next();
					this.currentConfig.put(valueEntry.getKey(), valueEntry.getValue());
				}
			}
			
			Iterator<Map.Entry<Object, Object>> publicEntries = this.publicConfig.entrySet().iterator();
			while(publicEntries.hasNext()){
				Map.Entry<Object, Object> entry = publicEntries.next();
				Map<Object ,Object> valueMap = new HashMap<Object,Object>();
				valueMap = (Map<Object, Object>) entry.getValue();
				Iterator<Map.Entry<Object, Object>> valueMapEntries = valueMap.entrySet().iterator();
				while(valueMapEntries.hasNext()){
					Map.Entry<Object, Object> valueEntry = valueMapEntries.next();
					this.currentConfig.put(valueEntry.getKey(), valueEntry.getValue());
				}
			}
			System.out.println("currentConfig: " + this.currentConfig);
		}
		
	}
	
	private void setEnvConfig(){
		Map<Object,Object> currentTempConfig = new HashMap<Object,Object>();
		Iterator<Map.Entry<Object, Object>> projectEntries = this.projectConfig.entrySet().iterator();
		while (projectEntries.hasNext()) {
			Map.Entry<Object, Object> entry = projectEntries.next();
			if (entry.getKey().equals("env")){
				Map<Object ,Object> valueMap = new HashMap<Object,Object>();
				valueMap = (Map<Object, Object>) entry.getValue();
				System.out.println("in setEnvConfig : " + valueMap);
				Iterator<Map.Entry<Object, Object>> valueMapEntries = valueMap.entrySet().iterator();
				while(valueMapEntries.hasNext()){
					Map.Entry<Object, Object> valueEntry = valueMapEntries.next();
					if(valueEntry.getKey().equals(this.getEnvironment())){
						Map<Object,Object> currentTempEnvConfig = new HashMap<Object,Object>();
						currentTempEnvConfig = (Map<Object, Object>) valueEntry.getValue();
						System.out.println("currentTempEnvConfig : " + currentTempEnvConfig);
						Iterator<Map.Entry<Object, Object>> currentTempEnvConfigEntries = currentTempEnvConfig.entrySet().iterator();
						while(currentTempEnvConfigEntries.hasNext()){
							Map.Entry<Object, Object> currentTempEnvConfigEntry = currentTempEnvConfigEntries.next();
							this.envConfig.put(currentTempEnvConfigEntry.getKey(), currentTempEnvConfigEntry.getValue());
						}
					}
				}
			}
		}
		System.out.println("envConfig is : " + this.envConfig);
	}
	
	
	private void setCommandParamenters(String[] args) throws ArgumentParserException{
		ArgumentParser parser = ArgumentParsers.newArgumentParser("prog");
		parser.addArgument("-p", "--profile").action(Arguments.append()).metavar("Release").type(String.class)
				.help("a string that indicates which test profile it will run with(Debug, Release or your custom profile)");
		
		parser.addArgument("-b", "--browser").nargs("?").metavar("IE").type(String.class)
				.setDefault("Chrome")
				.choices("Firefox", "Chrome", "IE", "Safari")
				.help("a string that indicates which brower it will run with(Firefox, Chrome, IE or Safari)");
		
		parser.addArgument("-u", "--users").nargs("?").metavar("Users_1").type(String.class)
				.setDefault("Users_1")
				.choices("Users_1", "Users_2")
				.help("a string that indicates which users it will run with(Users_1, Users_2)");
		
		parser.addArgument("-pr", "--project").nargs("?").metavar("cln").type(String.class)
				.setDefault("weather")
				.choices("rs","weather","baidu","cln")
				.help("a string that indicates which project it will run with(rs,cln, pci, amp, camp,pc,weather,gaming)");
		
		parser.addArgument("-T", "--tag").nargs("?").metavar("All").type(String.class)
				.setDefault("All")
				.choices("All","Smoke","Int","QA","Regression","Staging","Production")
				.help("a string that indicates which testcase type it will run with(All, Smoke, Regression, Staging, Production)");
		
		parser.addArgument("-rr", "--runresult").metavar("RunResult1.log").type(String.class)
				.setDefault("RunResult1.log")
				.help("a string that indicates which test run result file it will run with(RunResult1.log or RunResult2.log)");
		
		parser.addArgument("-lf", "--logfile").metavar("AutomatingTest1.log").type(String.class)
				.setDefault("AutomatingTest1.log")
				.help("a string that indicates which test log file it will run with(AutomatingTest1.log or AutomatingTest2.log)");
		
		parser.addArgument("-re", "--record").metavar("test").type(String.class)
				.setDefault("test")
				.choices("test", "formal")
				.help("a string that indicates whether it will be wrote to db with(test, formal)");
		
		
		MutuallyExclusiveGroup runModeGroup = parser.addMutuallyExclusiveGroup("group");
		runModeGroup.addArgument("-e", "--environment").metavar("DR").type(String.class)
				.help("a string that indicates which environment to test(QA, DR, Staging, PROD)");
		
		runModeGroup.addArgument("-lt", "--list_test").action(Arguments.storeTrue())
				.help("a indicator for listing all the test cases which you select");
		
		runModeGroup.addArgument("-lts", "--list_test_suite").action(Arguments.storeTrue())
				.help("a indicator for listing all the test suites which you select");
		
		parser.addArgument("-c", "--caller").metavar("Local Machine").type(String.class)
				.setDefault("Local Machine")
				.help("a string for describing where the test scripts are being called from, Continuous Integration, CLN Automation Center, PCI Automation Center or Local Machine?");
		
		
		parser.addArgument("-l", "--level").metavar("DEBUG").type(String.class)
				.setDefault("DEBUG")
				.help("a string for describing which level of logging you want to see, CRITICAL, ERROR, WARNING, INFO, DEBUG, NOTSET.");
	
		
		MutuallyExclusiveGroup testGroup = parser.addMutuallyExclusiveGroup("group");
		testGroup.addArgument("-t", "--test").metavar("test_class_name test_class_name.method_name")
				.nargs("+")
				.type(String.class)
				.help("a list of test classes or test methods");
		
		testGroup.addArgument("-et", "--exclude_test").metavar("test_class_name test_class_name.method_name")
				.nargs("+")
				.type(String.class)
				.help( "a list of test classes or test methods");
		
		this.res = parser.parseArgs(args);
	}
	
	public static void main(String [] args) throws FileNotFoundException, ArgumentParserException{
		Config conf = new Config(args);
		System.out.println("********************************");
		System.out.println("profile is : " + conf.getProfile());
		System.out.println("********************************");
	}
}
