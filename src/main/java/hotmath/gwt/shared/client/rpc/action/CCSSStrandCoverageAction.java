package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSStrandCoverage;

import java.util.Date;

public class CCSSStrandCoverageAction implements Action<CmList<CCSSStrandCoverage>> {
    
	private static final long serialVersionUID = 2899895393907942627L;

    Integer uid;
    Integer adminId;
    Date from;
    Date to;
    
    public CCSSStrandCoverageAction() {
    }

    public CCSSStrandCoverageAction(int adminId, int uid, Date from, Date to) {
    	this.adminId = adminId;
        this.uid = uid;
        this.from = from;
        this.to = to;
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
        return "CCSSStrandCoverageAction [adminId=" + adminId + ", uid=" + uid + ", from=" + from + ", to=" + to +"]";
    }

}
