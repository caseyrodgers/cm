package hotmath.gwt.cm_tutor.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface TutorWidgetInputCompleteHandler extends EventHandler {

    void tutorWidgetComplete(String pid, String inputValue, boolean correct);
    

}
