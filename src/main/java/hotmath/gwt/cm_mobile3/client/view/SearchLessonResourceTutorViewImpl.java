package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_core.client.BackAction;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.page.IPage.ApplicationType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;



public class SearchLessonResourceTutorViewImpl extends PrescriptionLessonResourceTutorViewImpl implements SearchLessonResourceTutorView {
    
    public SearchLessonResourceTutorViewImpl() {
        super("Work out the problem on our whiteboard.", new TutorCallbackDefault() {
            
            @Override
            public Action<RpcData> getSaveSolutionContextAction(String variablesJson, String pid, int problemNumber) {
                return null;
            }
            
            @Override
            public String getWhiteboardText() {
                return "Work out the problem on our whiteboard.";
            }
            
            @Override
            public void tutorWidgetComplete(String inputValue, boolean correct) {
                // do nothing
            }
            
            @Override
            public void solutionHasBeenViewed(String value) {
                // do nothing
            }
            
            @Override
            public BackAction getTutorReturnButtonAction() {
                return new BackAction() {
                    @Override
                    public boolean goBack() {
                        Controller.navigateBack();
                        return false;
                    }
                };
            }
        });
    }
    
    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.NONE;
    }
}
 