package com.grace.study.projects.weather.test;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.grace.study.common.testcasebase.ExecuteCasesBase;



public class TestWeatherSoapWebservice0 extends ExecuteCasesBase {
	
	@BeforeClass
	public static void Soap01Init(){
		logger.info("This is the BeforClass testSoap01Init");
		System.out.println("testSoap01Init");
		
	}

	@Test
	public void Soap01() throws InterruptedException{
		logger.info("This is the info testSoap01");
		System.out.println("testSoap01");
		System.out.println(getMethodParamsValues("login_1").get("param1"));
	}
	
	@Test
	public void Soap02() throws InterruptedException{
		logger.fatal("This is the fatal testSoap02");
		assertEquals(1,0);
		System.out.println(getMethodParamsValues("login_1").get("param2"));
	}
	
	@Test
	public void Soap03() throws InterruptedException{
		logger.error("This is the error testSoap03");
		System.out.println("testSoap03");
		System.out.println(getMethodParamsValues("login_1").get("param1"));
	}
	
	@AfterClass
	public static void DearDown(){
		logger.info("This is the AfterClass testSoap01DearDown");
		System.out.println("testSoap01DearDown");
	}

	@Override
	protected void start() {
		// TODO Auto-generated method stub
		
	}

}
