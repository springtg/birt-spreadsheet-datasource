package de.cbrell.birt.report.data.oda.spreadsheet.ui.utils;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import de.cbrell.birt.report.data.oda.spreadsheet.model.SpreadsheetColumn;

public class SpreadsheetDataSetColumnsViewerSorter extends ViewerSorter {
	
	
	@Override
	public int compare(Viewer viewer, Object data1, Object data2) {
		SpreadsheetColumn c1 = (SpreadsheetColumn)data1;
		SpreadsheetColumn c2 = (SpreadsheetColumn)data2;
		return new Integer(c1.getBirtColIndex()).compareTo(c2.getBirtColIndex());
	}

}
