package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;

public class GetAdminTrendingDataDetailAction implements Action<CmList<StudentModelExt>>{

    Integer adminId;
    Integer testDefId;
    Integer quizSegment;
    String lessonName;
    DataType dataType;
    GetStudentGridPageAction dataAction;
    
    public GetAdminTrendingDataDetailAction() {}
    
    public GetAdminTrendingDataDetailAction(Integer adminId, GetStudentGridPageAction action, Integer testDefId, Integer quizSegment) {
        this.dataType = DataType.PROGRAM_USERS;
        this.adminId = adminId;
        this.dataAction = action;
        this.testDefId = testDefId;
        this.quizSegment = quizSegment;
    }
    
    public GetAdminTrendingDataDetailAction(GetStudentGridPageAction action, String lessonName) {
        this.dataType = DataType.LESSON_USERS;
        this.dataAction = action;
        this.lessonName = lessonName;
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
    
    
    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
    
    public enum DataType{PROGRAM_USERS,LESSON_USERS};
}
