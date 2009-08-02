package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.util.RpcData;

public class GetQuizHtmlAction implements Action<RpcData> {

    int uid;
    int testSegment;
    


    public GetQuizHtmlAction() {}
    
    public GetQuizHtmlAction(int uid, int testSegment) {
        this.uid = uid;
        this.testSegment = testSegment;
    }
    
    
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getTestSegment() {
        return testSegment;
    }

    public void setTestSegment(int testSegment) {
        this.testSegment = testSegment;
    }


    
    @Override
    public String toString() {
        return "GetQuizHtmlAction [testSegment=" + testSegment + ", uid=" + uid + "]";
    }
    
}
