package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetSolutionAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplTutor extends CmResourcePanelImplWithWhiteboard {

    static ResourceViewerImplTutor _instance;
    static {
        publishNative();

        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            public void handleEvent(CmEvent event) {
                if (event.getEventType() == EventType.EVENT_TYPE_WHITEBOARDUPDATED) {
                    _instance.whiteBoardHasBeenUpdated((String) event.getEventData());
                }
                else if(event.getEventType() == EventType.EVENT_TYPE_MODAL_WINDOW_OPEN) {
                    if(__lastDisplayMode == DisplayMode.WHITEBOARD) {
                        //CmMainPanel.__lastInstance.removeResource();
                    }
                }
            }
        });
    }

    static public final String STYLE_NAME="resource-viewer-impl-tutor";
    
    public ResourceViewerImplTutor() {
        _instance = this;

        addStyleName(STYLE_NAME);
        setScrollMode(Scroll.AUTOY);
    }
    
    @Override
    public Widget getTutorDisplay() {
    	return tutorPanel;
    }

    @Override
    public void setupShowWorkPanel(ShowWorkPanel whiteboardPanel) {
    	whiteboardPanel.setupForPid(getPid());
    }
    
    
    @Override
    public Integer getOptimalWidth() {
        return 500;
    }

    @Override
    public Boolean allowMaximize() {
        return true;
    }
   
    
    @Override
    public String getContainerStyleName() {
        return STYLE_NAME;
    }
    
    
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

    	Log.debug("ResourceViewerImplTutor: loading solution '" + pid + "'");
        
        /** If panel has already been initialized, then 
         *  use existing panel.
         */
        if(tutorPanel != null)
            return;  

        
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetSolutionAction action = new GetSolutionAction(UserInfo.getInstance().getUid(), pid);
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }
            
            @Override
            public void oncapture(RpcData result) {
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
                    ResourceViewerImplTutor.initializeTutor(getResourceItem().getFile(), getResourceItem().getTitle(), hasShowWork,shouldExpandSolution);

                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_SOLUTION_SHOW, getResourceItem()));
                    
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    CatchupMathTools.showAlert(e.getMessage());
                }
                finally {
                    CmBusyManager.setBusy(false);
                }
                layout();
            }
        }.register();
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
        
        //createWhiteboardSnapshot_Jsni();
    }
    

    static private native void createWhiteboardSnapshot_Jsni() /*-{
        parent.createWhiteboardSnapshot_Jsni();
    }-*/;
    
    
    /**
     * publish native method to allow for opening of Show Window from external
     * JS using current instance
     * 
     */
    static private native void publishNative() /*-{
                                               $wnd.showWorkDialog_Gwt     
                                                 = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor::showWorkDialog();
                                               $wnd.showTutoringDialog_Gwt 
                                                 = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor::showTutoringDialog();
                                               $wnd.flashInputField_Gwt   
                                                 = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor::flashInputField_Gwt(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
                                               $wnd.saveWhiteboardSnapshot_Gwt
                                                 = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor::saveWhiteboardSnapshot_Gwt(Ljava/lang/String;);
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
        _instance.showTutoring(_instance.getPid());
    }
    
    static public void saveWhiteboardSnapshot_Gwt(String o) {
        CatchupMathTools.showAlert("Whiteboard snapshot: " + o);
    }

    static public void flashInputField_Gwt(String result, String input, String answer, String id) {
        Log.info("FIF: result: " + result + ",input: " + input + ", answer:  " + answer + ",id: " + id);
        if(result.equals("correct")) {
            EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_SOLUTION_FIF_CORRECT,_instance.getPid()));
        }
        else {
            // do nothing if incorrect
        }
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
        String url = "/collab/lwl/cm_lwl_launch.jsp?pid=" + pid + "&uid=" + UserInfo.getInstance().getUid() + "&contentUrl="
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

    static private native void expandAllSteps() /*-{
                                          $wnd.expandAllSteps();
                                          }-*/;
    
    static private native void setTutorState(Boolean yesNo) /*-{
        $wnd.setState('step',yesNo);
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
