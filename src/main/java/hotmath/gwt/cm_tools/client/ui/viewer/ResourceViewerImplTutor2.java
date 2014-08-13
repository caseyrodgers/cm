package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tutor.client.event.UserTutorWidgetStatusUpdatedEvent;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel.CallbackAfterSolutionLoaded;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.util.CmInfoConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.info.Info;

public class ResourceViewerImplTutor2 extends CmResourcePanelImplWithWhiteboard {

    static public final String STYLE_NAME="resource-viewer-impl-tutor";
    SolutionInfo _solutionInfo;

    public ResourceViewerImplTutor2() {
        _instance = this;
        addStyleName(STYLE_NAME);

        /** If in debug mode, the provide double click on
         * the tutor loads the solution editor for the current
         * solution.
         *
         */
        if(CmShared.getQueryParameter("debug") != null) {
            
            addHandler(new DoubleClickHandler() {
                @Override
                public void onDoubleClick(DoubleClickEvent event) {
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
            },DoubleClickEvent.getType());
            
        }
       // addExternTutorHooks(this);
    }

    TutorWrapperPanel tutorPanel;
    public void showSolution() {


        CmLogger.debug("ResourceViewerImplTutor2: loading solution '" + pid + "'");

        /** If panel has already been initialized, then
         *  use existing panel.
         */
        if(tutorPanel != null)
            return;

        
        tutorPanel = new TutorWrapperPanel(true,  false, false, true, new TutorCallbackDefault() {
            @Override
            public void tutorWidgetComplete(String inputValue, boolean correct) {
                if(correct) {
                    solutionHasBeenViewed(inputValue);
                }
            }
            
            @Override
            public void solutionHasBeenViewed(String value) {
                solutionHasBeenViewed_Gwt(value);
            }
            
            @Override
            public SaveSolutionContextAction getSaveSolutionContextAction(String variablesJson, String pid,
                    int problemNumber) {
                return new SaveSolutionContextAction(UserInfo.getInstance().getUid(), UserInfo.getInstance().getRunId(), pid, problemNumber, variablesJson);
            }
            
            @Override
            public Action<UserTutorWidgetStats> getSaveTutorWidgetCompleteAction(String value, boolean yesNo) {
                return new SaveTutorInputWidgetAnswerAction(UserInfo.getInstance().getUid(), UserInfo.getInstance().getRunId(),pid, value, yesNo);
            }
            
             @Override
            public void solutionHasBeenInitialized() {
                 if(getResourceItem().isViewed()) {
                     tutorPanel.unlockSolution();
                 }
            }
             
             @Override
            public void debugLogOut(String title, String message) {
                 if(message.contains("ERROR")) {
                     Window.alert(message);
                 }
                 else {
                     InfoPopupBox.display(title, message);
                 }
            }
             
             
             @Override
            public void scrollToBottomOfScrollPanel() {
            	 
            	 // move current tutor view to bottom of its 
            	 // scrolling panel
            	 Widget parent = tutorPanel.getParent();
            	 if(parent instanceof FlowLayoutContainer) {
            		 FlowLayoutContainer flc = (FlowLayoutContainer)parent;
            		 flc.getScrollSupport().scrollToBottom();
            	 }
            	 
            }
     
        });
        tutorPanel.addStyleName("tutor_solution_wrapper");
        addResource(tutorPanel, getResourceItem().getTitle());
        

        
        CmBusyManager.setBusy(true);
        
        
        if (!UserInfo.getInstance().isShowWorkRequired())
            hasShowWork = true;
        
        boolean shouldExpandSolution = false;
        if (UserInfo.getInstance().isAutoTestMode()) {
            shouldExpandSolution = true;
        }
        
        tutorPanel.loadSolution(pid, getResourceItem().getTitle(), hasShowWork, shouldExpandSolution, getResourceItem().getWidgetJsonArgs(), new CallbackAfterSolutionLoaded() {
            
            @Override
            public void solutionLoaded(SolutionInfo solutionInfo) {
                
                CmBusyManager.setBusy(false);
                
                if(solutionInfo == null) {
                    CmMessageBox.showAlert("There was a problem loading the solution");
                    return;
                }
                _solutionInfo = solutionInfo;
                
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_SOLUTION_SHOW, getResourceItem()));
                
                // CmMainPanel.__lastInstance._mainContent.addControl(showWorkBtn);
                if (CmMainPanel.__lastInstance != null) {
                    CmMainPanel.__lastInstance._mainContentWrapper.getResourceWrapper().forceLayout();
                }
            }
        });
        
    }
    
    
    
    @Override
    public void loadWhiteboardData(final ShowWorkPanel2 showWorkPanel) {

        new RetryAction<CmList<WhiteboardCommand>>() {
            
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetWhiteboardDataAction action = new GetWhiteboardDataAction(UserInfo.getInstance().getUid(), pid,  UserInfo.getInstance().getRunId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<WhiteboardCommand> whiteboardCommands) {
                try {
                    showWorkPanel.loadWhiteboard(whiteboardCommands);
                } finally {
                    CmBusyManager.setBusy(false);
                }
            }
        }.register();

    }

    /** Not needed anymore ?  See TutorWrapperPanel
     * 
     * @param yesNo
     */
    private void saveTutorInputWidgetComplete(final String value, final boolean yesNo) {
        new RetryAction<UserTutorWidgetStats>() {
            @Override
            public void attempt() {

                CmBusyManager.setBusy(true);

                SaveTutorInputWidgetAnswerAction action = new SaveTutorInputWidgetAnswerAction(UserInfo.getInstance().getUid(), UserInfo.getInstance().getRunId(), pid, value, yesNo);

                setAction(action);
                CmShared.getCmService().execute(action,this);
            }

            @Override
            public void oncapture(UserTutorWidgetStats userStats) {
                CmBusyManager.setBusy(false);
                
                CmRpcCore.EVENT_BUS.fireEvent(new UserTutorWidgetStatusUpdatedEvent(userStats));
            }
        }.register();
    }

    private void gwt_solutionHasBeenInitialized(final String variablesJson) {

        /** if this is already stored in variables, then no need to save on server
         *
         */
        if(variablesJson == null || variablesJson.length() < 100) {
            return;
        }

        if(_solutionInfo.getContext() != null) {
            /** 
             *  only store first one, each subsequent read
             *  on same prescription (run_id) will have the 
             *  existing context restored. 
             */
        }
        else {
            final String pid=getResourceItem().getFile();
            SaveSolutionContextAction action = new SaveSolutionContextAction(UserInfo.getInstance().getUid(),UserInfo.getInstance().getRunId(),pid,__problemNumber, variablesJson);
            CmShared.getCmService().execute(action, new AsyncCallback<RpcData>() {
                @Override
                public void onSuccess(RpcData result) {
                    _solutionInfo.setContext(new SolutionContext(pid,__problemNumber,variablesJson));
                    CmLogger.info("Context saved");
                }
                @Override
                public void onFailure(Throwable caught) {
                    CmLogger.error("Error saving solution context", caught);
                }
            });
        }
    }
    
    Map<Integer,String> _variableContexts = new HashMap<Integer, String>();
    private String gwt_getSolutionProblemContext(int probNum) {
       return _solutionInfo.getContext().getContextJson();
    }

    private native void  addExternTutorHooks(Widget x) /*-{

        // called from CatchupMath.js event.tutorHasBeenInitialized
        // used to store current tutor context on server providing
        // a way to restore the tutor to its current var defs.
        //
        $wnd.gwt_solutionHasBeenInitialized = function(tutorWrapper) {
        try {
            alert('the tutor wrapper is: ' + tutorWrapper);
            
            var vars = $wnd._tutorData._variables;
            var solutionVariablesJson = $wnd.getTutorVariableContextJson(vars);
            x.@hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2::gwt_solutionHasBeenInitialized(Ljava/lang/String;)(solutionVariablesJson);
        }
        catch(e) {
            alert('JSNI: error saving solution context: ' + e);}
        }
        // override global functions defined in tutor_dynamic
        //
        $wnd.solutionSetComplete = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2::setSolutionSetComplete(II);
        $wnd.TutorDynamic.setSolutionTitle = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2::setSolutionTitle(II);

        $wnd.gwt_getSolutionProblemContext = function(probNum) {
            try {
                return x.@hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2::gwt_getSolutionProblemContext(I)(probNum);
            }
            catch(e) {
                alert(e)
            };
        }

    }-*/;


    static private void setSolutionTitle(int probNum, int limit) {
        __problemNumber = probNum;
        if(probNum >  0) {
            String title = "Problem " + probNum + " of " + limit;
            InfoPopupBox.display(new CmInfoConfig("Problem Set Status",title));
            
            
            
            Info.display("Info", "Set resource title to '" + title + "'");
            //CmMainPanel.__lastInstance._mainContentWrapper._lastContainer.setHeadingHtml(title);
        }
    }


    static int __problemNumber;
