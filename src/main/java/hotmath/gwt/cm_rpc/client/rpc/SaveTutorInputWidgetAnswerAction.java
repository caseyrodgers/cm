package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class SaveTutorInputWidgetAnswerAction implements Action<UserTutorWidgetStats> {
    
    public SaveTutorInputWidgetAnswerAction() {};
    
    int userId;
    int runId;
    String pid;
    String value;
    boolean correct;
    
    public SaveTutorInputWidgetAnswerAction(int userId, int runId, String pid, String value, boolean correct) {
        this.userId = userId;
        this.runId = runId;
        this.pid = pid;
        this.value = value;        
        this.correct = correct;
    }
    


    public String getValue() {
        return value;
    }



    public void setValue(String value) {
        this.value = value;
    }



    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
    

    public int getUserId() {
        return userId;
    }



    public void setUserId(int userId) {
        this.userId = userId;
    }



    @Override
    public String toString() {
        return "SaveTutorInputWidgetAnswerAction [userId=" + userId + ", runId=" + runId + ", pid=" + pid + ", value=" + value + ", correct=" + correct + "]";
    }
}
