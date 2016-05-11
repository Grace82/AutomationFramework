package com.grace.study.projects.weather.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.grace.study.common.testcasebase.ExecuteCasesBase;


public class TestWeatherSoapWebservice1 extends ExecuteCasesBase {
	
	@BeforeClass
	public static void testSoap11Init(){
		logger.info("This is the BeforClass testSoap11Init");
		System.out.println("testSoap11Init");
	}
	
	@Test
	public void testSoap11(){
		logger.info("This is the info testSoap11");
		System.out.println("testSoap11");
	}
	
	@Test
	public void testSoap12(){
		logger.info("This is the info testSoap12");
		System.out.println("testSoap12");
	}
	
	@Test
	public void testSoap13(){
		logger.info("This is the info testSoap13");
		System.out.println("testSoap13");
	}
	
	@AfterClass
	public static void testSoap11DearDown(){
		logger.info("This is the AfterClass testSoap11DearDown");
		System.out.println("testSoap11DearDown");
	}

	@Override
	protected void start() {
		// TODO Auto-generated method stub
		
	}

}
