package de.cbrell.birt.report.data.oda.spreadsheet.model;

import java.math.BigDecimal;
import java.sql.Date;

public enum DataType {
	STRING(0, String.class), DECIMAL(1, BigDecimal.class), FLOAT(2, Double.class), DATE(3, Date.class), BOOLEAN(4, Boolean.class), INTEGER(5, Integer.class);
	
	private int ODADataTypeConstant;
	private Class<?> correspondingJavaType;
	
	
	private DataType(int constant, Class<?> javaDataType) {
		this.ODADataTypeConstant = constant;
		this.correspondingJavaType = javaDataType;
	}
	
	public String toString() {
		String name = this.name();
		name = name.toLowerCase();
		return Character.toUpperCase(name.charAt(0))+name.substring(1);
	}

	public int toODA() {
		return ODADataTypeConstant;
	}
	
	public Class<?> getJavaType() {
		return this.correspondingJavaType;
	}
}
