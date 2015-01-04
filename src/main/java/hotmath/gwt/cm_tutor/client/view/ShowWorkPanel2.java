package hotmath.gwt.cm_tutor.client.view;

import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.UserInfo;
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
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
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

    static int __whiteboardId;
    public ShowWorkPanel2(ShowWorkPanel2Callback whiteboardOutCallback, boolean setupWhiteboardNow) {
        this(whiteboardOutCallback,setupWhiteboardNow,true, "whiteboard-" + (__whiteboardId++), 0, null);
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


    public String getWhiteboardCommandsAsJson() {
        return jsni_getWhiteboardCommandsAsJson(whiteboardId);
    }

    native private String jsni_getWhiteboardCommandsAsJson(String whiteboardId) /*-{
        var theWhiteboard = $wnd._cmWhiteboards[whiteboardId];
        return theWhiteboard.getRenderedCommands();
    }-*/;
    
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
        resizeWhiteboard(whiteboardHeight);
    }

    public void resizeWhiteboard(int height) {
        if(_whiteboardOutCallback.allowWhiteboardResize()) {
            jnsi_resizeWhiteboard(whiteboardId, getParentWidget().getElement(), height);
            _whiteboardOutCallback.windowResized();
        }            
    }
    
    public void setAsTeacherMode(boolean yesNo) {
        _teacherMode=yesNo;
        jsni_setAsTeacherMode(whiteboardId, yesNo);
    }

    private native void jsni_setAsTeacherMode(String whiteboardId, boolean yesNo)/*-{
        var theWhiteboard = $wnd._cmWhiteboards[whiteboardId];
        theWhiteboard.setAsTeacherMode(yesNo);
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
        CmGwtUtils.jsni_updateWhiteboardAux(whiteboardId, command, commandData);
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

            jsni_enableWhiteboardUndo(whiteboardId, false);

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
                             
                jsni_clearWhiteboard();
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

    native private void jsni_enableWhiteboardUndo(String whiteboardId, boolean b) /*-{
        var theWhiteboard = $wnd._cmWhiteboards[whiteboardId];
        if(b) {
            theWhiteboard.enableUndo();
        }
        else {
            theWhiteboard.disableUndo();
        }
    }-*/;
    
    
    public void jsni_clearWhiteboard() {
        jsni_updateWhiteboard(this.whiteboardId, "clear", null);        
    }

    /**
     * Provide way to load data externally.
     *
     *
     * @param commands
     */

    public void loadWhiteboard(List<WhiteboardCommand> commands) {
        if(commands == null) {
            _lastCommands = new ArrayList<WhiteboardCommand>();
            jsni_clearWhiteboard();
            return;
        }
        
        long _whiteboardLoadTime=System.currentTimeMillis();
        
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
            	
            	long mills=0;
            	String cmd=null;
            	if(Log.isDebugEnabled()) {
            		mills = System.currentTimeMillis();
            		cmd = commands.get(i).getCommand();
            	}
            	 
                jsni_updateWhiteboard(this.whiteboardId, commands.get(i).getCommand(), commands.get(i).getData());
                
                if(Log.isDebugEnabled()) {
                	Log.debug("WHITEBOARD COMMAND: " + cmd + ", time=" + (System.currentTimeMillis() - mills) + ", commands=" + jsni_getWhiteboardCommandId(commands.get(i).getData()) + ", len = " + commands.get(i).getData().length() );
                }
            } catch (Exception e) {
                Log.error("Error processing whiteboard command: " + e.getMessage(), e);
            }
        }
        
        jsni_whiteboardComplete(this.whiteboardId);
        
        Log.debug("Whiteboard load time: " + (System.currentTimeMillis() - _whiteboardLoadTime));
    }
    
    native private void jsni_whiteboardComplete(String whiteboardId) /*-{
        var theWhiteboard  = $wnd._cmWhiteboards[whiteboardId];
        if(!theWhiteboard) {
            alert('whiteboard ' + whiteboardId + ' cannot be found in jsni_whiteboardComplete');
            return;
        }
        
        // prevent error if function not defined due to 
        // old copy of whiteboard_v3.js
        // TODO: remove after 1 month
        if(theWhiteboard.whiteboardLoadComplete) {
            try {
        	    theWhiteboard.whiteboardLoadComplete();
        	}
        	catch(e) {
        	    console.log('error calling whiteboardLoadComplete: ' + e);
            }
        }
    }-*/;

    native private String jsni_getWhiteboardCommandId(String data) /*-{
        return $wnd._debugGetCommandInfoLabel(data);
    }-*/;

	private void saveWhiteboardToServer() {

        Log.debug("saveWhiteboardToServer: actions=" + whiteboardActions.getActions().size());

        if (whiteboardActions.getActions().size() == 0)
            return;

        CmTutor.getCmService().execute(whiteboardActions, new AsyncCallback<CmList<Response>>() {
            @Override
            public void onSuccess(CmList<Response> result) {

                Log.debug("saveWhiteboardToServer: complete");
                whiteboardActions.getActions().clear();

                jsni_enableWhiteboardUndo(whiteboardId,true);
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
    public void saveAsTemplate(int adminId, final CallbackOnComplete callback) {
        final String dataUrl = jsniGetWhiteboardDataUrl(whiteboardId);
        
        SaveWhiteboardAsTemplateAction action = new SaveWhiteboardAsTemplateAction(adminId, dataUrl);
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
    
    private native String jsniGetWhiteboardDataUrl(String whiteboardId) /*-{
        var theWhiteboard = $wnd._cmWhiteboards[whiteboardId];
        return theWhiteboard.saveSelToImage();
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
    
    private void updateWhiteboardData_Gwt(int index, final String newJson) {
    	Log.info("updateWhiteboardData update whiteboard index on server: " + index + ", " + newJson);
    	UserInfo u = UserInfo.getInstance(); 
    	SaveWhiteboardDataAction action = new SaveWhiteboardDataAction(u.getUid(),u.getRunId(), pid, CommandType.UPDATE, newJson);
    	action.setIndex(index);
		whiteboardActions.getActions().add(action);
		saveWhiteboardToServer();
    }
    
    private void manageTemplates() {
        _whiteboardOutCallback.manageTemplates(this);
    }

    public void setInternalUndo(boolean yesNo) {
    	jsni_setInternalUndo(whiteboardId, yesNo);
    }
    
    native private void jsni_setInternalUndo(String whiteboardId, boolean yesNo) /*-{
    	var theWhiteboard = $wnd._cmWhiteboards[whiteboardId];
    	if(theWhiteboard == null) {
    	    alert('Cannot find requested whiteboard instance: ' + whiteboardId);
    	    return;
    	}
    	
    	theWhiteboard.options.callInternalUndo = yesNo;
    }-*/;

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
            var theWhiteboardDiv = $doc.getElementById(whiteboardId);
            if(theWhiteboardDiv == null) {
                alert('whiteboard not found: ' + whiteboardId);
                return;
            }

            var theWhiteboard = new $wnd.Whiteboard(whiteboardId, isStatic, {'showTemplates':true});
            $wnd._cmWhiteboards[whiteboardId] = theWhiteboard;
            
            theWhiteboard.initWhiteboard($doc);

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
            theWhiteboard.setWhiteboardViewPort(width, height);

            // overide methods in the Whiteboard instance
            theWhiteboard.whiteboardOut = function (data, boo) {
               that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::whiteboardOut_Gwt(Ljava/lang/String;Z)(data, boo);
            }

            theWhiteboard.whiteboardDelete = function (indexToDelete) {
               that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::whiteboardDelete_Gwt(I)(indexToDelete);
            }

            theWhiteboard.saveWhiteboard = function () {
               that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::whiteboardSave_Gwt()();
            }

            theWhiteboard.whiteboardIsReady = function() {
               that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::whiteboardIsReady()();
            }

            theWhiteboard.gwt_saveWhiteboardAsTemplate = function() {
               that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::gwt_saveWhiteboardAsTemplate()();
            }
           
            theWhiteboard.updateWhiteboardData = function (index, newJSON) {
               that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::updateWhiteboardData_Gwt(ILjava/lang/String;)(index, newJSON);
            }


           $wnd.gwt_manageTemplates = function() {
               that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::manageTemplates()();
            }
            

            
        } catch (e) {
            alert('error initializing whiteboard: ' + e);
            return;
        }
    }-*/;

    
    
    private native void jnsi_resizeWhiteboard(String whiteboardId, Element ele, int heightIn)/*-{
        if (typeof $wnd.Whiteboard == 'undefined') {
            $wnd.console.log('jnsi_resizeWhiteboard: Whiteboard not defined');
            return;
        }

        var theWhiteboard = $wnd._cmWhiteboards[whiteboardId];
        
        // tell the Whiteboard object the size of the parent container
        var height=0;
        if(heightIn == 0) {
            height = Number($wnd.grabComputedHeight(ele)) + 15;
        }
        else {
            height = heightIn;
        }
        var width = Number($wnd.grabComputedWidth(ele)) + 15;
        
        theWhiteboard.setWhiteboardViewPort(width, height);
        theWhiteboard.resizeWhiteboard();
     }-*/;

    static private native void jsni_disconnectWhiteboard(String whiteboardId)/*-{
       var theWhiteboard = $wnd._cmWhiteboards[whiteboardId];
       theWhiteboard.disconnectWhiteboard($doc);
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

        boolean allowWhiteboardResize();

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
        
        @Override
        public boolean allowWhiteboardResize() {
            return true;
        }

    }

    public void setWhiteboardTemplates(String templates) {
        jsni_setWhiteboardTemplates(whiteboardId, templates);
    }
    native private void jsni_setWhiteboardTemplates(String whiteboardId, String templates) /*-{
        var theWhiteboard = $wnd._cmWhiteboards[whiteboardId];
        theWhiteboard.appendTemplates('(' + templates + ')');
    }-*/;

    
    public void setWhiteboardTemplate(String path) {
        jsni_setWhiteboardTemplate(whiteboardId, "template_" + System.currentTimeMillis(), path);
    }

    native private void jsni_setWhiteboardTemplate(String whiteboardId, String templateKey, String path) /*-{
       var theWhiteboard = $wnd._cmWhiteboards[whiteboardId];
       theWhiteboard.setTemplate(templateKey, path);
    }-*/;

    public void loadWhiteboardFromJson(String whiteboardJson) {
        jsni_loadWhiteboardFromJson(whiteboardId, whiteboardJson);
    }
    
    native private void jsni_loadWhiteboardFromJson(String whiteboardId, String json) /*-{
        $wnd._cmWhiteboards[whiteboardId].loadFromJson(json);
    }-*/;


}
