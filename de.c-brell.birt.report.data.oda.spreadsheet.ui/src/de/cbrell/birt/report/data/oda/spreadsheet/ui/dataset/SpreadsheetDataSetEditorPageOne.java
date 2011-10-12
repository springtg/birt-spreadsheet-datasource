package de.cbrell.birt.report.data.oda.spreadsheet.ui.dataset;

import java.util.Arrays;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.cbrell.birt.report.data.oda.spreadsheet.Driver;
import de.cbrell.birt.report.data.oda.spreadsheet.SpreadsheetConnection;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Spreadsheet;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Worksheet;
import de.cbrell.birt.report.data.oda.spreadsheet.model.EndRowOption;
import de.cbrell.birt.report.data.oda.spreadsheet.model.SpreadsheetQueryData;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.ArrayContentProvider;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.DataSetEditorSaveHelper;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.IntegerTextField;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.ShareSpreadsheetQueryData;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.UIUtils;

public class SpreadsheetDataSetEditorPageOne extends DataSetWizardPage implements ShareSpreadsheetQueryData {
	private IntegerTextField startRow;
	private IntegerTextField endRowEmptyField;
	private IntegerTextField endRowFixedField;
	private ComboViewer worksheetName;
	private Button endRowEOFOption;
	private Button endRowEmptyRowsOption;
	private Button endRowFixedOption;
	private boolean canLeave;
	private SpreadsheetQueryData options;

