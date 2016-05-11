package com.grace.study.util.filehelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.grace.study.common.testcasebase.BusinessLogicBase;
import com.grace.study.common.testcasebase.ExecuteCasesBase;

public class InputFileFinder {
	public static InputStream getInputFileAsStream(ExecuteCasesBase base){
		return getInputFileAsStream(base);
	}
	
	public static InputStream getInputFileAsStream(BusinessLogicBase base){
		return getInputFileAsStream(base);
	}
	
	private static InputStream getInputFileAsStream(Class<?> testClass)throws IOException {
		String packageName = "";
		String resourcePackageName = "";
		if (testClass.getPackage() != null) {
			packageName = testClass.getPackage().getName();
			resourcePackageName = packageName.substring(0,packageName.lastIndexOf("."));
			System.out.println(resourcePackageName);
		}
		String resourceName = (resourcePackageName + ".testcases.").replace(".",File.separator) + testClass.getSimpleName() + ".xlxs";
		System.out.println(resourceName);
		ClassLoader classLoader = testClass.getClassLoader();
		InputStream input = classLoader.getResourceAsStream(resourceName);
		InputStreamReader isr = new InputStreamReader(input);
		BufferedReader br = new BufferedReader(isr);
		String read = null;
		while ((read = br.readLine()) != null) {
	        System.out.println(read);
	     }
		return input;
	}
}
