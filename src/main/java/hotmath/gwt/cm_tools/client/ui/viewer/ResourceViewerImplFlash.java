package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;

import com.google.gwt.user.client.ui.Widget;

abstract public class ResourceViewerImplFlash extends ResourceViewerContainer {
    
    public ResourceViewerImplFlash() {
        
    }

    abstract public Widget getResourcePanel(InmhItemData resource);
}