	/**
	 * Create the wizard.
	 */
	public SpreadsheetDataSetEditorPageOne(String title) {
		super(title);
		setTitle(title);
		setDescription(title);
		canLeave = false;
		setPageComplete(false);
	}
	
	
	private void setInput() {
		this.worksheetName.setInput(grabSheetNames());
		if(this.worksheetName!=null && options.getWorksheetName()!=null) {
			StructuredSelection selection = new StructuredSelection(options.getWorksheetName());
			worksheetName.setSelection(selection);
		}
		if(options.getStartRow()!=null) {
			startRow.setValue(options.getStartRow());
		}
		EndRowOption endRowOption = options.getEndRowOption();
		if(endRowOption!=null) {
			switch(endRowOption) {
			case EOF: endRowEOFOption.setSelection(true);
						break;
			case EMPTY_ROWS: endRowEmptyRowsOption.setSelection(true);
					endRowEmptyField.setValue(options.getEndRow());
					break;
			case FIXED: endRowFixedOption.setSelection(true);
				endRowFixedField.setValue(options.getEndRow() );
				break;
			}
		}
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setPageComplete(false);
		setControl(container);
		GridLayout gl_container = new GridLayout(3, false);
		gl_container.horizontalSpacing = 10;
		container.setLayout(gl_container);
		
		Label lblWorksheetToRead = new Label(container, SWT.NONE);
		lblWorksheetToRead.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWorksheetToRead.setText("Worksheet to read data from:");
		
		worksheetName = new ComboViewer(container, SWT.NONE);
		Combo worksheetCombo = worksheetName.getCombo();
		worksheetCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
		
		worksheetName.setContentProvider(new ArrayContentProvider());
		
		worksheetName.setComparer(new IElementComparer() {
			
			@Override
			public int hashCode(Object o) {
				String s = (String)o;
				return s.toUpperCase().hashCode();
			}
			
			@Override
			public boolean equals(Object a1, Object a2) {
				String s1 = (String)a1;
				String s2 = (String)a2;
				return s1.equalsIgnoreCase(s2);
			}
		});
		
		worksheetName.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				revalidate();
			}
		});
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Start Row:");
		
		startRow = new IntegerTextField(container, SWT.BORDER);
		startRow.getControl().addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent event) {
				revalidate();
			}
		});
		startRow.getControl().setLayoutData(new GridData(UIUtils.getWidth(startRow.getControl(), 5), SWT.DEFAULT));
		new Label(container, SWT.NONE);
		
		Label lblEndRow = new Label(container, SWT.NONE);
		lblEndRow.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEndRow.setText("End Row:");
		
		endRowEOFOption = new Button(container, SWT.RADIO);
		endRowEOFOption.setText("EOF");
		endRowEOFOption.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				endRowEmptyField.getControl().setEnabled(false);
				endRowFixedField.getControl().setEnabled(false);
				revalidate();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}
		});
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		endRowEmptyRowsOption = new Button(container, SWT.RADIO);
		endRowEmptyRowsOption.setText("Empty rows");
		endRowEmptyRowsOption.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				endRowEmptyField.getControl().setEnabled(true);
				endRowFixedField.getControl().setEnabled(false);
				revalidate();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
		});
		
		endRowEmptyField = new IntegerTextField(container, SWT.BORDER);
		//endRowEmptyField.getControl().setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		endRowEmptyField.getControl().setLayoutData(new GridData(UIUtils.getWidth(endRowEmptyField.getControl(), 5), SWT.DEFAULT));
		endRowEmptyField.getControl().addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				revalidate();
			}
		});
		new Label(container, SWT.NONE);
		
		endRowFixedOption = new Button(container, SWT.RADIO);
		endRowFixedOption.setText("Fixed");
		endRowFixedOption.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				endRowEmptyField.getControl().setEnabled(false);
				endRowFixedField.getControl().setEnabled(true);
				revalidate();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
		});
		
		endRowFixedField = new IntegerTextField(container, SWT.BORDER);
		//endRowFixedField.getControl().setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		endRowFixedField.getControl().setLayoutData(new GridData(UIUtils.getWidth(endRowFixedField.getControl(), 5), SWT.DEFAULT));
		endRowFixedField.getControl().addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				revalidate();
			}
		});
		
		Control[] tabList = new Control[]{worksheetName.getCombo(), startRow.getControl(), endRowEOFOption, endRowEmptyRowsOption, endRowEmptyField.getControl(), endRowFixedOption, endRowFixedField.getControl()};
		container.setTabList(tabList);
		setInput();
		revalidate();
	}

	@Override
	public void createPageCustomControl(Composite composite) {
		this.createControl(composite);
		
	}
	
	private String[] grabSheetNames() {
		Driver customDriver = new Driver();
		SpreadsheetConnection customConn;
		try {
			customConn = (SpreadsheetConnection)customDriver.getConnection( null );
			java.util.Properties connProps = 
					DesignSessionUtil.getEffectiveDataSourceProperties( 
							getInitializationDesign().getDataSourceDesign() );
			customConn.open( connProps );
			Spreadsheet spreadsheet = customConn.getUnderlyingSpreadsheet();
			List<Worksheet>worksheets = spreadsheet.getWorksheets();
			String[] names = new String[worksheets.size()];
			for(int i=0; i<worksheets.size(); i++) {
				names[i] = worksheets.get(i).getName();
			}
			Arrays.sort(names);
			return names;
			
		} catch (OdaException e) {
			throw new RuntimeException(e);
		}
    	
	}
	
	public void revalidate() {
		setPageComplete(false);
		String worksheet = null;
		IStructuredSelection worksheetSelection = (IStructuredSelection)worksheetName.getSelection();
		if(!worksheetSelection.isEmpty()) {
			worksheet = worksheetSelection.getFirstElement().toString();
		}
		if(worksheet==null || "".equals(worksheet)) {
			setErrorMessage("Please provide a worksheet to read data from");
			return;
		}
		options.setWorksheetName(worksheet);
		Integer value = startRow.getValue();
		if(value==null || value<1) {
			setErrorMessage("Please provide number of start row");
			return;
		}
		else {
			options.setStartRow(value);
		}
		if(endRowEmptyRowsOption.getSelection()) {
			options.setEndRowOption(EndRowOption.EMPTY_ROWS);
			value= endRowEmptyField.getValue();
			if(value==null || value<1) {
				setErrorMessage("Please set appropriate number of empty rows");
				return;
			}
			options.setEndRow(value);
		}
		else if(endRowFixedOption.getSelection()) {
			options.setEndRowOption(EndRowOption.FIXED);
			value = endRowFixedField.getValue();
			if(value==null||value<1) {
				setErrorMessage("Please set appropriate number of rows to read");
				return;
			}
			options.setEndRow(value);
		}
		else if(endRowEOFOption.getSelection()) {
			options.setEndRowOption(EndRowOption.EOF);
		}
		else {
			setErrorMessage("Please choose a valid end row option");
			return;
		}
		setErrorMessage(null);
		setMessage("Step complete");
		canLeave = true;
		setPageComplete(true);
		
	}
	
	@Override
	protected boolean canLeave() {
		if(getControl()==null)
			return true;
		return canLeave;
	}
	
	@Override
	protected DataSetDesign collectDataSetDesign(DataSetDesign design) {		
		if(getControl()!=null)
			DataSetEditorSaveHelper.updateDataSetProperties(design, options);
			//DataSetEditorSaveHelper.savePage(design, options);
		return design;
	}

	@Override
	public void shareInstance(SpreadsheetQueryData queryData) {
		this.options = queryData;
	}
}
