package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelContainer.ResourceViewerState;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.rpc.action.GetSolutionAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplTutor extends CmResourcePanelImplDefault {

    static ResourceViewerImplTutor _instance;
    static {
        publishNative();

        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            public void handleEvent(CmEvent event) {
                if (event.getEventName().equals(EventBus.EVENT_TYPE_WHITEBOARDUPDATED)) {
                    _instance.whiteBoardHasBeenUpdated((String) event.getEventData());
                }
                else if(event.getEventName().equals(EventBus.EVENT_TYPE_MODAL_WINDOW_OPEN)) {
                    if(__lastDisplayMode == DisplayMode.WHITEBOARD) {
                        _instance.removeResourcePanel();
                    }
                }
            }
        });
    }

    static public final String STYLE_NAME="resource-viewer-impl-tutor";
    
    enum DisplayMode{TUTOR,WHITEBOARD};
    
    DisplayMode _displayMode;
    static DisplayMode __lastDisplayMode = null;
    
    public ResourceViewerImplTutor() {
        _instance = this;
    
        /** If user has showwork enabled or system is currently in whiteboard mode
         *  The show whiteboard on startup.
         */
        if(UserInfo.getInstance().isShowWorkRequired() || __lastDisplayMode == DisplayMode.WHITEBOARD) {
            _displayMode = DisplayMode.WHITEBOARD;
        }
        else {
            _displayMode = DisplayMode.TUTOR;
        }
            
        addStyleName(STYLE_NAME);
        setScrollMode(Scroll.AUTOY);
    }

    @Override
    public Integer getOptimalWidth() {
        return 500;
    }
    
    @Override
    public Boolean showContainer() {
        return false;
    };


    @Override
    public Boolean allowMaximize() {
        return true;
    }
   
    
    @Override
    public String getContainerStyleName() {
        return STYLE_NAME;
    }
    
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
                        CmMainPanel.__lastInstance._mainContent.currentContainer.setMaximize(ResourceViewerImplTutor.this);
                    }
                    else {
                        CmMainPanel.__lastInstance._mainContent.currentContainer.setOptimized(ResourceViewerImplTutor.this);
                    }
                    
                    setDisplayMode(DisplayMode.TUTOR);
                }
            }
        });
        Component[] btn = {_showWorkBtn};
        return java.util.Arrays.asList(btn);
    }
    
    
    


    
    boolean _wasMaxBeforeWhiteboard;
    /** Central method to setup either tutor or whiteboard 
     * 
     * @param displayMode
     */
    private void setDisplayMode(DisplayMode displayMode) {

        removeAll();
        
        if(displayMode == DisplayMode.TUTOR) {
            _showWorkBtn.setText("Show Whiteboard");
            add(tutorPanel);
            
            CmMainPanel.__lastInstance._mainContent.currentContainer.getMaximizeButton().setEnabled(true);
        }
        else {
            _wasMaxBeforeWhiteboard = CmMainPanel.__lastInstance._mainContent.currentContainer.isMaximized();
            CmMainPanel.__lastInstance._mainContent.currentContainer.setMaximize(this,_wasMaxBeforeWhiteboard);
            
            CmMainPanel.__lastInstance._mainContent.currentContainer.getMaximizeButton().setEnabled(false);
            
            ShowWorkPanel swp = new ShowWorkPanel();
            swp.setupForPid(this.pid);

            LayoutContainer lcTutor = new LayoutContainer(new FitLayout());
            lcTutor.setScrollMode(Scroll.AUTO);
            lcTutor.add(tutorPanel);
            

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
    
    Button showWorkBtn, hideWorkBtn;
    String pid;
    boolean hasShowWork;

    @Override
    public Widget getResourcePanel() {
        
        this.pid = getResourceItem().getFile();
        showSolution();

        return this;
    }

    public void removeResourcePanel() {
        removeAll();
        layout();
        tutorPanel = null;
    }

    public String getPid() {
        return this.pid;
    }

    /**
     * Load the tutor
     * 
     */
    
    Widget tutorPanel;
    public void showSolution() {

        if(tutorPanel != null)
            return;  
        
        Log.debug("ResourceViewerImplTutor: loading solution '" + pid + "'");

        // call for the solution HTML
        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new GetSolutionAction(UserInfo.getInstance().getUid(), pid), new AsyncCallback<RpcData>() {
            public void onFailure(Throwable caught) {
                CatchupMathTools.showAlert(caught.getMessage());
            }

            public void onSuccess(RpcData result) {
                String html = result.getDataAsString("solutionHtml");
                boolean hasShowWork = result.getDataAsInt("hasShowWork") > 0;

                tutorPanel = new Html(html);
                tutorPanel.setStyleName("tutor_solution_wrapper");

                addResource(tutorPanel, getResourceItem().getTitle());

                
                // CmMainPanel.__lastInstance._mainContent.addControl(showWorkBtn);
                if (CmMainPanel.__lastInstance != null)
                    CmMainPanel.__lastInstance._mainContent.layout();

                try {

                    /**
                     * Show Work is not required, then do not show the
                     * ShowWorkRequired
                     * 
                     */
                    if (!UserInfo.getInstance().isShowWorkRequired())
                        hasShowWork = true;
                    boolean shouldExpandSolution = false;
                    if (UserInfo.getInstance().isAutoTestMode()) {
                        shouldExpandSolution = true;
                        // showWorkDialog();
                    }
                    ResourceViewerImplTutor.initializeTutor(getResourceItem().getFile(), getResourceItem().getTitle(), hasShowWork,
                            shouldExpandSolution);

                    EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_SOLUTION_SHOW, getResourceItem()));
                } catch (Exception e) {
                    e.printStackTrace();
                    CatchupMathTools.showAlert(e.getMessage());
                }

                layout();
            }
        });
    }

    /**
     * Notified whenever a showwork entry is made
     * 
     * @param pid
     */
    protected void whiteBoardHasBeenUpdated(String pid) {
        if (UserInfo.getInstance().isShowWorkRequired() && !this.hasShowWork && this.pid.equals(pid)) {
            /** 
             * this solution's whiteboard has been updated, so
             * we must make sure the ForceShowWork button is removed
             */
            initializeTutor(pid, this.getResourceItem().getTitle(), true, false);

            hasShowWork = true;
        }
    }

    /**
     * publish native method to allow for opening of Show Window from external
     * JS using current instance
     * 
     */
    static private native void publishNative() /*-{
                                               $wnd.showWorkDialog_Gwt = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor::showWorkDialog();
                                               $wnd.showTutoringDialog_Gwt = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor::showTutoringDialog();
                                               }-*/;

    /**
     * Called from the show-work-button display on the tutor and default in
     * tutor_wrapper.vm
     * 
     * @TODO: Make this an instance var. JSNI does not seem to be working when
     *        specified as instance (using this). Does not work in hosted mode
     *        (does in web mode)
     */
    static public void showWorkDialog() {
        _instance.setDisplayMode(DisplayMode.WHITEBOARD);
    }

    static public void showTutoringDialog() {
        _instance.setDisplayMode(DisplayMode.TUTOR);
    }
    
    public Widget createShowWork(final String pidIn) {
        Button viewSolution = new Button("View solution");
        viewSolution.setToolTip("View the tutorial to check your answer");
        viewSolution.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                // CatchupMathTools.showAlert("Pid: " + _pid);
                initializeTutor(pidIn, getResourceItem().getTitle(), hasShowWork, true);
                setDisplayMode(DisplayMode.TUTOR);
            }
        });

        Button back = new Button("Back");
        back.setToolTip("Return back to the solution");
        back.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                setDisplayMode(DisplayMode.TUTOR);
            }
        });

        Button examples = new Button("Examples");
        examples.setToolTip("View sample uses of Show Work");
        examples.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                // must remove whiteboard to show popup
                setDisplayMode(DisplayMode.TUTOR);
                new ShowWorkExampleWindow();
            }
        });

        ShowWorkPanel showWorkPanel = new ShowWorkPanel();
        showWorkPanel.setupForPid(pidIn);
        
        return showWorkPanel;
    }

    /**
     * Display LWL tutoring in separate browser window.
     * 
     * It does not play well in a DHTML controlled window
     * 
     * 
     * @param pid
     */
    public void showTutoring(String pid) {

        if (!UserInfo.getInstance().isTutoringAvail()) {
            CatchupMathTools.showAlert("Ask a Tutor", "Live Tutoring is not currently enabled on this account.");
            return;
        }

        String contentUrl = "pid=" + pid;
        String url = "/collab/lwl/cm_lwl_launch.jsp?uid=" + UserInfo.getInstance().getUid() + "&contentUrl="
                + contentUrl;

        int w = 800;
        int h = 560;
        String windowProps = "toolbar=0, titlebar=0, status=0, menubar=0, resizable=0, width=" + w + ", height=" + h
                + ", directories=0, location=0,scrollbars=0,directories=0,location=0";

        com.google.gwt.user.client.Window.open(url, "_blank", windowProps);
    }

    /**
     * Call specialized JavaScript defined in main js
     * 
     * @param pid
     */
    static private native void initializeTutor(String pid, String title, boolean hasShowWork,
            boolean shouldExpandSolution) /*-{
                                          $wnd.doLoad_Gwt(pid, title,hasShowWork,shouldExpandSolution);
                                          }-*/;

}

/**
 * Display a few examples of using the Show Work as a DHTML window.
 * 
 * @author casey
 * 
 */
class ShowWorkExampleWindow extends Window {

    public ShowWorkExampleWindow() {
        setStyleName("show-work-example-window");
        setSize(440, 480);
        setHeading("Show Work Examples");
        Frame frame = new Frame("/gwt-resources/show_work_examples.html");
        DOM.setElementPropertyInt(frame.getElement(), "frameBorder", 0); // disable
                                                                         // border
        DOM.setElementProperty(frame.getElement(), "scrolling", "auto"); // disable
                                                                         // border

        frame.setSize("630px", "475px");
        setLayout(new FitLayout());
        add(frame);
        setVisible(true);
    }
}
