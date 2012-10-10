package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceViewerState;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel.ShowWorkPanelCallback;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;


/** Provides a container that has a whiteboard and tutor section
 * 
 * @author casey
 *
 */
public abstract class CmResourcePanelImplWithWhiteboard extends SimpleContainer implements CmResourcePanel {

    public enum DisplayMode{TUTOR,WHITEBOARD};
    
    DisplayMode _displayMode;
    static DisplayMode __lastDisplayMode = null;
    
    static TextButton _saveWhiteboard;
    static {    
        _saveWhiteboard = new TextButton("Save Whiteboard");
        _saveWhiteboard.setVisible(false);
        
        _saveWhiteboard.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_WHITEBOARD_SAVE));                
            }
        });
        
        
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @Override
            public void handleEvent(CmEvent event) {
                switch(event.getEventType()) {
                    case EVENT_TYPE_SOLUTION_SHOW:
                    case EVENT_TYPE_WHITEBOARD_SAVE_COMPLETE:
                        _saveWhiteboard.setEnabled(false);
                        break;
                    case EVENT_TYPE_WHITEBOARD_SAVE_PENDING:
                        _saveWhiteboard.setEnabled(true);
                        break;
                }
            }
        });
    }
    
    public CmResourcePanelImplWithWhiteboard() {
        _displayMode = getInitialWhiteboardDisplay();
    }

    public boolean isWhiteboardActive() {
    	return _displayMode == DisplayMode.WHITEBOARD;
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
    public boolean shouldContainerBeTransparent() {
        return true;
    }


    @Override
    public Boolean allowMaximize() {
        return true;
    }
    
    /** Expand the tutor view initailly to support to
     * allow addition of whiteboard.
     */
    @Override
    public ResourceViewerState getInitialMode() {
            return ResourceViewerState.MAXIMIZED;
    }
    
    private void test() {
        if(DisplayMode.WHITEBOARD == _displayMode) {
            setDisplayMode(DisplayMode.TUTOR);
        }
        else { 
            setDisplayMode(DisplayMode.WHITEBOARD);
        }
        
    }
    
    TextButton _showWorkBtn;
    public List<Widget> getContainerTools() {
        String btnText = _displayMode == DisplayMode.WHITEBOARD?"Hide Whiteboard":"Show Whiteboard";
        _showWorkBtn = new TextButton(btnText, new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                
        
                TextButton btn = null;
                if(event.getSource() instanceof TextButton) {
                    btn = ((TextButton)event.getSource());
                }
                 
                _showWorkBtn = btn;
                
                if(btn != null && btn.getText().indexOf("Show") > -1) {
                    setDisplayMode(DisplayMode.WHITEBOARD);
                }
                else {
                    setDisplayMode(DisplayMode.TUTOR);
                    
                    if(_wasMaxBeforeWhiteboard) {
                        
                        //EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MAXIMIZE_RESOURCE));
                        
                        // CmMainPanel.__lastInstance._mainContent.currentContainer.setMaximize(CmResourcePanelImplWithWhiteboard.this);
                    }
                    else {
                        //EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_OPTIMIZE_RESOURCE));
                        
                        //CmMainPanel.__lastInstance._mainContent.currentContainer.setOptimized(CmResourcePanelImplWithWhiteboard.this);
                    }
                    
                    //setDisplayMode(DisplayMode.TUTOR);
                    
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_WHITEBOARD_CLOSED));
                }
            }
        });
                
        List<Widget> tools = new ArrayList<Widget>();
        tools.add(_saveWhiteboard);
        tools.add(_showWorkBtn);
        return tools;
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
    
    
    /** Allow for sub classes to load data in custom way
     * 
     * @param showWorkPanel
     */
    public void loadWhiteboardData(ShowWorkPanel showWorkPanel) {
        CmLogger.debug("Load whiteboard data, default null implmentation");
    }
    
    /** Called after the whiteboard has been initialized
     * 
     */
    public void whiteboardIsReady() {
        loadWhiteboardData(_showWorkPanel);
    }
    
    ShowWorkPanel _showWorkPanel;
    
    
    
    /** Central method to setup either tutor or whiteboard
     *  display modes.
     *  
     *   Key point is to not remove the resource panel, only remove
     *   the whiteboard and reset the layout.  This will make sure
     *   to not reset external radiobuttons in IE
     * 
     * @param displayMode
     */
    static int x = 1;
    public void setDisplayMode(DisplayMode displayMode) {
        
        _displayMode = displayMode;
        __lastDisplayMode = _displayMode;
        
        clear();
        
        if(displayMode == DisplayMode.TUTOR) {
        	if(_showWorkBtn != null)
                _showWorkBtn.setText("Show Whiteboard");
        	_saveWhiteboard.setVisible(false);
        	add(getTutorDisplay());
            //CmMainPanel.__lastInstance._mainContent.currentContainer.getMaximizeButton().setEnabled(true);
        }
        else {
            
            if(_showWorkBtn != null)
                _showWorkBtn.setText("Hide Whiteboard");

            /** show whiteboard panel
             * 
             */
            _showWorkPanel = new ShowWorkPanel(new ShowWorkPanelCallback() {
                @Override
                public void showWorkIsReady() {
                    whiteboardIsReady();
                }
                
                @Override
                public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                    return new SaveWhiteboardDataAction(UserInfo.getInstance().getUid(),UserInfo.getInstance().getRunId(), pid, commandType, data);
                }
                
                @Override
                public void windowResized() {
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_WINDOW_RESIZED));
                }              
            });
            setupShowWorkPanel(_showWorkPanel);

            BorderLayoutContainer borderLayoutContainer = new BorderLayoutContainer();
            borderLayoutContainer.addStyleName("whiteboard-container");
            
            
            /** always add a scrollable panel
             * 
             */
            FlowLayoutContainer flowContainer = new FlowLayoutContainer();
            flowContainer.setScrollMode(ScrollMode.AUTOY);
            flowContainer.add(getTutorDisplay());

            BorderLayoutData bld = new BorderLayoutData();
            bld.setSplit(true);
            bld.setFloatable(true);
            bld.setCollapsible(true);
            borderLayoutContainer.setCenterWidget(flowContainer,bld);

            bld = new BorderLayoutData(.50f);
            bld.setSplit(false);
            bld.setFloatable(true);
            bld.setCollapsible(true);
            bld.setCollapsed(true);
            
            ContentPanel cp = new ContentPanel();
            cp.setWidget(_showWorkPanel);
            borderLayoutContainer.setEastWidget(cp, bld);
            add(borderLayoutContainer);
        }
        

        forceLayout();
        
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_RESOURCE_CONTAINER_REFRESH));
    }
    
    public void addResource(Widget w, String title) {
        if(_displayMode == DisplayMode.WHITEBOARD) {
            setDisplayMode(DisplayMode.WHITEBOARD);
        }
        else{
            clear();
            
            setId("flowLayoutParent");
            FlowLayoutContainer flowContainer = new FlowLayoutContainer();
            flowContainer.setId("FLowLayoutContainer");
            flowContainer.setScrollMode(ScrollMode.AUTOY);
            flowContainer.add(w);

            add(flowContainer);
            //CmMainPanel.__lastInstance.forceLayout();
        }
    }
    


    @Override
    public Integer getMinHeight() {
        return 400;
    }

    @Override
    public Integer getOptimalHeight() {
        return -1;
    }

    @Override
    public Integer getOptimalWidth() {
        return 500;
    }

    @Override
    public Boolean showContainer() {
        return true;
    }

    @Override
    public String getContainerStyleName() {
        return null;
    }

    @Override
    public InmhItemData getResourceItem() {
        return item;
    }
    
    @Override
    public Boolean allowClose() {
        return true;
    }


    @Override
    public Widget getResourcePanel() {
        String url = item.getFile();
        String html = "<iframe frameborder='no' width='100%' height='400px' src='" + url + "'></iframe>";
        
        addResource(new HTML(html),item.getTitle());
        
        return this;
    }

    @Override
    public void removeResourcePanel() {
        clear();
    }

    
    InmhItemData item;
    
    @Override
    public void setResourceItem(InmhItemData item) {
        this.item = item;
    }
}
