package hotmath.gwt.cm_tutor.client.view;

import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedHandler;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardAsTemplateAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.CmTutor;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO: replace ShowWorkPanel
 *
 * Provides an overlay over existing tutor panel.
 *
 *
 * @author casey
 *
 */
public class ShowWorkPanel2 extends Composite {
    static public ShowWorkPanel2 __lastInstance;

    static public final String QUIZ_PREFIX = "quiz:";

    ShowWorkPanel2Callback _whiteboardOutCallback;
    String pid;
    String title;

    boolean isReady;

    private boolean _interactive;

    private boolean _teacherMode;

    private String whiteboardId;

    private int whiteboardHeight;

    private Widget parentWidget;


    public ShowWorkPanel2(ShowWorkPanel2Callback whiteboardOutCallback) {
        this(whiteboardOutCallback, true);
    }

    public ShowWorkPanel2(ShowWorkPanel2Callback whiteboardOutCallback, boolean setupWhiteboardNow) {
        this(whiteboardOutCallback,setupWhiteboardNow,true, "whiteboard-1", 0, null);
    }

    public ShowWorkPanel2(ShowWorkPanel2Callback whiteboardOutCallback, boolean setupWhiteboardNow, boolean isInteractive, String whiteboardId, int height, Widget parentWidget) {
        this._whiteboardOutCallback = whiteboardOutCallback;
        this._interactive = isInteractive;
        this.whiteboardId = whiteboardId;
        this.whiteboardHeight = height;  // if zero, calculate based on parent
        this.parentWidget = parentWidget; // if null, get parent

        String html = "<div id='" + this.whiteboardId + "'></div>";
        initWidget(new HTML(html));

        addStyleName("ShowWorkPanel2");

        if (setupWhiteboardNow) {
            setupWhiteboard();
        }

        __lastInstance = this;
    }

    public void setBackground(boolean showIt) {
        removeStyleName("transparent");
        if (showIt) {
            addStyleName("transparent");
        }
    }

    public void toggleBackground() {
        if (getStyleName().indexOf("transparent") > -1) {
            removeStyleName("transparent");
        } else {
            addStyleName("transparent");
        }
    }


    private Widget getParentWidget() {
        return parentWidget != null?parentWidget:getWidget();
    }

