package de.cbrell.birt.report.data.oda.spreadsheet.ui.dataset;

import java.util.Collections;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import de.cbrell.birt.report.data.oda.spreadsheet.Driver;
import de.cbrell.birt.report.data.oda.spreadsheet.SpreadsheetConnection;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Cell;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Cell.CellType;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Spreadsheet;
import de.cbrell.birt.report.data.oda.spreadsheet.input.Worksheet;
import de.cbrell.birt.report.data.oda.spreadsheet.model.DataType;
import de.cbrell.birt.report.data.oda.spreadsheet.model.SpreadsheetColumn;
import de.cbrell.birt.report.data.oda.spreadsheet.model.SpreadsheetQueryData;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.DataSetEditorSaveHelper;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.ShareSpreadsheetQueryData;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.SpreadsheetDataSetColumnsDataProvider;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.SpreadsheetDataSetColumnsLabelProvider;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.SpreadsheetDataSetColumnsViewerSorter;

public class SpreadsheetDataSetEditorPageTwo extends DataSetWizardPage implements ShareSpreadsheetQueryData {
	private Shell shell;
	private TableViewer viewer;
	private Table table;
	private Button removeButton;
	
	private List<SpreadsheetColumn> columns;
	private SpreadsheetQueryData queryData;

	/**
	 * Create the wizard.
	 */
	public SpreadsheetDataSetEditorPageTwo(String title) {
		super(title);
		setTitle(title);
		setDescription(title);
		setPageComplete(false);
	}
	
	@Override
	protected DataSetDesign collectDataSetDesign(DataSetDesign design) {
		if(getControl()!=null)
			DataSetEditorSaveHelper.savePage(design, queryData);
		return design;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		shell = parent.getShell();
		Composite container = new Composite(parent, SWT.NULL);
		
		setControl(container);
		container.setLayout(new GridLayout(2, false));
		setErrorMessage("Provide at least one column");
		viewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION); 
		viewer.setContentProvider(new SpreadsheetDataSetColumnsDataProvider());
		viewer.setLabelProvider(new SpreadsheetDataSetColumnsLabelProvider());
		viewer.setSorter(new SpreadsheetDataSetColumnsViewerSorter());
		
		table = viewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		table.addKeyListener(new TableKeyListener(this));
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(44);
		tableColumn.setText("#");
		
		TableColumn tableColumnSC = new TableColumn(table, SWT.NONE);
		tableColumnSC.setWidth(100);
		tableColumnSC.setText("spreadsheetCol");
		
		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(92);
		tblclmnName.setText("name");
		
		TableColumn tblclmnDataType = new TableColumn(table, SWT.NONE);
		tblclmnDataType.setWidth(100);
		tblclmnDataType.setText("data type");
		
//		TableColumn tblclmnDefault = new TableColumn(table, SWT.NONE);
//		tblclmnDefault.setWidth(100);
//		tblclmnDefault.setText("default");
//		
//		TableColumn tblclmnConversionMask = new TableColumn(table, SWT.NONE);
//		tblclmnConversionMask.setWidth(100);
//		tblclmnConversionMask.setText("conversion Mask");
		
		TableColumn tblClmnStrict = new TableColumn(table, SWT.NONE);
		tblClmnStrict.setWidth(100);
		tblClmnStrict.setText("strict");
		
		TableColumn tblClmnRepeat = new TableColumn(table, SWT.NONE);
		tblClmnRepeat.setWidth(100);
		tblClmnRepeat.setText("repeat");
		
		TableColumn tblclmnGroup = new TableColumn(table, SWT.NONE);
		tblclmnGroup.setWidth(100);
		tblclmnGroup.setText("group");
		
