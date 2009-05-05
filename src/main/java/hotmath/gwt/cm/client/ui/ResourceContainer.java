package hotmath.gwt.cm.client.ui;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;


/** Main resource viewer area
 * 
 */
public class ResourceContainer extends LayoutContainer {

	
	public ResourceContainer() {
		// setTitle("Resource Viewer");
		setStyleName("resource-container");
		
		setLayout(new FitLayout());
	}
}