package hotmath.gwt.cm_tools.client.model;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

import java.io.Serializable;

public class CmTreeModel extends BaseTreeModel implements Serializable {

	private static final long serialVersionUID = -8370354125432687765L;
	
	private Boolean loadChildrenAsynchronously = false;
	
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

	public Boolean loadChildrenAsynchronously() {
		return loadChildrenAsynchronously;
	}
	
	public void setLoadChildrenAsynchronously(Boolean loadChildrenAsynchronously) {
		this.loadChildrenAsynchronously = loadChildrenAsynchronously;
	}

}
