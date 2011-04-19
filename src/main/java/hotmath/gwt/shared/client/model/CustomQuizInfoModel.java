package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;

import java.util.ArrayList;
import java.util.List;

public class CustomQuizInfoModel implements Response {
    
    CustomLessonModel quiz;
    List<StudentModelExt> assignedStudents = new ArrayList<StudentModelExt>();
    int questionCount;
    
    
    public CustomLessonModel getQuiz() {
        return quiz;
    }
    public void setQuiz(CustomLessonModel quiz) {
        this.quiz = quiz;
    }
    public List<StudentModelExt> getAssignedStudents() {
        return assignedStudents;
    }
    public void setAssignedStudents(List<StudentModelExt> assignedStudents) {
        this.assignedStudents = assignedStudents;
    }
    public int getQuestionCount() {
        return questionCount;
    }
    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }
    
}
