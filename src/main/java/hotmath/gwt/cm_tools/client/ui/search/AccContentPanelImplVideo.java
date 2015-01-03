package hotmath.gwt.cm_tools.client.ui.search;

import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.search.TopicExplorer.TopicExplorerCallback;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplVideo;

import com.sencha.gxt.widget.core.client.event.BeforeHideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent.BeforeHideHandler;


public class AccContentPanelImplVideo extends AccContentPanel {
    
    public AccContentPanelImplVideo(PrescriptionSessionDataResource resource, TopicExplorerCallback callback) {
        super(resource, callback);
    }

    private ResourceViewerImplVideo _viewer;
    @Override
    protected void showResource(CmResourcePanel viewer, String title, boolean b) {
        super.showResource(viewer, title, b);
        
        ResourceViewerImplVideo _viewer = (ResourceViewerImplVideo)viewer;
        
        
        addBeforeHideHandler(new BeforeHideHandler() {
            @Override
            public void onBeforeHide(BeforeHideEvent event) {
                clear(); // make sure video stops
            }
        });
    }
    
}
