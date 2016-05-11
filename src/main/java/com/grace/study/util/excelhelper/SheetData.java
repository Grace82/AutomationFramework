package com.grace.study.util.excelhelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class SheetData {
	private Sheet sheet;
	private List<String []> dataList = new ArrayList<String []>();
	public SheetData(Sheet sheet){
		this.sheet = sheet;
		setSheetDateSet();
	}
	
	public SheetData() {
	}

	protected String getCellText(Row row,int column){
		String cellText = "";
		Cell cell = row.getCell(column,Row.CREATE_NULL_AS_BLANK);
		switch(cell.getCellType()){
		case Cell.CELL_TYPE_STRING:
			cellText = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_BLANK:
			cellText = "";
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			cellText = Boolean.toString(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if(DateUtil.isCellDateFormatted(cell)){
				cellText = String.valueOf(cell.getDateCellValue());
			}else{
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String value = cell.getStringCellValue();
				if(value.indexOf(".")>-1){
					cellText = String.valueOf(new Double(value)).trim();
				}else{
					cellText = value.trim();
				}
			}
			break;
		case Cell.CELL_TYPE_ERROR:
			cellText = "";
			break;
		case Cell.CELL_TYPE_FORMULA:
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cellText = cell.getStringCellValue();
			if(cellText != null){
				cellText = cellText.replaceAll("#N/A", "").trim();
			}
			break;
		default:
			cellText = "";
			break;
		}
		return cellText;
	}
	
	private void setSheetDateSet(){
		int columnNum = 0;
		if(sheet.getRow(0) != null){
			columnNum = sheet.getRow(0).getLastCellNum()-sheet.getRow(0).getFirstCellNum();
		}
		if(columnNum >0){
			for(Row row:sheet){
				String[] singleRow = new String[columnNum];
				int n=0;
				for(int i=0;i<columnNum;i++){
					singleRow[n] = this.getCellText(row, i);
					n++;
				}
				if("".equals(singleRow[0])){continue;}
				dataList.add(singleRow);
			}
		}
	}
	
	public List<String[]> getSheetDataSet(){
		return dataList;
	}
	
	public int getRowCount(){
		return sheet.getLastRowNum()+1;
	}
	
	public int getColumnCount(){
		Row row = sheet.getRow(0);
		if(row !=null && row.getLastCellNum()>0){
			return row.getLastCellNum();
		}
		return 0;
	}
	
	
	public String[] getRowData(int rowIndex){
		String[] dataArray = null;
		if(rowIndex>this.getRowCount()){
			return dataArray;
		}else{
			dataArray = new String[this.getColumnCount()];
			return this.dataList.get(rowIndex);
		}
	}
	
	public String[] getColumnData(int colIndex){
		String[] dataArray = null;
		if(colIndex>this.getColumnCount()){
			return dataArray;
		}else{
			if(this.dataList!=null && this.dataList.size()>0){
				dataArray = new String[this.getRowCount()+1];
				int index = 0;
				for(String[] rowData:dataList){
					if(rowData!=null){
					dataArray[index] = rowData[colIndex];
					index++;
					}
				}
			}
		}
		return dataArray;
	}
	
	public String getText(int row, int column){
		return getRowData(row)[column];
	}
	
	public static void main(String[] args){
		
	}
	
}
