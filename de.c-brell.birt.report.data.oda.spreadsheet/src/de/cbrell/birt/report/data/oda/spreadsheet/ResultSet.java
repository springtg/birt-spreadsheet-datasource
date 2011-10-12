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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import de.cbrell.birt.report.data.oda.spreadsheet.input.Cell;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Worksheet;
import de.cbrell.birt.report.data.oda.spreadsheet.model.DataType;
import de.cbrell.birt.report.data.oda.spreadsheet.model.EndRowOption;
import de.cbrell.birt.report.data.oda.spreadsheet.model.SpreadsheetColumn;
import de.cbrell.birt.report.data.oda.spreadsheet.model.SpreadsheetQueryData;
import de.cbrell.birt.report.data.oda.spreadsheet.utils.SpreadsheetColumnMatcher;

/**
 * Implementation class of IResultSet for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that returns a pre-defined set
 * of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place. 
 */
public class ResultSet implements IResultSet
{
	private int m_maxRows;
	private int m_currentRowId;

	private Worksheet worksheet;

	private List<SpreadsheetColumn> columns;
	private SpreadsheetQueryData query;

	private LinkedList<Object[]> rowLookahead;
	private int numLookAhead = 1;

	private int currentSpreadsheetRow;
	private int startCol;
	private int endCol;

	private int emptyRows=0;

	private Object[] currentRow;

	private Object[] lastNonNullValues;
	
	private HashMap<Integer, DecimalFormat> formats;


