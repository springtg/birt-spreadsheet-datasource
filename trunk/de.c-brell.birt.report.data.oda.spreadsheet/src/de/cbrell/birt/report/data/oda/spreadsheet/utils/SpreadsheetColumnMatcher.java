package de.cbrell.birt.report.data.oda.spreadsheet.utils;

import java.math.BigDecimal;

import de.cbrell.birt.report.data.oda.spreadsheet.input.Cell;
import de.cbrell.birt.report.data.oda.spreadsheet.model.DataType;

public class SpreadsheetColumnMatcher {
	
	public static Object match(DataType dataType, Cell cell) throws Exception {
		if(cell.getValue()==null) {
			//null can be everything
			return null;
		}
		
		DataType cellDataType = cell.getCellType().toDataType();
		if(cellDataType==dataType) {
			return cell.getValue();
		}
		try {
			switch(cellDataType) {
				case FLOAT: if(dataType.getJavaType()==String.class) 
								return cell.getRawDataValue();
							return fromNumber((Double)cell.getValue(), dataType.getJavaType());
				case STRING: return fromString((String)cell.getValue(), dataType.getJavaType());
			}
		}catch(Exception e) {
			throw new Exception("cell value "+cell.getRawDataValue()+" can't be converted to "+dataType.getJavaType().getCanonicalName());
		}
		
		throw new IllegalArgumentException("cell data type "+cellDataType.getJavaType().getCanonicalName()+" can't be converted into "+dataType.getJavaType().getCanonicalName());
	}
	
	private static Object fromNumber(Double value, Class<?> toClass) {
		if(toClass==String.class)
			return String.valueOf(value);
		else if(toClass==Integer.class) {
			return Integer.valueOf(value.intValue());
		}
		else if(toClass==BigDecimal.class) {
			return new BigDecimal(value);
		}
		else if(toClass==Boolean.class) {
			return value>=0;
		}
		throw new IllegalArgumentException(toClass.getCanonicalName()+" is not in the list of supported data types");
	}
	
	private static Object fromString(String s, Class<?> toClass) {
		if(toClass==Double.class) {
			return Double.valueOf(s);
		}
		if(toClass==BigDecimal.class) {
			return new BigDecimal(s);
		}
		if(toClass==Boolean.class) {
			if(s.trim().equalsIgnoreCase("YES")||s.trim().equalsIgnoreCase("Y") || s.trim().equalsIgnoreCase("TRUE")) {
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}
		throw new IllegalArgumentException(toClass.getCanonicalName()+" is not in the list of supported data types");
	}
	
	

}
