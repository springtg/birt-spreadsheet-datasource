package de.cbrell.birt.report.data.oda.spreadsheet.input;

import java.util.List;

public interface Spreadsheet {
	
	/**
	 * returns all Worksheets in that spreadsheet file
	 * @return
	 */
	public List<Worksheet> getWorksheets();
	
	/**
	 * returns worksheet having name worksheetName or null if it doesn't exist
	 * @param worksheetName the name of the worksheet
	 * @return
	 */
	public Worksheet getWorksheetNamed(String worksheetName);
	
	/**
	 * returns worksheet at pos. This is the pos-th element of the List returned by getWorksheets()
	 * @param pos
	 * @return
	 */
	public abstract Worksheet getWorksheetAt(int pos);
	
	/**
	 * closes the spreadsheet file and deallocates memory
	 */
	public void close();

}
