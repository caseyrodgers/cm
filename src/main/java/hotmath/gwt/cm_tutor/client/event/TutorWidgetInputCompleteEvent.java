package hotmath.gwt.cm_tutor.client.event;

import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;

import com.google.gwt.event.shared.GwtEvent;

/** Fired when a tutor widget has had its value updated
 * 
 *  This is to allow different parts of the system to update
 *  their current view of this solution
 *  
 * @author casey
 *
 */
public class TutorWidgetInputCompleteEvent extends GwtEvent<TutorWidgetInputCompleteHandler> {
    
    public static Type<TutorWidgetInputCompleteHandler> TYPE = new Type<TutorWidgetInputCompleteHandler>();
    private SolutionInfo solutionInfo;
    private String inputValue;
    private boolean correct;

    public TutorWidgetInputCompleteEvent(SolutionInfo solutionInfo, String inputValue, boolean correct) {
        this.solutionInfo = solutionInfo;
        this.inputValue = inputValue;
        this.correct = correct;
    }

    public SolutionInfo getSolutionInfo() {
        return solutionInfo;
    }

    public void setSolutionInfo(SolutionInfo solutionInfo) {
        this.solutionInfo = solutionInfo;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    @Override
    public Type<TutorWidgetInputCompleteHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TutorWidgetInputCompleteHandler handler) {
        handler.tutorWidgetComplete(solutionInfo, inputValue, correct);
    }

}
