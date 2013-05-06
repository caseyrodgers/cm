package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/** Encapsulates the information about a current 
 * users status.   
 * 
 *  
 * @author casey
 *
 */
public class UserSyncInfo implements Response{
    CatchupMathVersion versionInfo;
    String currentUserLoginKey;
    AssignmentUserInfo assignmentInfo;

    
    //CmList<Assignment> assignments;
    
    public UserSyncInfo() {}
    
    public UserSyncInfo(CatchupMathVersion version, String currentUserLoginKey, AssignmentUserInfo assignmentInfo) {
        this.versionInfo = version;        
        this.currentUserLoginKey = currentUserLoginKey;
        this.assignmentInfo = assignmentInfo;
    }

    public CatchupMathVersion getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(CatchupMathVersion versionInfo) {
        this.versionInfo = versionInfo;
    }

    public String getCurrentUserLoginKey() {
        return currentUserLoginKey;
    }

    public void setCurrentUserLoginKey(String currentUserLoginKey) {
        this.currentUserLoginKey = currentUserLoginKey;
    }

    public AssignmentUserInfo getAssignmentInfo() {
        return assignmentInfo;
    }

    public void setAssignmentInfo(AssignmentUserInfo _assignmentInfo) {
        this.assignmentInfo = _assignmentInfo;
    }

    @Override
    public String toString() {
        return "UserSyncInfo [versionInfo=" + versionInfo + ", currentUserLoginKey=" + currentUserLoginKey + ", _assignmentInfo=" + assignmentInfo + "]";
    }
}
