package de.cbrell.birt.report.data.oda.spreadsheet.model;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SpreadsheetQueryData {

	private String worksheetName;

	private Integer startRow;
	private EndRowOption endRowOption;
	private Integer endRow;

	private List<SpreadsheetColumn> columns = new ArrayList<SpreadsheetColumn>();
	public static final Comparator<SpreadsheetColumn> DEFAULT_SORTER = new Comparator<SpreadsheetColumn>() {

		@Override
		public int compare(SpreadsheetColumn o1, SpreadsheetColumn o2) {
			return new Integer(o1.getBirtColIndex()).compareTo(o2
					.getBirtColIndex());
		}
	};
	
	public static final Comparator<SpreadsheetColumn> QUERY_COLUMN_SORTER = new Comparator<SpreadsheetColumn>() {
		
		@Override
		public int compare(SpreadsheetColumn o1, SpreadsheetColumn o2) {
			return new Integer(o1.getColNum()).compareTo(o2.getColNum());
		}
	};

	public String getWorksheetName() {
		return worksheetName;
	}

	public void setWorksheetName(String worksheetName) {
		if (this.worksheetName != null
				&& !this.worksheetName.equals(worksheetName)) {
			this.columns = new ArrayList<SpreadsheetColumn>();
		}
		this.worksheetName = worksheetName;
	}

	public Integer getStartRow() {
		return startRow;
	}

	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}

	public EndRowOption getEndRowOption() {
		return endRowOption;
	}

	public void setEndRowOption(EndRowOption endRowOption) {
		this.endRowOption = endRowOption;
	}

	public Integer getEndRow() {
		return endRow;
	}

	public void setEndRow(Integer endRow) {
		this.endRow = endRow;
	}

	public void addColumn(SpreadsheetColumn column) {
		columns.add(column);
		Collections.sort(columns, DEFAULT_SORTER);
	}

	public void removeAllColumns() {
		columns = new ArrayList<SpreadsheetColumn>();
	}

	public void addAll(Collection<SpreadsheetColumn> columns) {
		if (columns != null) {
			this.columns.addAll(columns);
			Collections.sort(this.columns, DEFAULT_SORTER);
		}
	}

	public String serializeQuery() {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element cols = doc.createElement("cols");
			doc.appendChild(cols);
			for (SpreadsheetColumn column : columns) {
				Element col = doc.createElement("col");
				col.setAttribute("type", column.getDataType().name());
				col.setAttribute("id",
						Integer.toString(column.getBirtColIndex()));
				col.setAttribute("spreadsheetCol",
						Integer.toString(column.getColNum()));
				col.setAttribute("repeat",
						Boolean.toString(column.shouldRepeat()));
				col.setAttribute("strictMode",
						Boolean.toString(column.isStrictMode()));
				if (column.getTrimType() != null) {
					col.setAttribute("trimType", column.getTrimType().name());
				}
				if (column.getConversionMask() != null) {
					Element conversionMask = doc
							.createElement("conversionMask");
					conversionMask.setTextContent(column.getConversionMask());
					col.appendChild(conversionMask);
				}
				if (column.getGroupSymbol() != null
						&& column.getDecimalSymbol() != null) {
					Element groupSymbol = doc.createElement("groupSymbol");
					groupSymbol.setTextContent(column.getGroupSymbol());
					col.appendChild(groupSymbol);
					Element decimalSymbol = doc.createElement("decimalSymbol");
					decimalSymbol.setTextContent(column.getDecimalSymbol());
					col.appendChild(decimalSymbol);
				}

				Element name = doc.createElement("name");
				name.setTextContent(column.getColumnName());
				col.appendChild(name);
				cols.appendChild(col);
			}
			TransformerFactory transfac = TransformerFactory.newInstance();

			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			return sw.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void deserializeQuery(String xmlString) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setCoalescing(true);
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(
					xmlString.getBytes("UTF-8")));
			NodeList columnTagList = document.getElementsByTagName("col");

			for (int i = 0; i < columnTagList.getLength(); i++) {
				SpreadsheetColumn column = new SpreadsheetColumn();
				Node singleColumnTag = columnTagList.item(i);
				NamedNodeMap attributes = singleColumnTag.getAttributes();

				NodeList childNodes = singleColumnTag.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node child = childNodes.item(j);

					if ("name".equalsIgnoreCase(child.getNodeName())) {
						column.setColumnName(child.getTextContent());
					} else if ("conversionMask".equalsIgnoreCase(child
							.getNodeName())) {
						column.setConversionMask(child.getTextContent());
					} else if ("groupSymbol".equalsIgnoreCase(child
							.getNodeName())) {
						column.setGroupSymbol(child.getTextContent());
					} else if ("decimalSymbol".equalsIgnoreCase(child
							.getNodeName())) {
						column.setDecimalSymbol(child.getTextContent());
					}
				}

				// columnDataType
				Node node = attributes.getNamedItem("type");
				String typeName = node.getNodeValue().trim();
				column.setDataType(DataType.valueOf(typeName.toUpperCase()
						.trim()));

				// trim only for String dataType
				if (column.getDataType() == DataType.STRING) {
					Node trimType = attributes.getNamedItem("trimType");
					if (trimType != null && trimType.getNodeValue() != null) {
						column.setTrimType(TrimType.valueOf(trimType
								.getNodeValue().toUpperCase().trim()));
					}
				}
				node = attributes.getNamedItem("repeat");
				if (node != null && node.getNodeValue() != null) {
					column.setRepeat(Boolean
							.valueOf(node.getNodeValue().trim()));
				}

				node = attributes.getNamedItem("strictMode");
				if (node != null && node.getNodeValue() != null) {
					column.setStrictMode(Boolean.valueOf(node.getNodeValue()
							.trim()));
				}

				// colNum
				node = attributes.getNamedItem("spreadsheetCol");
				if (node != null && node.getNodeValue() != null) {
					column.setColNum(new Integer(node.getNodeValue().trim()));
				}
				node = attributes.getNamedItem("id");
				if (node != null && node.getNodeValue() != null) {
					column.setBirtColIndex(new Integer(node.getNodeValue()
							.trim()));
				}
				columns.add(column);
			}
			Collections.sort(columns, DEFAULT_SORTER);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<SpreadsheetColumn> getColumns() {
		return this.columns;
	}

	public boolean isComplete() {
		boolean valid = this.worksheetName != null && this.startRow > 0;
		valid = valid && (this.columns != null && this.columns.size() > 0);
		if (this.endRowOption != EndRowOption.EOF) {
			valid = valid && this.endRow != null && this.endRow > 0;
		}
		return valid;
	}
}
