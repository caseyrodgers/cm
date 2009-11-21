package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelContainer.ResourceViewerState;
import hotmath.gwt.shared.client.rpc.action.GetReviewHtmlAction;
import hotmath.gwt.shared.client.util.RpcData;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
        
        String file = "/hotmath_help/" + resource.getFile();
             
        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        GetReviewHtmlAction action = new GetReviewHtmlAction(file, "/hotmath_help/topics");
        s.execute(action, new AsyncCallback<RpcData>() {
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }
            public void onSuccess(RpcData result) {
                String html = result.getDataAsString("html");
                
                addResource(new Html(html),resource.getTitle());
            }
        });
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
