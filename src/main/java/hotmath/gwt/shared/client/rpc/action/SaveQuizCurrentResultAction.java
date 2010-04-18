package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

public class SaveQuizCurrentResultAction implements Action<RpcData> {

    int testId;
    boolean correct;
    int answerIndex;
    

    String pid;
    
    public SaveQuizCurrentResultAction() {}
    
    public SaveQuizCurrentResultAction(int testId, boolean correct, int answerIndex, String pid) {
        this.testId = testId;
        this.correct = correct;
        this.answerIndex = answerIndex;
        this.pid = pid;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getAnswerIndex() {
        return answerIndex;
    }

    public void setAnswerIndex(int answerIndex) {
        this.answerIndex = answerIndex;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
    
    @Override
    public String toString() {
        return "SaveQuizCurrentResultAction [answerIndex=" + answerIndex + ", correct=" + correct + ", pid=" + pid
                + ", testId=" + testId + "]";
    }
    
}
