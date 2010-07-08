package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetWhiteboardDataAction;
import hotmath.gwt.shared.client.rpc.action.SaveWhiteboardDataAction;
import hotmath.gwt.shared.client.rpc.action.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.shared.client.rpc.result.WhiteboardCommand;
import hotmath.gwt.shared.client.util.UserInfo;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;

public class ShowWorkPanel extends Frame {

    String id;
    String flashId;
    boolean eatNextWhiteboardOut;
    MultiActionRequestAction whiteboardActions = new MultiActionRequestAction();

    CmAsyncRequest callbackAfterWhiteboardInitialized;

    static ShowWorkPanel __lastInstance;
    String pid;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    /**
     * Display the Show Work Flash component in an IFRAME
     * 
     * @param callbackAfterWhiteboardInitialized
     *            If non null, then called after WB has been initialized.
     */
    public ShowWorkPanel(CmAsyncRequest callbackAfterWhiteboardInitialized) {
        super("/gwt-resources/show_work_panel_student.html");
        
        __lastInstance = this;

        this.callbackAfterWhiteboardInitialized = callbackAfterWhiteboardInitialized;
        setStyleName("show-work-panel");
        DOM.setElementProperty(this.getElement(), "frameBorder", "no"); // disable
        DOM.setElementPropertyInt(this.getElement(), "border", 0); // disable
        DOM.setElementPropertyInt(this.getElement(), "frameSpacing", 0); // disable
        DOM.setElementProperty(this.getElement(), "scrolling", "no"); // disable
        id = "show_work_" + System.currentTimeMillis();
        flashId = id + "_flash";
        // CmMainPanel.__lastInstance._mainContent.setScrollMode(Scroll.NONE);
    }

