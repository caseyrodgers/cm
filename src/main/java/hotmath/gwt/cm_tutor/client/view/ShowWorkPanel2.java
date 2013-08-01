package hotmath.gwt.cm_tutor.client.view;

import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedHandler;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tutor.client.CmTutor;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
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

    @UiField
    Element canvasBackground;

    boolean isReady;

    interface MyUiBinder extends UiBinder<Widget, ShowWorkPanel2> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public ShowWorkPanel2(ShowWorkPanel2Callback whiteboardOutCallback) {
        this(whiteboardOutCallback, true);
    }

    public ShowWorkPanel2(ShowWorkPanel2Callback whiteboardOutCallback, boolean setupWhiteboardNow) {
        this._whiteboardOutCallback = whiteboardOutCallback;
        initWidget(uiBinder.createAndBindUi(this));

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

    /**
     * Call after the whiteboard had been inserted into DOM
     * 
     */
    public void setupWhiteboard() {
        /** execute initialize only after HTML is loaded */
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                Log.debug("Calling initializeWhiteboard with element: " + getWidget().getElement());

                // call initialization. This will call
                // whiteboardIsReady, which will then do
                // the callback. This way we know the whiteboard
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

    public void setAsTeacherMode(boolean yesNo) {
        jsni_setAsTeacherMode(yesNo);
    }

    private native void jsni_setAsTeacherMode(boolean yesNo)/*-{
                                                            $wnd.Whiteboard.setAsTeacherMode(yesNo);
                                                            }-*/;

    public void setProblemStatement(String problemStatement) {
        if (problemStatement != null) { // showProblem.getValue()) {
            canvasBackground.setInnerHTML("<div>" + problemStatement + "</div>");
            canvasBackground.setAttribute("style", "display: block");

            // initializeWidgets();
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
            commandType = CommandType.UNDO;

            if (_lastCommands != null && _lastCommands.size() > 0) {
                WhiteboardCommand cmd = _lastCommands.get(_lastCommands.size() - 1);
                if (cmd.isAdmin()) {
                    PopupMessageBox.showMessage("Cannot undo teacher notes");
                    return;
                }
                _lastCommands.remove(_lastCommands.size() - 1); // remove last
                                                                // element
                loadWhiteboard(_lastCommands);
            } else {
                PopupMessageBox.showMessage("Nothing to undo");
                return;
            }
        } else {
            commandType = CommandType.DRAW;

            /** So it can be redrawn on undo */
            _lastCommands.add(new WhiteboardCommand("draw", json, false));
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

        Log.error("NOT FIRING MODIFIED EVENT");
        // CmRpcCore.EVENT_BUS.fireEvent(new ShowWorkModifiedEvent(this));
    }

    /**
     * Provide generate way to load data externally
     * 
     * @param commands
     */
    public void loadWhiteboard(List<WhiteboardCommand> commands) {

        _lastCommands = commands;
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
    
    
    private void whiteboardDelete_Gwt(int index) {
        Window.alert("Need to delete whitebaord command: " + index);
    }
    

    private native void jsni_initializeWhiteboard(Element ele)/*-{
        // load all Whiteboard external dependencies
        //
        $wnd.requireJsLoad_whiteboard(function(wb) {
        
            // overide methods in the Whiteboard instance
            $wnd.Whiteboard.whiteboardOut = function (data, boo) {
                that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::whiteboardOut_Gwt(Ljava/lang/String;Z)(data, boo);
            }
            
            $wnd.Whiteboard.whiteboardDelete = function (wbIndex) {
                that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::whiteboardDelete_Gwt(I)(data, boo);
            }
            
            $wnd.Whiteboard.saveWhiteboard = function () {
                that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::whiteboardSave_Gwt()();
            }
                                                              
            $wnd.Whiteboard.whiteboardIsReady = function() {
                // callback into Java 
                that.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2::whiteboardIsReady()();
            }
            
            try {
                if (typeof $wnd.Whiteboard == 'undefined') {
                    alert('Whiteboard JS is not loaded');
                    return;
                }
                                                              
                // tell the Whiteboard object the size of the parent container
                var height = Number($wnd.grabComputedHeight(ele)) + 15;
                var width = Number($wnd.grabComputedWidth(ele)) + 15;
                 
                //alert('setting whiteboard size: ' + height + ', ' + width);
                $wnd.Whiteboard.setWhiteboardViewPort(width, height);
                $wnd.Whiteboard.initWhiteboard($doc);
            } catch (e) {
                alert('error initializing whiteboard: ' + e);
                return;
            }
        }
    );
                                                              
                                                              }-*/;

    private native void jnsi_resizeWhiteboard(Element ele)/*-{
                                                          if (typeof $wnd.Whiteboard == 'undefined') {
                                                          $wnd.console.log('jnsi_resizeWhiteboard: Whiteboard not defined');
                                                          return;
                                                          }
                                                          
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
    static public class ShowWorkPanelCallbackDefault implements ShowWorkPanel2Callback {

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

    public void alignWhiteboard(Element tutorElement) {
        int width = tutorElement.getParentElement().getClientWidth();
        int height = tutorElement.getParentElement().getClientWidth();

        // height is predetermined in whiteboard.js
        getElement().setAttribute("style", "width: " + width + ";height: " + height);
    }
}
