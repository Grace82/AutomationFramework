package com.grace.study.util.excelhelper;

import java.io.File;
import java.io.InputStream;

import com.grace.study.common.testcasebase.BusinessLogicBase;
import com.grace.study.common.testcasebase.ExecuteCasesBase;
import com.grace.study.util.loghelper.LoggedTestCase;


public class InputExcelFileFinder extends LoggedTestCase{
	public static InputStream getInputFileAsStream(ExecuteCasesBase executeClass){
		return getInputFileAsStream(executeClass.getClass());
	}
	
	public static InputStream getInputFileAsStream(BusinessLogicBase businessClass){
		return getInputFileAsStream(businessClass.getClass());
	}
	
	public static InputStream getInputFileAsStream(Class<?> testClass){
		String packageName = "";
		String resourcePackageName = "";
		if(testClass.getPackage() != null){
			packageName = testClass.getPackage().getName();
			resourcePackageName = packageName.substring(0,packageName.lastIndexOf("."));
		}
		String resourceName = (resourcePackageName + ".testcases." + testClass.getSimpleName()).replace(".", File.separator) + ".xlsx";
		ClassLoader classLoader = testClass.getClassLoader();
		logger.info("Searching the default input excel testcases file: " + resourceName);
		return classLoader.getResourceAsStream(resourceName);
	}
}
