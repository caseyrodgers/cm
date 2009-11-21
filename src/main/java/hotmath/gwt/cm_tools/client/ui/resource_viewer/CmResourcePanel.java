package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelContainer.ResourceViewerState;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.ui.Widget;

public interface CmResourcePanel {
	
	/** Does this panel allow maximizing the content?
	 * 
	 */
	Boolean allowMaximize();
	
	
	/** Can the user close this viewer ?
	 * 
	 * @return
	 */
	Boolean allowClose();
	
	/** Return the width that the content
	 * has been optimized for.
	 * 
	 * @return
	 */
	Integer getOptimalWidth();
	
	
	
	/** Return the height that is optimized
	 * for this content.
	 * 
	 * @return
	 */
	Integer getOptimalHeight();
	
	
	
	
	/** Should this Resource Viewer have a standard container visible?
	 * 
	 * @return
	 */
	public Boolean showContainer();
	
	
	
	
	/** Return the minimal height for this Panel
	 * 
	 * @return
	 */
	public Integer getMinHeight();
	
	
	
	
    /** display the resource in the proper viewer 
     * 
     * @param resource
     */
    Widget getResourcePanel();
    
    
    
    /** Do whatever is needed to clean up after removal
     * 
     */
    void removeResourcePanel();
    
    
    /** return unique style for this container */
    String getContainerStyleName();
    
    
    /** Return the Resource item that was used to initialize this viewer
     * 
     * @return
     */
    public InmhItemData getResourceItem();	
    
    
    /** Set the resource item this viewer is for
     * 
     */
    public void setResourceItem(InmhItemData item);
    
 
    
    /** Return any tools that should be available to users of this resource.
     * 
     * 
     * @return
     */
    public List<Component> getContainerTools();
    
    
    
    /** Return the intial mode of the resource container for this
     * type of resource.  Either OPTIMIZED or MAXIMIZED
     * 
     * @return
     */
    public ResourceViewerState getInitialMode();
}
