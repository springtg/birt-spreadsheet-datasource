package de.cbrell.birt.report.data.oda.spreadsheet.input.jxl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Spreadsheet;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Worksheet;

public class JXLSpreadsheet implements Spreadsheet {
	
	private Workbook workbook;
	
	private JXLSpreadsheet(Workbook workbook) {
		this.workbook = workbook;
	}

	public JXLSpreadsheet(InputStream stream) throws BiffException, IOException {
		this(Workbook.getWorkbook(stream));
	}

	public List<Worksheet> getWorksheets() {
		List<Worksheet> worksheets = new ArrayList<Worksheet>();
		Sheet[] sheets = workbook.getSheets();
		for(Sheet sheet:sheets) {
			worksheets.add(new JXLWorksheet(sheet));
			
		}
		return worksheets;
	}

	public Worksheet getWorksheetNamed(String worksheetName) {
		Sheet sheet = workbook.getSheet(worksheetName);
		if(sheet!=null) {
			return new JXLWorksheet(sheet);
		}
		return null;
	}
	
	public Worksheet getWorksheetAt(int pos) {
		Sheet sheet = workbook.getSheet(pos);
		if(sheet!=null) {
			return new JXLWorksheet(sheet);
		}
		return null;
	}

	@Override
	public void close() {
		workbook.close();
	}

}
