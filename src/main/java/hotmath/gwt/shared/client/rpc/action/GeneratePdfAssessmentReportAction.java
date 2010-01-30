package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.CmWebResource;

public class GeneratePdfAssessmentReportAction implements Action<CmWebResource>{

    Integer adminId;
    GetStudentGridPageAction pageAction;
    
    public GeneratePdfAssessmentReportAction() {}
    
    public GeneratePdfAssessmentReportAction(Integer adminId, GetStudentGridPageAction pageAction) {
        this.pageAction = pageAction;
        this.adminId = adminId;
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
}
