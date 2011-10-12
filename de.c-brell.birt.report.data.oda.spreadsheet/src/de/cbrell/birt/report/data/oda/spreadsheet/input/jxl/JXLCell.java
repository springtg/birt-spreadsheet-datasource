package de.cbrell.birt.report.data.oda.spreadsheet.input.jxl;

import jxl.BooleanCell;
import jxl.DateCell;
import jxl.LabelCell;
import jxl.NumberCell;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Cell;



public class JXLCell implements Cell {
	
	private jxl.Cell cell;
	
	public JXLCell(jxl.Cell cell) {
		this.cell = cell;
	}

	public CellType getCellType() {
		jxl.CellType cellType = cell.getType();
		if(cellType==jxl.CellType.BOOLEAN) {
			return CellType.BOOLEAN;
		}
		if(cellType==jxl.CellType.BOOLEAN_FORMULA) {
			return CellType.FORMULA_BOOLEAN;
		}
		if(cellType==jxl.CellType.DATE) {
			return CellType.DATE;
		}
		if(cellType==jxl.CellType.DATE_FORMULA) {
			return CellType.FORMULA_DATE;
		}
		if(cellType==jxl.CellType.EMPTY) {
			return CellType.EMPTY;
		}
		if(cellType==jxl.CellType.ERROR) {
			return CellType.ERROR;
		}
		if(cellType==jxl.CellType.FORMULA_ERROR) {
			return CellType.ERROR_FORMULA;
		}
		if(cellType==jxl.CellType.LABEL) {
			return CellType.STRING;
		}
		if(cellType==jxl.CellType.STRING_FORMULA) {
			return CellType.FORMULA_STRING;
		}
		if(cellType==jxl.CellType.NUMBER) {
			return CellType.NUMERIC;
		}
		if(cellType==jxl.CellType.NUMBER_FORMULA) {
			return CellType.FORMULA_NUMERIC;
		}
		
		return null;
	}


	public Object getValue() {
		switch(getCellType()) {
		    case FORMULA_BOOLEAN: 
		    case BOOLEAN: return Boolean.valueOf( ((BooleanCell)cell).getValue() );
		    case FORMULA_DATE:
		    case DATE: return new java.sql.Date(((DateCell)cell).getDate().getTime());
		    case FORMULA_NUMERIC:
		    case NUMERIC: return Double.valueOf( ((NumberCell)cell).getValue() );
		    case FORMULA_STRING:
		    case STRING: return ((LabelCell)cell).getString();
		    case EMPTY:
		    default: return null;
	    }
	}

	public boolean hasError() {
		return getCellType()==CellType.ERROR;
	}
	
	@Override
	public String getRawDataValue() {
		return cell.getContents();
	}

}
