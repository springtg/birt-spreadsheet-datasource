package de.cbrell.birt.report.data.oda.spreadsheet.ui.utils;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDriver;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSetParameters;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.ResultSetColumns;
import org.eclipse.datatools.connectivity.oda.design.ResultSetDefinition;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;

import de.cbrell.birt.report.data.oda.spreadsheet.Driver;
import de.cbrell.birt.report.data.oda.spreadsheet.model.EndRowOption;
import de.cbrell.birt.report.data.oda.spreadsheet.model.SpreadsheetQueryData;
import de.cbrell.birt.report.data.oda.spreadsheet.utils.SpreadsheetConstants;

public class DataSetEditorSaveHelper {
	private static Logger logger = Logger
			.getLogger(DataSetEditorSaveHelper.class.getName());

	public static void updateDataSetProperties(DataSetDesign design,
			SpreadsheetQueryData queryData) {
		logger.finest("update dataSetDesign with dataSet properties");
		Properties properties = new java.util.Properties();
		properties.setProperty(
				SpreadsheetConstants.SPREADSHEET_SHEETNAME_PROPERTY,
				queryData.getWorksheetName());
		properties.setProperty(
				SpreadsheetConstants.SPREADSHEET_STARTROW_PROPERTY, queryData
						.getStartRow().toString());
		properties.setProperty(
				SpreadsheetConstants.SPREADSHEET_STOP_OPTION_PROPERTY,
				queryData.getEndRowOption().toXMLProperty().toString());
		if (queryData.getEndRowOption() != EndRowOption.EOF) {
			properties.setProperty(
					SpreadsheetConstants.SPREADSHEET_ENDROW_PROPERTY, queryData
							.getEndRow().toString());
		}
		org.eclipse.datatools.connectivity.oda.design.Properties dsProps;
		try {
			dsProps = DesignSessionUtil.createDataSetPublicProperties(
					Driver.ODA_DATA_SOURCE_ID, Driver.ODA_DATA_SET_ID,
					properties);
		} catch (OdaException e) {
			logger.log(Level.WARNING,
					"DataSource Properties could not be created");
			logger.throwing(DataSetEditorSaveHelper.class.getCanonicalName(),
					"updateDataSetProperties", e);
			throw new RuntimeException(e);
		}
		design.setPublicProperties(dsProps);
	}

	public static void savePage(DataSetDesign dataSetDesign,
			SpreadsheetQueryData queryData) {
		String queryText = queryData.serializeQuery();
		// updateDataSetProperties(dataSetDesign, queryData);
		dataSetDesign.setQueryText(queryText);

		IConnection customConn = null;
		try {
			IDriver customDriver = new Driver();

			customConn = customDriver.getConnection(null);
			Properties connProps = DesignSessionUtil
					.getEffectiveDataSourceProperties(dataSetDesign
							.getDataSourceDesign());
			customConn.open(connProps);

			updateDesign(dataSetDesign, customConn, queryText);
		} catch (OdaException e) {
			dataSetDesign.setResultSets(null);
			dataSetDesign.setParameters(null);

			e.printStackTrace();
		} finally {
			closeConnection(customConn);
		}
	}

	private static void closeConnection(IConnection customConn) {
		try {
			if (customConn.isOpen()) {
				customConn.close();
			}
		} catch (OdaException e) {
		}
	}

	private static void updateDesign(DataSetDesign dataSetDesign,
			IConnection conn, String queryText) throws OdaException {
		IQuery query = conn.newQuery(null);
		query.prepare(queryText);
		try {
			IResultSetMetaData md = query.getMetaData();
			updateResultSetDesign(md, dataSetDesign);
		} catch (OdaException e) {
			dataSetDesign.setResultSets(null);
			e.printStackTrace();
		}

		try {
			IParameterMetaData paramMd = query.getParameterMetaData();
			updateParameterDesign(paramMd, dataSetDesign);
		} catch (OdaException ex) {
			dataSetDesign.setParameters(null);
			ex.printStackTrace();
		}
	}

	private static void updateResultSetDesign(IResultSetMetaData md,
			DataSetDesign dataSetDesign) throws OdaException {
		ResultSetColumns columns = DesignSessionUtil
				.toResultSetColumnsDesign(md);

		ResultSetDefinition resultSetDefn = DesignFactory.eINSTANCE
				.createResultSetDefinition();

		resultSetDefn.setResultSetColumns(columns);

		dataSetDesign.setPrimaryResultSet(resultSetDefn);
		dataSetDesign.getResultSets().setDerivedMetaData(true);
	}

	private static void updateParameterDesign(IParameterMetaData paramMd,
			DataSetDesign dataSetDesign) throws OdaException {
		DataSetParameters paramDesign = DesignSessionUtil
				.toDataSetParametersDesign(paramMd,
						DesignSessionUtil.toParameterModeDesign(1));

		dataSetDesign.setParameters(paramDesign);
		if (paramDesign == null) {
			return;
		}
		paramDesign.setDerivedMetaData(true);
		// just in case we want to support parameters in future
		// if (paramDesign.getParameterDefinitions().size() > 0)
		// {
		// ParameterDefinition paramDef =
		// (ParameterDefinition)paramDesign.getParameterDefinitions().get(0);
		// if (paramDef != null)
		// paramDef.setDefaultScalarValue("dummy default value");
		// }
	}

	public static SpreadsheetQueryData buildFromDataSetDesign(
			DataSetDesign design) {
		SpreadsheetQueryData data = new SpreadsheetQueryData();
		org.eclipse.datatools.connectivity.oda.design.Properties properties = design
				.getPublicProperties();
		if (properties == null) {
			return data;
		}
		if (properties
				.getProperty(SpreadsheetConstants.SPREADSHEET_SHEETNAME_PROPERTY) != null) {
			data.setWorksheetName(properties
					.getProperty(SpreadsheetConstants.SPREADSHEET_SHEETNAME_PROPERTY));
			data.setStartRow(new Integer(
					properties
							.getProperty(SpreadsheetConstants.SPREADSHEET_STARTROW_PROPERTY)));
			data.setEndRowOption(de.cbrell.birt.report.data.oda.spreadsheet.model.EndRowOption.fromXML(new Integer(
					properties
							.getProperty(SpreadsheetConstants.SPREADSHEET_STOP_OPTION_PROPERTY))));
		}
		if (properties
				.getProperty(SpreadsheetConstants.SPREADSHEET_ENDROW_PROPERTY) != null) {
			data.setEndRow(new Integer(
					properties
							.getProperty(SpreadsheetConstants.SPREADSHEET_ENDROW_PROPERTY)));
		}
		if (design.getQueryText() != null) {
			data.deserializeQuery(design.getQueryText());
		}
		return data;
	}
}
