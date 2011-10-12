package de.cbrell.birt.report.data.oda.spreadsheet.input.oo;

import de.cbrell.birt.report.data.oda.spreadsheet.input.Cell;

public class OOCell implements Cell {
	
	org.odftoolkit.simple.table.Cell ooCell;
	
	public OOCell(org.odftoolkit.simple.table.Cell cell) {
		this.ooCell = cell;
	}

	public CellType getCellType() {
		String type = ooCell.getValueType();
		if(isEmpty(type)) {
			return CellType.EMPTY;
		}
		if("boolean".equalsIgnoreCase(type)) {
			if(!isEmpty(ooCell.getFormula()))
				return CellType.FORMULA_BOOLEAN;
			return CellType.BOOLEAN;
		}
		if("time".equalsIgnoreCase(type)) {
			if(!isEmpty(ooCell.getFormula())) {
				return CellType.FORMULA_DATE;
			}
			return CellType.DATE;
		}
		if("string".equalsIgnoreCase(type)) {
			if(!isEmpty(ooCell.getFormula())) {
				return CellType.FORMULA_STRING;
			}
			return CellType.STRING;
		}
		if("float".equalsIgnoreCase(type) || "currency".equalsIgnoreCase(type)) {
			if(!isEmpty(ooCell.getFormula())) {
				return CellType.FORMULA_NUMERIC;
			}
			return CellType.NUMERIC;
		}
		if("date".equalsIgnoreCase(type)) {
			if(!isEmpty(ooCell.getFormula())) {
				return CellType.FORMULA_DATE;
			}
			return CellType.DATE;
		}
		return null;
	}

	public Object getValue() {
		
		switch(getCellType()) {
		case FORMULA_BOOLEAN:
		case BOOLEAN:
			return ooCell.getBooleanValue();
		case FORMULA_STRING:
		case STRING:
			return ooCell.getStringValue();
		case FORMULA_NUMERIC:
		case NUMERIC:
			return ooCell.getDoubleValue();
		case EMPTY:
			return null;
		case FORMULA_DATE:
		case DATE:
			return new java.sql.Date(ooCell.getDateValue().getTime().getTime());
		}
		
		return null;
	}

	public boolean hasError() {
		return false;
	}
	
	private boolean isEmpty(String string) {
		if(string==null || "".equals(string.trim())) {
			return true;
		}
		return false;
	}
	
	@Override
	public String getRawDataValue() {
		return ooCell.getStringValue();
	}

}
