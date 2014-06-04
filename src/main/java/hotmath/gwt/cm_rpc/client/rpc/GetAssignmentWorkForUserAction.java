package hotmath.gwt.cm_rpc.client.rpc;

import java.util.Date;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetAssignmentWorkForUserAction implements Action<CmList<StudentAssignment>>{
    
    private static final long serialVersionUID = 2167430662533822188L;

	private int uid;
    private Date fromDate;
    private Date toDate;

    public GetAssignmentWorkForUserAction(){}
    
    public GetAssignmentWorkForUserAction(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Override
    public String toString() {
        return "GetAssignmentWorkForUserAction [uid:" + uid + "]";
    }
}
