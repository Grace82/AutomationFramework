package com.grace.study.common.testcasebase;

import java.util.HashMap;
import java.util.Map;

import com.grace.study.util.i18nhelper.I18NHelper;
import com.grace.study.util.loghelper.LoggedTestCase;


public abstract class BusinessLogicBase extends LoggedTestCase{
	protected Map<String,String> parameters = new HashMap<String,String>();
	public BusinessLogicBase(){
		super();
		I18NHelper.processI18NKeys(this);
	}
	
	public void parseParams(String... params){
		for(String param : params){
			try{
				String name = param.substring(0, param.indexOf('=')).trim();
				String value = param.substring(param.indexOf('=') + 1).trim();
				if(!parameters.containsKey(name)){
					parameters.put(name, value);
				}else{
					logger.warn("Duplicated parameter'" + name +"' given. Ignoring...");
				}
			}catch(Exception e){
				logger.error("BusinessLogicBase Error Happened Evaulating Parameter '" + param + "'.  Correct format is 'name=value'. This parameter will be ignored.\n" + e.getMessage());
			}
		}
	}
	
	public String getParam(String name){
		if(!parameters.containsKey(name)){
			logger.debug("Requested parameter'" + name + "' does not exist! Returning null.");
		}
		return parameters.get(name);
	}
	
	public void setParam(String name, String value){
		parameters.put(name, value);
	}
	
	protected boolean paramDefined(String paramName){
		return parameters.containsKey(paramName) && (!parameters.get(paramName).equals("") && parameters.get(paramName) != null );	
	}
	
	public abstract void prepare();
	
	public <X extends BusinessLogicBase> X navigateTo(X se){
		se.prepare();
		return se;
	}
	
	public static void waitfor(long milles){
		try{
			Thread.sleep(milles);
		}catch(Exception e){
			logger.error("\n\n Time Out With Exception: " + e.getMessage());
		}
	}
}
