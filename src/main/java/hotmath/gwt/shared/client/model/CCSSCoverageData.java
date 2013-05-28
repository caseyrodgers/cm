package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class CCSSCoverageData implements Response {
    
    int uid;
    int groupId;

    String ccssName;

    CmList<String> columnLabels = new CmArrayList<String>();

    public CCSSCoverageData(){}

    public CCSSCoverageData(String name) {
        this.ccssName = name;
    }

    public CmList<String> getColumnLabels() {
        return columnLabels;
    }

    public void setColumnLabels(CmList<String> columnLabels) {
        this.columnLabels = columnLabels;
    }

    public String getId() {
        return ccssName;
    }

    public String getName() {
        return ccssName;
    }

    public void setName(String name) {
        this.ccssName = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

}
