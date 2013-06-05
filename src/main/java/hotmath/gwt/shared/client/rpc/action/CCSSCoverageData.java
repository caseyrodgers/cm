package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class CCSSCoverageData implements Response {

	private static final long serialVersionUID = -8162142054866769276L;

	int uid;

    String lessonName;
    String ccssName;

    CmList<String> columnLabels = new CmArrayList<String>();

    public CCSSCoverageData(){}

    public CCSSCoverageData(String lessonName, String ccssName) {
    	this.lessonName = lessonName;
        this.ccssName = ccssName;
    }

    public CmList<String> getColumnLabels() {
        return columnLabels;
    }

    public void setColumnLabels(CmList<String> columnLabels) {
        this.columnLabels = columnLabels;
    }

    public String getLessonName() {
		return lessonName;
	}

	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
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
