package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;

/** Return Custom Program Lesson Items/Standards for given testId
 * 
 * @author bob
 *
 */
public class GetLessonItemsForCustomProgramTestAction implements Action<CmList<LessonItemModel>>{
    
    Integer testId;

    public GetLessonItemsForCustomProgramTestAction() {}

    public GetLessonItemsForCustomProgramTestAction(Integer testId) {
        this.testId = testId;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    @Override
    public String toString() {
        return "GetLessonItemsForCustomProgramTestAction [testId=" + testId + "]";
    }
}