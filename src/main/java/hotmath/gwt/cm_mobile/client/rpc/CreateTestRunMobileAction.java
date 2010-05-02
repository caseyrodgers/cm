package hotmath.gwt.cm_mobile.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;


public class CreateTestRunMobileAction implements Action<PrescriptionSessionResponse>{
    
    CmMobileUser user;
    MultiActionRequestAction selections;
    
    public CreateTestRunMobileAction() {}
    
    public CreateTestRunMobileAction(CmMobileUser user, MultiActionRequestAction selections) {
        this.user = user;
        this.selections = selections;
    }

    public CmMobileUser getUser() {
        return user;
    }

    public void setUser(CmMobileUser user) {
        this.user = user;
    }

    public MultiActionRequestAction getSelections() {
        return selections;
    }

    public void setSelections(MultiActionRequestAction selections) {
        this.selections = selections;
    }
}
