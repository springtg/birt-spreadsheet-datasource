package de.cbrell.birt.report.data.oda.spreadsheet.ui.datasource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.cbrell.birt.report.data.oda.spreadsheet.input.SpreadsheetFactory;
import de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.DataSourcePageValidationListener;
import de.cbrell.birt.report.data.oda.spreadsheet.utils.SpreadsheetConstants;

public class SpreadsheetDataSourceHelper { //extends Composite {

	private Text spreadsheetFolder;
	private ComboViewer spreadsheetFilename;
	private ComboViewer spreadsheetType;
	private DataSourcePageValidationListener page;

	public SpreadsheetDataSourceHelper(DataSourcePageValidationListener dialog) {
		page = dialog;
	}
	
	public Properties collectCustomProperties(Properties properties) {
		properties.setProperty(SpreadsheetConstants.SPREADSHEET_DIRNAME_PROPERTY, spreadsheetFolder.getText());
		properties.setProperty(SpreadsheetConstants.SPREADSHEET_FILENAME_PROPERTY, spreadsheetFilename.getCombo().getText());
		properties.setProperty(SpreadsheetConstants.SPREADSHEET_TYPE_PROPERTY, spreadsheetType.getCombo().getText());
		return properties;
	}
	
	public Composite initialize(Composite parent) {
		ScrolledComposite scrolledContent = new ScrolledComposite(parent, SWT.V_SCROLL|SWT.H_SCROLL);
		
		Composite content = new Composite( scrolledContent, SWT.NONE );
		content.setLayout(new GridLayout(3, false));
		
		Label lblSpreadsheetFolder = new Label(content, SWT.NONE);
		lblSpreadsheetFolder.setText("Spreadsheet Folder:");
		
		spreadsheetFolder = new Text(content, SWT.BORDER);
		spreadsheetFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		spreadsheetFolder.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				invalidateFileChooser();
				String text = spreadsheetFolder.getText();
				File f = new File(text);
				if(f.exists()) {
					refreshFileChooser(f);
				}
				
			}
		});
		
		Button button = new Button(content, SWT.NONE);
		button.setText("...");
		button.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				DirectoryDialog dialog = new DirectoryDialog(spreadsheetFilename.getCombo().getShell(), SWT.OPEN);
			    dialog.setFilterPath("c:\\"); 
				String path = dialog.open();
				spreadsheetFolder.setText(path);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				DirectoryDialog dialog = new DirectoryDialog(spreadsheetFilename.getCombo().getShell());
			    //dialog.setFilterPath("c:\\"); 
				String path = dialog.open();
				spreadsheetFolder.setText(path);
			}
		});
		
		Label lblSpreadsheetFile = new Label(content, SWT.NONE);
		lblSpreadsheetFile.setText("Spreadsheet File:");
		
		spreadsheetFilename = new ComboViewer(content, SWT.NONE);
		spreadsheetFilename.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		spreadsheetFilename.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateFormatChooser(spreadsheetFilename.getCombo().getText());
			}
		});
		
		Label lblSpreadsheetType = new Label(content, SWT.NONE);
		lblSpreadsheetType.setText("Spreadsheet Type:");	
		
		spreadsheetType = new ComboViewer(content, SWT.NONE);
		
		spreadsheetType.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		spreadsheetType.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if(spreadsheetType.getSelection().isEmpty()) {
					page.setValid(false);
				}
				else {
					page.setValid(true);
				}
			}
		});
		spreadsheetType.setContentProvider(new de.cbrell.birt.report.data.oda.spreadsheet.ui.utils.ArrayContentProvider());//ArrayContentProvider.getInstance());
		spreadsheetType.add(SpreadsheetFactory.SupportedType.values());
		
		Point size = content.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		content.setSize( size.x, size.y );
		if(size.x<500) {
			size.x = 500;
		}
		content.setSize(size.x, size.y);
		scrolledContent.setMinWidth( size.x + 10 );
		scrolledContent.setMinHeight(size.y + 10);
		scrolledContent.setContent( content );

		return scrolledContent;
	}
	
	public void setInput(Properties properties) {
		String dirName = (String)properties.get(SpreadsheetConstants.SPREADSHEET_DIRNAME_PROPERTY);
		String fileName = (String)properties.get(SpreadsheetConstants.SPREADSHEET_FILENAME_PROPERTY);
		String spreadsheetType = (String)properties.get(SpreadsheetConstants.SPREADSHEET_TYPE_PROPERTY);
		
		if(dirName!=null)
			spreadsheetFolder.setText(dirName);
		if(fileName!=null)
			spreadsheetFilename.setSelection(new StructuredSelection(fileName));
		if(spreadsheetType!=null)
			this.spreadsheetType.setSelection(new StructuredSelection(SpreadsheetFactory.SupportedType.fromString(spreadsheetType)));
	}
	
	private void updateFormatChooser(String text) {
		if(text==null || text.trim().length()==0) {
			spreadsheetType.getCombo().setEnabled(false);
			spreadsheetType.setSelection(StructuredSelection.EMPTY);
		}
		else {
			spreadsheetType.getCombo().setEnabled(true);
			int lastDot = text.lastIndexOf(".");
			if(lastDot>0) {
				String fileExt = text.substring(lastDot+1, text.length());
				if("XLS".equalsIgnoreCase(fileExt)) {	
					spreadsheetType.setSelection(new StructuredSelection(SpreadsheetFactory.SupportedType.XLS));//.select(0);
					//spreadsheetType.setText("XLS");
				}
				else if("XLSX".equalsIgnoreCase(fileExt)) {
					spreadsheetType.setSelection(new StructuredSelection(SpreadsheetFactory.SupportedType.XLSX));
					//spreadsheetType.setText("XLSX");
				}
				else if("ODS".equalsIgnoreCase(fileExt)) {
					spreadsheetType.setSelection(new StructuredSelection(SpreadsheetFactory.SupportedType.ODS));
					//spreadsheetType.setText("ODX");
				}
			}
		}
	}

	private void refreshFileChooser(File f) {
		if(f.isDirectory()) {
			File[] files = f.listFiles();
			List<String> fileNameList = new ArrayList<String>();
			for(File file: files) {
				if(!file.isDirectory())
					fileNameList.add(file.getName());
			}
			spreadsheetFilename.getCombo().setEnabled(true);
			spreadsheetFilename.add(fileNameList.toArray(new String[]{}));
		}
	}

	private void invalidateFileChooser() {
		spreadsheetFilename.setSelection(StructuredSelection.EMPTY);
		spreadsheetFilename.getCombo().removeAll();
		spreadsheetFilename.getCombo().setEnabled(false);
	}

}
