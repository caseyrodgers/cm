package hotmath.gwt.cm_rpc.client.model;

import java.util.Date;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class DateRange implements Response {

	private static final long serialVersionUID = 4916193224448156740L;

	private Date fromDate;
	private Date toDate;
	private Date minFromDate;

	private static DateRange _instance;

	public static DateRange getInstance() {
		if (_instance == null) {
			_instance = new DateRange();
		}
		return _instance;
	}

	private DateRange() {
	}

	public Date getFromDate() {
        if (fromDate == null) {
            fromDate = getMinFromDate();
        }
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		if (fromDate != null && fromDate.before(minFromDate))
			fromDate = getMinFromDate();
		this.fromDate = fromDate;
	}

	@SuppressWarnings("deprecation")
	public Date getToDate() {
        if (toDate == null) {
            toDate = new Date(150,0,0);
        }
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@SuppressWarnings("deprecation")
	public Date getMinFromDate() {
        if (minFromDate == null) {
            minFromDate = new Date(111,6,1);
        }
		return minFromDate;
	}

	public void setMinFromDate(Date minFromDate) {
 		this.minFromDate = minFromDate;
	}

}
