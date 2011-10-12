package de.cbrell.birt.report.data.oda.spreadsheet.ui.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;

public class UIUtils {

	public static int getWidth(Control control, int numChars) {
		GC gc = new GC(control);
		int singleCharWidth = gc.getFontMetrics().getAverageCharWidth();
		gc.dispose();
		return control.computeSize(numChars * singleCharWidth, SWT.DEFAULT).x;
	}

}
