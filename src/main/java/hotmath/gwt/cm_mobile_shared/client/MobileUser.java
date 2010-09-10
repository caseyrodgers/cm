package hotmath.gwt.cm_mobile_shared.client;

@Deprecated
public class MobileUser {
    int uid,testId,testSegment;
    public MobileUser() {
    }
    
    public MobileUser(int uid, int testId,int testSegment) {
        this.uid = uid;
        this.testId = testId;
        this.testSegment = testSegment;
    }
    public int getTestSegment() {
        return testSegment;
    }

    public void setTestSegment(int testSegment) {
        this.testSegment = testSegment;
    }

    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
    public int getTestId() {
        return testId;
    }
    public void setTestId(int testId) {
        this.testId = testId;
    }
}
