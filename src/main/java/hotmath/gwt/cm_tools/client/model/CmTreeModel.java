package hotmath.gwt.cm_tools.client.model;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

import java.io.Serializable;

public class CmTreeModel extends BaseTreeModel implements Serializable {

	private static final long serialVersionUID = -8370354125432687765L;
	
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
	
}
