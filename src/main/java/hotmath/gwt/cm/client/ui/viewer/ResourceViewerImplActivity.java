package hotmath.gwt.cm.client.ui.viewer;

import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;
import hotmath.gwt.cm.client.data.InmhItemData;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplActivity extends ResourceViewerContainer implements ResourceViewer {
	
	
	public Widget getResourcePanel(InmhItemData resource) {
	    SWFWidget swfWidget = new SWFWidget(resource.getFile(),500,400);
	    swfWidget.setStyleName("activity-widget");
	    setHeight(400);
	    Label title = new Label(resource.getTitle());
	    title.setStyleName("resource-viewer-impl-video-title");
	    add(title);
		addResource(swfWidget,resource.getTitle());
		return this;
	}
	
	public Widget _getResourcePanel(InmhItemData resource) {
	    String url = "http://hotmath.kattare.com/util/hm_flash_movie.jsp" +
	                 "?title=" + resource.getTitle() +
	                 "&movie=" + resource.getFile(); 
		String html = "<iframe frameborder='no' width='100%' height='400px' src='" + url + "'></iframe>";
		
		
		addResource(new HTML(html),resource.getTitle());
		
		return this;
	}
}
