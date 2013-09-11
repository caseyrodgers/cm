package hotmath.gwt.cm_rpc_assignments.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tools.client.model.StudentModel;

import java.util.List;

public class AssignmentRealTimeStatsUsers implements Response {
    
    private List<StudentModel> correct;
    private List<StudentModel> incorrect;
    private List<StudentModel> unanswered;
    private List<StudentModel> submitted;

    public AssignmentRealTimeStatsUsers() {}
    
    public AssignmentRealTimeStatsUsers(List<StudentModel> correct,List<StudentModel> incorrect,List<StudentModel> submitted,List<StudentModel> unanswered ) {
        this.correct = correct;
        this.incorrect = incorrect;
        this.submitted = submitted;
        this.unanswered = unanswered;
    }

    public List<StudentModel> getCorrect() {
        return correct;
    }

    public void setCorrect(List<StudentModel> correct) {
        this.correct = correct;
    }

    public List<StudentModel> getIncorrect() {
        return incorrect;
    }

    public void setIncorrect(List<StudentModel> incorrect) {
        this.incorrect = incorrect;
    }

    public List<StudentModel> getUnanswered() {
        return unanswered;
    }

    public void setUnanswered(List<StudentModel> unanswered) {
        this.unanswered = unanswered;
    }

    public List<StudentModel> getSubmitted() {
        return submitted;
    }

    public void setSubmitted(List<StudentModel> submitted) {
        this.submitted = submitted;
    }

}
