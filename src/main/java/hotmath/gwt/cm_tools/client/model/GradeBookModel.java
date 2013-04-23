package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GradeBookModel extends BaseModel {

	private static final long serialVersionUID = -5790068503224882819L;
	
	public GradeBookModel() {
    }
    
    public GradeBookModel(Integer uid, String userName) {
        set("uid", uid);
        set("userName", userName);
    }
    
    public Integer getUid() {
        return get("uid");
    }
    
    public String getUserName() {
        return get("userName");
    }

    public CmList<AssignmentModel> getAssignmentList() {
    	return get("assignmentList");
    }

    public void setAssignmentList(CmList<AssignmentModel> assignmentList) {
    	set("assignmentList", assignmentList);
    }
}

