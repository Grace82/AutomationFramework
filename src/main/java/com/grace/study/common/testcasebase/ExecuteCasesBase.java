package com.grace.study.common.testcasebase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.grace.study.util.AutoException;
import com.grace.study.util.CheckPointUtil;
import com.grace.study.util.HsqldbUtil;
import com.grace.study.util.Timer;
import com.grace.study.util.excelhelper.ExcelPaser;
import com.grace.study.util.excelhelper.SheetData;
import com.grace.study.util.i18nhelper.I18NHelper;
import com.grace.study.util.loghelper.LoggedTestCase;

public abstract class ExecuteCasesBase extends LoggedTestCase {
	private static ArrayList<Timer> timers = null;
	private static ArrayList<CheckPointUtil> checkPoints = null;
	private static Map<String,Map<String,Long>> caseTimeMap = null;
	private static Map<String,Map<String,Long>> caseTimeTmp = null;
	private static Map<String,Map<String,String>> methodsParam = null;
	private static Map<String,Long> methodTime = null;
	private static long startTime = 0;
	private static long endTime = 0;
	private static long caseRunTime = 0;
	private static String methodName = null;
	private static String className=null;
	private static String logFile=null;
	private static String cwd = null;
	private static String caseFilePath=null;
	
	
	public ExecuteCasesBase(){
		super();
		I18NHelper.processI18NKeys(this);
		initializeResultLog();
		initializeDataSet();
	}
	
//	@Rule
//    public Timeout globalTimeout = new Timeout(300000,TimeUnit.MILLISECONDS);
	
    @Rule  
    public TestName name = new TestName();
    
    @Rule  
    public TestWatcher watchman = new TestWatcher() {  
  
        protected void starting(Description d) { 
        	logger.info("Begin to test " + d.getClassName() + "." + d.getMethodName());
        	className = d.getClassName();
        }  
  
        protected void succeeded(Description d) {  
        }  
  
        protected void failed(Throwable e, Description d) { 
        }  
        
        @Override  
        protected void skipped(final AssumptionViolatedException e,  
                final Description description) {  
            System.out.println("method " + description.getMethodName()  
                    + " was skipped because of '" + e.getMessage() + "'");  
        }  
        
        protected void finished(Description d) {  
        	logger.info("Finish to test " + d.getClassName() + "." + d.getMethodName());
        	methodTime.put(d.getMethodName(),caseRunTime);
        	System.out.println(methodTime.size());
        } 
    };  
	

