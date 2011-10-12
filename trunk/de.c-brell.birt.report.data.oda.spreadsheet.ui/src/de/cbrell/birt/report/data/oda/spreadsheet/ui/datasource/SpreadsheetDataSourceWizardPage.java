package de.cbrell.birt.report.data.oda.spreadsheet.ui.datasource;

import java.util.Properties;

import org.eclipse.swt.widgets.Composite;

import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.DataSourcePageValidationListener;

public class SpreadsheetDataSourceWizardPage extends org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage implements DataSourcePageValidationListener {
	
	private SpreadsheetDataSourceHelper helper;
	private Properties properties;

	public SpreadsheetDataSourceWizardPage(String s) {
		super(s);
		properties = new Properties();
		
	}
	
	@Override
	public Properties collectCustomProperties() {
		return helper.collectCustomProperties(properties);
	}
	
	@Override
	public void setInitialProperties(Properties properties) {
		this.properties = properties;
		if(this.btnPing!=null)
			this.btnPing.setEnabled(false);
		if(helper!=null)
			helper.setInput(properties);
	}
	

	@Override
	public void createPageCustomControl(Composite parent) {
		this.setPageComplete(false);
		
		helper = new SpreadsheetDataSourceHelper(this);
		setControl(helper.initialize(parent));
		helper.setInput(properties);
	}

	@Override
	public void setValid(boolean valid) {
		setPageComplete(valid);
		if(btnPing!=null)
			btnPing.setEnabled(valid);
	}
}