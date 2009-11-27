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
 */
public class CmResourcePanelContainer extends ContentPanel {
	ResourceViewerState viewerState = ResourceViewerState.OPTIMIZED;
	
	CmMainResourceContainer container;
	Button _maximize;
	
	public CmResourcePanelContainer(CmMainResourceContainer container, final CmResourcePanel panel) {
		this.container = container;
		
		
		addStyleName("cm-resource-viewer-container");
		addStyleName(panel.getContainerStyleName());
		
		String modeButton=null;
		if(panel.getInitialMode() == ResourceViewerState.OPTIMIZED) {
    		viewerState = ResourceViewerState.OPTIMIZED;
    		setHeight( CmMainResourceContainer.getCalculatedHeight(container, panel));
    		setWidth(panel.getOptimalWidth());
    		modeButton = "Maximize";
		}
		else {
            viewerState = ResourceViewerState.MAXIMIZED;
            CmResourcePanelContainer.this.container.setLayout(new FitLayout());
            modeButton = "Minimal";
		}
		
		setLayout(new FitLayout());
		add(panel.getResourcePanel());
		
		
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
			_maximize = new Button(modeButton, new SelectionListener<ButtonEvent>() {
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
		
	}
	
	
	public void setOptimized(CmResourcePanel panel) {
	    
       if(viewerState == ResourceViewerState.OPTIMIZED)
            return;

	    
        // minimize the resource area
        CmResourcePanelContainer.this.container.setLayout(new CenterLayout());
        CmResourcePanelContainer.this.setWidth(panel.getOptimalWidth());
        CmResourcePanelContainer.this.setHeight(CmMainResourceContainer.getCalculatedHeight(CmResourcePanelContainer.this.container, panel));
        

        viewerState = ResourceViewerState.OPTIMIZED;
        
        
        _maximize.setText("Maximize");
        
        
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
        
        _maximize.setText("Minimal");
        
        
        /** Reset the panel widget
         * 
         */
        resetPanelWidget(panel);
        
        CmResourcePanelContainer.this.layout();        
	}
	
	private void closeResource(ButtonEvent ce, CmResourcePanel panel) {
        boolean isMax = ce.getButton().getText().equals("Maximize");
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
	}
	
	
	public ResourceViewerState getViewerState() {
		return viewerState;
	}

	public void setViewerState(ResourceViewerState viewerState) {
		this.viewerState = viewerState;
	}

	public enum ResourceViewerState{OPTIMIZED,MAXIMIZED};
}
