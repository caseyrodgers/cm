package hotmath.gwt.cm_tutor.client.event;

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
    String pid;
    private String inputValue;
    private boolean correct;

    public TutorWidgetInputCompleteEvent(String pid, String inputValue, boolean correct) {
        this.pid = pid;
        this.inputValue = inputValue;
        this.correct = correct;
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
        handler.tutorWidgetComplete(pid, inputValue, correct);
    }

}
