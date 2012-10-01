package hotmath.gwt.cm_tutor.client.event;

import hotmath.gwt.cm_rpc.client.event.ShowWorkModifiedHandler;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel;

import com.google.gwt.event.shared.GwtEvent;

/** The whiteboard has been updated by some event
 * 
 * @author casey
 *
 */
public class ShowWorkModifiedEvent extends GwtEvent<ShowWorkModifiedHandler> {
    
    private ShowWorkPanel showWorkPanel;

    public ShowWorkModifiedEvent(ShowWorkPanel showWorkPanel) {
        this.showWorkPanel = showWorkPanel;
    }
    
    public static Type<ShowWorkModifiedHandler> TYPE = new Type<ShowWorkModifiedHandler>();

    @Override
    public Type<ShowWorkModifiedHandler> getAssociatedType() {
        return TYPE;
    }


    public ShowWorkPanel getShowWorkPanel() {
        return showWorkPanel;
    }

    public void setShowWorkPanel(ShowWorkPanel showWorkPanel) {
        this.showWorkPanel = showWorkPanel;
    }

    @Override
    protected void dispatch(ShowWorkModifiedHandler handler) {
        handler.whiteboardUpdated(showWorkPanel);
    }
}
