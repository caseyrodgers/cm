package hotmath.gwt.cm_qa.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

public class UpdateQaItemAction implements Action<RpcData> {

	String item, description;
	public UpdateQaItemAction() {}
	
	public UpdateQaItemAction(String item, String description) {
		this.item = item;
		this.description = description;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "UpdateQaItemAction [item=" + item + ", description="
				+ description + "]";
	}
}
