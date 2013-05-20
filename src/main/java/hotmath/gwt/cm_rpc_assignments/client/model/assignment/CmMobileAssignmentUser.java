package hotmath.gwt.cm_rpc_assignments.client.model.assignment;


import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.List;

public class CmMobileAssignmentUser implements Response {

    private int uid;
    private String userName;
    private List<StudentAssignmentInfo> assignments;
    private AssignmentUserInfo assignmentUserInfo;

    public CmMobileAssignmentUser(){}

    public CmMobileAssignmentUser(int uid, String userName, AssignmentUserInfo assignmentUserInfo, List<StudentAssignmentInfo> assignments) {
        this.uid = uid;
        this.userName = userName;
        this.assignmentUserInfo = assignmentUserInfo;
        this.assignments = assignments;
    }

    public AssignmentUserInfo getAssignmentUserInfo() {
        return assignmentUserInfo;
    }

    public void setAssignmentUserInfo(AssignmentUserInfo assignmentUserInfo) {
        this.assignmentUserInfo = assignmentUserInfo;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<StudentAssignmentInfo> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<StudentAssignmentInfo> assignments) {
        this.assignments = assignments;
    }

    @Override
    public String toString() {
        return "CmMobileAssignmentUser [uid=" + uid + ", userName=" + userName + ", assignments=" + assignments + ", assignmentUserInfo=" + assignmentUserInfo
                + "]";
    }
}
