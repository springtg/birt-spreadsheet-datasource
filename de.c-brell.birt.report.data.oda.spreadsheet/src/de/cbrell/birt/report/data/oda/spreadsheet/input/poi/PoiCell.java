package de.cbrell.birt.report.data.oda.spreadsheet.input.poi;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;

import de.cbrell.birt.report.data.oda.spreadsheet.input.Cell;

public class PoiCell implements Cell {
	
	private XSSFCell hCell;
	public PoiCell(XSSFCell cell) {
		this.hCell = cell;
	}

	public CellType getCellType() {
		if(hCell==null) {
			return CellType.EMPTY;
		}
		switch(hCell.getCellType()) {
		case XSSFCell.CELL_TYPE_BLANK: return CellType.EMPTY;
		case XSSFCell.CELL_TYPE_BOOLEAN: return CellType.BOOLEAN;
		case XSSFCell.CELL_TYPE_ERROR: return CellType.ERROR;
		case XSSFCell.CELL_TYPE_NUMERIC:  
		  if (DateUtil.isCellDateFormatted(hCell)) {
	        return CellType.DATE; 
	      } else {
	        return CellType.NUMERIC;
	      }
		case HSSFCell.CELL_TYPE_STRING: return CellType.STRING;
		case HSSFCell.CELL_TYPE_FORMULA: return getCachedFormulaCellType();
		}
		
		return null;
	}
	
	private CellType getCachedFormulaCellType() {
		if(hCell==null) {
			return CellType.EMPTY;
		}
		switch(hCell.getCachedFormulaResultType()) {
	      case HSSFCell.CELL_TYPE_BLANK: return CellType.EMPTY;
	      case HSSFCell.CELL_TYPE_ERROR: return CellType.EMPTY;
	      case HSSFCell.CELL_TYPE_BOOLEAN: return CellType.FORMULA_BOOLEAN;
	      case HSSFCell.CELL_TYPE_STRING: return CellType.FORMULA_STRING;
	      case HSSFCell.CELL_TYPE_NUMERIC: 
	        if (HSSFDateUtil.isCellDateFormatted(hCell)) {
	          return CellType.FORMULA_DATE; 
	        } else {
	          return CellType.FORMULA_NUMERIC;
	        }
	      }
		return null;
	}

	public Object getValue() {
		if(hCell==null) {
			return null;
		}
		switch(getCellType()) {
		case DATE:
		case FORMULA_DATE:
			long timestamp = hCell.getDateCellValue().getTime();
//			int offset = TimeZone.getDefault().getOffset(timestamp);
			return new java.sql.Date(timestamp);
		case BOOLEAN:
		case FORMULA_BOOLEAN:
			boolean booleanCellValue = hCell.getBooleanCellValue();
			return Boolean.valueOf(booleanCellValue);
		case NUMERIC:
		case FORMULA_NUMERIC:
			double numericCellValue = hCell.getNumericCellValue();
			return Double.valueOf(numericCellValue);
		case EMPTY:
		case ERROR:
			return null;
		case STRING:
		case FORMULA_STRING:
			return hCell.getStringCellValue();
		}
		return null;
	}
	
	/**
	 * Poi doesn't have a method to get the raw string content of a cell
	 * so we have to do a toString using the interpreted content 
	 */
	public String getRawDataValue() {
		if(hCell==null) {
			return null;
		}
		Object value = getValue();
		if(value==null) {
			return null;
		}
		return value.toString();
	}

	public boolean hasError() {
		return getCellType()==CellType.ERROR;
	}

}
