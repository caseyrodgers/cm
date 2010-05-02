package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplWorkbook extends CmResourcePanelImplDefault {
	
	public Widget getResourcePanel(InmhItemData resource) {
	    
	    setResourceItem(resource);

	    String url = "http://hotmath.kattare.com/help/gt/" + resource.getFile();
		String html = "<iframe frameborder='no' width='100%' height='400px' src='" + url + "'></iframe>";
		
		addResource(new HTML(html),resource.getTitle());
		return this;
	}
}