//    static private void setSolutionTitle(int probNum, int limit) {
//        __problemNumber = probNum;
//        if(probNum >  0) {
//            String title = "Problem " + probNum + " of " + limit;
//            InfoPopupBox.display(new CmInfoConfig("Problem Set Status",title));
//            
//            
//            
//            Info.display("Info", "Set resource title to '" + title + "'");
//            //CmMainPanel.__lastInstance._mainContentWrapper._lastContainer.setHeadingHtml(title);
//        }
//    }

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
    public void setupShowWorkPanel(ShowWorkPanel2 whiteboardPanel) {
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
        
    }

    public String getPid() {
        return this.pid;
    }

    public void setPid(String pid){
        this.pid = pid;
    }


    public List<Widget> getContainerTools() {
        List<Widget> tools = new ArrayList<Widget>();
        tools.add(new TextButton("How to Use This", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new ShowHowToUseDialog().setVisible(true);            }
        }));
        tools.addAll(super.getContainerTools());
        return tools;
    }

    /**
     * Load the tutor
     *
     */
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
            //item.setViewed(true);
            UserInfo.getInstance().setViewCount(UserInfo.getInstance().getViewCount()+1);
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
             = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2::showWorkDialog();
           $wnd.showTutoringDialog_Gwt
             = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2::showTutoringDialog();
           $wnd.flashInputField_Gwt
             = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2::flashInputField_Gwt(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
           $wnd.saveWhiteboardSnapshot_Gwt
             = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2::saveWhiteboardSnapshot_Gwt(Ljava/lang/String;);

           $wnd.solutionHasBeenViewed_Gwt = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2::solutionHasBeenViewed_Gwt(Ljava/lang/String;);
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
    static private native void initializeTutor(String pid, String title, String jsonConfig, boolean hasShowWork, boolean shouldExpandSolution,String solutionHtml,String solutionData,boolean isEpp, String contextVarsJson) /*-{
                                              $wnd.doLoad_Gwt(pid, title,jsonConfig, hasShowWork,shouldExpandSolution,solutionHtml,solutionData,isEpp,contextVarsJson);
                                          }-*/;

    static private native void expandAllSteps() /*-{
                                          $wnd.expandAllSteps();
                                          }-*/;

    static private native void setTutorState(Boolean yesNo) /*-{
        $wnd.setState('step',yesNo);
    }-*/;


    static ResourceViewerImplTutor2 _instance;
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
