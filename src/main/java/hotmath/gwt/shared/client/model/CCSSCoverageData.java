package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class CCSSCoverageData implements Response {

	private static final long serialVersionUID = -8162142054866769276L;

	int    count;
	int    userId;
	int    sequenceNum;
	
    String lessonName;
    String ccssName;

    CmList<String> columnLabels = new CmArrayList<String>();

    public CCSSCoverageData(){}

    public CCSSCoverageData(String lessonName, String ccssName) {
    	this.lessonName = lessonName;
        this.ccssName = ccssName;
    }

    public CCSSCoverageData(String ccssName, int count) {
        this.ccssName = ccssName;
        this.count = count;
    }

    public CmList<String> getColumnLabels() {
        return columnLabels;
    }

    public String getLabel() {
    	return columnLabels.get(0);
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

    public String getCountStr() {
    	return String.valueOf(count);
    }

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(int sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

}
