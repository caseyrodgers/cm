package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.google.gwt.user.client.ui.Widget;


/** Holds the RPP as Flash Card widget
 * 
 * @author casey
 *
 */
public class ResourceViewerImplRppFlashCard extends ResourceViewerImplActivity  {
    

    @Override
    public Widget getResourcePanel() {
        Widget widget = super.getResourcePanel();
        
        getResourceItem().setViewed(true);
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_SOLUTIONS_COMPLETE,getResourceItem()));
        return widget;
    }
	
}
