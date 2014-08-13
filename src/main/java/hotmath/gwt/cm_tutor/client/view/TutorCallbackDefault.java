package hotmath.gwt.cm_tutor.client.view;

import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

import com.allen_sauer.gwt.log.client.Log;

public class TutorCallbackDefault implements TutorCallback {

    @Override
    public void onNewProblem(int problemNumber) {
        Log.debug("TutorCallbackDefault: on new problem");
    }

    @Override
    public void tutorWidgetComplete(String inputValue, boolean correct) {
        Log.debug("TutorCallbackDefault: tutorWidgetComplete: " + inputValue + ", " + correct);        
    }

    @Override
    public void solutionHasBeenViewed(String value) {
        Log.debug("TutorCallbackDefault: solutionHasBeenViewed: " + value);        
    }


    @Override
    public Action<RpcData> getSaveSolutionContextAction(String variablesJson, String pid, int problemNumber) {
        Log.debug("TutorCallbackDefault: getSaveSolutionContextAction");
        return null;
    }
    
    @Override
    public void tutorWidgetCompleteDenied(String inputValue, boolean correct) {
        Log.debug("TutorCallbackDefault: tutorWidgetCompleteDenied");
    }

    @Override
    public void showWhiteboard() {
        Log.debug("TutorCallbackDefault: showWhiteboard");
    }

    @Override
    public boolean showTutorWidgetInfoOnCorrect() {
        Log.debug("TutorCallbackDefault: showTutorWidgetInfoOnCorrect");
        return false;
    }

    @Override
    public void solutionHasBeenInitialized() {
        Log.debug("TutorCallbackDefault: solutionHasBeenInitialized");
    }

    @Override
    public Action<UserTutorWidgetStats> getSaveTutorWidgetCompleteAction(String value, boolean yesNo) {
        return null;
    }

    @Override
    public boolean moveFirstHintOnWidgetIncorrect() {
        return true;
    }

    @Override
    public String getSubmitButtonText() {
        return "Check Answer";
    }
    
    @Override
    public WidgetStatusIndication indicateWidgetStatus() {
        return WidgetStatusIndication.DEFAULT;
    }
    
    @Override
    public boolean installCustomSteps() {
        return false;
    }

    @Override
    public void showWorkHasBeenSubmitted() {
    }
    
    @Override
    public void debugLogOut(String title, String message) {
        Log.debug(title, message);
    }
    
    @Override
    native public void scrollToBottomOfScrollPanel() /*-{
        $wnd.scrollTo(0,$doc.body.scrollHeight);
    }-*/;
}
