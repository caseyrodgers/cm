package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.model.program_listing.CmTreeNode;

public class ProgListModel {

    ProgListModelData data = new ProgListModelData();
    private String label;
    private String path;

	protected ProgListModel() {
    }

    public ProgListModel(CmTreeNode data) {
        this.label =  data.getLabel();
        this.data.data = data;
    }

    public CmTreeNode getData() {
        return data.data;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public String getLabel() {
        return this.label;
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
