package hotmath.gwt.cm_rpc.client.rpc;

import java.util.Date;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;


/** Get a single user's widget stats
 * 
 * @author casey
 *
 */
public class GetUserInfoStatsAction implements Action<UserInfoStats>{
    
	private static final long serialVersionUID = -2178743709339367928L;

    private int uid;
    private Date fromDate;
    private Date toDate;

    public GetUserInfoStatsAction(){}
    
    public GetUserInfoStatsAction(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Override
    public String toString() {
        return "GetUserInfoStatsAction [uid=" + uid + "]";
    }
}
