package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplFlash;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/** Class to define a standard container for all resources.
 * 
 * Will provide basic services, such as removing/minimizing,etc.
 *  
 */
class CmResourcePanelContainer extends ContentPanel {
	ResourceViewerState viewerState = ResourceViewerState.OPTIMIZED;
	
	CmMainResourceContainer container;
	public CmResourcePanelContainer(CmMainResourceContainer container, final CmResourcePanel panel) {
		this.container = container;
		
		viewerState = ResourceViewerState.OPTIMIZED;
		
		setHeight( CmMainResourceContainer.getCalculatedHeight(container, panel));
		setWidth(panel.getOptimalWidth());
		setLayout(new FitLayout());
		add(panel.getResourcePanel());
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
		
		getHeader().addTool(new Button("Close", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				CmResourcePanelContainer.this.container.removeAll();
				CmResourcePanelContainer.this.container.layout();
			}
		}));
		
		
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
        CmResourcePanelContainer.this.add(panel.getResourcePanel());
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