	public ResultSet(Worksheet worksheet, SpreadsheetQueryData query) {
		this.worksheet = worksheet;
		this.query = query;
		this.formats = new HashMap<Integer, DecimalFormat>();
		columns = new ArrayList<SpreadsheetColumn>(query.getColumns());
		Collections.sort(columns, SpreadsheetQueryData.QUERY_COLUMN_SORTER);

		m_currentRowId = 0;
		currentSpreadsheetRow = query.getStartRow();
		if(query.getEndRowOption()==EndRowOption.EMPTY_ROWS) {
			numLookAhead = query.getEndRow();
		}

		startCol = columns.get(0).getColNum();
		endCol = columns.get(columns.size()-1).getColNum();
		lastNonNullValues = new Object[columns.size()];
		rowLookahead = new LinkedList<Object[]>();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getMetaData()
	 */
	public IResultSetMetaData getMetaData() throws OdaException {
		return new ResultSetMetaData(query);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#setMaxRows(int)
	 */
	public void setMaxRows( int max ) throws OdaException {
		m_maxRows = max;
	}

	/**
	 * Returns the maximum number of rows that can be fetched from this result set.
	 * @return the maximum number of rows to fetch.
	 */
	protected int getMaxRows() {
		return m_maxRows;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#next()
	 */
	public boolean next() throws OdaException {

		while(rowLookahead.size()<numLookAhead && currentSpreadsheetRow<=worksheet.getTotalNumRows()) {
			//append element
			rowLookahead.addLast(getNextRow());
			currentSpreadsheetRow++;
		}

		if(rowLookahead.isEmpty()) {
			return false;
		}


		if(query.getEndRowOption()==EndRowOption.EMPTY_ROWS && emptyRows>= query.getEndRow()) {
			return false;
		}
		else if(query.getEndRowOption()==EndRowOption.FIXED && m_currentRowId>=query.getEndRow()) {
			return false;
		}

		int maxRows = getMaxRows();

		if(maxRows==0 || m_currentRowId < maxRows)
		{
			m_currentRowId++;
			//pop first
			currentRow = rowLookahead.pop();
			return true;
		}
		return false;        
	}

	private Object[] getNextRow() throws OdaException {
		if(endCol>worksheet.getTotalNumCols()) {
			String colName = "";
			if(!query.getColumns().isEmpty()) {
				colName = query.getColumns().get(query.getColumns().size()-1).getColumnName();
			}
			throw new OdaException("The worksheet only contains "+worksheet.getTotalNumCols()+" columns. Please correct column number of dataset column named "+colName);
		}
		Cell[] cells = worksheet.getCells(currentSpreadsheetRow, startCol, endCol);
		Object[] row = new Object[columns.size()];
		int index=0;
		boolean wasEmpty = true;
		for(SpreadsheetColumn column: query.getColumns()) {
			Cell cell = cells[column.getColNum()-startCol];
			if(cell.hasError() && column.isStrictMode()) {
				throw new OdaException("Spreadsheet row "+currentSpreadsheetRow+" column "+column.getColNum()+" has a error. Resolve error or disable strict mode for column "+column.getColumnName());
			}

			else if(cell.getCellType()==Cell.CellType.EMPTY || cell.getRawDataValue()==null || cell.getRawDataValue().length()==0) {
				row[index] = null;
				if(column.shouldRepeat()) {
					row[index] = lastNonNullValues[index];
				}
			}
			
			else if(column.getDecimalSymbol()!=null || column.getGroupSymbol()!=null) {
				wasEmpty = false;
				try {
					Object converted = convertWithSymbols(column, column.getDataType(), cell.getRawDataValue());
					row[index] = converted;
				} catch (Exception e) {
					if(column.isStrictMode()) {
						OdaException ex = new OdaException("Spreadsheet Row "+currentSpreadsheetRow+" Column "+column.getColNum()+" having value "+cell.getRawDataValue()+" can't be parsed to Number. Deactivate strict mode for column "+column.getColumnName()+" or correct cell content.");
						ex.initCause(e);
					}
					row[index] = null;
				}
			}

			else  {
				try {
				Object value = SpreadsheetColumnMatcher.match(column.getDataType(), cell);
					if(value!=null) {
						wasEmpty = false;
						lastNonNullValues[index] = value;
					}
					else if(column.shouldRepeat()){
						value = lastNonNullValues[index];
					}
					row[index] = value;
				} catch(Exception e) {
					if(column.isStrictMode()) {
						OdaException ex =  new OdaException("Spreadsheet Row "+currentSpreadsheetRow+" Column "+column.getColNum()+" having value "+cell.getRawDataValue()+" can't be parsed to Number. Deactivate strict mode for column "+column.getColumnName()+" or correct cell content.");
						ex.initCause(e);
						throw ex;
					}
					else {
						row[index] = null;
					}
				}
			}
			index++;
		}
		if(wasEmpty) {
			emptyRows++;
		}
		else {
			emptyRows = 0;
		}
		return row;
	}
	
	private Object convertWithSymbols(SpreadsheetColumn column, DataType dataType, String value) throws Exception {
		DecimalFormat format = formats.get(column.getBirtColIndex());
		if(format==null) {
			DecimalFormatSymbols symbols = new DecimalFormatSymbols();
			symbols.setDecimalSeparator(column.getDecimalSymbol().charAt(0));
			symbols.setGroupingSeparator(column.getGroupSymbol().charAt(0));
			format = new DecimalFormat();
			format.setParseBigDecimal(true);
			format.setDecimalFormatSymbols(symbols);
			formats.put(column.getBirtColIndex(), format);
		}
		try {
			Number parse = format.parse(value);
			if(dataType==DataType.FLOAT) {
				return parse.doubleValue();
			}
			if(dataType==DataType.DECIMAL) {
				if(parse instanceof BigDecimal)
					return (BigDecimal)parse;
				return new BigDecimal(parse.doubleValue());
			}
			if(dataType==DataType.INTEGER) {
				return parse.intValue();
			}
			throw new IllegalArgumentException(dataType+" is not valid to be used with Group and DecimalSymbols");
		} catch (ParseException e) {
			throw new Exception(e);
		}
	}
	
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#close()
	 */
	public void close() throws OdaException {
		m_currentRowId = 0;     // reset row counter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getRow()
	 */
	public int getRow() throws OdaException {
		return m_currentRowId;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getString(int)
	 */
	public String getString( int index ) throws OdaException {
		return (String)currentRow[index-1];
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getString(java.lang.String)
	 */
	public String getString( String columnName ) throws OdaException {
		return getString( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getInt(int)
	 */
	public int getInt( int index ) throws OdaException {
		if(currentRow[index-1]==null) {
			return 0;
		}
		return (Integer)currentRow[index-1];
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getInt(java.lang.String)
	 */
	public int getInt( String columnName ) throws OdaException {
		return getInt( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDouble(int)
	 */
	public double getDouble( int index ) throws OdaException {
		if(currentRow[index-1]==null) {
			return 0;
		}
		return (Double)currentRow[index-1];
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDouble(java.lang.String)
	 */
	public double getDouble( String columnName ) throws OdaException {
		return getDouble( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal( int index ) throws OdaException {
		return (BigDecimal)currentRow[index-1];
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal( String columnName ) throws OdaException {
		return getBigDecimal(findColumn(columnName));
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDate(int)
	 */
	public Date getDate( int index ) throws OdaException {
		return (Date)currentRow[index-1];
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDate(java.lang.String)
	 */
	public Date getDate( String columnName ) throws OdaException {
		return getDate( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTime(int)
	 */
	public Time getTime( int index ) throws OdaException {
		throw new UnsupportedOperationException();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTime(java.lang.String)
	 */
	public Time getTime( String columnName ) throws OdaException {
		throw new UnsupportedOperationException();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTimestamp(int)
	 */
	public Timestamp getTimestamp( int index ) throws OdaException {
		throw new UnsupportedOperationException();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp( String columnName ) throws OdaException {
		throw new UnsupportedOperationException();
	}

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBlob(int)
	 */
	public IBlob getBlob( int index ) throws OdaException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBlob(java.lang.String)
	 */
	public IBlob getBlob( String columnName ) throws OdaException {
		return getBlob( findColumn( columnName ) );
	}

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getClob(int)
	 */
	public IClob getClob( int index ) throws OdaException
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getClob(java.lang.String)
	 */
	public IClob getClob( String columnName ) throws OdaException {
		return getClob( findColumn( columnName ) );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBoolean(int)
	 */
	public boolean getBoolean( int index ) throws OdaException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBoolean(java.lang.String)
	 */
	public boolean getBoolean( String columnName ) throws OdaException {
		return getBoolean( findColumn( columnName ) );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getObject(int)
	 */
	public Object getObject( int index ) throws OdaException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getObject(java.lang.String)
	 */
	public Object getObject( String columnName ) throws OdaException {
		throw new UnsupportedOperationException();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#wasNull()
	 */
	public boolean wasNull() throws OdaException {
		return false;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#findColumn(java.lang.String)
	 */
	public int findColumn( String columnName ) throws OdaException {
		for(int i=0; i<query.getColumns().size(); i++) {
			SpreadsheetColumn spreadsheetColumn = query.getColumns().get(i);
			if(columnName.equalsIgnoreCase(spreadsheetColumn.getColumnName())) {
				return i+1;
			}
		}
		throw new OdaException("Column "+columnName+" doesn't exist");
	}

}
