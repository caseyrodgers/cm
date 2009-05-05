package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.ui.CmMainPanel;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplVideo  extends ResourceViewerContainer implements ResourceViewer {
	
	public ResourceViewerImplVideo() {
		addStyleName("resource-viewer-impl-video");
	}
	
	public Widget getResourcePanel(InmhItemData resource) {
	    String videoPath = "/help/flvs/tw/" + resource.getFile() + ".flv";
	    String url = "http://hotmath.kattare.com/util/hm_flash_movie.jsp" +
	                 "?title=" + resource.getTitle() +
	                 "&movie=/util/flvplayer/flvplayer.swf?file=" + videoPath; 
	    	
	    SWFWidget swfWidget = new SWFWidget("flvplayer.swf?file=" + videoPath,320,240);
	    swfWidget.setStyleName("flvplayer-widget");
	    swfWidget.addParam("file", videoPath);
	    // add(swfWidget);
	    setHeight(290);
	    Label title = new Label(resource.getTitle());
	    title.setStyleName("resource-viewer-impl-video-title");
	    add(title);
		addResource(swfWidget,resource.getTitle());
		return this;
	}
	
	public void removeResourcePanel() {
		
	}
}
