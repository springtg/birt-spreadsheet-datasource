package de.cbrell.birt.report.data.oda.spreadsheet.input;

import de.cbrell.birt.report.data.oda.spreadsheet.model.DataType;

public interface Cell {
	
	/**
	 * enum for cell data types
	 * @author cbrell
	 *
	 */
	public static enum CellType {
		NUMERIC, STRING, DATE, FORMULA, EMPTY, BOOLEAN, ERROR, FORMULA_BOOLEAN, FORMULA_DATE, ERROR_FORMULA, FORMULA_STRING, FORMULA_NUMERIC;
		
		/**
		 * converts cell data type to DataType used by this plugin or null if it couldn't be converted
		 * @return
		 */
		public DataType toDataType() {
			switch(this) {
			case NUMERIC:
			case FORMULA_NUMERIC: return DataType.FLOAT;
			
			case BOOLEAN:
			case FORMULA_BOOLEAN: return DataType.BOOLEAN;
			
			case DATE:
			case FORMULA_DATE: return DataType.DATE;
			
			case STRING:
			case FORMULA_STRING: return DataType.STRING;
			
			default: return null;
			}
		}
	}
	
	/**
	 * @return the cells data type
	 */
	public CellType getCellType();

	/**
	 * @return content of the cell having corrsponsing java data type
	 */
	public Object getValue();
	
	/**
	 * @return written content of a cell
	 */
	public String getRawDataValue();
	
	/**
	 * @return true if the cell has formula that has errors, false otherwise
	 */
	public boolean hasError();
	

}
