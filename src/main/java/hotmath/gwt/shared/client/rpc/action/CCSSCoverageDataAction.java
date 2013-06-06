package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

import java.util.Date;

public class CCSSCoverageDataAction implements Action<CmList<CCSSCoverageData>> {
    
	private static final long serialVersionUID = 934238051392406321L;

	ReportType type;
    Integer uid;
    Date from;
    Date to;
    
    public CCSSCoverageDataAction() {
    }

    public CCSSCoverageDataAction(ReportType type, int uid, Date from, Date to) {
        this.type = type;
        this.uid = uid;
        this.from = from;
        this.to = to;
    }
    
    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public Integer getUID() {
		return uid;
	}

	public void setUID(Integer uid) {
		this.uid = uid;
	}

	public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "CCSSCoverageDataAction [type=" + type + ", from=" + from + ", to=" + to +"]";
    }


    public enum ReportType {
    	STUDENT_ASSIGNED_COMPLETED, STUDENT_QUIZZED_PASSED, STUDENT_REVIEWED, STUDENT_COMBINED, STUDENT_NOT_COVERED,
    	GROUP};}
