package hotmath.gwt.cm_mobile_assignments.client.user;

import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignmentInfo;
import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.List;

public class CmMobileAssignmentUser implements Response {

    private int uid;
    private String userName;
    private List<StudentAssignmentInfo> assignments;

    public CmMobileAssignmentUser(){}

    public CmMobileAssignmentUser(int uid, String userName, List<StudentAssignmentInfo> assignments) {
        this.uid = uid;
        this.userName = userName;
        this.assignments = assignments;
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
        return "CmMobileAssignmentUser [uid=" + uid + ", userName=" + userName + ", assignments=" + assignments + "]";
    }
}
