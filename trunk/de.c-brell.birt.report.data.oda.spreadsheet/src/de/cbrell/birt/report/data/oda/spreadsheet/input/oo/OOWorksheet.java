package de.cbrell.birt.report.data.oda.spreadsheet.input.oo;

import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

import de.cbrell.birt.report.data.oda.spreadsheet.input.Cell;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Worksheet;

public class OOWorksheet implements Worksheet {
	
	private Table table;
	
	public OOWorksheet(Table table) {
		this.table = table;
	}

	public int getTotalNumRows() {
		return table.getRowCount();
	}

	public int getTotalNumCols() {
		return table.getColumnCount();
	}

	public Cell[] getCells(int rowNum, int startCol, int endCol) {
		if(startCol>endCol || startCol<1 || endCol<1 || rowNum>getTotalNumRows() || endCol>getTotalNumCols()) {
			return null;
		}
		Cell[] cells = new Cell[endCol-startCol+1];
		
		for(int i=startCol; i<=endCol; i++) {
			Row row = table.getRowByIndex(rowNum-1);
			org.odftoolkit.simple.table.Cell cell = row.getCellByIndex(i-1);
			cells[i-startCol] = new OOCell(cell); 
		}
		
		return cells;
	}

	@Override
	public String getName() {
		return table.getTableName();
	}

}
