package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.rpc.Action;

public class GetAdminTrendingDataDetailAction implements Action<CmList<StudentModelExt>>{
    
    Integer adminId;
    Integer testDefId;
    Integer quizSegment;
    GetStudentGridPageAction dataAction;
    
    public GetAdminTrendingDataDetailAction() {}
    
    public GetAdminTrendingDataDetailAction(Integer adminId, GetStudentGridPageAction action, Integer testDefId, Integer quizSegment) {
        this.adminId = adminId;
        this.dataAction = action;
        this.testDefId = testDefId;
        this.quizSegment = quizSegment;
    }

    public Integer getTestDefId() {
        return testDefId;
    }

    public void setTestDefId(Integer testDefId) {
        this.testDefId = testDefId;
    }

    public Integer getQuizSegment() {
        return quizSegment;
    }

    public void setQuizSegment(Integer quizSegment) {
        this.quizSegment = quizSegment;
    }

    public GetStudentGridPageAction getDataAction() {
        return dataAction;
    }

    public void setDataAction(GetStudentGridPageAction dataAction) {
        this.dataAction = dataAction;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
}
