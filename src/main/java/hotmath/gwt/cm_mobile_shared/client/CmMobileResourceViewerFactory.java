package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

public class CmMobileResourceViewerFactory {
    static public CmMobileResourceViewer createViewer(InmhItemData item) {
        CmMobileResourceViewer viewer=null;
        
        if("review".equals(item.getType())) {
            viewer = new CmResourceViewerImplLesson();
        }
        else if("practice".equals(item.getType())
                || "cmextra".equals(item.getType())) {
            viewer = new CmResourceViewerImplSolution();
        }
        else if("video".equals(item.getType())) {
            viewer = new CmResourceViewerImplVideo();
        }
        else 
            viewer = new CmResourceViewerImplDefault();
        
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_RES_VIEW_LOADED,viewer));
        
        return viewer;
    }
}
