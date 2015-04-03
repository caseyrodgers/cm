package hotmath.gwt.cm_core.client.model;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionResource {
    
    private String type;
    private List<ResourceItem> items = new ArrayList<ResourceItem>();

    public PrescriptionResource() {}
    
    public PrescriptionResource(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ResourceItem> getItems() {
        return items;
    }

    public void setItems(List<ResourceItem> items) {
        this.items = items;
    }
}