		TableColumn tblclmnDecimal = new TableColumn(table, SWT.NONE);
		tblclmnDecimal.setWidth(100);
		tblclmnDecimal.setText("decimal");
		new Label(container, SWT.NONE);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if(event.getSelection().isEmpty()) {
					removeButton.setEnabled(false);
				}
				else {
					removeButton.setEnabled(true);
				}
			}
		});
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				SpreadsheetColumn column = (SpreadsheetColumn)selection.getFirstElement();
				SpreadsheetColumnDialog dialog = new SpreadsheetColumnDialog(shell, column);
				int open = dialog.open();
				if(open==Dialog.OK) {
					column = dialog.copyOutputTo(column);
					refreshTable();
				}
			}
		});
		
		
		
		Button btnAdd = new Button(container, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int newIndex = 0;
				if(!columns.isEmpty()) {
					Collections.sort(columns, SpreadsheetQueryData.DEFAULT_SORTER);
					newIndex = columns.get(columns.size()-1).getBirtColIndex();
				}
				SpreadsheetColumnDialog dialog = new SpreadsheetColumnDialog(shell, newIndex+1);
				int open = dialog.open();
				
				if(open==Dialog.OK) {
					columns.add(dialog.getEditedInstance());
					refreshTable();
				}
			}
		});
		btnAdd.setText("add...");
		
		removeButton = new Button(container, SWT.NONE);
		removeButton.setText("remove");
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				widgetDefaultSelected(event);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
				SpreadsheetColumn colToRemove = (SpreadsheetColumn)selection.getFirstElement();
				delete(colToRemove, true);
			}
		});
		Button btnGrab = new Button(container, SWT.NONE);
		btnGrab.setText("grab..");
		btnGrab.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				widgetDefaultSelected(arg0);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				boolean fetchRowHeadersFromPreviousRow = false;
				if(queryData.getStartRow()>1) {
					fetchRowHeadersFromPreviousRow = MessageDialog.openQuestion(
			            getShell(),
			            "Should Row Header be fetched from previous row",
			            "Should Row Header be fetched from previous row?");
				}
				grab(fetchRowHeadersFromPreviousRow);
			}
		});
		
		new Label(container, SWT.NONE);
		table.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				if(table.getSelectionCount()>0) {
					removeButton.setEnabled(true);
				}
				else {
					removeButton.setEnabled(false);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}
		});
		refreshTable();
	}
	
	@Override
	public void createPageCustomControl(Composite composite) {
		this.createControl(composite);
	}
	
	private boolean swapWithPrevious(SpreadsheetColumn columnToSwap) {
		int index = getListIndexFor(columnToSwap);
		if(index>0) {
			SpreadsheetColumn previous = columns.get(index-1);
			int newIndexForNext = columnToSwap.getBirtColIndex();
			columnToSwap.setBirtColIndex(previous.getBirtColIndex());
			previous.setBirtColIndex(newIndexForNext);
			Collections.swap(columns, index, index-1);
			refreshTable();
			return true;
		}
		return false;
	}
	
	private boolean swapWithNext(SpreadsheetColumn columnToSwap) {
		int index = getListIndexFor(columnToSwap);
		if(index<columns.size()-1 && index>-1) {
			SpreadsheetColumn next = columns.get(index+1);
			int newIndexForNext = columnToSwap.getBirtColIndex();
			columnToSwap.setBirtColIndex(next.getBirtColIndex());
			next.setBirtColIndex(newIndexForNext);
			Collections.swap(columns, index, index+1);
			refreshTable();
			return true;
		}
		return false;
	}
	
	private int getListIndexFor(SpreadsheetColumn columnTo) {
		int index = -1;
		for(SpreadsheetColumn column: columns) {
			index++;
			if(columnTo==column) {
				return index;
			}
		}
		return -1;
	}
	
	public void refreshTable() {
		viewer.setInput(columns);
		viewer.refresh(true, true);
		revalidate();
	}
	
	private void revalidate() {
		setPageComplete(false);
		if(columns!=null && !columns.isEmpty()) {
			setErrorMessage(null);
			setMessage("Step complete");
			if(queryData.isComplete()) {
				setPageComplete(true);
			}
		}
	}
	
	public void grab(boolean grabHeaderFromPreviousRow) {
		Driver customDriver = new Driver();
        
        // obtain and open a live connection
        try {
        	SpreadsheetConnection customConn = (SpreadsheetConnection)customDriver.getConnection( null );
        	
        	java.util.Properties connProps = 
        			DesignSessionUtil.getEffectiveDataSourceProperties( 
        					getInitializationDesign().getDataSourceDesign() );
			customConn.open( connProps );
			Spreadsheet spreadsheet = customConn.getUnderlyingSpreadsheet();
			Worksheet worksheet = spreadsheet.getWorksheetNamed(queryData.getWorksheetName());
			int colNums = worksheet.getTotalNumCols();
			Integer startRow = queryData.getStartRow();
			Cell[] cellHeader = null;
			if(grabHeaderFromPreviousRow) {
				cellHeader = worksheet.getCells(startRow-1, 1, colNums);
			}
			Cell[] cellData = worksheet.getCells(startRow, 1, colNums);
			columns.clear();
			int birtColIndex = 0;
			for(int i=0; i<cellData.length; i++) {
				boolean headerWasNull = true;
				String colName = "Col"+(i+1);
				if(grabHeaderFromPreviousRow) {
					Object value = cellHeader[i].getValue();
					String strVal = cellHeader[i].getRawDataValue();
					if(value!=null && strVal!=null && strVal.trim().length()>0) {
						colName = strVal;
						headerWasNull = false;
					}
				}
				DataType dataType = null;
				CellType cellType = cellData[i].getCellType();
				switch(cellType) {
				case BOOLEAN: dataType = DataType.BOOLEAN; break;
				case DATE: dataType = DataType.DATE; break;
				case NUMERIC: dataType = DataType.DECIMAL; break;
				case STRING: dataType = DataType.STRING; break;
				}
				
				if(dataType!=null || !headerWasNull) {
					if(dataType==null) {
						dataType = DataType.STRING;
					}
					SpreadsheetColumn column = new SpreadsheetColumn();
					column.setColNum(i+1);
					column.setBirtColIndex(birtColIndex);
					column.setColumnName(colName);
					column.setDataType(dataType);
					columns.add(column);
					birtColIndex++;
				}
			}
		} catch (OdaException e) {
			MessageDialog.openError(getShell(), "Error occured while fetching rows", e.getMessage());
		}
        refreshTable();
        revalidate();
	}
	
	private void delete(SpreadsheetColumn column, boolean confirm) {
		boolean yes = true;
		if(confirm) 
			yes = MessageDialog.openQuestion(shell, "Confirm delete", "Are you sure that you want to delete column "+column.getColumnName()+"?");
		if(yes) {
			while(swapWithNext(column));
			columns.remove(column);
			refreshTable();
			boolean removed = columns.remove(column);
			if(removed)
				refreshTable();
		}
	}
	
	@Override
	protected boolean canLeave() {
		if(getControl()==null) {
			return true;
		}
		return true;
	}
	
	
	private static class TableKeyListener implements KeyListener {

		private SpreadsheetDataSetEditorPageTwo parent;
		
		public TableKeyListener(SpreadsheetDataSetEditorPageTwo parent) {
			this.parent = parent;
			
		}
		
		@Override
		public void keyPressed(KeyEvent event) {
			IStructuredSelection select = (IStructuredSelection)parent.viewer.getSelection();
			if(select==null)
				return;
			SpreadsheetColumn column = (SpreadsheetColumn)select.getFirstElement();
			if((event.stateMask&SWT.CTRL)!=0) {
				if(event.keyCode == SWT.ARROW_UP) {
					parent.swapWithPrevious(column);
				}
				else if(event.keyCode==SWT.ARROW_DOWN) {
					parent.swapWithNext(column);
				}
			}
			else if(event.keyCode==SWT.DEL) {
				parent.delete(column, true);
			}
			
		}

		@Override
		public void keyReleased(KeyEvent event) {
		}
		
	}


	@Override
	public void shareInstance(SpreadsheetQueryData queryData) {
		this.queryData = queryData;
		this.columns = queryData.getColumns();
	}
}
