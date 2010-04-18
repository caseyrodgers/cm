package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.shared.client.util.RpcData;

public class GetQuizCurrentResultsAction implements Action<CmList<RpcData>> {
    
    Integer uid;
    public GetQuizCurrentResultsAction() {}
    
    public GetQuizCurrentResultsAction(Integer uid) {
        this.uid = uid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }
}
