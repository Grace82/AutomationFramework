package com.grace.study.common;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.grace.study.util.loghelper.LoggedTestCase;


public class TestLogger extends LoggedTestCase{
	
	@BeforeClass
	public static void initTestLogger(){
		logger.info("This is the initTestLogger");
	}
	
	@Test
	public void testInfo(){
		logger.info("This is the Info");
	}
	
	@Test
	public void testTrace(){
		logger.trace("This is the Trace");
	}
	
	@Test
	public void testDebug(){
		logger.debug("This is the Debug");
	}

	@Test
	public void testError(){
		logger.error("This is the Error");
	}

	@Test
	public void testFatal(){
		logger.fatal("This is the Fatal");
	}
	
	@AfterClass
	public static void downTestLogger(){
		logger.info("This is the downTestLogger");
	}
}
