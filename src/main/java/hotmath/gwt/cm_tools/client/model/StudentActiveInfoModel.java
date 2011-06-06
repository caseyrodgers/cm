package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class StudentActiveInfoModel extends StudentActiveInfo implements Response {
	
	private static final long serialVersionUID = 2810917345257213217L;

	int totalSegments;

	public StudentActiveInfoModel() {	
	}
	
	public StudentActiveInfoModel(StudentActiveInfo activeInfo) {
		this.activeRunId       = activeInfo.activeRunId;
		this.activeRunSession  = activeInfo.activeRunSession;
		this.activeSegment     = activeInfo.activeSegment;
		this.activeSegmentSlot = activeInfo.activeSegmentSlot;
		this.activeTestId      = activeInfo.activeTestId;
	}
	
	public int getTotalSegments() {
		return totalSegments;
	}

	public void setTotalSegments(int totalSegments) {
		this.totalSegments = totalSegments;
	}

    @Override
    public String toString() {
        return "StudentActiveInfoModel [activeRunId=" + activeRunId + ", activeRunSession=" + activeRunSession
                + ", activeSegment=" + activeSegment + ", activeSegmentSlot=" + activeSegmentSlot + ", activeTestId="
                + activeTestId + ", totalSegments= " + totalSegments + "]";
    }

}
