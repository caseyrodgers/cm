package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;

import com.google.gwt.user.client.ui.Widget;

public interface ResourceViewer {

	/** display the resource in the proper viewer 
	 * 
	 * @param resource
	 */
	Widget getResourcePanel(InmhItemData resource);
	
	/** Do whatever is needed to clean up after removal
	 * 
	 */
	void removeResourcePanel();
	
	
	/** return unique style for this container */
	String getContainerStyleName();
	
	
	public InmhItemData getResourceItem();
	
}
