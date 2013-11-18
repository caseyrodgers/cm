package hotmath.gwt.cm_tutor.client.view;

import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public interface TutorCallback {

    static public enum WidgetStatusIndication {
        /** do normal thing */
        DEFAULT,
        
        /** just indicate value was saved, not correct/false */
        INDICATE_SUBMIT_ONLY, 
        
        /** do not indicate, it is taken care of at a higher level
         * 
         */
        NONE
    }


    /**
     * When the NewProblem button is pressed
     * 
     * @param problemNumber
     */
    void onNewProblem(int problemNumber);

    /**
     * Return the text that is shown in the check widget save button
     * 
     * @return
     */
    String getSubmitButtonText();

    /**
     * Return action that will define the tutor widget action save
     * 
     * @param value
     * @param yesNo
     * @return
     */
    Action<UserTutorWidgetStats> getSaveTutorWidgetCompleteAction(String value, boolean yesNo);

    /**
     * Return the action used to save this context, null if no context save
     * should happen.
     * 
     * @param variablesJson
     * @param pid
     * @param problemNumber
     * @return
     */
    Action<RpcData> getSaveSolutionContextAction(String variablesJson, String pid, int problemNumber);

    /**
     * Called when the tutor widget input is complete
     * 
     * @param inputValue
     * @param correct
     */
    void tutorWidgetComplete(String inputValue, boolean correct);

    /**
     * Called if a widget value change was detected when such change is not
     * allowed.
     */
    void tutorWidgetCompleteDenied(String inputValue, boolean correct);

    /**
     * Call after the last step has been viewed in a solution
     * 
     * @param value
     */
    void solutionHasBeenViewed(String value);

    /**
     * Show the associated whiteboard for this tutor
     * 
     */
    void showWhiteboard();

    /**
     * Show the standard tutor widget info mesaage on the tutor be shown when
     * the correct answer was entered?
     * 
     * @return
     */
    boolean showTutorWidgetInfoOnCorrect();

    /**
     * Called when the solution has been fully initialized
     * 
     */
    void solutionHasBeenInitialized();

    /**
     * Should the first hint be shown on incorrect widget value
     * 
     * @return
     */
    boolean moveFirstHintOnWidgetIncorrect();

    /**
     * What should be shown when widget value is 'saved/checked'
     * 
     * 
     * @return
     */
    WidgetStatusIndication indicateWidgetStatus();

    
    /** Should custom, dynamicall generate steps be used for solution
     * 
     * NOTE: for now there is only the MC type problems showing the correct answer
     * 
     * @return
     */
    boolean installCustomSteps();
    
    
    /** called when the Submit Whiteboard button has been pressed
     * 
     */
    void showWorkHasBeenSubmitted();

    /** Implementation specific admin/debug output
     * 
     * @param string
     * @param string2
     */
    void debugLogOut(String string, String string2);
}
