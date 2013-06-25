package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/**
 * 
 * @author bob
 *
 */
public class CCSSGroupCoverageData implements Response {

	private static final long serialVersionUID = 6005772995848261893L;

	int count;

    String ccssName;

    CmList<String> columnLabels = new CmArrayList<String>();

    public CCSSGroupCoverageData(){}

    public CCSSGroupCoverageData(String ccssName, int count) {
        this.ccssName = ccssName;
        this.count = count;
    }

    public CmList<String> getColumnLabels() {
        return columnLabels;
    }

    public void setColumnLabels(CmList<String> columnLabels) {
        this.columnLabels = columnLabels;
    }

    public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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

}
