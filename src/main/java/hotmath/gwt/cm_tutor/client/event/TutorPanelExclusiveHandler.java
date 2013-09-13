package hotmath.gwt.cm_tutor.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface TutorPanelExclusiveHandler extends EventHandler {

    /** The firing tutor is about to take control the of the tutor 
     *  This means any absolute pids, ie tutor_data, tutor_wrapper, etc.. 
     *  cannot be shared and must be removed from the dom.
     *  
     */
    void tutorNeedsExclusiveAccess();

}
