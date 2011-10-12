package de.cbrell.birt.report.data.oda.spreadsheet.model;

public class SpreadsheetColumn {
	
	private String columnName;
	private DataType dataType;
	private int colNum;
	private String conversionMask;
	private TrimType trimType;
	private String groupSymbol;
	private String decimalSymbol;
	private String defaultValue;
	private boolean strictMode = false;
	private int birtColIndex;
	
	private boolean repeat = false;
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public int getColNum() {
		return colNum;
	}
	public void setColNum(int colNum) {
		this.colNum = colNum;
	}
	public String getConversionMask() {
		return conversionMask;
	}
	public void setConversionMask(String conversionMask) {
		this.conversionMask = conversionMask;
	}
	public TrimType getTrimType() {
		return trimType;
	}
	public void setTrimType(TrimType trimType) {
		this.trimType = trimType;
	}
	public String getGroupSymbol() {
		return groupSymbol;
	}
	public void setGroupSymbol(String groupSymbol) {
		this.groupSymbol = groupSymbol;
	}
	public String getDecimalSymbol() {
		return decimalSymbol;
	}
	public void setDecimalSymbol(String decimalSymbol) {
		this.decimalSymbol = decimalSymbol;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public boolean isStrictMode() {
		return strictMode;
	}
	public void setStrictMode(boolean strictMode) {
		this.strictMode = strictMode;
	}
	public int getBirtColIndex() {
		return birtColIndex;
	}
	public void setBirtColIndex(int birtColIndex) {
		this.birtColIndex = birtColIndex;
	}
	
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}
	
	public boolean shouldRepeat() {
		return repeat;
	}

}
