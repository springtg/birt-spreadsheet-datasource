package de.cbrell.birt.report.data.oda.spreadsheet.model;

import de.cbrell.birt.report.data.oda.spreadsheet.utils.SpreadsheetConstants;


public enum EndRowOption {
	EOF(SpreadsheetConstants.STOP_PROPERTY_EOF), FIXED(SpreadsheetConstants.STOP_PROPERTY_ROW), EMPTY_ROWS(SpreadsheetConstants.STOP_PROPERTY_EMPTY);
	
	private Integer val;
	private EndRowOption(int val) {
		this.val = val;
	}
	
	public Integer toXMLProperty() {
		return val;
	}
	
	public static EndRowOption fromXML(int val) {
		EndRowOption[] values = EndRowOption.values();
		for(EndRowOption value:values) {
			if(value.toXMLProperty()==val) {
				return value;
			}
		}
		return null;
	}
}
