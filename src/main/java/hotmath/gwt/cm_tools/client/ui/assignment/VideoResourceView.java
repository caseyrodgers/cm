package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplVideo;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;


public class VideoResourceView extends ContentPanel implements ResourceView  {

    private PrescriptionSessionDataResource resource;
    private ProblemDto problem;
    TextButton _spanishButton;
    ResourceViewerImplVideo video;
    
    public VideoResourceView(PrescriptionSessionDataResource resource, ProblemDto problem) {
        
        this.resource = resource;
        this.problem = problem;
        
        addStyleName("video-resource-view");
        
        video = new ResourceViewerImplVideo();
    }

    @Override
    public String getResourceTitle() {
        return this.resource.getItems().get(0).getTitle();
    }
    
    @Override
    public Widget asWidget() {
        video.setResourceItem(resource.getItems().get(0));
        return video.getResourcePanel();
    }
}

