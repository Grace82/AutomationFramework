package com.grace.study.util.loghelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;

public class LoggedTestCase extends CustomTestCase {
	protected static Class<?> projectClazz = null;
	protected static Class<?> baseClazz = null;
	protected static Logger logger = null;
	protected static Logger baseLogger = null;
	
//	@BeforeClass
//	public static void initLoggedTestCase()  {
//		try {
//			baseClazz = Class.forName(baseClassName);
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		baseLogger = LogManager.getLogger(baseClazz);
//	} 
	
	public LoggedTestCase(){
		super();
		try {
			projectClazz = Class.forName(projectClassName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger = LogManager.getLogger(projectClazz);
	}
}






