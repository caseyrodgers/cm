package hotmath.gwt.shared.client.rpc.result;

import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentMetaInfo;
import hotmath.gwt.cm_rpc.client.rpc.Response;

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
    AssignmentMetaInfo assignmentInfo;

    
    //CmList<Assignment> assignments;
    
    public UserSyncInfo() {}
    
    public UserSyncInfo(CatchupMathVersion version, String currentUserLoginKey, AssignmentMetaInfo assignmentInfo) {
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

    public AssignmentMetaInfo getAssignmentInfo() {
        return assignmentInfo;
    }

    public void setAssignmentInfo(AssignmentMetaInfo _assignmentInfo) {
        this.assignmentInfo = _assignmentInfo;
    }

    @Override
    public String toString() {
        return "UserSyncInfo [versionInfo=" + versionInfo + ", currentUserLoginKey=" + currentUserLoginKey + ", _assignmentInfo=" + assignmentInfo + "]";
    }
}
