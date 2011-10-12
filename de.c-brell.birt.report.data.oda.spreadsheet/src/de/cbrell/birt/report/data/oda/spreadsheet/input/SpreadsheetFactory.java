package de.cbrell.birt.report.data.oda.spreadsheet.input;

import java.io.InputStream;

import de.cbrell.birt.report.data.oda.spreadsheet.input.jxl.JXLSpreadsheet;
import de.cbrell.birt.report.data.oda.spreadsheet.input.oo.OOSpreadsheet;
import de.cbrell.birt.report.data.oda.spreadsheet.input.poi.PoiSpreadsheet;

public class SpreadsheetFactory {
	
	public static enum SupportedType {
		XLS, XLSX, ODS;
		
		public static SupportedType fromString(String name) {
			if(name!=null) {
				name = name.trim();
				for(SupportedType type:SupportedType.values()) {
					if(type.name().equalsIgnoreCase(name)) {
						return type;
					}
				}
			}
			return null;
		}
	}
	
	
	public static Spreadsheet factory(InputStream stream, SupportedType type) throws Exception {
		try {
			switch(type) {
			case XLS: return new JXLSpreadsheet(stream);
			case XLSX: return new PoiSpreadsheet(stream);
			case ODS: return new OOSpreadsheet(stream);
			}
		}catch(Exception e) {
			throw new Exception("Could not read stream...it's most likely that the data source file is not of type "+type.name(), e);
		}
		return null;
	}

}
