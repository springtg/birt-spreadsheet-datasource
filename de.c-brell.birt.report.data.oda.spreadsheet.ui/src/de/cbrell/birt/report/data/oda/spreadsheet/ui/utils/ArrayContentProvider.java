package de.cbrell.birt.report.data.oda.spreadsheet.ui.utils;

import org.eclipse.jface.viewers.Viewer;
/**
 * Array Content Provider was added to JFace lately that's why we don't want to use it to
 * enable BIRT <2.5 to work with the dataSource
 * @author cbrell
 *
 */
public class ArrayContentProvider implements org.eclipse.jface.viewers.IStructuredContentProvider{
	
	@Override
	public void dispose() {
		//nothing to do
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		//nothing to do
	}

	@Override
	public Object[] getElements(Object array) {
		return (Object[])array;
	}


}
