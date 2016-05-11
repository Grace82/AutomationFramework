package com.grace.study.util.loghelper;

import org.junit.BeforeClass;

public class CustomTestCase {
	protected String projectClassName = null;
	protected static String baseClassName = null;
	protected String packageName = null;
	
//	@BeforeClass
//	public static void initCustomTestCase()  {
//		baseClassName = CustomTestCase.class.getName();
//	} 
	
	public CustomTestCase(){
		System.out.println("This is the CustomTestCase");
		projectClassName = this.getClass().getName();
		packageName = this.getClass().getPackage().getName();
	}
}
