package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.ui.Widget;

public interface CmResourcePanel {
	
	/** Does this panel allow maximizing the content?
	 * 
	 */
	Boolean allowMaximize();
	
	
	
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
}
