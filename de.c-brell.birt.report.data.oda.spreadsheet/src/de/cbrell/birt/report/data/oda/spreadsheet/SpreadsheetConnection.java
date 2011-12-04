/*
 *************************************************************************
 * Copyright (c) 2010 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package de.cbrell.birt.report.data.oda.spreadsheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import com.ibm.icu.util.ULocale;

import de.cbrell.birt.report.data.oda.spreadsheet.input.Spreadsheet;
import de.cbrell.birt.report.data.oda.spreadsheet.input.SpreadsheetFactory;
import de.cbrell.birt.report.data.oda.spreadsheet.input.SpreadsheetFactory.SupportedType;
import de.cbrell.birt.report.data.oda.spreadsheet.utils.ContextHelper;
import de.cbrell.birt.report.data.oda.spreadsheet.utils.SpreadsheetConstants;

/**
 * Implementation class of IConnection for an ODA runtime driver.
 */
public class SpreadsheetConnection implements IConnection
{
	private InputStream stream;
	private Spreadsheet spreadsheet;
	private SpreadsheetFactory.SupportedType type;
	private Map<String, Object> appContext;

	public void close() throws OdaException {
		spreadsheet.close();
		spreadsheet = null;
		try {
			stream.close();
		}
		catch(Exception e){}

	}

	public void commit() throws OdaException {
		//not supported
	}

	public int getMaxQueries() throws OdaException {
		return 0;
	}

	public IDataSetMetaData getMetaData(String query) throws OdaException {

		return new SpreadsheetDataSetMetaData(this);
	}

	public boolean isOpen() throws OdaException {
		return (spreadsheet!=null);
	}

	public IQuery newQuery(String queryType) throws OdaException {
		SpreadsheetQuery queryData = new SpreadsheetQuery(spreadsheet);
		return queryData;
	}

	public void open(Properties properties) throws OdaException {
		ContextHelper ctx = new ContextHelper(appContext);
		String dirName = "";

		if(ctx.getStream()!= null && ctx.getSpreadsheetType()!=null) {
			this.stream = (InputStream)ctx.getStream();	
			for(SupportedType type: SpreadsheetFactory.SupportedType.values()){
				if(type.name().equalsIgnoreCase(ctx.getSpreadsheetType())) {
					this.type = type;
				}
			}
			if(type!=null) {
				try {
					this.spreadsheet = SpreadsheetFactory.factory(stream, type);
					return;
				} catch (Exception e) {
					throw new OdaException(e);
				}
			}
			throw new OdaException("The spreadsheet type "+ctx.getSpreadsheetType()+" that you injected into appContext is invalid");
		}

		else if(ctx.getStream()!=null) {
			throw new OdaException("Do not inject a inputStream without a Spreadsheet Type");
		}

		if(ctx.getInputFilename()!=null && ctx.getSpreadsheetType()!=null) {
			if(tryFile(ctx.getInputFilename())) {
				try {
					for(SupportedType type: SpreadsheetFactory.SupportedType.values()){
						if(type.name().equalsIgnoreCase(ctx.getSpreadsheetType())) {
							this.type = type;
						}
					}
					if(this.type==null) {
						throw new Exception("Do not inject a spreadsheet file without type");
					}
					this.spreadsheet = SpreadsheetFactory.factory(stream, type);
					return;
				} catch (Exception e) {
					throw new OdaException(e);
				}
			}
			else {
				throw new OdaException("Spreadsheet file "+ctx.getInputFilename()+" not found");
			}
		}


		String pathSeparator = System.getProperty("file.separator");
		if(pathSeparator==null) {
			pathSeparator = "/";
		}
		
		String filename = properties.getProperty(SpreadsheetConstants.SPREADSHEET_FILENAME_PROPERTY);
		boolean failed = true;
		if(ctx.getHomeFolder()!=null) {
			dirName = ctx.getHomeFolder();
			failed = !tryFile(dirName+pathSeparator+filename);
		}
		if(this.stream==null && ctx.isDirectAccessAllowed()) {
			dirName = properties.getProperty(SpreadsheetConstants.SPREADSHEET_DIRNAME_PROPERTY);
			failed = !tryFile(dirName+pathSeparator+filename);
		}
		if(failed) {
			throw new OdaException("File not found");
		}
		String spreadsheetType = properties.getProperty(SpreadsheetConstants.SPREADSHEET_TYPE_PROPERTY);


		for(SupportedType type: SpreadsheetFactory.SupportedType.values()){
			if(type.name().equalsIgnoreCase(spreadsheetType)) {
				this.type = type;
				break;
			}
		}
		if(type==null) {
			throw new OdaException("Spreadsheet Type "+spreadsheetType+" is unknown");
		}
		try {
			this.spreadsheet = SpreadsheetFactory.factory(stream, type);
		} catch (Exception e) {
			throw new OdaException(e);
		}

	}

	public void rollback() throws OdaException {
		//not supported
	}

	private boolean tryFile(String file) {
		try {
			this.stream = new FileInputStream(new File(file));
			return true;
		} catch (FileNotFoundException e1) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public void setAppContext(Object appContext) throws OdaException {
		if(appContext instanceof Map) {
			this.appContext = (Map<String, Object>)appContext;
		}
	}

	public void setLocale(ULocale locale) throws OdaException {
	}

	public Spreadsheet getUnderlyingSpreadsheet() {
		return this.spreadsheet;
	}

}
