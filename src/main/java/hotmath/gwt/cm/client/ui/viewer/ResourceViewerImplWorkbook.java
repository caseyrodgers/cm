package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.data.InmhItemData;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplWorkbook extends ResourceViewerContainer implements ResourceViewer {
	
	public Widget getResourcePanel(InmhItemData resource) {
	    String url = "http://hotmath.kattare.com/help/gt/" + resource.getFile();
		String html = "<iframe frameborder='no' width='100%' height='400px' src='" + url + "'></iframe>";
		
		addResource(new HTML(html),resource.getTitle());
		return this;
	}
}
