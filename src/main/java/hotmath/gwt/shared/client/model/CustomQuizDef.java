package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class CustomQuizDef implements Response {
    
    int quizId;
    String quizName;
    int adminId;
    
    public CustomQuizDef() {
    }
    public CustomQuizDef(int quizId, String quizName, int adminId) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.adminId = adminId;
    }
    public String getQuizName() {
        return quizName;
    }
    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }
    public int getAdminId() {
        return adminId;
    }
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    public int getQuizId() {
        return quizId;
    }
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
    
}
