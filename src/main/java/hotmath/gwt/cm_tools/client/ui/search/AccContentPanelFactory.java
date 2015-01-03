package hotmath.gwt.cm_tools.client.ui.search;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_tools.client.ui.search.TopicExplorer.TopicExplorerCallback;

public class AccContentPanelFactory {

    public static AccContentPanel create(PrescriptionSessionDataResource resource, TopicExplorerCallback callback) {
        
        CmResourceType type = resource.getType();
        switch(type) {
            case REVIEW:
                return new AccContentPanelImplLesson(resource, callback);
                
            case VIDEO:
                return new AccContentPanelImplVideo(resource, callback);
                
                
                default:
                    return new AccContentPanel(resource, callback);
            
        }
    }

}