	@BeforeClass
	public static void setUpClass() throws IOException, ClassNotFoundException {
		// 未实现
		HsqldbUtil.startHSQL();
		methodTime = new HashMap<String, Long>();
		caseTimeTmp = new HashMap<String, Map<String, Long>>();
		caseTimeMap = new HashMap<String, Map<String, Long>>();
		File file = new File(logFile);
		if (!file.exists()) {
			file.createNewFile();
		}
		if (file.exists() && file.isFile()) {
			if (file.length() > 0) {
				InputStream in = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(in);
				if (ois != null) {
					caseTimeTmp = (Map<String, Map<String, Long>>) ois.readObject();
					Iterator<Map.Entry<String, Map<String, Long>>> iterator = caseTimeTmp.entrySet().iterator();
					System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+caseTimeTmp.size());
					while (iterator.hasNext()) {
						Map.Entry<String, Map<String, Long>> entry = iterator.next();
						if (entry.getKey() != null) {
							caseTimeMap.put(entry.getKey(), entry.getValue());
						}
					}
				}
				in.close();
				ois.close();
			}
		}
	}
	
	@Before
	public void setUp(){
		checkPoints = new ArrayList<CheckPointUtil>();
		timers = new ArrayList<Timer>();
		start();
		startTime = System.currentTimeMillis();
	}
	
	@After
	public void tearDown(){
		checkForFailures();
		checkTimers();
    	endTime = System.currentTimeMillis();
    	caseRunTime = endTime -startTime;
    	System.out.println(caseRunTime);
	}
	
	@AfterClass
	public static void tearDownClass() throws IOException {
		HsqldbUtil.stopHSQL();
		caseTimeMap.put(className, methodTime);
		System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWSerializable: "+caseTimeMap.size());
		System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWSerializable: "+caseTimeMap.get("com.grace.study.projects.weather.test.TestWeatherSoapWebservice1"));
		System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWSerializable: "+caseTimeMap.get("com.grace.study.projects.weather.test.TestWeatherSoapWebservice0"));

		Iterator<Map.Entry<String, Map<String, Long>>> iterator = caseTimeMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Map<String, Long>> entry = iterator.next();
			Map<String, Long> caseTimeMap = entry.getValue();
			Iterator<Map.Entry<String, Long>> caseTimeEntries = caseTimeMap.entrySet().iterator();
			while(caseTimeEntries.hasNext()){
				Map.Entry<String, Long> caseTimeEntry = caseTimeEntries.next();
				System.out.println(caseTimeEntry.getKey());
				System.out.println(caseTimeEntry.getValue());
			}
		}
		
		File file = new File(logFile);
		if (file.exists()){
			file.delete();
		}
		file.createNewFile();
		OutputStream out = new FileOutputStream(file);
		ObjectOutputStream oout = new ObjectOutputStream(out);
		System.out.println(caseTimeMap.size());
		oout.writeObject(caseTimeMap);
		out.close();
		oout.close();
	}
	
	protected abstract void start();
	
	public String uniquify(String str){
		return str + " " + System.currentTimeMillis();
	}
	
	public static void waitfor(long milles){
		try{
			Thread.sleep(milles);
		}catch(Exception e){
			logger.info("\n\n timed out with exception : " + e.getMessage());
		}
	}
	
	private static void checkTimers(){
		StringBuilder sb = new StringBuilder("ID,TIME");
		for(Timer timer : timers){
			sb.append("\n");
			sb.append("\"" + timer.getId()+"\"");
			sb.append(",");
			sb.append("\""+timer.getTime()+"\"");
		}
		logger.info(sb.toString());
	}
	
	public Timer newTimer(String id){
		Timer timer = new Timer(id);
		timers.add(timer);
		return timer;
	}
	
	private void checkForFailures(){
		String exceptionMessage = "";
		boolean checkFailureFound = false;
		for (CheckPointUtil check : checkPoints){
			if(!check.status()){
				checkFailureFound = true;
				logger.error(check.getStatusMessage());
			}
		}
		if(checkFailureFound){
			exceptionMessage = exceptionMessage + "Found CheckPoint failures! See log for details.";
		}
		
		if(!exceptionMessage.isEmpty()){
			throw new AutoException(exceptionMessage);
			
		}
	}
	
	public static ArrayList<CheckPointUtil> getCheckPoints(){
		return checkPoints;
	}
	
	public CheckPointUtil newCheckPoint(String id){
		return newCheckPoint(id,"");
	}
	
	public CheckPointUtil newCheckPoint(String id,String description){
		if (!checkExists(id)){
			CheckPointUtil check = new CheckPointUtil(id,description);
			checkPoints.add(check);
			return check;
		}
		throw new AutoException("Check with id:" +id + "already exists. Specify unique id for your check");
	}
	
	private boolean checkExists(String id){
		for (CheckPointUtil check : checkPoints){
			if(check.getId().equals(id)){
				return true;
			}
		}
		return false;
	}
	
	private void initializeResultLog(){
		String usrDir = System.getProperty("user.dir");
		String packagePath = packageName.substring(0,packageName.lastIndexOf(".")).replace(".", File.separator);
		Path logPath = Paths.get(usrDir,File.separator,"target",File.separator,"classes",File.separator,packagePath,File.separator,"logs",File.separator,"RunCaseTimeResult.log");
		logFile = logPath.toString();
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" + logFile);
	}
	
	private void initializeDataSet(){
		try{
			String usrDir = System.getProperty("user.dir");
			String packagePath = packageName.substring(0,packageName.lastIndexOf(".")).replace(".", File.separator);
			Path caseFile = Paths.get(usrDir,File.separator,"target",File.separator,"classes",File.separator,packagePath,File.separator,"testcases",File.separator,this.getClass().getSimpleName()+".xlsx");
			caseFilePath = caseFile.toString();
			File file = new File(caseFilePath);
			if(file.exists()){
				InputStream in = new FileInputStream(file);
				methodsParam = new HashMap<String,Map<String,String>>();
				if(in != null){
					logger.info("Found test input...digesting file...");
					ExcelPaser excelPaser = new ExcelPaser(in);
					SheetData data = new SheetData();
					data = excelPaser.getSheetDataByName("Sheet1");
					int rowCount = data.getRowCount();
					System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +rowCount);
					for(int i=0;i<rowCount;i++){
						String[] strArray = data.getRowData(i);
						Map<String,String> params=new HashMap<String,String>();
						for(int j = 1;j<strArray.length;j+=2){
							String paramName  = strArray[j];
							String value = strArray[j+1];
							params.put(paramName, value);
						}
						methodsParam.put(strArray[0], params);
					}
				}
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +methodsParam.size());
				Iterator<Map.Entry<String,Map<String,String>>> entries = methodsParam.entrySet().iterator();
				while(entries.hasNext()){
					Map.Entry<String,Map<String,String>> entry = entries.next();
					System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + entry.getKey());
				}
			}else{
				logger.info("Error Can't find the cases file..");
			}
		}catch(Exception e){
			logger.error("Error while parsing input file.." ,e);
		}
	}
	
	public static Map<String,String> getMethodParamsValues(String methodName){
		return methodsParam.get(methodName);
	}
	
	public static String getMethodParamValue(String methodName,String paramName){
		return methodsParam.get(methodName).get(paramName);
	}
}	

