package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.util.NetTestModel;

import java.util.List;

public class RunNetTestAction implements Action<NetTestModel>{
    
    Integer uid;
    Integer testNum;
    Long dataSize;
    TestAction action;
    List<NetTestModel> testResults;

    public RunNetTestAction(){}
    
    public RunNetTestAction(TestAction action, int uid, int testNum, long dataSize) {
        this.action = action;
        this.uid = uid;
        this.testNum = testNum;
        this.dataSize = dataSize;
    }
    
    public RunNetTestAction(TestAction action, int uid, List<NetTestModel> testResults) {
        this.action=action;
        this.uid = uid;
        this.testResults = testResults;
    }

    public TestAction getAction() {
        return action;
    }

    public void setAction(TestAction action) {
        this.action = action;
    }

    public Integer getUid() {
        return uid;
    }
    public void setUid(Integer uid) {
        this.uid = uid;
    }
    public Integer getTestNum() {
        return testNum;
    }
    public void setTestNum(Integer testNum) {
        this.testNum = testNum;
    }
    public Long getDataSize() {
        return dataSize;
    }
    public void setDataSize(Long dataSize) {
        this.dataSize = dataSize;
    }
    

    public List<NetTestModel> getTestResults() {
        return testResults;
    }

    public void setTestResults(List<NetTestModel> testResults) {
        this.testResults = testResults;
    }

    @Override
    public String toString() {
        return "RunNetTestAction [action=" + action + ", dataSize=" + dataSize + ", testNum=" + testNum + ", uid="
                + uid + "]";
    }
    
    public enum TestAction{RUN_TEST, SAVE_RESULTS};

}
