package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceViewerState;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

public interface CmResourcePanel {
    
    
    void addResource(Widget w, String title);
    
	
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
    public List<Widget> getContainerTools();
    
    
    
    /** Return the intial mode of the resource container for this
     * type of resource.  Either OPTIMIZED or MAXIMIZED
     * 
     * @return
     */
    public ResourceViewerState getInitialMode();


    /** Should the resource's panel content panel
     *  be transparent
     *  
     * @return
     */
    boolean shouldContainerBeTransparent();


    /** does this resource require forced UI refresh, even
     * overriding the normal forceUpdate.
     * 
     * THIS IS A HACK and is only required to work around a refresh bug with externally initialed code (outside of the ui refresh loop)
     * such as Flash videos .. where there is an external stack executing ... seems to be a problem only in GXT 3.
     * 
     * @return
     */
    boolean needForcedUiRefresh();
}
