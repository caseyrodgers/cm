package hotmath.gwt.cm_tools.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class CmTreeModel extends BaseTreeModel implements Serializable {
	private int id = 0;
	private int number = 0;
	
	public CmTreeModel() {}
	
	public CmTreeModel(String name) {
		setName(name);
	}
	
	public void setName(String name) {
		set("name", name);
	}
	
	public String getName() {
		return get("name");
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
