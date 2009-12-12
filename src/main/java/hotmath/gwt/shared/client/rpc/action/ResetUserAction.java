package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.util.RpcData;

/** Reset the given user's current program 
 * 
 * @author casey
 *
 */
public class ResetUserAction implements Action<RpcData> {
	
	Integer uid;
	
	public ResetUserAction() {}
	

	public ResetUserAction(Integer uid) {
		this.uid = uid;
	}


	public Integer getUid() {
		return uid;
	}


	public void setUid(Integer uid) {
		this.uid = uid;
	}
}
