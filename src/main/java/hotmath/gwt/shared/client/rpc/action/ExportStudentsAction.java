package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;

import java.util.Date;
import java.util.Map;

@SuppressWarnings("serial")
public class ExportStudentsAction implements Action<StringHolder>{

    Integer adminId;
    GetStudentGridPageAction pageAction;
    String  emailAddress;
	Map<FilterType,String> filterMap;
	Date fromDate;
	Date toDate;
    
	public ExportStudentsAction() {
	}
    
    public ExportStudentsAction(Integer adminId, GetStudentGridPageAction pageAction) {
        this.pageAction = pageAction;
        this.adminId = adminId;
        this.pageAction.setAdminId(adminId);
    }

    public Integer getAdminId() {
        return adminId;
    }

    public GetStudentGridPageAction getPageAction() {
        return pageAction;
    }

    public void setPageAction(GetStudentGridPageAction pageAction) {
        this.pageAction = pageAction;
    }

    public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Map<FilterType,String> getFilterMap() {
		return filterMap;
	}

	public void setFilterMap(Map<FilterType,String> filterMap) {
		this.filterMap = filterMap;
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
