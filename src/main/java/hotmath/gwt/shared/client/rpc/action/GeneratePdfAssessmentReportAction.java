package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.ClientInfo.UserType;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.ActionBase;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;

import java.util.Map;

public class GeneratePdfAssessmentReportAction extends ActionBase implements Action<CmWebResource>{

    Integer adminId;
    GetStudentGridPageAction pageAction;
	Map<FilterType,String> filterMap;
    
	public GeneratePdfAssessmentReportAction() {
		getClientInfo().setUserType(UserType.ADMIN);
	}
    
    public GeneratePdfAssessmentReportAction(Integer adminId, GetStudentGridPageAction pageAction) {
        this.pageAction = pageAction;
        this.adminId = adminId;
		getClientInfo().setUserId((adminId!=null)?adminId:0);
		getClientInfo().setUserType(UserType.ADMIN);
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

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Map<FilterType,String> getFilterMap() {
		return filterMap;
	}

	public void setFilterMap(Map<FilterType,String> filterMap) {
		this.filterMap = filterMap;
	}

}
