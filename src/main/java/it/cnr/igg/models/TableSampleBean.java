package it.cnr.igg.models;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import it.cnr.igg.isotopedb.beans.SampleBean;
import it.cnr.igg.isotopedb.beans.SampleFieldBean;
import it.cnr.igg.isotopedb.beans.ComponentBean;

class TableItem {
	public String headerName;
	public int headerColumn;
	public String value;
}

public class TableSampleBean {
	public static String[][] equivalent = {
			{ "sample", "sample id", "sample_id", "sample name", "sample_name" }
	};
	private ArrayList<String> header;	
	private ArrayList<ArrayList<String>> body;
	private HashMap<String, TableItem> index;

	public TableSampleBean() {
		header = new ArrayList<String>();
		body = new ArrayList<ArrayList<String>>();
		index = new HashMap<String, TableItem>();
	}

	public ArrayList<String> getHeader() {
		return header;
	}

	public ArrayList<ArrayList<String>> getBody() {
		return body;
	}

	public HashMap<String, TableItem> getIndex() {
		return index;
	}

	public void build(List<SampleBean> beans, boolean typeFlag) {
		buildHeader(beans, typeFlag);
		body.add(header);
		buildBody(beans);
	}

	private void buildBody(List<SampleBean> beans) {
		int rowElements = header.size();
		for (SampleBean s : beans) {
			ArrayList<String> row = new ArrayList<String>();
			for (int i = 0; i < rowElements; i++) {
				row.add("");
			}
			List<SampleFieldBean> fields = s.getFields();
			if (fields != null) {
				for (SampleFieldBean f : fields) {
					TableItem item = index.get(f.getFieldName());
					row.set(item.headerColumn, f.getFieldValue());
				}
			}
			List<ComponentBean> components = s.getComponents();
			if (components != null) {
				for (ComponentBean c : components) {
					TableItem item = index.get(c.getComponent());
					row.set(item.headerColumn, "" + c.getValue());
				}
			}
			body.add(row);
		}
	}

	private void buildHeader(List<SampleBean> beans, boolean typeFlag) {
		int position = 0;
		for (SampleBean s : beans) {
			List<SampleFieldBean> fields = s.getFields();
			if (fields != null) {
				for (SampleFieldBean f : fields) {
					if (index.get(f.getFieldName()) == null) {
						TableItem item = new TableItem();
						item.headerColumn = position;
						item.headerName = f.getFieldName();
						index.put(f.getFieldName(), item);
						position++;
						header.add(typeFlag == false ? f.getFieldName() : "F\\" + f.getFieldName());
					}
				}
			}
		}
		for (SampleBean s : beans) {
			List<ComponentBean> components = s.getComponents();
			if (components != null) {
				for (ComponentBean f : components) {
					if (index.get(f.getComponent()) == null) {
						if (!f.getIsIsotope()) {
							TableItem item = new TableItem();
							item.headerColumn = position;
							item.headerName = f.getComponent();
							index.put(f.getComponent(), item);
							position++;
							header.add(typeFlag == false ? f.getComponent() : "C\\" + f.getComponent());
						}
					}
				}
			}
		}
		for (SampleBean s : beans) {
			List<ComponentBean> components = s.getComponents();
			if (components != null) {
				for (ComponentBean f : components) {
					if (index.get(f.getComponent()) == null) {
						if (f.getIsIsotope()) {
							TableItem item = new TableItem();
							item.headerColumn = position;
							item.headerName = f.getComponent();
							index.put(f.getComponent(), item);
							position++;
							header.add(typeFlag == false ? f.getComponent() : "I\\" + f.getComponent());
						}
					}
				}
			}
		}
	}
}
