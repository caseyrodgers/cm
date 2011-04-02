package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelContainer.ResourceViewerState;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplActivity;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplVideo;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

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
	
	public CmResourcePanelContainer currentContainer;
	CmResourcePanel currentPanel;
	String currentTitle;

    public CmMainResourceContainer() {
        addStyleName("resource-container");
		addStyleName("main-resource-panel");
		
		if(UserInfo.getInstance().getBackgroundStyle() != null)
		    addStyleName(UserInfo.getInstance().getBackgroundStyle());
		
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
            
            ResourceViewerFactory.ResourceViewerFactory_Client client = new ResourceViewerFactory.ResourceViewerFactory_Client() {
				@Override
				public void onUnavailable() {
					CatchupMathTools.showAlert("Resource not available");
				}
				
				@Override
				public void onSuccess(ResourceViewerFactory instance) {
					try {
		                CmResourcePanel viewer = instance.create(resourceItem);
		                showResource(viewer,resourceItem.getTitle());
					}
					catch(Exception e) {
						CatchupMathTools.showAlert("Could not load resource: " + e.getLocalizedMessage());
					}
				}
			};
			ResourceViewerFactory.createAsync(client);
        } catch (Exception hme) {
            hme.printStackTrace();
            CatchupMathTools.showAlert("Error: " + hme.getMessage());
        }
    }
    
    /** show the last viewed resource, if any
     * 
     */
    public void showResource() {
    	if(currentPanel != null) {
    		showResource(currentPanel, currentTitle);
    	}
    }

    
    public void showResource(CmResourcePanel viewer, String title) {
        
        setLayout(new MyCenterLayout());

        currentContainer = new CmResourcePanelContainer(this,viewer);
        currentContainer.setHeading(title);
        add(currentContainer);
        
        layout();
        
        if(viewer instanceof ResourceViewerImplActivity 
                || viewer instanceof CmResourcePanelImplWithWhiteboard
                || viewer instanceof ResourceViewerImplVideo) {
        	/** 
        	 * do not slide in activity to avoid bugs:
        	 * - double load of flash objecs
        	 * - unsetting of external HTML radio buttons
        	 */
        }
        else {
        	currentContainer.el().slideIn(Direction.DOWN, FxConfig.NONE);
        }
        
        currentPanel = viewer;
        currentTitle = title;
        
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_RESOURCE_VIEWER_OPEN, viewer));  
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
	    int HEADER_FOOTER_GUTTER=10;
		if(panel.getOptimalHeight() == -1) {
			int h = container.getHeight()-HEADER_FOOTER_GUTTER;
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
