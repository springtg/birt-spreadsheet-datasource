package de.cbrell.birt.report.data.oda.spreadsheet.input.jxl;

import jxl.Sheet;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Cell;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Worksheet;

public class JXLWorksheet implements Worksheet {
	
	private Sheet sheet;
	
	
	public JXLWorksheet(Sheet sheet) {
		this.sheet = sheet;
	}

	public int getTotalNumRows() {
		return sheet.getRows();
	}

	public int getTotalNumCols() {
		return sheet.getColumns();
	}

	public Cell[] getCells(int rowNum, int startCol, int endCol) {
		if(rowNum<1 || startCol<1 || startCol>endCol) {
			return new Cell[]{};
		}
		Cell[] cells = new Cell[endCol-startCol+1];
		for(int pos=startCol; pos<=endCol; pos++) {
			jxl.Cell cell = sheet.getCell(pos-1, rowNum-1);
			cells[pos-startCol] = new JXLCell(cell);
		}
		return cells;
	}
	
	public String getName() {
		return sheet.getName();
	}
}
