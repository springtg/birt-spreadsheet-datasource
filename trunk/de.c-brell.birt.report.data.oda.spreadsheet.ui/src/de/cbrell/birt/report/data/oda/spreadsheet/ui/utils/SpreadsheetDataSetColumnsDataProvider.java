package de.cbrell.birt.report.data.oda.spreadsheet.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.cbrell.birt.report.data.oda.spreadsheet.model.SpreadsheetColumn;

public class SpreadsheetDataSetColumnsDataProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
		//do nothing
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//nothing to do
		
	}

	@Override
	public Object[] getElements(Object input) {
		@SuppressWarnings("unchecked")
		List<SpreadsheetColumn> content = (ArrayList<SpreadsheetColumn>)input;
		return content.toArray();
	}

}
