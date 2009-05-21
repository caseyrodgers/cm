package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.data.InmhItemData;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;

import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplActivity extends ResourceViewerContainer {
	
	
	public Widget getResourcePanel(InmhItemData resource) {
	    SWFWidget swfWidget = new SWFWidget(resource.getFile(),"500px","100%"); 
	    swfWidget.setStyleName("activity-widget");
		addResource(swfWidget,resource.getTitle());
		return this;
	}
}
