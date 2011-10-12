package de.cbrell.birt.report.data.oda.spreadsheet.input.poi;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import de.cbrell.birt.report.data.oda.spreadsheet.input.Cell;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Worksheet;

public class PoiWorksheet implements Worksheet {
	
	private XSSFSheet sheet;
	
	
	public PoiWorksheet(XSSFSheet sheet) {
		this.sheet = sheet;
	}

	public int getTotalNumRows() {
		return sheet.getLastRowNum();
	}

	public int getTotalNumCols() {
		return sheet.getRow(sheet.getFirstRowNum()).getLastCellNum()-sheet.getRow(sheet.getFirstRowNum()).getFirstCellNum();
		//return sheet.getRow(sheet.getLastRowNum()).getPhysicalNumberOfCells();
	}

	public Cell[] getCells(int rowNum, int startCol, int endCol) {
		Cell[] cells = new Cell[endCol-startCol+1];
		XSSFRow row = sheet.getRow(rowNum-1);
		for(int i=startCol; i<=endCol; i++) {
			XSSFCell cell = null;
			if(row!=null) {
				cell = row.getCell(i-1);
			}
			cells[i-startCol] = new PoiCell(cell);
		}
		return cells;
	}

	@Override
	public String getName() {
		return sheet.getSheetName();
	}

}
