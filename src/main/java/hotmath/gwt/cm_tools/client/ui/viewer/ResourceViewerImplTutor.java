package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm.client.ui.context.PrescriptionCmGuiDefinition;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.MarkHomeWorkAttemptedAction;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SaveTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.client.rpc.SetInmhItemAsViewedAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard.DisplayMode;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.util.CmInfoConfig;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplTutor extends CmResourcePanelImplWithWhiteboard {

    static public final String STYLE_NAME="resource-viewer-impl-tutor";
    SolutionInfo _solutionInfo;
    
    public ResourceViewerImplTutor() {
        _instance = this;
        addStyleName(STYLE_NAME);
        setScrollMode(Scroll.AUTOY);
        
        /** If in debug mode, the provide double click on 
         * the tutor loads the solution editor for the current
         * solution.
         * 
         */
        if(CmShared.getQueryParameter("debug") != null) {
            addListener(Events.OnDoubleClick, new Listener<BaseEvent>() {
                public void handleEvent(BaseEvent be) {
                    final String pid = getResourceItem().getFile();
                    MessageBox.confirm("Edit Solution", "Edit solution " +  pid + " with Solution Editor?", new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                            if(be.getButtonClicked().getText().equals("Yes")) {
                                String url = CmShared.CM_HOME_URL + "/solution_editor/SolutionEditor.html?pid=" + pid;
                                com.google.gwt.user.client.Window.open(url, "_blank", "height=480,width=640,status=yes,scrollbars=1");
                            }
                        }
                    });
                }
            });
            sinkEvents(Event.ONDBLCLICK);
        }
        
        
        addExternTutorHooks(this);
    }
    
    private void tutorInputWidgetComplete_gwt(final int yesNo) {
        new RetryAction<CmList<Response>>() {
            @Override
            public void attempt() {
                
                
                CmBusyManager.setBusy(true);
                MultiActionRequestAction mAction = new MultiActionRequestAction();
                setAction(mAction);
                
                SaveTutorInputWidgetAnswerAction action = new SaveTutorInputWidgetAnswerAction(UserInfo.getInstance().getRunId(), pid, yesNo==0?false:true);
                mAction.getActions().add(action);


                InmhItemData item = getResourceItem();
                // if EPP (homework), then emarked as viewed as 
                // soon as input widget is attempted.
                if(item.getType().equals("cmextra")) {
                    if(item.isViewed() == false) {
                        item.setViewed(true);
                        SetInmhItemAsViewedAction eppViewedAction = new SetInmhItemAsViewedAction(UserInfo.getInstance().getRunId(),item.getType(), item.getFile(), UserInfo.getInstance().getSessionNumber());
                        mAction.getActions().add(eppViewedAction);
                        
                        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_EPP_COMPLETE,item));
                    }
                }
                
                
                CmShared.getCmService().execute(mAction,this);
            }
            
            @Override
            public void oncapture(CmList<Response> results) {
                CmBusyManager.setBusy(false);
            }
        }.register();
    }
    
    private native void  addExternTutorHooks(ResourceViewerImplTutor x) /*-{
    
         // override global functions defined in tutor_dynamic
         //
         $wnd.solutionSetComplete = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor::setSolutionSetComplete(II);
         $wnd.TutorDynamic.setSolutionTitle = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor::setSolutionTitle(II);
         $wnd.tutorInputWidgetComplete_gwt = function (yesNo) {
            x.@hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor::tutorInputWidgetComplete_gwt(I)(yesNo);
        };
         
    }-*/;
    
    static private void setSolutionTitle(int progNum, int limit) {
        if(progNum >  0) {
            String title = "Problem " + progNum + " of " + limit;
            InfoPopupBox.display(new CmInfoConfig("Problem Set Status",title));
            CmMainPanel.__lastInstance._mainContent.currentContainer.setHeading(title);
        }
    }
    
    static private void setSolutionSetComplete(int numCorrect, int limit) {
        String title = "Correct " + numCorrect + " out of " + limit;
        InfoPopupBox.display(new CmInfoConfig("Problem Set Complete",title));
        
        CmMainPanel.__lastInstance.removeResource();
        
        new SolutionSetCompleteDialog(numCorrect, limit);
    }
    
    private native void showSolutionEditorForPid(String pid) /*-{
        var se = window.open('/solution_editor/SolutionEditor.html?pid=' + pid);
        se.focus();
    }-*/;
    
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
    
    public void setPid(String pid){
        this.pid = pid;
    }

    
    public List<Component> getContainerTools() {
        List<Component> tools = new ArrayList<Component>();
        tools.add(new Button("How to Use This", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                new ShowHowToUseDialog().setVisible(true);
            }
        }));
        tools.addAll(super.getContainerTools());
        return tools;
    }
    
    /**
     * Load the tutor
     * 
     */
    
    TutorWrapperPanel tutorPanel;
    public void showSolution() {

        
    	CmLogger.debug("ResourceViewerImplTutor: loading solution '" + pid + "'");
        
        /** If panel has already been initialized, then 
         *  use existing panel.
         */
        if(tutorPanel != null)
            return;  

        
        new RetryAction<SolutionInfo>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetSolutionAction action = new GetSolutionAction(UserInfo.getInstance().getUid(), pid);
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }
            
            @Override
            public void oncapture(SolutionInfo result) {
            	_solutionInfo = result;

            	/** We want to NOT show the Show Work
                 *  buttons over the tutor, so force
                 *  it off.
                 *  
                 *  result.isHasShowWork();
                 */
                boolean hasShowWork = true; 
                
                tutorPanel = new TutorWrapperPanel();
                tutorPanel.addStyleName("tutor_solution_wrapper");
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
                    
                    
                    /** if is EPP, then turn off steps/hints and hide button bar */
                    boolean isEpp=false;
                    if(getResourceItem().getType().equals("cmextra")) {
                        isEpp=true;
                        tutorPanel.addStyleName("is_epp");
                    }

                    ResourceViewerImplTutor.initializeTutor(getResourceItem().getFile(), 
                             getResourceItem().getTitle(),getResourceItem().getWidgetJsonArgs(),
                             hasShowWork,shouldExpandSolution,result.getHtml(),result.getJs(),isEpp);
                    
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
     * Mark the pid as being viewed called from external JS
     * 
     * called from CatchupMath.js
     * 
     * 
     * @TODO: remove access to globals
     * 
     * @param pid
     */
    static public void solutionHasBeenViewed_Gwt(String eventName) {
        InmhItemData item = _instance.getResourceItem();
        if(CmShared.getQueryParameter("debug") != null || !item.isViewed()) {
            item.setViewed(true);
            EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REQUIRED_COMPLETE, item));
        }
    }    

    /**
     * Notified whenever a showwork entry is made
     * 
     *  If EPP and first whiteboard entry, then mark this
     *  EPP resource as  'attempted'.
     *  
     * 
     * @param pid
     */
    protected void whiteBoardHasBeenUpdated(String pid) {
        if(getResourceItem().getType().equals("cmextra")) {
            if(!getResourceItem().isViewed()) {
                getResourceItem().setViewed(true);
                
                solutionHasBeenViewed_Gwt(null);
            }
        }
    }
    

    static private native void createWhiteboardSnapshot_Jsni() /*-{
        parent.createWhiteboardSnapshot_Jsni();
    }-*/;

    /**
     * publish native method to allow for opening of Show Window from external
     * JS using current instance
     * 
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
	     
       $wnd.solutionHasBeenViewed_Gwt = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor::solutionHasBeenViewed_Gwt(Ljava/lang/String;);

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
        CmLogger.info("FIF: result: " + result + ",input: " + input + ", answer:  " + answer + ",id: " + id);
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
    static private native void initializeTutor(String pid, String title, String jsonConfig, boolean hasShowWork, boolean shouldExpandSolution,String solutionHtml,String solutionData,boolean isEpp) /*-{
                                          $wnd.doLoad_Gwt(pid, title,jsonConfig, hasShowWork,shouldExpandSolution,solutionHtml,solutionData,isEpp);
                                          }-*/;

    static private native void expandAllSteps() /*-{
                                          $wnd.expandAllSteps();
                                          }-*/;
    
    static private native void setTutorState(Boolean yesNo) /*-{
        $wnd.setState('step',yesNo);
    }-*/;
    

    static ResourceViewerImplTutor _instance;
    static {
        publishNative();

        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            public void handleEvent(CmEvent event) {
                if (event.getEventType() == EventType.EVENT_TYPE_WHITEBOARDUPDATED) {
                    _instance.whiteBoardHasBeenUpdated((String) event.getEventData());
                }
                else if(event.getEventType() == EventType.EVENT_TYPE_MODAL_WINDOW_OPEN) {
//                    if(__lastDisplayMode == DisplayMode.WHITEBOARD) {
//                        CmMainPanel.__lastInstance.removeResource();
//                    }
                }
            }
        });
    }
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


class ShowHowToUseDialog extends CmWindow {
    public ShowHowToUseDialog() {
    	EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
        setModal(true);
        setSize(350,300);
        setHeading("How To Use This");
        add(new Html(html));
        addCloseButton();
        addWindowListener(new WindowListener() {
        	@Override
        	public void windowHide(WindowEvent we) {
        		EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED));
        	}
        });
    }
    String html =
        "<div style='padding: 10px 5px;'>" +
        "<p>Work out your answer using pencil and paper, or click 'Show Whiteboard' to enter your work there.  (Some teachers may require this.)</p>" +
        "<p>If you see an 'Enter Your Answer' input box, you can check your answer there.</p>" +
        "<p>Then, click the green arrow keys to see a step-by-step solution.</p>" +
        "</div>";
}