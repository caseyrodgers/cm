package hotmath.gwt.cm_admin.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GroupModel extends BaseModelData {
	
	private static final long serialVersionUID = 2783538975661520681L;
	
	public static final String NAME_KEY = "group-name";
	public static final String ID_KEY = "id";
	public static final String DESC_KEY = "description";
	public static final String IS_ACTIVE_KEY = "is-active";
	public static final String NEW_GROUP = "--- Create Group ---";
	
	private String id;
	private String name;
	private String description;
	private String isActive;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
		set(ID_KEY, id);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		set(NAME_KEY, name);
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
		set(DESC_KEY, description);
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
		set(IS_ACTIVE_KEY, isActive);
	}
}
