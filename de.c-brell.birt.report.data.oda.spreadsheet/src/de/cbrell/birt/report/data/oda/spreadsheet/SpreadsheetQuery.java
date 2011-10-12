/*
 *************************************************************************
 * Copyright (c) 2010 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package de.cbrell.birt.report.data.oda.spreadsheet;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.SortSpec;
import org.eclipse.datatools.connectivity.oda.spec.QuerySpecification;

import de.cbrell.birt.report.data.oda.spreadsheet.input.Spreadsheet;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Worksheet;
import de.cbrell.birt.report.data.oda.spreadsheet.model.EndRowOption;
import de.cbrell.birt.report.data.oda.spreadsheet.model.SpreadsheetQueryData;
import de.cbrell.birt.report.data.oda.spreadsheet.utils.SpreadsheetConstants;

/**
 * Implementation class of IQuery for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that returns a pre-defined set
 * of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place. 
 */
public class SpreadsheetQuery implements IQuery
{
	
	private int maxRows;
	private ResultSetMetaData resultSetMetaData;
	private SpreadsheetQueryData queryData;
	private Spreadsheet spreadsheet;

	public SpreadsheetQuery(Spreadsheet spreadsheet) {
		this.queryData = new SpreadsheetQueryData();
		this.spreadsheet = spreadsheet;
	}

	public void cancel() throws OdaException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public void clearInParameters() throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void close() throws OdaException {
		this.maxRows = 0;
		this.resultSetMetaData = null;
	}

	public IResultSet executeQuery() throws OdaException {
		Worksheet worksheet= spreadsheet.getWorksheetNamed(queryData.getWorksheetName());
		if(worksheet==null) {
			throw new OdaException("sheet with name "+queryData.getWorksheetName()+" doesn't exist in this spreadsheet");
		}
		ResultSet rs = new ResultSet(worksheet, queryData);
		
		return rs;
	}

	public int findInParameter(String arg0) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public String getEffectiveQueryText() {
		try {
			return queryData.serializeQuery();
		} catch (Exception e) {
			return "";
		}
	}

	public int getMaxRows() throws OdaException {
		return this.maxRows;
	}

	public IResultSetMetaData getMetaData() throws OdaException {
		if(resultSetMetaData==null) {
			resultSetMetaData = new ResultSetMetaData(queryData);
		}
		return this.resultSetMetaData;
	}

	public IParameterMetaData getParameterMetaData() throws OdaException {
		return null;
	}

	public SortSpec getSortSpec() throws OdaException {
		throw new UnsupportedOperationException();
	}

	public QuerySpecification getSpecification() {
		return null;
	}

	public void prepare(String query) throws OdaException {
		try {
			queryData.deserializeQuery(query);
		} catch (Exception e) {
			throw new OdaException(e);
		}	
	}
	
	public void setQueryData(SpreadsheetQueryData data) {
		this.queryData = data;
	}

	public void setAppContext(Object context) throws OdaException {
		
	}

	public void setBigDecimal(String arg0, BigDecimal arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setBigDecimal(int arg0, BigDecimal arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setBoolean(String arg0, boolean arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setBoolean(int arg0, boolean arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setDate(String arg0, Date arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setDate(int arg0, Date arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setDouble(String arg0, double arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setDouble(int arg0, double arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setInt(String arg0, int arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setInt(int arg0, int arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setMaxRows(int rows) throws OdaException {
		this.maxRows = rows;
	}

	public void setNull(String arg0) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setNull(int arg0) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setObject(String arg0, Object arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setObject(int arg0, Object arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setProperty(String name, String value) throws OdaException {
		if(name.equals(SpreadsheetConstants.SPREADSHEET_SHEETNAME_PROPERTY)) {
			queryData.setWorksheetName(value);
		}
		else if(name.equals(SpreadsheetConstants.SPREADSHEET_STARTROW_PROPERTY)&& value!=null) {
			queryData.setStartRow(new Integer(value));
		}
		else if(name.equals(SpreadsheetConstants.SPREADSHEET_STOP_OPTION_PROPERTY) && value!=null) {
			queryData.setEndRowOption(EndRowOption.fromXML(new Integer(value)));
		}
		else if(name.equals(SpreadsheetConstants.SPREADSHEET_ENDROW_PROPERTY) && value!=null) {
			queryData.setEndRow(new Integer(value));
		}
	}

	public void setSortSpec(SortSpec arg0) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setSpecification(QuerySpecification spec) throws OdaException,
			UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public void setString(String arg0, String arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setString(int arg0, String arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setTime(String arg0, Time arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setTime(int arg0, Time arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setTimestamp(String arg0, Timestamp arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setTimestamp(int arg0, Timestamp arg1) throws OdaException {
		throw new UnsupportedOperationException();
	}
}
