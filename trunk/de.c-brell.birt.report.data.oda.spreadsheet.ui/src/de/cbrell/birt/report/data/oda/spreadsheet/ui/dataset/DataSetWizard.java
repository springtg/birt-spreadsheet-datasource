package de.cbrell.birt.report.data.oda.spreadsheet.ui.dataset;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.OdaDesignSession;
import org.eclipse.datatools.connectivity.oda.design.ui.manifest.DataSetUIElement;
import org.eclipse.jface.wizard.IWizardPage;

import de.cbrell.birt.report.data.oda.spreadsheet.model.SpreadsheetQueryData;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.DataSetEditorSaveHelper;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.ShareSpreadsheetQueryData;

public class DataSetWizard extends org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizard {
	
	private SpreadsheetQueryData queryData;

	public void initialize(OdaDesignSession odaDesign, DataSetUIElement dataSetUIElement, boolean isForNewDesign) throws OdaException {
		super.initialize(odaDesign, dataSetUIElement, isForNewDesign);
		queryData = DataSetEditorSaveHelper.buildFromDataSetDesign(getEditingDataSet());
		for(IWizardPage page:this.getPages()) {
			if(page instanceof ShareSpreadsheetQueryData) {
				ShareSpreadsheetQueryData c = (ShareSpreadsheetQueryData)page;
				c.shareInstance(queryData);
			}
		}
	}
}