    /**
     * Call after the whiteboard had been inserted into DOM
     *
     */
    public void setupWhiteboard() {
        /** execute initialize only after HTML is loaded */
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                Log.debug("Calling initializeWhiteboard with element: " + getParentWidget().getElement());

                // call initialization. This will call
                // whiteboardIsReady, which will then do
                // the callback. This way we know the whiteboard
                // is 100% ready.
                jsni_initializeWhiteboard(whiteboardId, getParentWidget().getElement(),!_interactive, whiteboardHeight);
            }
        });
    }

    private void whiteboardIsReady() {
        Log.debug("whiteboardIsReady called from external JS");
        isReady = true;
       _whiteboardOutCallback.showWorkIsReady(this);
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
        jnsi_resizeWhiteboard(getParentWidget().getElement(), whiteboardHeight);
        _whiteboardOutCallback.windowResized();
    }

    public void setAsTeacherMode(boolean yesNo) {
        _teacherMode=yesNo;
        jsni_setAsTeacherMode(yesNo);
    }

    private native void jsni_setAsTeacherMode(boolean yesNo)/*-{
                                                            $wnd._theWhiteboard.setAsTeacherMode(yesNo);
                                                            }-*/;

    native public void setProblemStatement(String theProbStmt) /*-{
        var ps = $wnd.$('.problemStatement');
        if(ps.length > 0) {
            ps.html(theProbStmt);
        }
    }-*/;

    native private void initializeWidgets() /*-{
                                            $wnd.AuthorApi.initializeWidgets();
                                            }-*/;

    /**
     * send an array of commands to whiteboard.
     *
     * Each element in array is a command and an array of data.
     */
    private void jsni_updateWhiteboard(String whiteboardId, String command, String commandData) {
        if(!isReady()) {
            Window.alert("Whiteboard is not ready");
            return;
        }
        CmGwtUtils.jsni_updateWhiteboardAux(null, command, commandData);
    }


    protected void whiteboardSave_Gwt() {
        saveWhiteboardToServer();
    }

    MultiActionRequestAction whiteboardActions = new MultiActionRequestAction();
    boolean eatNextWhiteboardOut = false;

    private List<WhiteboardCommand> _lastCommands;

    protected void whiteboardOut_Gwt(String json, boolean boo) {

        if (eatNextWhiteboardOut) {
            eatNextWhiteboardOut = false;
            return;
        }

        if (json == null) {
            Log.debug("whitebordOut_Gwt: JSON is null");
            return;
        }

        Log.debug("whitebordOut_Gwt: JSON length " + json.length());

        /**
         * If json is simple string 'clear', then force a full clear and remove
         * all chart data for this user/pid. Otherwise, it is a normal draw
         * command.
         */
        CommandType commandType = null;
        if (json.equals("clear")) {
            if (_lastCommands != null) {
                _lastCommands.clear();
            }
            commandType = CommandType.CLEAR;
        } else if (json.equals("undo")) {

            jsni_enableWhiteboardUndo(false);

            Log.debug("Whiteboard Undo: disabling undo button");

            commandType = CommandType.UNDO;

            if (_lastCommands != null && _lastCommands.size() > 0) {
                Log.debug("Whiteboard Undo: removing from _lastCommands");
                WhiteboardCommand cmd = _lastCommands.get(_lastCommands.size() - 1);
                if (!_teacherMode && cmd.isAdmin()) {
                    PopupMessageBox.showMessage("Cannot undo teacher notes");
                    return;
                }
                _lastCommands.remove(_lastCommands.size() - 1); // remove last

                Log.debug("Whiteboard Undo: _lastCommands size == " + _lastCommands.size());
                                                                // element
                loadWhiteboard(_lastCommands);
            } else {
                Log.debug("Whiteboard: Nothing to undo");
                return;
            }
        } else {
            commandType = CommandType.DRAW;

            if(_lastCommands == null) {
                Log.error("Whiteboard has not been loaded!");
            }
            else {
                /** So it can be redrawn on undo */
                _lastCommands.add(new WhiteboardCommand("draw", json, false));
            }
        }

        if (commandType == CommandType.CLEAR) {
            whiteboardActions.getActions().clear();
        }

        Action<? extends Response> action = _whiteboardOutCallback.createWhiteboardSaveAction(pid, commandType, json);
        if (action != null) {
            whiteboardActions.getActions().add(action);
        }

        // do every time...
        saveWhiteboardToServer();

        // Log.error("NOT FIRING MODIFIED EVENT");
        // CmRpcCore.EVENT_BUS.fireEvent(new ShowWorkModifiedEvent(this));
    }

    native private void jsni_enableWhiteboardUndo(boolean b) /*-{
        if(b) {
            $wnd._theWhiteboard.enableUndo();
        }
        else {
            $wnd._theWhiteboard.disableUndo();
        }
    }-*/;

    /**
     * Provide generate way to load data externally.
     *
     * Always clears before processing
     *
     * @param commands
     */
    public void loadWhiteboard(List<WhiteboardCommand> commands) {
        _lastCommands = commands;
        Log.debug("Loading whiteboard with " + commands.size() + " commands");
        try {
            //jsni_updateWhiteboard(flashId, "clear", null);
        } catch (Exception e) {
            Log.debug("Error clearing whiteboard: " + e);
        }
        for (int i = 0, t = commands.size(); i < t; i++) {
            try {
                // Log.debug("processing whiteboard command: " +
                // commands.get(i));
                jsni_updateWhiteboard(this.whiteboardId, commands.get(i).getCommand(), commands.get(i).getData());
            } catch (Exception e) {
                Log.error("Error processing whiteboard command: " + e.getMessage(), e);
            }
        }
    }

    private void saveWhiteboardToServer() {

        Log.debug("saveWhiteboardToServer: actions=" + whiteboardActions.getActions().size());

        if (whiteboardActions.getActions().size() == 0)
            return;

        CmTutor.getCmService().execute(whiteboardActions, new AsyncCallback<CmList<Response>>() {
            @Override
            public void onSuccess(CmList<Response> result) {

                Log.debug("saveWhiteboardToServer: complete");
                whiteboardActions.getActions().clear();

                jsni_enableWhiteboardUndo(true);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.debug("saveWhiteboardToServer: error", caught);
                caught.printStackTrace();
                Window.alert(caught.getMessage());
            }
        });
    }


    /** Save the currently defined whiteboard as named template
     * 
     * @param name
     */
    public void saveAsTemplate(int adminId, final String name, final CallbackOnComplete callback) {
        final String dataUrl = jsniGetWhiteboardDataUrl();
        
        SaveWhiteboardAsTemplateAction action = new SaveWhiteboardAsTemplateAction(adminId, name, dataUrl);
        CmTutor.getCmService().execute(action,  new AsyncCallback<RpcData>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error saving template: " + caught);
                Log.error("Error saving template", caught);
            }

            @Override
            public void onSuccess(RpcData result) {
                Log.info("SaveWhiteboardAsTemplateAction: " + result);
                
                callback.isComplete();
            }});
    }
    
    private native String jsniGetWhiteboardDataUrl() /*-{
        return $wnd._theWhiteboard.saveSelToImage();
    }-*/;

    private void whiteboardDelete_Gwt(int index) {
        _lastCommands.remove(index);

        /** callback to get needed indexing information
         * (ie, each app will have different needs)
         */
        SaveWhiteboardDataAction action = (SaveWhiteboardDataAction)_whiteboardOutCallback.createWhiteboardSaveAction(pid,  CommandType.DELETE, index + "");
        action.setIndex(index);
        whiteboardActions.getActions().add(action);
    }

    private void gwt_saveWhiteboardAsTemplate() {
        _whiteboardOutCallback.saveWhiteboardAsTemplate(this);
    }
    
    private void manageTemplates() {
        _whiteboardOutCallback.manageTemplates(this);
    }


    private native void jsni_initializeWhiteboard(String whiteboardId, Element ele, boolean isStatic, int heightIn)/*-{

        var that = this;
        try {
            $wnd.console.log('Initializing whiteboard: ' + whiteboardId);

            if (typeof $wnd.Whiteboard == 'undefined') {
                alert('Whiteboard JS is not loaded');
                return;
            }

            // create a single global object for now.
            // TODO: add support for multiple whiteboards
            //
            var _theWhiteboard;
            if($wnd._theWhiteboard) {
              // $wnd._theWhiteboard.releaseResources();
              $wnd._theWhiteboard = null;
            }

            //new $wnd.Whiteboard();
            $wnd._theWhiteboardDiv = $doc.getElementById(whiteboardId);
            if($wnd._theWhiteboardDiv == null) {
                alert('whiteboard not found: ' + whiteboardId);
                return;
            }

            $wnd._theWhiteboard = new $wnd.Whiteboard(whiteboardId, isStatic, {'showTemplates':true});
            $wnd._theWhiteboard.initWhiteboard($doc);

            try {
                //var templates = {"type":"img","path":"./","icon":"tn-","list":["temp_integral.png"]};
                //$wnd._theWhiteboard.appendTemplates(templates);
            }
            catch(e) {
                //alert('error assigning whiteboard templates: ' + e);
            }

            // tell the Whiteboard object the size of the parent container
            // if height is passed in use it, otherwise calculate based on parent

            var height=0;
            if(heightIn) {
                height = heightIn;
            }
            else {
                height = Number($wnd.grabComputedHeight(ele)) + 15;
            }
            var width = Number($wnd.grabComputedWidth(ele)) + 15;

            //alert('setting whiteboard size: ' + height + ', ' + width);
            $wnd._theWhiteboard.setWhiteboardViewPort(width, height);

            // overide methods in the Whiteboard instance
            $wnd._theWhiteboard.whiteboardOut = function (data, boo) {
               that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::whiteboardOut_Gwt(Ljava/lang/String;Z)(data, boo);
            }

            $wnd._theWhiteboard.whiteboardDelete = function (indexToDelete) {
               that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::whiteboardDelete_Gwt(I)(indexToDelete);
            }

            $wnd._theWhiteboard.saveWhiteboard = function () {
               that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::whiteboardSave_Gwt()();
            }

            $wnd._theWhiteboard.whiteboardIsReady = function() {
               that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::whiteboardIsReady()();
            }

            $wnd._theWhiteboard.gwt_saveWhiteboardAsTemplate = function() {
               that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::gwt_saveWhiteboardAsTemplate()();
            }

           $wnd.gwt_manageTemplates = function() {
               that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::manageTemplates()();
            }
            
            
            
        } catch (e) {
            alert('error initializing whiteboard: ' + e);
            return;
        }
    }-*/;

    private native void jnsi_resizeWhiteboard(Element ele, int heightIn)/*-{
        if (typeof $wnd.Whiteboard == 'undefined') {
            $wnd.console.log('jnsi_resizeWhiteboard: Whiteboard not defined');
            return;
        }

        // tell the Whiteboard object the size of the parent container


        var height=0;
        if(heightIn == 0) {
            height = Number($wnd.grabComputedHeight(ele)) + 15;
        }
        else {
            height = heightIn;
        }
        var width = Number($wnd.grabComputedWidth(ele)) + 15;

        $wnd._theWhiteboard.setWhiteboardViewPort(width, height);
        $wnd._theWhiteboard.resizeWhiteboard();
     }-*/;

    static private native void jsni_disconnectWhiteboard()/*-{
                                                          $wnd._theWhiteboard.disconnectWhiteboard($doc);
                                                          }-*/;

    static {
        CmRpcCore.EVENT_BUS.addHandler(WindowHasBeenResizedEvent.TYPE, new WindowHasBeenResizedHandler() {
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

    static public interface ShowWorkPanel2Callback {

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

        /** Provide a UI to manage the whiteboard templates
         * 
         * @param showWorkPanel2
         */
        void manageTemplates(ShowWorkPanel2 showWorkPanel2);
        

        /** Save the current whiteboard as a template
         * 
         * @param showWorkPanel2
         */
        void saveWhiteboardAsTemplate(ShowWorkPanel2 showWorkPanel2);

        /**
         * Indicate the whiteboard is ready for operation
         *
         */
        void showWorkIsReady(ShowWorkPanel2 showWork);

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
    static public class ShowWorkPanelCallbackDefault implements ShowWorkPanel2Callback {

        @Override
        public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
            Log.debug("Not saving whiteboard changes");
            return null;
        }

        @Override
        public void showWorkIsReady(ShowWorkPanel2 showWork) {
            Log.debug("Show Work Panel is ready");
        }

        @Override
        public void windowResized() {
            Log.debug("Show Work Panel was resized");
        }
        
        @Override
        public void saveWhiteboardAsTemplate(ShowWorkPanel2 showWorkPanel2) {
            Log.info("No saveWhiteboardAsTemplate defined");
        }
        
        @Override
        public void manageTemplates(ShowWorkPanel2 showWorkPanel2) {
            Log.info("No Manage Templates defined");
        }

    }

    native public void setWhiteboardTemplates(String templates) /*-{
        $wnd._theWhiteboard.appendTemplates('(' + templates + ')');
    }-*/;

    native public void setWhiteboardTemplate(String name, String path) /*-{
       $wnd._theWhiteboard.setTemplate(name, path);
    }-*/;


}
