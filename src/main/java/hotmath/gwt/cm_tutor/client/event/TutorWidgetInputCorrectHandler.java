package hotmath.gwt.cm_tutor.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface TutorWidgetInputCorrectHandler extends EventHandler {
	void widgetWasCorrect(boolean firstTime);
}
