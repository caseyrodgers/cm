package hotmath.gwt.cm.client.data;

import java.util.ArrayList;
import java.util.List;

/** 
 *   A single INMH resource + all items POJO
 *   
 * @author Casey
 *
 */
public class PrescriptionSessionDataResource {
	String type;
	String label;
	boolean viewed;
	
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
}
