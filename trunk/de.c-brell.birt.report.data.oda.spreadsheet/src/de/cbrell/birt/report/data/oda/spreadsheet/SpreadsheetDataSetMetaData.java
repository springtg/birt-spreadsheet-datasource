/*
 *************************************************************************
 * Copyright (c) 2010 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package de.cbrell.birt.report.data.oda.spreadsheet;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * Implementation class of IDataSetMetaData for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that assume this custom ODA data set
 * is capable of handling a query that returns a single result set and 
 * accepts scalar input parameters by index.
 * A custom ODA driver is expected to implement own data set specific
 * behavior in its place. 
 */
public class SpreadsheetDataSetMetaData implements IDataSetMetaData
{
	private SpreadsheetConnection connection;
	
	public SpreadsheetDataSetMetaData(SpreadsheetConnection connection) {
		this.connection = connection;
	}
	
	public IConnection getConnection() throws OdaException {
		return connection;
	}

	public int getDataSourceMajorVersion() throws OdaException {
		return 1;
	}

	public int getDataSourceMinorVersion() throws OdaException {
		return 0;
	}

	public IResultSet getDataSourceObjects(String arg0, String arg1,
			String arg2, String arg3) throws OdaException {
		return null;
	}

	public String getDataSourceProductName() throws OdaException {
		return "Spreadsheet Data Source";
	}

	public String getDataSourceProductVersion() throws OdaException {
		return null;
	}

	public int getSQLStateType() throws OdaException {
		return IDataSetMetaData.sqlStateXOpen;
	}

	public int getSortMode() {
		return IDataSetMetaData.sortModeNone;
	}

	public boolean supportsInParameters() throws OdaException {
		return false;
	}

	public boolean supportsMultipleOpenResults() throws OdaException {
		return true;
	}

	public boolean supportsMultipleResultSets() throws OdaException {
		return true;
	}

	public boolean supportsNamedParameters() throws OdaException {
		return false;
	}

	public boolean supportsNamedResultSets() throws OdaException {
		return false;
	}

	public boolean supportsOutParameters() throws OdaException {
		return false;
	}
    
}
