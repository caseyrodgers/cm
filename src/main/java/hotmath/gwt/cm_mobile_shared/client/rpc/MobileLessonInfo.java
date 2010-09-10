package hotmath.gwt.cm_mobile_shared.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.Response;

public class MobileLessonInfo implements Response{
    
    PrescriptionData presData;
    
    public MobileLessonInfo() {}

    public PrescriptionData getPresData() {
        return presData;
    }

    public void setPresData(PrescriptionData presData) {
        this.presData = presData;
    }
}
