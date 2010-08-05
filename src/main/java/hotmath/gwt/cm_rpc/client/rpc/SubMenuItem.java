package hotmath.gwt.cm_rpc.client.rpc;

import java.util.ArrayList;
import java.util.List;

public class SubMenuItem {
	List<InmhItemData> itemData = new ArrayList<InmhItemData>();
	List<SubMenuItem> children = new ArrayList<SubMenuItem>();
	String title;
	
	public SubMenuItem(String title) {
		this.title = title;
	}

	public List<InmhItemData> getItemData() {
		return itemData;
	}

	public void setItemData(List<InmhItemData> itemData) {
		this.itemData = itemData;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<SubMenuItem> getChildren() {
		return children;
	}

	public void setChildren(List<SubMenuItem> children) {
		this.children = children;
	}
	
}
