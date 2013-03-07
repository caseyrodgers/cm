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
    private int activeAssignments;
    private int unreadMessages;
    
    //CmList<Assignment> assignments;
    
    public UserSyncInfo() {}
    
    public UserSyncInfo(CatchupMathVersion version, String currentUserLoginKey, int activeAssignments, int unreadMessages) {
        this.versionInfo = version;        
        this.currentUserLoginKey = currentUserLoginKey;
        this.setActiveAssignments(activeAssignments);
        this.setUnreadMessages(unreadMessages);
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

    public int getActiveAssignments() {
        return activeAssignments;
    }

    public void setActiveAssignments(int activeAssignments) {
        this.activeAssignments = activeAssignments;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    @Override
    public String toString() {
        return "UserSyncInfo [versionInfo=" + versionInfo + ", currentUserLoginKey=" + currentUserLoginKey + ", activeAssignments=" + activeAssignments
                + ", unreadMessages=" + unreadMessages + "]";
    }
    
}
