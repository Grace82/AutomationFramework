package com.grace.study.projects.weather.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.grace.study.common.testcasebase.ExecuteCasesBase;


public class TestWeatherSoapWebservice3 extends ExecuteCasesBase {
	
	@BeforeClass
	public static void testSoap31Init(){
		logger.info("This is the BeforClass testSoap31Init");
		System.out.println("testSoap31Init");
	}
	
	@Test
	public void testSoap31(){
		logger.info("This is the info testSoap31");
		System.out.println("testSoap31");
	}
	
	@Test
	public void testSoap32(){
		logger.info("This is the info testSoap32");
		System.out.println("testSoap32");
	}
	
	@Test
	public void testSoap33(){
		logger.info("This is the info testSoap33");
		System.out.println("testSoap33");
	}
	
	@AfterClass
	public static void testSoap31DearDown(){
		logger.info("This is the AfterClass testSoap31DearDown");
		System.out.println("testSoap31DearDown");
	}

	@Override
	protected void start() {
		// TODO Auto-generated method stub
		
	}
}
