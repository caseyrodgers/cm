package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplFlashCard extends ResourceViewerContainer  {
	public Widget getResourcePanel(InmhItemData resource) {
        String html = "<h1>The Flash Card would be here</h1>";
        addResource(new HTML(html),resource.getTitle());
		return this;
	}
}
