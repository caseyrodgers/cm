package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.model.program_listing.CmTreeNode;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class ProgListModel implements Response {

	private static final long serialVersionUID = -2063044970288967076L;

	ProgListModelData plmData = new ProgListModelData();
    String label;
    String path;
    int    id;

	protected ProgListModel() {
    }

    public ProgListModel(CmTreeNode data) {
        label = data.getLabel();
        plmData.data = data;
        id = data.getId();
    }

    public CmTreeNode getData() {
        return plmData.data;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getLabel() {
        return label;
    }

    public int getLevel() {
        return plmData.data.getLevel();
    }

    public void setParent(ProgListModel model) {
    	
    }

    public void setId(int id) {
    	this.id = id;
    }

    public int getId() {
    	return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ProgListModel) {
            ProgListModel plMdl = (ProgListModel) obj;
            return (plMdl.getData() == plmData.data);
        }
        return false;
    }
}
