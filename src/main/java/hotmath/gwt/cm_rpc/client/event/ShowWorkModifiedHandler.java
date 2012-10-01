package hotmath.gwt.cm_rpc.client.event;

import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel;

import com.google.gwt.event.shared.EventHandler;

public interface ShowWorkModifiedHandler extends EventHandler {

    /** The whiteboard has been updated
     * 
     */
    void whiteboardUpdated(ShowWorkPanel showWorkPanel);
}
