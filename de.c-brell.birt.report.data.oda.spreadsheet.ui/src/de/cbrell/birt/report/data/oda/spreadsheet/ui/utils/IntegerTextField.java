package de.cbrell.birt.report.data.oda.spreadsheet.ui.utils;

import java.util.regex.Pattern;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class IntegerTextField {
	
	private Text internalField;
	
	private static final Pattern INT_PATTERN = Pattern.compile("\\d*");

	public IntegerTextField(Composite parent, int style) {
		internalField = new Text(parent, style);
		internalField.addVerifyListener(new VerifyListener() {
			
			@Override
			public void verifyText(VerifyEvent event) {
				String value = (String)event.text;
				if(INT_PATTERN.matcher(value).matches()) {
					event.doit = true;
				}
				else {
					event.doit = false;
				}
			}
		});
		internalField.setTabs(5);
	}
	
	public Integer getValue() {
		String txt = internalField.getText();
		if(txt!=null && txt.length()>0) {
			return Integer.parseInt(txt);
		}
		return null;
	}
	
	public void setValue(int value) {
		internalField.setText(Integer.toString(value));
	}
	
	public Text getControl() {
		return internalField;
	}

}
