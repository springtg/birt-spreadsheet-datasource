package de.cbrell.birt.report.data.oda.spreadsheet.ui.datasource;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceEditorPage;
import org.eclipse.swt.widgets.Composite;

import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.DataSourcePageValidationListener;

public class SpreadsheetDataSourceEditorPage extends DataSourceEditorPage implements DataSourcePageValidationListener {

	
	private SpreadsheetDataSourceHelper helper;

	
	@Override
	public Properties collectCustomProperties(Properties properties) {
		return helper.collectCustomProperties(properties);
	}
	
	
	

	@Override
	protected void createAndInitCustomControl(Composite parent, Properties properties) {
		helper = new SpreadsheetDataSourceHelper(this);
		helper.initialize(parent);
		//helper.initialize();
		helper.setInput(properties);
	}
	
	@Override
	public void setValid(boolean b) {
		super.setValid(b);
		if(btnPing!=null)
			btnPing.setEnabled(b);
		
	}
}
