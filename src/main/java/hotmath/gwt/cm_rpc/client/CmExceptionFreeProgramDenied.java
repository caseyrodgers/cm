package hotmath.gwt.cm_rpc.client;

import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;

public class CmExceptionFreeProgramDenied extends CmRpcException{
    int uid;
    String testName;
    
    public CmExceptionFreeProgramDenied(int uid, String testName) {
        super("Sorry, this program is not currently available under your school license, please contact your teacher.");
        this.uid = uid;
        this.testName = testName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    @Override
    public String toString() {
        return "CmExceptionFreeProgramDenied [uid=" + uid + ", testName=" + testName + "]";
    }

}
