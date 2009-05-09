package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.ui.CmMainPanel;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
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
    HorizontalPanel _header;
	public ResourceViewerContainer() {
		setStyleName("resource-viewer-container");

		setScrollMode(Scroll.AUTO);
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
