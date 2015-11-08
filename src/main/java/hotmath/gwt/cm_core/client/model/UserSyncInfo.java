package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/** Encapsulates the information about a current 
 * users status.   
 * 
 * return from polled call to server for info/events about 
 * a single student
 * 
 *  
 * @author casey
 *
 */
public class UserSyncInfo implements Response{
    CatchupMathVersion versionInfo;
    String currentUserLoginKey;
    AssignmentUserInfo assignmentInfo;
    
    CmList<StudentEvent> events = new CmArrayList<StudentEvent>();

    
    //CmList<Assignment> assignments;
    
    public UserSyncInfo() {}
    
    public UserSyncInfo(CatchupMathVersion version, String currentUserLoginKey, AssignmentUserInfo assignmentInfo) {
        this.versionInfo = version;        
        this.currentUserLoginKey = currentUserLoginKey;
        this.assignmentInfo = assignmentInfo;
    }
 
    public CmList<StudentEvent> getEvents() {
        return events;
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
