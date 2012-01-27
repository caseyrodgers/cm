package hotmath.gwt.cm_rpc.client.rpc;

public class SaveTutorInputWidgetAnswerAction implements Action<RpcData> {
    
    public SaveTutorInputWidgetAnswerAction() {};
    
    int runId;
    String pid;
    boolean correct;
    
    public SaveTutorInputWidgetAnswerAction(int runId, String pid, boolean correct) {
        this.runId = runId;
        this.pid = pid;
        this.correct = correct;
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

    @Override
    public String toString() {
        return "SaveTutorInputWidgetAnswerAction [runId=" + runId + ", pid=" + pid + ", correct=" + correct + "]";
    }
}
