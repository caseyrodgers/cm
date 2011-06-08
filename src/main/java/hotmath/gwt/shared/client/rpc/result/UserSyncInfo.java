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
    
    public UserSyncInfo() {}
    
    public UserSyncInfo(CatchupMathVersion version, String currentUserLoginKey) {
        this.versionInfo = version;        
        this.currentUserLoginKey = currentUserLoginKey;
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
    

    @Override
    public String toString() {
        return "UserSyncInfo [versionInfo=" + versionInfo + ", currentUserLoginKey=" + currentUserLoginKey + "]";
    }
}
