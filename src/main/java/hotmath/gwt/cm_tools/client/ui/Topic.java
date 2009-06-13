package hotmath.gwt.cm_tools.client.ui;

import com.extjs.gxt.ui.client.data.BaseModelData;

class Topic extends BaseModelData {
	public Topic(String name) {
		set("topic", name);
		set("text",name);
	}

	public String getTopic() {
		return this.<String> get("topic");
	}
}