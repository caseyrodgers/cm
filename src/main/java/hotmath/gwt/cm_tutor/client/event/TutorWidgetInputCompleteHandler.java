package hotmath.gwt.cm_tutor.client.event;

import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;

import com.google.gwt.event.shared.EventHandler;

public interface TutorWidgetInputCompleteHandler extends EventHandler {

    void tutorWidgetComplete(SolutionInfo solutionInfo, String inputValue, boolean correct);
    

}
