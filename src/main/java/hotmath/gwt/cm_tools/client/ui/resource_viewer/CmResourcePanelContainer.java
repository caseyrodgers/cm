package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import java.util.List;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.FxConfig;
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
	public CmResourcePanelContainer(CmMainResourceContainer container, final CmResourcePanel panel) {
		this.container = container;
		
		viewerState = ResourceViewerState.OPTIMIZED;
		
		setHeight( CmMainResourceContainer.getCalculatedHeight(container, panel));
		setWidth(panel.getOptimalWidth());
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
			Button maximize = new Button("Maximize", new SelectionListener<ButtonEvent>() {
				public void componentSelected(ButtonEvent ce) {
					boolean isMax = ce.getButton().getText().equals("Maximize");
					if(isMax) {
						// maximize the resource area
						//
					    CmResourcePanelContainer.this.container.setLayout(new FitLayout());
					    ce.getButton().setText("Minimize");
					    viewerState = ResourceViewerState.MAXIMIZED;
					}
					else {
						// minimize the resource area
					    CmResourcePanelContainer.this.container.setLayout(new CenterLayout());
					    CmResourcePanelContainer.this.setWidth(panel.getOptimalWidth());
					    CmResourcePanelContainer.this.setHeight(CmMainResourceContainer.getCalculatedHeight(CmResourcePanelContainer.this.container, panel));
					    
					    ce.getButton().setText("Maximize");
					    viewerState = ResourceViewerState.OPTIMIZED;
					}
					
                    
                    /** Reset the panel widget
                     * 
                     */
					resetPanelWidget(panel);
					
					CmResourcePanelContainer.this.layout();
				}
			});
			getHeader().addTool(maximize);
		}
		
		if(panel.allowClose()) {
    		getHeader().addTool(new Button("Close", new SelectionListener<ButtonEvent>() {
    			public void componentSelected(ButtonEvent ce) {
    			    
    			    CmResourcePanelContainer.this.el().fadeOut(FxConfig.NONE);
    			    
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
	
	
	/** Clean and add the resource panel, forcing a new layout
	 * 
	 * @param panel
	 */
	private void resetPanelWidget(CmResourcePanel panel) {
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

	enum ResourceViewerState{OPTIMIZED,MAXIMIZED};
}
