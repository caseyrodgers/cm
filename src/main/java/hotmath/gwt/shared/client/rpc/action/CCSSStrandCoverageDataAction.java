package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.model.CCSSStrandCoverageData;

import java.util.Date;

public class CCSSStrandCoverageDataAction implements Action<CmList<CCSSStrandCoverageData>> {
    
	private static final long serialVersionUID = -5265157500969042404L;

	Integer uid;
    Integer adminId;
    String levelName;   // aka strand name
    Date from;
    Date to;
    
    public CCSSStrandCoverageDataAction() {
    }

    public CCSSStrandCoverageDataAction(int adminId, int uid, Date from, Date to) {
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

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
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
        return "CCSSStrandCoverageAction [adminId=" + adminId + ", uid=" + uid + ", levelName=" + levelName + ", from=" + from + ", to=" + to +"]";
    }

}
