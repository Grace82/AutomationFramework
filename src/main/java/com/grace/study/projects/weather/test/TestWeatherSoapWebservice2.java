package com.grace.study.projects.weather.test;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.grace.study.common.testcasebase.ExecuteCasesBase;



public class TestWeatherSoapWebservice2 extends ExecuteCasesBase{
	
	@BeforeClass
	public static void testSoap21Init(){
		logger.info("This is the BeforClass testSoap21Init");
		System.out.println("testSoap21Init");
	}
	
	@Test
	public void testSoap21() throws InterruptedException{
		logger.info("This is the info testSoap21");
		Thread.sleep(1000);
		System.out.println("testSoap21");
	}
	
	@Test
	public void testSoap22() throws InterruptedException{
		logger.info("This is the info testSoap22");
		Thread.sleep(1000);
		System.out.println("testSoap22");
	}
	
	@Test
	public void testSoap23() throws InterruptedException{
		logger.info("This is the info testSoap23");
		Thread.sleep(1000);
		System.out.println("testSoap23");
		assertEquals(1,0);
	}
	
	
	@AfterClass
	public static void testSoap21DearDown(){
		logger.info("This is the AfterClass testSoap21DearDown");
		System.out.println("testSoap21DearDown");
	}

	@Override
	protected void start() {
		// TODO Auto-generated method stub
		
	}
}
