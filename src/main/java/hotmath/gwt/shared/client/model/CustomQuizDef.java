package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class CustomQuizDef implements Response {
    
    String quizName;
    int adminId;
    
    public CustomQuizDef() {
    }
    public CustomQuizDef(String quizName, int adminId) {
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
    
}
