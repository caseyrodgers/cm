package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelContainer.ResourceViewerState;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;

import com.extjs.gxt.ui.client.Style.Direction;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.google.gwt.user.client.Timer;


/** Main resource panel that contains the actual resource viewers
 * for each resource type.  It provides the ability to maximize/optimize
 * the content area.
 * 
 * 
 * @author casey
 *
 */
public class CmMainResourceContainer extends LayoutContainer {
	
	CmResourcePanelContainer currentContainer;
	CmResourcePanel currentPanel;

    public CmMainResourceContainer() {
		addStyleName("main-resource-panel");
		addStyleName("resource-container-bike1");
		
        
        setScrollMode(Scroll.AUTO);
	}

    
	
	/** Add a new resource panel to the main resource container.
	 * 
	 * There is only one active CmResourcePanel active at any one time.
	 * 
	 * Each CmResourcePanel can be in one of two states:
	 * 
	 * 1. default state:  static sizing define by the resource that is
	 * optimized for the given content.
	 * 
	 * 2. (optional) Maximized, which will maximize the content within 
	 * the entire usable space of the resource content area.
	 * 
	 *  
	 * 
	 * @param panel
	 */

    
    /** Display a single resource, remove any previous
     * 
     * Do not  track its view
     * 
     * @param resourceItem
     */
    public void showResource(final InmhItemData resourceItem) {
        try {
            removeAll();
            
            CmResourcePanel viewer = ResourceViewerFactory.create(resourceItem);
            setLayout(new MyCenterLayout());
            
            /** create a new resource container to encapsulate the resource
             * 
             */
            if(viewer.showContainer()) {
                currentContainer = new CmResourcePanelContainer(this,viewer);
                currentContainer.setHeading(resourceItem.getTitle());
                add(currentContainer);
            }
            else {
                currentContainer = new CmResourcePanelContainer(this,viewer);
                currentContainer.setHeading(resourceItem.getTitle());
                add(currentContainer);
            }
            
            layout();
            
            currentPanel = viewer;
            
            EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_RESOURCE_VIEWER_OPEN, viewer));     
        } catch (Exception hme) {
            hme.printStackTrace();
            CatchupMathTools.showAlert("Error: " + hme.getMessage());
        }
    }

    
    
    public CmResourcePanel getCurrentPanel() {
        return currentPanel;
    }


    public void setCurrentPanel(CmResourcePanel currentPanel) {
        this.currentPanel = currentPanel;
    }

    
    
	/** Remove this resource from display
	 * 
	 */
	public void removeResource() {
	    currentContainer = null;
	    currentPanel = null;
	    removeAll();
	    layout();
	}
	
	
	/** Fired when the window container has been resized
	 *  manually.
	 */
	boolean isFiring;
    public void fireWindowResized() {
    	if(isFiring)
    		return;
    	
    	if(currentPanel != null && currentContainer.getViewerState() == ResourceViewerState.OPTIMIZED && currentPanel.getOptimalHeight() == -1) {
        	isFiring = true;
        	/** run in timer to allow x/y to be set first
        	 * 
        	 * @TODO: why isn't x/y already correct?
        	 */
    		Timer t = new Timer() {
    			public void run() {
    				try {
    					setLayout(new MyCenterLayout());
    					if(currentContainer != null) {
    					    currentContainer.setHeight(CmMainResourceContainer.getCalculatedHeight(CmMainResourceContainer.this, currentPanel));
    					}
    					layout();
    				}
    				finally {
    					isFiring = false;
    				}
    			}
    		};
    		t.schedule(1);
    	}
    	else if(currentPanel != null && currentContainer.getViewerState() == ResourceViewerState.MAXIMIZED) {
    	    /** Window changed while resource is MAXIMIZED
    	     * 
    	     */
//    	    CmMainResourceContainer.this.currentContainer.removeAll();
//    	    CmMainResourceContainer.this.currentContainer.setLayout(new FitLayout());
//    	    CmMainResourceContainer.this.currentContainer.add(new Button("Test"));
//    	    CmMainResourceContainer.this.currentContainer.layout();
    	}
    }
    
    
    
	
    /** Calculate the proper height for this widget.  
     * 
     *  If OptimialHeight is set to -1, then expand to 
     *  available space.  Otherwise, return the static height.  
     *  
     *  Only shrink down to getMinHeight on panel.
     *  
     * @param container
     * @param panel
     * @return
     */
	static protected Integer getCalculatedHeight(CmMainResourceContainer container, CmResourcePanel panel) {
		if(panel.getOptimalHeight() == -1) {
			int h = container.getHeight()-50;
			if(h < panel.getMinHeight()) {
				h = panel.getMinHeight();
			}
			return h;
		}
		else {
			return panel.getOptimalHeight();
		}
	}    
}


/** Modify the CenterLayout to not allow components to
 *  to be positioned out of view.  This causes the head portion
 *  to be unreachable.
 *  
 * @author casey
 *
 */
class MyCenterLayout extends CenterLayout {

	@Override
	protected void onLayout(Container<?> container, El target) {
		super.onLayout(container, target);
		
		Component item = container.getItem(0);
		if(container.getItemCount() > 0) {
			int top = item.getAbsoluteTop();
	
			if(top < 0)
				setPosition(item,item.el().getLeft() , 0);
		}
	}
}
