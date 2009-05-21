package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.data.InmhItemData;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplActivity extends ResourceViewerContainer {
	
	
	public Widget getResourcePanel(InmhItemData resource) {
	    SWFWidget swfWidget = new SWFWidget(resource.getFile(),500,400);
	    swfWidget.setStyleName("activity-widget");
	    Label title = new Label(resource.getTitle());
	    title.setStyleName("resource-viewer-impl-video-title");
	    add(title);
		addResource(swfWidget,resource.getTitle());
		return this;
	}
}
