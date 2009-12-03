package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;

import java.util.List;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/** Class to define a standard container for all resources.
 * 
 * Will provide basic services, such as removing/minimizing,etc.
 * 
 * Restore current setting when new resource is added, unless resource
 * does not allow said state.  In this case, set to OPTIMAL.
 *  
 */
public class CmResourcePanelContainer extends ContentPanel {
	ResourceViewerState viewerState = null;
	
	CmMainResourceContainer container;
	Button _maximize;
	
	static String EXPAND_TEXT = "Expand";
	static String SHRINK_TEXT = "Shrink";
	
	/** Keep a static representation of the current display state
	 * 
	 */
	static ResourceViewerState __currentDisplayState = ResourceViewerState.OPTIMIZED;
	
	
	/** Create a new Resource Container with named container as the outer container and panel
	 *  as the child.
	 *  
	 * @param container
	 * @param panel
	 */
	public CmResourcePanelContainer(CmMainResourceContainer container, final CmResourcePanel panel) {
		this.container = container;
		
		
		addStyleName("cm-resource-viewer-container");
		addStyleName(panel.getContainerStyleName());

		setLayout(new FitLayout());
		/** add any resource specific tools to resource container header 
		 * 
		 */
		List<Component> tools = panel.getContainerTools();
		if(tools != null) {
		    for(int i=0,t=tools.size();i<t;i++) {
		        getHeader().addTool(tools.get(i));
		    }
		}
		
		
		/** Maximize is an optional feature
		 * 
		 */
		if(panel.allowMaximize()) {
			_maximize = new Button(EXPAND_TEXT, new SelectionListener<ButtonEvent>() {
				public void componentSelected(ButtonEvent ce) {
				    closeResource(ce, panel);
				}
			});
			getHeader().addTool(_maximize);
		}
		
		/** Close is optional
		 * 
		 */
	    if(panel.allowClose()) {
            getHeader().addTool(new Button("Close", new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    panel.removeResourcePanel();
                    EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_RESOURCE_VIEWER_CLOSE, panel));
                    
                    CmResourcePanelContainer.this.container.removeAll();
                    CmResourcePanelContainer.this.container.layout();
                }
            }));
        }		
		
		
		if(!panel.showContainer()) {
			CmResourcePanelContainer.this.setStyleAttribute("background", "transparent");
			CmResourcePanelContainer.this.setBorders(false);
			CmResourcePanelContainer.this.setBodyBorder(false);
			CmResourcePanelContainer.this.layout();
		}

		/** If user has already maximized resource viewer, try to open all resources maximized
		 * 
		 */
		if(__currentDisplayState != null && (__currentDisplayState == ResourceViewerState.MAXIMIZED) && panel.allowMaximize()) {
		    setMaximize(panel);
		}
		else if(panel.getInitialMode() == ResourceViewerState.OPTIMIZED) {
            setOptimized(panel);
        }
        else {          
            setMaximize(panel);
        }

	}
	
	public Button getMaximizeButton() {
	    return _maximize;
	}
	
	/** Configure container in OPTIMIZED configuration
	 * 
	 * @param panel
	 */
	public void setOptimized(CmResourcePanel panel) {
	    
       if(viewerState == ResourceViewerState.OPTIMIZED)
            return;
	    
        // minimize the resource area
        CmResourcePanelContainer.this.container.setLayout(new CenterLayout());
        CmResourcePanelContainer.this.setWidth(panel.getOptimalWidth());
        CmResourcePanelContainer.this.setHeight(CmMainResourceContainer.getCalculatedHeight(CmResourcePanelContainer.this.container, panel));
        

        viewerState = ResourceViewerState.OPTIMIZED;
        
        if(_maximize != null)
            _maximize.setText(EXPAND_TEXT);
        
        
        /** Reset the panel widget
         * 
         */
        resetPanelWidget(panel);
        
        CmResourcePanelContainer.this.layout();
	}
	
	public boolean isMaximized() {
	    return CmResourcePanelContainer.this.container.getLayout() instanceof FitLayout;
	}
	
	public void setMaximize(CmResourcePanel panel) {
	    
	    if(viewerState == ResourceViewerState.MAXIMIZED)
	        return;
	    
	    
	    // maximize the resource area
        //
        CmResourcePanelContainer.this.container.setLayout(new FitLayout());
        viewerState = ResourceViewerState.MAXIMIZED;	    
        
        if(_maximize != null)
            _maximize.setText(SHRINK_TEXT);
        
        
        /** Reset the panel widget
         * 
         */
        resetPanelWidget(panel);
        
        CmResourcePanelContainer.this.layout();
        
	}
	
	private void closeResource(ButtonEvent ce, CmResourcePanel panel) {
        boolean isMax = ce.getButton().getText().equals(EXPAND_TEXT);
        if(isMax) {
           setMaximize(panel);
        }
        else {
            setOptimized(panel);
        }
	}
	
	/** Clean and add the resource panel, forcing a new layout
	 * 
	 * @param panel
	 */
	public void resetPanelWidget(CmResourcePanel panel) {
        CmResourcePanelContainer.this.removeAll();
        LayoutContainer lc =(LayoutContainer) panel.getResourcePanel();
        
        CmResourcePanelContainer.this.add(lc);
        CmResourcePanelContainer.this.container.layout();
        
        
        
        /** If resource is 'forced' to open maximum, do not allow it affect
         *  the viewer for other resources.
         */
        if(panel.getInitialMode() == ResourceViewerState.MAXIMIZED && viewerState == ResourceViewerState.MAXIMIZED) {
            // skip tracking it
        }
        /** IF resource is 'forced' to open optimized, do not allow it affect ..
         * 
         */
        else if(panel.getInitialMode() == ResourceViewerState.OPTIMIZED && panel.allowMaximize() == false) {
   	        // skip
        }
        else {
            __currentDisplayState = viewerState;
        }
	}
	
	
	public ResourceViewerState getViewerState() {
		return viewerState;
	}

	public void setViewerState(ResourceViewerState viewerState) {
		this.viewerState = viewerState;
	}

	public enum ResourceViewerState{OPTIMIZED,MAXIMIZED};
}
