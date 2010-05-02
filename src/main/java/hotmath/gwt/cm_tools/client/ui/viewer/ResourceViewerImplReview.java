package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelContainer.ResourceViewerState;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetReviewHtmlAction;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplReview extends CmResourcePanelImplDefault {
    
    static final String STYLE_NAME = "resource-viewer-impl-review"; 
    public ResourceViewerImplReview() {
        addStyleName(STYLE_NAME);
    }
    
    @Override
    public String getContainerStyleName() {
        return STYLE_NAME;
    }
    
    
    public Widget getResourcePanel() {

        setScrollMode(Scroll.AUTOY);
        final InmhItemData resource=getResourceItem();
        
        final String file = "/hotmath_help/" + resource.getFile();
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                GetReviewHtmlAction action = new GetReviewHtmlAction(file, "/hotmath_help/topics");
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }
            public void oncapture(RpcData result) {
                String html = result.getDataAsString("html");
                
                addResource(new Html(html),resource.getTitle());
            }
        }.register();
        
        return this;
    }
    
    public Integer getOptimalWidth() {
        return 550;
    }
    
    @Override
    public ResourceViewerState getInitialMode() {
        return ResourceViewerState.OPTIMIZED;
    }
}
