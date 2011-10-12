package de.cbrell.birt.report.data.oda.spreadsheet.input.oo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

import de.cbrell.birt.report.data.oda.spreadsheet.input.Spreadsheet;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Worksheet;

public class OOSpreadsheet implements Spreadsheet {

	private SpreadsheetDocument spreadsheet;
	
	public OOSpreadsheet(SpreadsheetDocument spreadsheet) {
		this.spreadsheet = spreadsheet;
	}
	
	public OOSpreadsheet(InputStream stream) throws Exception {
		this(SpreadsheetDocument.loadDocument(stream));
	}
	
	public List<Worksheet> getWorksheets() {
		List<Table> tables = spreadsheet.getTableList();
		List<Worksheet> sheets = new ArrayList<Worksheet>();
		for(Table table:tables) {
			OOWorksheet ws = new OOWorksheet(table);
			sheets.add(ws);
		}
		return sheets;
	}

	public Worksheet getWorksheetNamed(String worksheetName) {
		Table table = spreadsheet.getTableByName(worksheetName);
		if(table==null)
			return null;
		return new OOWorksheet(table);
	}

	public Worksheet getWorksheetAt(int pos) {
		List<Worksheet> sheets = getWorksheets();
		if(pos>sheets.size()-1)
			return null;
		return sheets.get(pos-1);
	}

	@Override
	public void close() {
		spreadsheet.close();
	}

}
