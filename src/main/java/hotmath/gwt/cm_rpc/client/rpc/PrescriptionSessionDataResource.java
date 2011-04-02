package hotmath.gwt.cm_rpc.client.rpc;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/** 
 *   A single lesson resource
 *   
 * @author Casey
 *
 */
public class PrescriptionSessionDataResource implements IsSerializable {

    String type;
	String label;
	boolean viewed;
	String description;
	List<SubMenuItem> subMenuItems = new ArrayList<SubMenuItem>();
	
	public PrescriptionSessionDataResource() {}
	
	public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	List<InmhItemData> items = new ArrayList<InmhItemData>();


	public List<InmhItemData> getItems() {
		return items;
	}

	public void setItems(List<InmhItemData> items) {
		this.items = items;
	}
	
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public List<SubMenuItem> getSubMenuItems() {
		return subMenuItems;
	}

	public void setSubMenuItems(List<SubMenuItem> subMenuItems) {
		this.subMenuItems = subMenuItems;
	}

}
