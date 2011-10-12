package de.cbrell.birt.report.data.oda.spreadsheet.input.poi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.cbrell.birt.report.data.oda.spreadsheet.input.Spreadsheet;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Worksheet;

public class PoiSpreadsheet implements Spreadsheet{
	
	private XSSFWorkbook workbook;
	
	private PoiSpreadsheet(XSSFWorkbook workbook) {
		this.workbook = workbook;
	}
	
	public PoiSpreadsheet(InputStream stream) throws IOException {
		this(new XSSFWorkbook(stream));
	}

	public List<Worksheet> getWorksheets() {
		List<Worksheet> worksheets = new ArrayList<Worksheet>();
		for(int i=0; i<workbook.getNumberOfSheets(); i++) {
			worksheets.add(new PoiWorksheet(workbook.getSheetAt(i)));
		}
		return worksheets;
	}

	public Worksheet getWorksheetNamed(String worksheetName) {
		XSSFSheet sheet = workbook.getSheet(worksheetName);
		if(sheet==null) {
			return null;
		}
		return new PoiWorksheet(sheet);
	}

	public Worksheet getWorksheetAt(int pos) {
		XSSFSheet sheet = workbook.getSheetAt(pos);
		if(sheet==null) {
			return null;
		}
		return new PoiWorksheet(sheet);
	}

	@Override
	public void close() {
		workbook = null;
	}

}
