package hotmath.gwt.cm_tools.client.ui.viewer;



import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.Widget;

/** A resource viewer container with close button
 *  
 *   set to BorderLayout, so add(cmp, CENTER);
 * @author Casey
 * 
 * Provide a standard panel that provides nice rounded edges
 * and a common look and feel between all resource types.
 * 
 * using html struct like below
 *     div id='res_wrapper'
 *         div class='header'
 *             div class='left'
 *             div class='right'
 *         div class='body'
 *             div class='left'
 *             div class='right'
 *             div class='content'
 *         div class='footer'
 *             div class='left'
 *             div class='right'
 *             
 *             
 *
 */
abstract public class ResourceViewerContainer extends LayoutContainer  implements	ResourceViewer{
    
 
    
    /** The default amount of vertical space allowed to resource container
     * 
     */
    static public final double ALLOWED_VERTICAL_SPACE=.80;
    HorizontalPanel _header;
	public ResourceViewerContainer() {
		setStyleName("resource-viewer-container");

		setScrollMode(Scroll.AUTOY);
	}
	
	
	/** Hide header and footer from resource container
	 * 
	 * @TODO: move into  abstraction
	 */
	protected void setNoHeaderOrFooter() {
	    if(CmMainPanel.__lastInstance != null)
	        CmMainPanel.__lastInstance._mainContent.setNoHeaderOrFooter();
	}
	
	
	
	/** Add the resource to the center
	 * 
	 * @param w
	 */
	public void addResource(Widget w, String title) {
	    addResource(w, title, null);
	}

   public void addResource(Widget w, String title, String styleName) {
        add(w);
        
        if(CmMainPanel.__lastInstance != null)
            CmMainPanel.__lastInstance._mainContent.addResourceViewerHeader(title, styleName);
    }

	abstract public Widget getResourcePanel(InmhItemData resource);
	
	/** Should the height be set depending on viewport size
	 * 
	 * @return
	 */
	public boolean shouldSetResourceContinerHeight() {
	    return true;
	}
	
	
	/** Return the amount of vertical space to allow for the container
	 * 
	 * @return
	 */
	public double getAllowedVerticalSpace() {
	    return ResourceViewerContainer.ALLOWED_VERTICAL_SPACE;
	}
	
	/** Called when this object is resized 
	 * 
	 * @param height
	 */
	public void setResourceContinerHeight(int height) {
	    
	}

	public void removeResourcePanel() {
		// empty
	}
	
	
	public String getContainerStyleName() {
	    String s = getStyleName();
	    
	    s = s.trim();
	    // if multiple classes, return last
	    if(s != null && s.indexOf(" ") > -1) {
	        int x = s.indexOf(" ")+1;
	        s = s.substring(x);
	    }
	    return s;
	}
	
	
}
