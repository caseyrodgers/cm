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
    TestApplication testApp;

    public RunNetTestAction(){}
    
    public RunNetTestAction(TestApplication testApp,TestAction action, int uid, int testNum, long dataSize) {
        this.testApp = testApp;
        this.action = action;
        this.uid = uid;
        this.testNum = testNum;
        this.dataSize = dataSize;
    }
    
    public RunNetTestAction(TestApplication testApp,TestAction action, int uid, List<NetTestModel> testResults) {
        this.testApp = testApp;
        this.action=action;
        this.uid = uid;
        this.testResults = testResults;
    }

    public TestApplication getTestApp() {
        return testApp;
    }

    public void setTestApp(TestApplication testApp) {
        this.testApp = testApp;
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
        return "RunNetTestAction [action=" + action + ", dataSize=" + dataSize + ", testApp=" + testApp + ", testNum="
                + testNum + ", testResults=" + testResults + ", uid=" + uid + "]";
    }
    
    /** Define the different test actions
     * 
     */
    public enum TestAction{
        /** run a test and return results (no local io */
        RUN_TEST, 
        
        /** save the results of a group RUN_TESTS */
        SAVE_RESULTS};
        
    /** Define the applications that can be tested.
     *  This is needed to know how to identify the 
     *  user running the test (either aid or uid).
     */
    public enum TestApplication{CM_STUDENT,CM_ADMIN};
}
