package de.cbrell.birt.report.data.oda.spreadsheet.ui.dataset;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.cbrell.birt.report.data.oda.spreadsheet.model.DataType;
import de.cbrell.birt.report.data.oda.spreadsheet.model.SpreadsheetColumn;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.Activator;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.i18n.Messages;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.ArrayContentProvider;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.DataTypeLabelProvider;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.IntegerTextField;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.UIUtils;

public class SpreadsheetColumnDialog extends TitleAreaDialog {

	
	private Text columnName;
	private IntegerTextField columnInSpreadsheet;
	//private Text defaultValue;
	//private Text conversionMask;
	private Text groupSymbol;
	private Text decimalSymbol;
	//private Label sampleFormatted;
	private Combo dataTypeCombo;
	private Button strictMode;
	private Button shouldRepeat;
	
	private int index;
	private SpreadsheetColumn input;
	
	private SpreadsheetColumn output;


	private ComboViewer dataTypeComboViewer;
	private Composite container;
	private Label lblGroup;
	private Label lblDecimal;

	
	/**
	 * @wbp.parser.constructor
	 */
	private SpreadsheetColumnDialog(Shell shell) {
		super(shell);
	}
	
	public SpreadsheetColumnDialog(Shell parentShell, SpreadsheetColumn column) {
		this(parentShell);
		this.input = column;
		this.index = column.getBirtColIndex();
	}
	
	public SpreadsheetColumnDialog(Shell parentShell, int newIndex) {
		this(parentShell);
		this.index = newIndex;
	}
	
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		if(this.input==null) {
			setTitle("Create new column mapping");
		}
		else {
			setTitle("Modify existing column mapping");
		}
		return contents;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite parentContainer = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(parentContainer, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		container.setLayout(new GridLayout(2, false));
		
		Label lblColumnName = new Label(container, SWT.NONE);
		lblColumnName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblColumnName.setText(Messages.getString("SpreadsheetColumnDialog_1")); //$NON-NLS-1$
		
		columnName = new Text(container, SWT.BORDER);
		columnName.setToolTipText(Messages.getString("SpreadsheetColumnDialog_2")); //$NON-NLS-1$
		columnName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		columnName.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				revalidate();
			}
		});
		
		Label lblColumnNumber = new Label(container, SWT.NONE);
		lblColumnNumber.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblColumnNumber.setText(Messages.getString("SpreadsheetColumnDialog_3")); //$NON-NLS-1$
		
		columnInSpreadsheet = new IntegerTextField(container, SWT.BORDER);
		columnInSpreadsheet.getControl().setToolTipText(Messages.getString("SpreadsheetColumnDialog_4")); //$NON-NLS-1$
		columnInSpreadsheet.getControl().setLayoutData(new GridData(UIUtils.getWidth(columnInSpreadsheet.getControl(), 5), SWT.DEFAULT));
		//columnInSpreadsheet.getControl().setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		columnInSpreadsheet.getControl().addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				revalidate();
			}
		});
		
		Label lblDataType = new Label(container, SWT.NONE);
		lblDataType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDataType.setText(Messages.getString("SpreadsheetColumnDialog_5"));
		
		dataTypeComboViewer = new ComboViewer(container, SWT.NONE);
		dataTypeComboViewer.setContentProvider(new ArrayContentProvider());
		dataTypeComboViewer.setLabelProvider(new DataTypeLabelProvider());
		dataTypeComboViewer.setInput(DataType.values());
		dataTypeCombo = dataTypeComboViewer.getCombo();
		dataTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		dataTypeCombo.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent paramModifyEvent) {
				dataTypeModified();
				revalidate();
			}
		});
		new Label(container, SWT.NONE);
		
		strictMode = new Button(container, SWT.CHECK);
		strictMode.setText(Messages.getString("SpreadsheetColumnDialog_6")); //$NON-NLS-1$
		strictMode.setToolTipText(Messages.getString("SpreadsheetColumnDialog_7"));
		new Label(container, SWT.NONE);
		
		shouldRepeat = new Button(container, SWT.CHECK);
		shouldRepeat.setText("Repeat");
		shouldRepeat.setToolTipText("if checked the dataSource repeats the last non null value if column value is null for current row");
		
