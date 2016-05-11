package com.grace.study.projects.testlink.test;

import org.junit.Test;

import com.grace.study.common.testcasebase.ExecuteCasesBase;

public class TestLogin extends ExecuteCasesBase {
	@Test
	public void Soap01() throws InterruptedException{
		logger.info("This is the info testSoap01");
		System.out.println("testSoap01");
		System.out.println(getMethodParamsValues("login_1").get("param1"));
	}

	@Override
	protected void start() {
		// TODO Auto-generated method stub
		
	}
	
}
