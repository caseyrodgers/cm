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
	    SWFWidget swfWidget = new SWFWidget(resource.getFile(),"100%","650px");
	    swfWidget.setStyleName("activity-widget");
	    addResource(swfWidget,resource.getTitle());
 	   
	    return this;
	}
	
	public double getAllowedVerticalSpace() {
        return .90;
    }
}


/** Special SWFWidget to spit out the contents of the Flash widget
 * 
 * @author casey
 *
 */
class MySWFWidget extends SWFWidget {

    public MySWFWidget(String n, String w, String h) {
        super(n, w, h);
    }
    
    @Override
    protected void onAfterSWFInjection() {
        
        String flashHtml = toString();
        Log.info(ResourceViewerImplActivity.class.getName() + ": HTML + " + flashHtml);

    }
}