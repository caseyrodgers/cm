package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.view.ResourceViewer;
import hotmath.gwt.cm_mobile3.client.view.ResourceViewerImplDefault;
import hotmath.gwt.cm_mobile3.client.view.ResourceViewerImplLesson;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

public class ResourceFactory {
    
    static public ResourceViewer createViewer(InmhItemData resourceItem) {
        
        String type = resourceItem.getType();
        if(type.equals("review")) {
            return new ResourceViewerImplLesson(resourceItem);
        }
        else {
            return new ResourceViewerImplDefault(resourceItem);
        }
    }

}
