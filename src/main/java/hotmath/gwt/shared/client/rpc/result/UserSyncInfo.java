package hotmath.gwt.shared.client.rpc.result;

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
    boolean hasNewAssignments;
    
    //CmList<Assignment> assignments;
    
    public UserSyncInfo() {}
    
    public UserSyncInfo(CatchupMathVersion version, String currentUserLoginKey, boolean hasNewAssignments) {
        this.versionInfo = version;        
        this.currentUserLoginKey = currentUserLoginKey;
        this.hasNewAssignments = hasNewAssignments;
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

    public boolean isHasNewAssignments() {
        return hasNewAssignments;
    }

    public void setHasNewAssignments(boolean hasNewAssignments) {
        this.hasNewAssignments = hasNewAssignments;
    }

    @Override
    public String toString() {
        return "UserSyncInfo [versionInfo=" + versionInfo + ", currentUserLoginKey=" + currentUserLoginKey
                + ", hasNewAssignments=" + hasNewAssignments + "]";
    }
    
}