    /**
     * clear whiteboard, and optionally do not issue server clear.
     * 
     * This is to allow reusing already loaded whiteboard
     * 
     * When a clear is issued to the whiteboard, it reissues the 
     * command so we must eat this command to not clear the db
     * for this pid.
     * 
     * @param doNotSendToServer
     */
    public void clearWhiteBoard(boolean doNotSendToServer) {
        eatNextWhiteboardOut = doNotSendToServer;
        updateFlashWhiteboard(flashId, "clear", "{}");
    }

    
    protected void handleFlashFlushWhiteboardChanges() {
        
        if(whiteboardActions.getActions().size() == 0)
            return;
        
        new RetryAction<CmList<Response>>() {
            @Override
            public void attempt() {
                setAction(whiteboardActions);
                CmShared.getCmService().execute(whiteboardActions, this);
            }

            public void oncapture(CmList<Response> result) {
                whiteboardActions.getActions().clear();
                
                /** fire event that whiteboard has been saved successfully */
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_WHITEBOARD_SAVE_COMPLETE));
            }
        }.register();

    }
    
    /**
     * Called from flash component Save in DB.
     *  
     * collection actions and send as a group on request
     * and on focus lost.
     * 
     * 
     * @param json
     */
    public void handleFlashWhiteboardOut(final String json) {

        if (eatNextWhiteboardOut) {
            eatNextWhiteboardOut = false;
            return;
        }

        if (UserInfo.getInstance().isShowWorkRequired())
            EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_WHITEBOARDUPDATED, this.pid));


        /**
         * If json is simple string 'clear', then force a full clear and
         * remove all chart data for this user/pid. Otherwise, it is a
         * normal draw command.
         */
        CommandType commandType = json.equals("clear") ? CommandType.CLEAR : CommandType.DRAW;
        SaveWhiteboardDataAction action = new SaveWhiteboardDataAction(UserInfo.getInstance().getUid(),UserInfo.getInstance().getRunId(), pid, commandType, json);
        whiteboardActions.getActions().add(action);
        
        /** fire event about pending changes only on first entry
         */
        if(whiteboardActions.getActions().size() == 1)
            EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_WHITEBOARD_SAVE_PENDING));
    }

    boolean initialized = false;

    /**
     * Called by flash once it has been initialized.
     * 
     * After flash is loaded and JS communication hooks established, we get data
     * from server and send whiteboard commands to create the whiteboard image.
     */
    public void handleFlashWhiteboardIsReady() {

        if (this.callbackAfterWhiteboardInitialized != null)
            callbackAfterWhiteboardInitialized.requestComplete(null);

        loadWhiteboardData();
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_WHITEBOARD_READY));
    }

    private void loadWhiteboardData() {

        if (pid == null || pid.equals("quiz:null"))
            return;

        new RetryAction<CmList<WhiteboardCommand>>() {
            @Override
            public void attempt() {
                int runId = pid.startsWith("quiz") ? 0 : UserInfo.getInstance().getRunId();
                GetWhiteboardDataAction action = new GetWhiteboardDataAction(UserInfo.getInstance().getUid(), pid,
                        runId);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<WhiteboardCommand> commands) {
                for (int i = 0, t = commands.size(); i < t; i++) {
                    updateFlashWhiteboard(flashId, commands.get(i).getCommand(), commands.get(i).getData());
                }
            }
        }.register();
    }

    String backgroundHtml;

    /**
     * Setup for pid, the panel is not loaded yet so we cannot update the
     * background image .. so we store it until we know the flash component is
     * ready.
     * 
     * @param pid
     */
    public void setupForPid(String pid) {
        this.pid = pid;
    }

    /**
     * Push a GWT method onto the global space for the app window
     * 
     * This wil be called from Flash whiteboard component after it initializes.
     * 
     * which is called after each guess selection.
     * 
     */
    static private native void publishNative() /*-{
                                               $wnd.flashWhiteboardIsReady = @hotmath.gwt.cm_tools.client.ui.viewer.ShowWorkPanel::flashWhiteboardIsReady();
                                               $wnd.flashWhiteboardOut = @hotmath.gwt.cm_tools.client.ui.viewer.ShowWorkPanel::flashWhiteboardOut(Ljava/lang/String;);
                                               $wnd.flushWhiteboardChanges = @hotmath.gwt.cm_tools.client.ui.viewer.ShowWorkPanel::flushWhiteboardChanges();
                                               }-*/;

    static boolean _flashIsReady;

    static public void flashWhiteboardIsReady() {
        __lastInstance.handleFlashWhiteboardIsReady();
    }

    static public void flashWhiteboardOut(String json) {
        __lastInstance.handleFlashWhiteboardOut(json);
    }

    static public void flushWhiteboardChanges() {
        __lastInstance.handleFlashFlushWhiteboardChanges();
    }
    
    /**
     * Calls Javascript hook that will lookup Flash object and make call via the
     * ExternalInterface.
     * 
     * @param flashId
     * @param command
     * @param commandData
     */
    static public native void updateFlashWhiteboard(String flashId, String command, String commandData) /*-{
                                                                                                        $wnd.updateWhiteboard(flashId, command, commandData);
                                                                                                        }-*/;

    /**
     * Tell the current whiteboard that it is in readonly mode
     * 
     */
    static public native void setWhiteboardIsReadonly() /*-{
                                                        $wnd.setWhiteboardIsReadonly();
                                                        }-*/;

    static public native void setWhiteboardBackground(String html) /*-{
                                                                   $wnd.setWhiteboardBackground(html);
                                                                   }-*/;
    
    static {
        publishNative();

        /**
         * add a listener to keep the whiteboard updated when the quiz question
         * changes.
         */
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @Override
            public void handleEvent(CmEvent event) {
                switch(event.getEventType()) {
                case EVENT_TYPE_QUIZ_QUESTION_FOCUS_CHANGED:
                    if (__lastInstance != null) {
                        if(event.getEventData() == null) {
                            /** whiteboard should NOT be shown at this point
                             * 
                             */
                            __lastInstance.setPid(null);
                        }
                        else {
                            __lastInstance.setPid("quiz:" + event.getEventData().toString());
                            __lastInstance.clearWhiteBoard(true);
                            __lastInstance.loadWhiteboardData();
                        }
                    }
                    break;
                    
                case EVENT_TYPE_WHITEBOARD_SAVE:
                    __lastInstance.handleFlashFlushWhiteboardChanges();
                }
            }
        });
    }
}
