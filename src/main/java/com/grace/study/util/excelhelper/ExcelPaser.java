package com.grace.study.util.excelhelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelPaser {
	private Workbook wb = null;
	private Map<String,SheetData> sheetDataMap= new HashMap<String,SheetData>();
	private int sheetNum = -1;
	public ExcelPaser(InputStream in){
		try{
			wb = WorkbookFactory.create(in);
			sheetNum = wb.getNumberOfSheets();
			setSheetDataMap();
		}catch(InvalidFormatException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void setSheetDataMap() {
		for (int sheetIndex = 0; sheetIndex < sheetNum; sheetIndex++) {
			String sheetName = wb.getSheetName(sheetIndex);
			SheetData sheetData = new SheetData(wb.getSheetAt(sheetIndex));
			sheetDataMap.put(sheetName, sheetData);
		}
	}
	
	public SheetData getSheetDataByName(String sheetName){
		return sheetDataMap.get(sheetName);
	}
	
	public int getSheetIndex(String sheetName){
		int index = wb.getSheetIndex(sheetName);
		return index;
	}
	
	public String getText(String sheetName,int row,int column){
		return getSheetDataByName(sheetName).getText(row, column);
	}
	
	
	public static void main(String[] args) throws FileNotFoundException{
		File file = new File("G:\\projects\\study.auto\\target\\classes\\com\\grace\\study\\projects\\weather\\testcases\\TestWeatherSoapWebservice0.xlsx");
		Map<String,String> params = new HashMap<String,String>();
		InputStream in = new FileInputStream(file);
		ExcelPaser ex = new ExcelPaser(in);
		SheetData data = new SheetData();
		data = ex.getSheetDataByName("Sheet1");
		int rowCount = data.getRowCount();
		System.out.println(rowCount);
//		String[] strArray = data.getRowData(1);
//		for(int j = 1;j<strArray.length;j+=2){
//			String methodName  = strArray[j];
//			String value = strArray[j+1];
//			params.put(methodName, value);
//		}
//		System.out.println(params.size());
//		Iterator<Map.Entry<String,String>> entries = params.entrySet().iterator();
//		while(entries.hasNext()){
//			 Map.Entry<String, String> entry = entries.next(); 
//			 System.out.println(entry.getKey());
//			 System.out.println(entry.getValue());
//		}
//		for(int j=0;j<=rowCount;j++){
//			String[] strArray = data.getRowData(j);
//			for(String str : strArray){
//				System.out.println(str);
//			}
//		}
	}
}
