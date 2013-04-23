package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class SaveAssignmentTutorInputWidgetAnswerAction implements Action<RpcData> {
    
    public SaveAssignmentTutorInputWidgetAnswerAction() {};
    
    int assignKey;
    String pid;
    boolean correct;
    private int uid;
    String value;
    
    public SaveAssignmentTutorInputWidgetAnswerAction(int uid, int assignKey, String pid, String value, boolean correct) {
        this.uid = uid;
        this.assignKey = assignKey;
        this.pid = pid;
        this.value = value;
        this.correct = correct;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String valueStr = value!=null?value.replace("\n", " "):"";
        return "SaveAssignmentTutorInputWidgetAnswerAction [assignKey=" + assignKey + ", pid=" + pid + ", correct="
                + correct + ", uid=" + uid + ", value=" + (valueStr!=null ? "(len=" + valueStr.length() + ")":null) + "]";
    }
    
}
