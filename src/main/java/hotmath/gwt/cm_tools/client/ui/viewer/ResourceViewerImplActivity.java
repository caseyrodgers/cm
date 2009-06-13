package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplActivity extends ResourceViewerContainer {
    
    public ResourceViewerImplActivity() {
        addStyleName("resource-viewer-impl-activity");
        setScrollMode(Scroll.AUTOY);
    }
	
	
	public Widget getResourcePanel(InmhItemData resource) {
	    SWFWidget swfWidget = new SWFWidget(resource.getFile(),"520px","100%"); 
	    swfWidget.setStyleName("activity-widget");
 	    addResource(swfWidget,resource.getTitle());
 	   
	    return this;
	}
	
	public double getAllowedVerticalSpace() {
        return .90;
    }
}
