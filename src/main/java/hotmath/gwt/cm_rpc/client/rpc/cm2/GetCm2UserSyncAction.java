package hotmath.gwt.cm_rpc.client.rpc.cm2;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_core.client.model.UserSyncInfo;


/** Defines the movement of the active test by specifying
 * the segment of the current program.
 * 
 * Maybe a better name is GetUserProgramSegmentAction(uid, testSegment);
 * @author casey
 *
 */
public class GetCm2UserSyncAction implements Action<UserSyncInfo> {

    int testId;

    public GetCm2UserSyncAction() {}
    
    public GetCm2UserSyncAction(int testId) {
        this.testId = testId;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }
}
