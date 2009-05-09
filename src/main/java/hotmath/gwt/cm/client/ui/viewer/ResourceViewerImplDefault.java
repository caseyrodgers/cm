package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.data.InmhItemData;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplDefault extends ResourceViewerContainer  {
	public Widget getResourcePanel(InmhItemData resource) {

	    String url = resource.getFile();
        String html = "<iframe frameborder='no' width='100%' height='400px' src='" + url + "'></iframe>";
        
        addResource(new HTML(html),resource.getTitle());
        
		return this;
	}
}
