package de.cbrell.birt.report.data.oda.spreadsheet.ui.utils;

import org.eclipse.jface.viewers.LabelProvider;

import de.cbrell.birt.report.data.oda.spreadsheet.model.DataType;

public class DataTypeLabelProvider extends LabelProvider{
	@Override
	public String getText(Object element) {
		DataType dt = (DataType)element;
		return dt.toString();
	}
}
