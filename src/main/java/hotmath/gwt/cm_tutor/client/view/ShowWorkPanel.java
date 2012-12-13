package hotmath.gwt.cm_tutor.client.view;

import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedHandler;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.event.ShowWorkModifiedEvent;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ShowWorkPanel extends Composite {
    static public ShowWorkPanel __lastInstance;

    static public final String QUIZ_PREFIX = "quiz:";

    ShowWorkPanelCallback _whiteboardOutCallback;
    String pid;
    String title;

    @UiField
    CheckBox showProblem;

    @UiField
    Element canvasBackground;

    boolean isReady;
    
    interface MyUiBinder extends UiBinder<Widget, ShowWorkPanel> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


    public ShowWorkPanel(ShowWorkPanelCallback whiteboardOutCallback) {
        this(whiteboardOutCallback, true);
    }
    
    public ShowWorkPanel(ShowWorkPanelCallback whiteboardOutCallback,boolean setupWhiteboardNow) {
        this._whiteboardOutCallback = whiteboardOutCallback;
        initWidget(uiBinder.createAndBindUi(this));

        showProblem.setVisible(false);

        setExternalJsniHooks(this);
        
        
        if(setupWhiteboardNow) {
            setupWhiteboard();
        }

        __lastInstance = this;
    }
    
    
    /** Call after the whiteboard had been inserted into DOM
     * 
     */
    public void setupWhiteboard() {
        /** execute initialize only after HTML is loaded */
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                Log.debug("Calling initializeWhiteboard with element: " + getWidget().getElement());
                
                // call initialization.  This will call
                // whiteboardIsReady, which will then do 
                // the callback.  This way we know the whiteboard
                // is 100% ready.
                jsni_initializeWhiteboard(getWidget().getElement());
                
                Log.debug("Calling showWorkIsReady");
                isReady = true;
            }
        });        
    }
    
    
    private void whiteboardIsReady() {
        Log.debug("whiteboardIsReady called from external JS");
        _whiteboardOutCallback.showWorkIsReady();
    }


    public boolean isReady() {
        return isReady;
    }

    public void setupForPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String getTitle() {
        return "Show Work for " + title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public void resizeWhiteboard() {
        jnsi_resizeWhiteboard(getWidget().getElement());
        _whiteboardOutCallback.windowResized();
    }

    @UiHandler("showProblem")
    protected void handleShowProblem(ClickEvent ce) {
        setProblemStatement("The Problem Statement");
    }

    public void setAsTeacherMode(boolean yesNo) {
        jsni_setAsTeacherMode(yesNo);
    }

    private native void jsni_setAsTeacherMode(boolean yesNo)/*-{
                                                            $wnd.Whiteboard.setAsTeacherMode(yesNo);
                                                            }-*/;

    public void setProblemStatement(String problemStatement) {
        if (true) { // showProblem.getValue()) {
            canvasBackground.setInnerHTML("<div>" + problemStatement + "</div>");
            canvasBackground.setAttribute("style", "display: block");

            initializeWidgets();
        } else {
            canvasBackground.setAttribute("style", "display: none");
            canvasBackground.setInnerHTML("");
        }
    }

    native private void initializeWidgets() /*-{
        $wnd.AuthorApi.initializeWidgets();
    }-*/;

    /**
     * send an array of commands to whiteboard.
     *
     * Each element in array is a command and an array of data.
     */
    private native void jsni_updateWhiteboard(String flashId, String command, String commandData) /*-{
        var cmdArray = [];
        if (command == 'draw') {
            cmdArray = [
                ['draw', [commandData]]
            ];
        } else if (command == 'clear') {
            cmdArray = [
                ['clear', []]
            ];
        }

        var realArray = [];
        for (var i = 0, t = cmdArray.length; i < t; i++) {
            var ele = [];
            ele[0] = cmdArray[i][0];
            ele[1] = cmdArray[i][1];
            realArray[i] = ele;
        }
        $wnd.Whiteboard.updateWhiteboard(realArray);
    }-*/;

    protected void whiteboardSave_Gwt() {
        saveWhiteboardToServer();
    }

    MultiActionRequestAction whiteboardActions = new MultiActionRequestAction();
    boolean eatNextWhiteboardOut = false;

    protected void whiteboardOut_Gwt(String json, boolean boo) {

        if (eatNextWhiteboardOut) {
            eatNextWhiteboardOut = false;
            return;
        }
        
        if(json == null) { 
            Log.debug("whitebordOut_Gwt: JSON is null");
            return;
        }
        
        Log.debug("whitebordOut_Gwt: JSON length " + json.length());

        /**
         * If json is simple string 'clear', then force a full clear and remove
         * all chart data for this user/pid. Otherwise, it is a normal draw
         * command.
         */
        CommandType commandType = json.equals("clear") ? CommandType.CLEAR : CommandType.DRAW;
        if (commandType == CommandType.CLEAR) {
            whiteboardActions.getActions().clear();
        }

        Action<? extends Response> action = _whiteboardOutCallback.createWhiteboardSaveAction(pid, commandType, json);
        if (action != null) {
            whiteboardActions.getActions().add(action);
        }

        // do every time...
        saveWhiteboardToServer();

        CmRpc.EVENT_BUS.fireEvent(new ShowWorkModifiedEvent(this));
    }

    /**
     * Provide generate way to load data externally
     *
     * @param commands
     */
    public void loadWhiteboard(List<WhiteboardCommand> commands) {
        Log.debug("Loading whiteboard with " + commands.size() + " commands");
        final String flashId = "";
        try {
            jsni_updateWhiteboard(flashId, "clear", null);
        } catch (Exception e) {
            Log.debug("Error clearing whiteboard: " + e);
        }
        for (int i = 0, t = commands.size(); i < t; i++) {
            try {
                // Log.debug("processing whiteboard command: " +
                // commands.get(i));
                jsni_updateWhiteboard(flashId, commands.get(i).getCommand(), commands.get(i).getData());
            } catch (Exception e) {
                Log.error("Error processing whiteboard command: " + e.getMessage(), e);
            }
        }
    }

    private void saveWhiteboardToServer() {

        if (whiteboardActions.getActions().size() == 0)
            return;

        Log.debug("saveWhiteboardToServer: actions=" + whiteboardActions.getActions().size());

        CmTutor.getCmService().execute(whiteboardActions, new AsyncCallback<CmList<Response>>() {
            @Override
            public void onSuccess(CmList<Response> result) {

                Log.debug("saveWhiteboardToServer: complete");
                whiteboardActions.getActions().clear();
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.debug("saveWhiteboardToServer: error", caught);
                caught.printStackTrace();
                Window.alert(caught.getMessage());
            }
        });
    }

    private native void setExternalJsniHooks(ShowWorkPanel x)/*-{
        // overide methods in the Whiteboard instance
        $wnd.Whiteboard.whiteboardOut = function (data, boo) {
            x.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel::whiteboardOut_Gwt(Ljava/lang/String;Z)(data, boo);

        }
        $wnd.Whiteboard.saveWhiteboard = function () {
            alert('save whiteboard');
            x.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel::whiteboardSave_Gwt()();
        }
    }-*/;

    private native void jsni_initializeWhiteboard(Element ele)/*-{
    
        var that = this;
        
        $wnd.Whiteboard.whiteboardIsReady = function() {
            // callback into Java 
            that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel::whiteboardIsReady()();
        }

        try {
            if (typeof $wnd.Whiteboard == 'undefined') {
                alert('Whiteboard JS is not loaded');
                return;
            }

            // tell the Whiteboard object the size of the parent container
            var height = Number($wnd.grabComputedHeight(ele)) + 15;
            var width = Number($wnd.grabComputedWidth(ele)) + 15;
            
            alert('setting whiteboard size: ' + height + ', ' + width);
            $wnd.Whiteboard.setWhiteboardViewPort(width, height);
            $wnd.Whiteboard.initWhiteboard($doc);
            
            
        } catch (e) {
            alert('error initializing whiteboard: ' + e);
            return;
        }
                                                              }-*/;

    private native void jnsi_resizeWhiteboard(Element ele)/*-{
    
        // tell the Whiteboard object the size of the parent container
        var height = Number($wnd.grabComputedHeight(ele)) + 15;
        var width = Number($wnd.grabComputedWidth(ele)) + 15;
        
        
        $wnd.Whiteboard.setWhiteboardViewPort(width, height);
        $wnd.Whiteboard.resizeWhiteboard();
     }-*/;

    static private native void jsni_disconnectWhiteboard()/*-{
                                                          $wnd.Whiteboard.disconnectWhiteboard($doc);
                                                          }-*/;

    static {
        CmRpc.EVENT_BUS.addHandler(WindowHasBeenResizedEvent.TYPE, new WindowHasBeenResizedHandler() {
            @Override
            public void onWindowResized(WindowHasBeenResizedEvent windowHasBeenResizedEvent) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (__lastInstance != null && __lastInstance.isReady()) {
                            __lastInstance.resizeWhiteboard();
                        }
                    }
                });
            }
        });
    }

    static public interface ShowWorkPanelCallback {

        /**
         * Create the appropriate action added to list of commands saved for
         * this whiteboard
         *
         * Return null for no save operation.
         *
         * @param commandType
         * @param data
         */
        Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data);

        /**
         * Indicate the whiteboard is ready for operation
         *
         */
        void showWorkIsReady();

        /**
         * Fired when the window has been resized
         *
         */
        void windowResized();

    }

    /**
     * no op dummy proxy
     *
     * @author casey
     *
     */
    static public class ShowWorkPanelCallbackDefault implements ShowWorkPanelCallback {

        @Override
        public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
            Log.debug("Not saving whiteboard changes");
            return null;
        }

        @Override
        public void showWorkIsReady() {
            Log.debug("Show Work Panel is ready");
        }

        @Override
        public void windowResized() {
            Log.debug("Show Work Panel was resized");
        }

    }
}
