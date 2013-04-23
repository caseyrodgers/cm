package hotmath.gwt.cm_mobile.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class CmMobileUser implements Response {
    
    String name;
    int userId;
    int testId;
    int testSegment;
    int testSlot;
    int runId;
    PrescriptionData prescripion;


    public PrescriptionData getPrescripion() {
        return prescripion;
    }

    public void setPrescripion(PrescriptionData prescripion) {
        this.prescripion = prescripion;
    }

    public CmMobileUser() {}
    
    public CmMobileUser(int uid, int testId, int testSegment, int testSlot, int runId) {
        this.userId = uid;
        this.testId = testId;
        this.testSegment = testSegment;
        this.testSlot = testSlot;
    }
    

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public int getTestSegment() {
        return testSegment;
    }

    public void setTestSegment(int testSegment) {
        this.testSegment = testSegment;
    }

    public int getTestSlot() {
        return testSlot;
    }

    public void setTestSlot(int testSlot) {
        this.testSlot = testSlot;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
