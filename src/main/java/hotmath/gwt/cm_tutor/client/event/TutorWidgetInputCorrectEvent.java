package hotmath.gwt.cm_tutor.client.event;

import com.google.gwt.event.shared.GwtEvent;

/** The whiteboard has been updated by some event
 * 
 * @author casey
 *
 */
public class TutorWidgetInputCorrectEvent extends GwtEvent<TutorWidgetInputCorrectHandler> {

    public static Type<TutorWidgetInputCorrectHandler> TYPE = new Type<TutorWidgetInputCorrectHandler>();
	private boolean firstTime;

    @Override
    public Type<TutorWidgetInputCorrectHandler> getAssociatedType() {
        return TYPE;
    }

	public TutorWidgetInputCorrectEvent(boolean firstTime) {
		this.firstTime = firstTime;
	}

	@Override
	protected void dispatch(TutorWidgetInputCorrectHandler handler) {
		handler.widgetWasCorrect(this.firstTime);
	}
	
}


