package com.grace.study.common;

import java.util.Map;

public class CaseHelper {
	private int caseCount;
	private Map<String,Map<String,Long>> runModuleCaseAmountTimeDict = null;
	
	private CaseHelper() {}  
	private static CaseHelper single=null; 
	
	public static CaseHelper getInstance() {  
        if (single == null) {    
        	synchronized (CaseHelper.class) {    
               if (single == null) {    
            	   single = new CaseHelper();   
               }    
            }    
        }    
        return single;   
    }
	
	public void setCaseCount(int count){
		this.caseCount = count;
	}
	
	public int getCaseCount(){
		return this.caseCount;
	}
	
	public void setRunModuleCaseAmountTimeDict(Map<String,Map<String,Long>> runModuleCaseAmountTimeDict){
		this.runModuleCaseAmountTimeDict = runModuleCaseAmountTimeDict;
	}
	
	public Map<String,Map<String,Long>> getRunModuleCaseAmountTimeDict(){
		return this.runModuleCaseAmountTimeDict;
	}
}
