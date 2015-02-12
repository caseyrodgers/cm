package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

/** Reset the given user's current program 
 * 
 * @author casey
 *
 */
public class ResetUserAction implements Action<RpcData> {

    static public enum ResetType {FULL, RESENT_QUIZ};

    int altTest;
    private ResetType type;
	int id;
	
	public ResetUserAction() {}

	public ResetUserAction(ResetType type, int id, int altTest) {
	    this.type = type;
        this.id = id;  // might be uid, or programId
        this.altTest = altTest;
	}


	public int getAltTest() {
        return altTest;
    }

    public void setAltTest(int altTest) {
        this.altTest = altTest;
    }

    
    /** is uid if FULL, else is programId
     * 
     * @return
     */
    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public ResetType getType() {
        return type;
    }


    public void setType(ResetType type) {
        this.type = type;
    }

}
