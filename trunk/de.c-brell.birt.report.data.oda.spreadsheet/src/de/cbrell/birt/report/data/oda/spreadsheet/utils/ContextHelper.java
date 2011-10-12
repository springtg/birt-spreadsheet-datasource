package de.cbrell.birt.report.data.oda.spreadsheet.utils;

import java.io.InputStream;
import java.util.Map;

import de.cbrell.birt.report.data.oda.spreadsheet.SpreadsheetConfigConstants;

public class ContextHelper {
	
	private String homeDir;
	private Boolean allowDirectAccess = true;

	private InputStream stream;
	private String spreadsheetType;
	
	private String inputFileName;
	
	private Map<String, Object> appContext;
	
	public ContextHelper(Map<String, Object> applicationContext) {
		WebAppExtractor.initialize(applicationContext);
		this.appContext = applicationContext;
		
		Boolean accessHomeFolderOnly = getConfigValue(SpreadsheetConfigConstants.SPREADSHEET_ACCESS_HOME_FOLDER_ONLY, Boolean.class);
		if(accessHomeFolderOnly==null) {
			allowDirectAccess = Boolean.TRUE;
		}
		else {
			allowDirectAccess = !accessHomeFolderOnly;
		}
		
		this.stream = getConfigValue(SpreadsheetConfigConstants.SPREADSHEET_PASS_IN_STREAM, InputStream.class);
		this.spreadsheetType = getConfigValue(SpreadsheetConfigConstants.SPREADSHEET_PASS_IN_SPREADSHEET_TYPE, String.class);
		
		this.homeDir = getConfigValue(SpreadsheetConfigConstants.SPREADSHEET_HOME_FOLDER, String.class);
		this.inputFileName = getConfigValue(SpreadsheetConfigConstants.SPREADSHEET_PASS_IN_FILENAME, String.class);
		
	}
	
	@SuppressWarnings("unchecked")
	private <E> E getConfigValue(String configValueName, Class<E> clazz) {
		Object value = null;
		if(appContext!=null) {
			value = appContext.get(configValueName);
		}
		if(value!=null) {
			return (E)value;
		}
		else {
			if(configValueName.equals(SpreadsheetConfigConstants.SPREADSHEET_ACCESS_HOME_FOLDER_ONLY)) {
				return (E)WebAppExtractor.getAccessHomeFolderOnly();
			}
			else if(configValueName.equals(SpreadsheetConfigConstants.SPREADSHEET_HOME_FOLDER)) {
				return (E)WebAppExtractor.getSpreadsheetHomeFolderPath();
			}
		}
		return null;
		
	}
	
	public InputStream getStream() {
		return this.stream;
	}
	
	public String getType() {
		return this.spreadsheetType;
	}
	
	public String getHomeFolder() {
		return this.homeDir;
	}
	
	public String getInputFilename() {
		return this.inputFileName;
	}
	
	public String getSpreadsheetType() {
		return this.spreadsheetType;
	}
	
	public Boolean isDirectAccessAllowed() {
		return allowDirectAccess;
	}
	

}