//		Label lblDefaultValue = new Label(container, SWT.NONE);
//		lblDefaultValue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
//		lblDefaultValue.setText(Messages.getString("SpreadsheetColumnDialog_8")); //$NON-NLS-1$
//		
//		defaultValue = new Text(container, SWT.BORDER);
//		defaultValue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
//		defaultValue.setToolTipText(Messages.getString("SpreadsheetColumnDialog_9")); //$NON-NLS-1$
//		
//		Label lblConversionMask = new Label(container, SWT.NONE);
//		lblConversionMask.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
//		lblConversionMask.setText(Messages.getString("SpreadsheetColumnDialog_10")); //$NON-NLS-1$
//		
//		conversionMask = new Text(container, SWT.BORDER);
//		conversionMask.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblGroup = new Label(container, SWT.NONE);
		lblGroup.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGroup.setText("Group:");
		lblGroup.setVisible(false);
		
		groupSymbol = new Text(container, SWT.BORDER);
		groupSymbol.setTextLimit(1);
		//groupSymbol.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		groupSymbol.setLayoutData(new GridData(UIUtils.getWidth(groupSymbol, 5), SWT.DEFAULT));
		groupSymbol.setVisible(false);
		groupSymbol.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent event) {
				revalidate();
			}
		});
		groupSymbol.addVerifyListener(new SymbolValidator());
		
		lblDecimal = new Label(container, SWT.NONE);
		lblDecimal.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDecimal.setText(Messages.getString("SpreadsheetColumnDialog_11"));
		lblDecimal.setVisible(false);
		
		decimalSymbol = new Text(container, SWT.BORDER);
		decimalSymbol.setTextLimit(1);
		//decimalSymbol.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		decimalSymbol.setLayoutData(new GridData(UIUtils.getWidth(decimalSymbol, 5), SWT.DEFAULT));
		decimalSymbol.setVisible(false);
		new Label(container, SWT.NONE);
		decimalSymbol.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent event) {
				revalidate();
			}
		});
		decimalSymbol.addVerifyListener(new SymbolValidator());

		//container.setTabList(new Control[]{columnName, });
		
		setInput();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, 
				Activator.PLUGIN_ID + ".spreadsheetColumnEditor");
		
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}
	
	private void setInput() {
		if(input==null) {
			return;
		}
		this.columnInSpreadsheet.setValue(input.getColNum());
		this.columnName.setText(input.getColumnName());
		
		if(input.isStrictMode()) {
			this.strictMode.setSelection(true);
		}
		else {
			this.strictMode.setSelection(false);
		}
		
		if(input.shouldRepeat()) {
			this.shouldRepeat.setSelection(true);
		}
		else {
			this.shouldRepeat.setSelection(false);
		}
//		if(input.getConversionMask()!=null)
//			this.conversionMask.setText(input.getConversionMask());
		if(input.getDecimalSymbol()!=null)
			this.decimalSymbol.setText(input.getDecimalSymbol());
		if(input.getGroupSymbol()!=null) {
			this.groupSymbol.setText(input.getGroupSymbol());
		}
		StructuredSelection selection = StructuredSelection.EMPTY;
		if(input.getDataType()!=null) {
			selection = new StructuredSelection(input.getDataType());
		}
		this.dataTypeComboViewer.setSelection(selection);
	}
	
	public SpreadsheetColumn getEditedInstance() {
		return output;
	}
	
	@Override
	protected void okPressed() {
		output = new SpreadsheetColumn();
		
		output.setBirtColIndex(index);
		output.setColNum(this.columnInSpreadsheet.getValue());
		output.setColumnName(this.columnName.getText());
		
		IStructuredSelection dataTypeSelection = (IStructuredSelection)dataTypeComboViewer.getSelection();
		DataType dataType = (DataType)dataTypeSelection.getFirstElement();
		output.setDataType(dataType);
		
		output.setStrictMode(this.strictMode.getSelection());
		
		if(dataType==DataType.INTEGER || dataType==DataType.DECIMAL || dataType==DataType.FLOAT) {
			if(this.decimalSymbol.getText()!=null && this.decimalSymbol.getText().trim().length()>0) {
				output.setDecimalSymbol(this.decimalSymbol.getText());
				output.setGroupSymbol(this.groupSymbol.getText());
			}
		}
		
		output.setRepeat(this.shouldRepeat.getSelection());
		
		super.okPressed();
	}
	
	public SpreadsheetColumn getEditedOutput() {
		return output;
	}
	
	public SpreadsheetColumn copyOutputTo(SpreadsheetColumn input) {
		input.setColNum(output.getColNum());
		input.setColumnName(output.getColumnName());
		input.setDataType(output.getDataType());
		input.setStrictMode(output.isStrictMode());
		
		input.setDecimalSymbol(output.getDecimalSymbol());
		input.setGroupSymbol(output.getGroupSymbol());
		
		input.setRepeat(output.shouldRepeat());
		return input;
	}
	
	public void dispose() {
		container.dispose();
	}
	
	private void dataTypeModified() {
		if(dataTypeSelectionIsNumber()) {
			this.lblGroup.setVisible(true);
			this.groupSymbol.setVisible(true);
			this.lblDecimal.setVisible(true);
			this.decimalSymbol.setVisible(true);
		}
		else {
			this.lblGroup.setVisible(false);
			this.groupSymbol.setVisible(false);
			this.lblDecimal.setVisible(false);
			this.decimalSymbol.setVisible(false);
		}
	}
	
	private boolean dataTypeSelectionIsNumber() {
		IStructuredSelection selection = (IStructuredSelection)dataTypeComboViewer.getSelection();
		if(!selection.isEmpty()) {
			DataType dt = (DataType)selection.getFirstElement();
			return (dt == DataType.INTEGER || dt==DataType.DECIMAL || dt==DataType.FLOAT);
		}
		return false;
	}
	
	private void revalidate() {
		String text = this.columnName.getText();
		if(text==null||text.trim().length()<1) {
			setErrorMessage("Please provide a column name for BIRT");
			setValid(false);
			return;
		}
		Integer value = this.columnInSpreadsheet.getValue();
		if(value==null) {
			setErrorMessage("Please provide a column number");
			setValid(false);
			return;
		}
		
		IStructuredSelection dataTypeSelection = (IStructuredSelection)dataTypeComboViewer.getSelection();
		if(dataTypeSelection.isEmpty()) {
			setErrorMessage("Please select data type");
			setValid(false);
			return;
		}
		
		if(dataTypeSelectionIsNumber()) {
			String group = groupSymbol.getText();
			String decimal = decimalSymbol.getText();
			if(group.length()==1||decimal.length()==1) {
				try {
					DecimalFormat sampleFormatter = getSampleFormatter(new String[]{group, decimal});
					double testValue = 12345.67;
					sampleFormatter.format(testValue);
				}
				catch(Exception e) {
					setErrorMessage("Group and or Decimal Symbol is invalid: "+e.getMessage());
					setValid(false);
					return;
				}
			}
		}
		
		setValid(true);
	}
	
	private void setValid(boolean isValid) {
		if(isValid) {
			setErrorMessage(null);
		}
	}
	
	private DecimalFormat getSampleFormatter(String[] symbols) {
		Character groupSymbol = null;
		Character decimalSymbol = null;
		if(symbols[0].length()==1 && symbols[1].length()==0) {
			groupSymbol = symbols[0].charAt(0);
			if(groupSymbol.equals(".")) {
				decimalSymbol = ',';
			}
			else {
				decimalSymbol = '.';
			}
		}
		else if(symbols[0].length()==0) {
			decimalSymbol = symbols[1].charAt(0);
			if(decimalSymbol.equals(".")) {
				groupSymbol = ',';
			}
			else {
				groupSymbol = '.';
			}
		}
		else {
			groupSymbol = symbols[0].charAt(0);
			decimalSymbol = symbols[1].charAt(0);
		}
		
		
		DecimalFormatSymbols dcSymbols = new DecimalFormatSymbols();
		dcSymbols.setGroupingSeparator(groupSymbol);
		dcSymbols.setDecimalSeparator(decimalSymbol);
		
		return new DecimalFormat("#.#", dcSymbols);
		
	}
	
	private static class SymbolValidator implements VerifyListener {

		@Override
		public void verifyText(VerifyEvent event) {
			char text = event.character;

			boolean valid = (!Character.isDigit(text));
			if(!valid) {
				event.doit = false;
			}
			else {
				event.doit = true;
			}
		}
	}

}