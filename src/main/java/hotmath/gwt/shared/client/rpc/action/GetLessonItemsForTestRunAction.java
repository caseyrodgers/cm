package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.shared.client.rpc.Action;

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
