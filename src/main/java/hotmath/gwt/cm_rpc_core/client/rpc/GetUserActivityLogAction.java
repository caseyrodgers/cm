package hotmath.gwt.cm_rpc_core.client.rpc;

import java.util.Date;

import hotmath.gwt.cm_tools.client.model.ActivityLogRecord;

public class GetUserActivityLogAction implements Action<CmList<ActivityLogRecord>> {

	private static final long serialVersionUID = -5077957877651762065L;

	private int uid;

    private Date fromDate;
    private Date toDate;

    public GetUserActivityLogAction() {}

    public GetUserActivityLogAction(int uid) {
        this.uid = uid;
    }

    public GetUserActivityLogAction(int uid, Date fromDate, Date toDate) {
        this.uid      = uid;
        this.fromDate = fromDate;
        this.toDate   = toDate;
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
}
