package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.model.CCSSCoverageBar;

import java.util.Date;

/**
 * 
 * @author bob
 *
 */

public class CCSSCoverageChartDataAction implements Action<CmList<CCSSCoverageBar>> {
    
	private static final long serialVersionUID = 934238051392406321L;

	ReportType type;
    Integer uid;
    Integer adminId;
    Date from;
    Date to;
    
    public CCSSCoverageChartDataAction() {
    }

    public CCSSCoverageChartDataAction(ReportType type, int uid, Date from, Date to) {
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

	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
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
        return "CCSSCoverageChartDataAction [type=" + type + ", from=" + from + ", to=" + to +"]";
    }

    public enum ReportType {STUDENT_CUMULATIVE_CHART, GROUP_CUMULATIVE_CHART};

}
