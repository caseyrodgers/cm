package hotmath.gwt.cm_tutor.client.view;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel.TutorCallback;

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
        return null;
    }
    

}
