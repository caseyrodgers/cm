package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_tools.client.model.StudentActiveInfoModel;

public class GetActiveInfoForStudentUidAction implements Action<StudentActiveInfoModel>{
    
	private static final long serialVersionUID = 5916971365418745422L;

	int userId;
    
    public GetActiveInfoForStudentUidAction() {}

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "GetActiveInfoForStudentUidAction [userId=" + userId + "]";
    }
}
