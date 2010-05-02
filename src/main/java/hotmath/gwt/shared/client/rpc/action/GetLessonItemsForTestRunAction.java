package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;

/** Return Lession Items, Standards for given runId
 * 
 * @author casey
 *
 */
public class GetLessonItemsForTestRunAction implements Action<CmList<LessonItemModel>>{
    
    Integer runId;
    

    public GetLessonItemsForTestRunAction() {}
    
    public GetLessonItemsForTestRunAction(Integer runId) {
        this.runId = runId;
    }
    
    
    public Integer getRunId() {
        return runId;
    }

    public void setRunId(Integer runId) {
        this.runId = runId;
    }

    @Override
    public String toString() {
        return "GetLessonItemsForTestRunAction [runId=" + runId + "]";
    }


}
