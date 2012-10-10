package hotmath.gwt.cm_rpc.client.rpc;


public class SaveAssignmentSolutionContextAction implements Action<RpcData> {
    
    private int uid;
    private int assignKey;
    private String pid;
    private int problemNumber;
    private String variablesJson;

    public SaveAssignmentSolutionContextAction(){}

    public SaveAssignmentSolutionContextAction(int uid, int assignKey, String pid, int problemNumber, String variablesJson) {
        this.uid = uid;
        this.assignKey = assignKey;
        this.pid = pid;
        this.problemNumber = problemNumber;
        this.variablesJson = variablesJson;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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

    public int getProblemNumber() {
        return problemNumber;
    }

    public void setProblemNumber(int problemNumber) {
        this.problemNumber = problemNumber;
    }

    public String getVariablesJson() {
        return variablesJson;
    }

    public void setVariablesJson(String variablesJson) {
        this.variablesJson = variablesJson;
    }
}
