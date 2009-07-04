package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplActivity extends ResourceViewerContainer {
    
    public ResourceViewerImplActivity() {
        addStyleName("resource-viewer-impl-activity");
        setScrollMode(Scroll.AUTOY);
    }
	
	
	public Widget getResourcePanel(InmhItemData resource) {
	    SWFWidget swfWidget = new MySWFWidget(resource.getFile(),"520px","100%"); 
	    swfWidget.setStyleName("activity-widget");
	    
	    addResource(swfWidget,resource.getTitle());
 	   
	    return this;
	}
	
	public double getAllowedVerticalSpace() {
        return .90;
    }
}


class MySWFWidget extends SWFWidget {

    public MySWFWidget(String n, String h, String w) {
        super(n, h, w);
    }
    
    @Override
    protected void onAfterSWFInjection() {
        
        String flashHtml = toString();
        Log.info(ResourceViewerImplActivity.class.getName() + ": HTML + " + flashHtml);

    }
}