package hotmath.gwt.shared.client.rpc.result;

import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.rpc.Response;

/** Encapsulates the information about a current 
 * users state in their active program.  Also,the current
 * state of the CM client version in use is compared against
 * the current
 * 
 *  
 * @author casey
 *
 */
public class UserSyncInfo implements Response{
    CatchupMathVersion versionInfo;
    StudentActiveInfo activeInfo;
    
    public UserSyncInfo() {}
    
    public UserSyncInfo(CatchupMathVersion version, StudentActiveInfo activeInfo) {
        this.versionInfo = version;        
        this.activeInfo = activeInfo;
    }

    public CatchupMathVersion getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(CatchupMathVersion versionInfo) {
        this.versionInfo = versionInfo;
    }

    public StudentActiveInfo getActiveInfo() {
        return activeInfo;
    }

    public void setActiveInfo(StudentActiveInfo activeInfo) {
        this.activeInfo = activeInfo;
    }

    @Override
    public String toString() {
        return "UserSyncInfo [versionInfo=" + versionInfo + ", activeInfo=" + activeInfo + "]";
    }
}
