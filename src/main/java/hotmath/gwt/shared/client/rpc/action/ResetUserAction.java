package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

/** Reset the given user's current program 
 * 
 * @author casey
 *
 */
public class ResetUserAction implements Action<RpcData> {
	
	Integer uid;
	int altTest;  // make sure default is zero,not null
	
	public ResetUserAction() {}
	

	public ResetUserAction(Integer uid, Integer alternateTest) {
		this.uid = uid;
		this.altTest = alternateTest;
		
	}


	public Integer getUid() {
		return uid;
	}


	public void setUid(Integer uid) {
		this.uid = uid;
	}


    public Integer getAltTest() {
        return altTest;
    }


    public void setAltTest(Integer altTest) {
        this.altTest = altTest;
    }
    


    @Override
    public String toString() {
        return "ResetUserAction [uid=" + uid + ", altTest=" + altTest + "]";
    }
}
