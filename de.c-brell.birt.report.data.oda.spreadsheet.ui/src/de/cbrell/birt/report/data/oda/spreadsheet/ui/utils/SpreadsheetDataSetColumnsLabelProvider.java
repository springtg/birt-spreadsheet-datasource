package de.cbrell.birt.report.data.oda.spreadsheet.ui.utils;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.cbrell.birt.report.data.oda.spreadsheet.model.SpreadsheetColumn;

public class SpreadsheetDataSetColumnsLabelProvider implements ITableLabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	@Override
	public String getColumnText(Object data, int column) {
		SpreadsheetColumn row = (SpreadsheetColumn)data;
		switch(column) {
		case 0: return Integer.toString(row.getBirtColIndex());
		case 1: return Integer.toString(row.getColNum());
		case 2: return row.getColumnName();
		case 3: return row.getDataType().toString();
		case 4: if(row.isStrictMode()) return "Y"; return "N";
		case 5: if(row.shouldRepeat()) return "Y"; return "N";
		case 6: return row.getGroupSymbol();
		case 7: return row.getDecimalSymbol();
		}
		return null;
	}

}
