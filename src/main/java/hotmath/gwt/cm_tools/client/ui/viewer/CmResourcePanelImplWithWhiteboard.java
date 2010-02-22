package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelContainer.ResourceViewerState;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Widget;


/** Provides a container that has a whiteboard and tutor section
 * 
 * @author casey
 *
 */
public abstract class CmResourcePanelImplWithWhiteboard extends CmResourcePanelImplDefault {
    
    public enum DisplayMode{TUTOR,WHITEBOARD};
    
    DisplayMode _displayMode;
    static DisplayMode __lastDisplayMode = null;
    
    public CmResourcePanelImplWithWhiteboard() {

        _displayMode = getInitialWhiteboardDisplay();
    }

    /** Should the whiteboard be shown
     */
    protected DisplayMode getInitialWhiteboardDisplay() {
        if(UserInfo.getInstance().isShowWorkRequired() || __lastDisplayMode == DisplayMode.WHITEBOARD) {
            return DisplayMode.WHITEBOARD;
        }
        else {
            return DisplayMode.TUTOR;
        }        
    }


    @Override
    public Boolean allowMaximize() {
        return true;
    }
    
    /** If display is in WHITEBOARD mode then
     * make sure to expand display.  If in 
     * TUTOR mode, then initially show in OPTIMIZED mode.
     */
    @Override
    public ResourceViewerState getInitialMode() {
        if(_displayMode == DisplayMode.WHITEBOARD) {
            return ResourceViewerState.MAXIMIZED;
        }
        else {
            return ResourceViewerState.OPTIMIZED;
        }
    }
    
    Button _showWorkBtn;
    public List<Component> getContainerTools() {
        
        String btnText = _displayMode == DisplayMode.WHITEBOARD?"Hide Whiteboard":"Show Whiteboard";
        _showWorkBtn = new Button(btnText,new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                
                if(_showWorkBtn.getText().indexOf("Show") > -1) {
                    setDisplayMode(DisplayMode.WHITEBOARD);
                }
                else {
                    if(_wasMaxBeforeWhiteboard) {
                        CmMainPanel.__lastInstance._mainContent.currentContainer.setMaximize(CmResourcePanelImplWithWhiteboard.this);
                    }
                    else {
                        CmMainPanel.__lastInstance._mainContent.currentContainer.setOptimized(CmResourcePanelImplWithWhiteboard.this);
                    }
                    
                    setDisplayMode(DisplayMode.TUTOR);
                }
            }
        });
        Component[] btn = {_showWorkBtn};
        return java.util.Arrays.asList(btn);
    }
    

    
    boolean _wasMaxBeforeWhiteboard;
    
    
    /** 
     * 
     * Add the main tutor/quiz panel
     * 
     */
    abstract public Widget getTutorDisplay();
    
    /** swp.setupForPid(this.pid);
     * 
     *  prepare the showwork/whiteboard for display
     *  
     * @param p
     */
    abstract public void setupShowWorkPanel(ShowWorkPanel whiteboardPanel);
    
    
    /** Called after the whiteboard has been initialized
     * 
     */
    public void whiteboardIsReady() {
    	/** empty */
    }
    
    /** Central method to setup either tutor or whiteboard
     *  display modes. 
     * 
     * @param displayMode
     */
    protected void setDisplayMode(DisplayMode displayMode) {
    	
        removeAll();
        if(displayMode == DisplayMode.TUTOR) {
        	if(_showWorkBtn != null)
                _showWorkBtn.setText("Show Whiteboard");
            add(getTutorDisplay());
            
            CmMainPanel.__lastInstance._mainContent.currentContainer.getMaximizeButton().setEnabled(true);
        }
        else {
        	if(CmMainPanel.__lastInstance._mainContent.currentContainer != null) {
        		_wasMaxBeforeWhiteboard = CmMainPanel.__lastInstance._mainContent.currentContainer.isMaximized();
                CmMainPanel.__lastInstance._mainContent.currentContainer.setMaximize(this,_wasMaxBeforeWhiteboard);
                CmMainPanel.__lastInstance._mainContent.currentContainer.getMaximizeButton().setEnabled(false);
        	}
            
            ShowWorkPanel swp = new ShowWorkPanel(new CmAsyncRequestImplDefault() {
				@Override
				public void requestComplete(String requestData) {
					whiteboardIsReady();
				}
			});
            setupShowWorkPanel(swp);

            LayoutContainer lcTutor = new LayoutContainer(new FitLayout());
            lcTutor.setScrollMode(Scroll.AUTO);
            lcTutor.add(getTutorDisplay());
            
            Object o = getTutorDisplay();
            
            LayoutContainer lcMain = new LayoutContainer(new BorderLayout());
            lcMain.addStyleName("whiteboard-container");
            lcMain.setStyleAttribute("background", "transparent");
            lcMain.setScrollMode(Scroll.NONE);

            BorderLayoutData bld = new BorderLayoutData(LayoutRegion.WEST, .50f);
            bld.setSplit(false);
            lcMain.add(lcTutor,bld);
            

            bld = new BorderLayoutData(LayoutRegion.EAST, .50f);
            bld.setSplit(false);
            lcMain.add(swp, bld);

        
            if(_showWorkBtn != null)
               _showWorkBtn.setText("Hide Whiteboard");
            
            add(lcMain);
        }
        
        _displayMode = displayMode;
        __lastDisplayMode = _displayMode;
        
        
        layout();
    }
    
    @Override
    public void addResource(Widget w, String title) {
        if(_displayMode == DisplayMode.WHITEBOARD) {
            setLayout(new FitLayout());
            setDisplayMode(DisplayMode.WHITEBOARD);
        }
        else 
            super.addResource(w,title);
    }
}
