package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.model.program_listing.CmTreeNode;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ProgListModel extends BaseModelData {

    ProgListModelData data = new ProgListModelData();

	protected ProgListModel() {
    }

    public ProgListModel(CmTreeNode data) {
        set("label", data.getLabel());
        this.data.data = data;
    }

    public CmTreeNode getData() {
        return data.data;
    }

    public void setLabel(String label) {
        set("label", label);
    }

    public void setPath(String path) {
        set("path", path);
    }

    public String getPath() {
        return get("path");
    }

    public String getLabel() {
        return get("label");
    }

    public int getLevel() {
        return data.data.getLevel();
    }

    public void setParent(ProgListModel model) {
    	
    }
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ProgListModel) {
            ProgListModel mobj = (ProgListModel) obj;
            boolean b = mobj.getData() == data.data;
            return b;
        }
        return super.equals(obj);
    }
}
