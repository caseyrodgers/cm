package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class CustomProblemModel implements Response {
    String pid;
    TeacherIdentity teacher;
    int problemNumber;
    String comments;

    public CustomProblemModel(){}
            
    public CustomProblemModel(String pid, int problemNumber, TeacherIdentity teacher, String comments) {
        this.pid = pid;
        this.problemNumber = problemNumber;
        this.teacher = teacher;
        this.comments = comments;
    }
    
    public int getProblemNumber() {
        return problemNumber;
    }

    public void setProblemNumber(int problemNumber) {
        this.problemNumber = problemNumber;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setTeacher(TeacherIdentity teacher) {
        this.teacher = teacher;
    }

    public String getPid() {
        return this.pid;
    }

    public TeacherIdentity getTeacher() {
        return teacher;
    }
    
    public String getTeacherName() {
        return teacher.getTeacherName();
    }

    @Override
    public String toString() {
        return "CustomProblemModel [pid=" + pid + ", teacher=" + teacher + ", problemNumber=" + problemNumber + ", comments=" + comments + "]";
    }
}

