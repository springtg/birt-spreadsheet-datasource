package de.cbrell.birt.report.data.oda.spreadsheet;
/**
 * class can be used to inject config values using the BIRT ApplicationContext
 * @author cbrell
 *
 */
public class SpreadsheetConfigConstants {

	/**
	 * property to disallow folder access to BIRT engine using applicationContext...default value is false
	 */
	public static final String SPREADSHEET_ACCESS_HOME_FOLDER_ONLY = "SPREADSHEET_ACCESS_HOME_FOLDER_ONLY";	
	
	/**
	 * property to inject any input stream as a spreadsheet 
	 */
	public static final String SPREADSHEET_PASS_IN_STREAM = "SPREADSHEET_PASS_IN_STREAM";
	
	/**
	 * property to set a folder as root for BIRT to look for spreadsheets used in your reports
	 */
	public static final String SPREADSHEET_HOME_FOLDER = "SPREADSHEET_HOME_FOLDER";
	
	/**
	 * property to inject a filename as String to be used as spreadsheet for your spreadsheet data sources
	 * to be used with SPREADSHEET_HOME_FOLDER that has to contain the folder for the passed in filename
	 */
	public static final String SPREADSHEET_PASS_IN_FILENAME = "SPREADSHEET_PASS_IN_FILENAME";
	
	/**
	 * property to pass in the spreadsheet type...it is required to be set if you use SPREADSHEET_PASS_IN_STREAM
	 * or SPREADSHEET_PASS_IN_FILENAME it is ignored otherwise
	 */
	public static final String SPREADSHEET_PASS_IN_SPREADSHEET_TYPE = "SPREADSHEET_PASS_IN_SPREADSHEET_TYPE";
}
