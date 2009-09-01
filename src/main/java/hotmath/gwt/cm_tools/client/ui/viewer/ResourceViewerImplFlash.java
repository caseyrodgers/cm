package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;

import com.google.gwt.user.client.ui.Widget;

abstract public class ResourceViewerImplFlash extends ResourceViewerContainer {
    
    public ResourceViewerImplFlash() {
        
        EventBus.getInstance().addEventListener(new CmEventListener() {
            
            @Override
            public void handleEvent(CmEvent event) {
                CmMainPanel.__lastInstance._mainContent.removeAll();
            }
            
            @Override
            public String getEventOfInterest() {
                // TODO Auto-generated method stub
                return EventBus.EVENT_TYPE_MODAL_WINDOW_OPEN;
            }
        });
      
    }

    abstract public Widget getResourcePanel(InmhItemData resource);
}
