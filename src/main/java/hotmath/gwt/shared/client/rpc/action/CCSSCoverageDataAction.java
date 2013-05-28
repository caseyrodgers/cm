package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

import java.util.Date;

public class CCSSCoverageDataAction implements Action<CmList<CCSSCoverageData>> {
    
	private static final long serialVersionUID = 934238051392406321L;

	ReportType type;
    Integer adminId;
    Integer userId;
    Integer groupId;
    Date from;
    Date to;
    
    GetStudentGridPageAction studentGridPageAction;

    public CCSSCoverageDataAction() {
    }

    public CCSSCoverageDataAction(GetStudentGridPageAction studentGridAction, ReportType type, Integer adminId, Integer userId, Integer groupId, Date from, Date to) {
        this.studentGridPageAction = studentGridAction;
        this.type = type;
        this.adminId = adminId;
        this.userId = userId;
        this.groupId = groupId;
        this.from = from;
        this.to = to;
    }
    
    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
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

    public GetStudentGridPageAction getStudentGridPageAction() {
        return studentGridPageAction;
    }

    public void setStudentGridPageAction(GetStudentGridPageAction studentGridPageAction) {
        this.studentGridPageAction = studentGridPageAction;
    }

    @Override
    public String toString() {
        return "CCSSCoverageDataAction [type=" + type + ", adminId=" + adminId + ", from=" + from + ", to=" + to
                + ", _studentGridPageAction=" + studentGridPageAction + "]";
    }


    public enum ReportType{STUDENT_ASSIGNED_COMPLETED, STUDENT_QUIZZED_PASSED, STUDENT_REVIEWED, GROUP};}
