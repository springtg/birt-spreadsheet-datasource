package de.cbrell.birt.report.data.oda.spreadsheet.input;

public interface Worksheet {
	
	/**
	 * @return the total number of rows in that worksheet
	 */
	public int getTotalNumRows();
	
	/**
	 * 
	 * @return the total number of columns in that worksheet
	 */
	public int getTotalNumCols();
	
	/**
	 * @return worksheet name
	 */
	public String getName();

	/**
	 * returns a array of cells in corresponding worksheet row, starting at col startCol ending at col endCol
	 * if startCol>endCol or row<1 or startCol<1 or endCol<1 the method return a empty array
	 * @param rowNum the number of the row in the spreadsheet starting with 1
	 * @param startCol the number of the column in that row starting with 1
	 * @param endCol the number of the end column in that row starting with 1
	 * @return a array of cells containing cells from startCol up to endCol
	 */
	public Cell[] getCells(int rowNum, int startCol, int endCol);

}
