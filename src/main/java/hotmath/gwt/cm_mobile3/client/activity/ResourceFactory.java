package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.view.ResourceViewer;
import hotmath.gwt.cm_mobile3.client.view.ResourceViewerImplDefault;
import hotmath.gwt.cm_mobile3.client.view.ResourceViewerImplLesson;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;

public class ResourceFactory {
    
    static public ResourceViewer createViewer(InmhItemData resourceItem) {
        
        CmResourceType type = resourceItem.getType();
        if(type == CmResourceType.REVIEW) {
            return new ResourceViewerImplLesson(resourceItem);
        }
        else {
            return new ResourceViewerImplDefault(resourceItem);
        }
    }

}
