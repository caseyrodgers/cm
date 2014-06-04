package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetActiveInfoForStudentUidAction implements Action<StudentActiveInfo>{
    
	private static final long serialVersionUID = 5916971365418745422L;

	int studentUid;
    
    public GetActiveInfoForStudentUidAction() {
    }

    public int getUserId() {
        return studentUid;
    }

    public void setUserId(int userId) {
        this.studentUid = userId;
    }

    @Override
    public String toString() {
        return "GetActiveInfoForStudentUidAction [studentUid=" + studentUid + "]";
    }
}
