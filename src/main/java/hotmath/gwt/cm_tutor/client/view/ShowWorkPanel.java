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

    ShowWorkProxy _whiteboardOutCallback;
    String pid;
    String title;
    public ShowWorkPanel(ShowWorkProxy whiteboardOutCallback) {
        this._whiteboardOutCallback = whiteboardOutCallback; 
        initWidget(uiBinder.createAndBindUi(this));
        
        showProblem.setVisible(false);
        
        
        /** execute initialize only after HTML is loaded */
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                initializeWhiteboard(getWidget().getElement());
                _whiteboardOutCallback.showWorkIsReady();
            }
        });
        setExternalJsniHooks(this);
        
        __lastInstance = this;
    }

    interface MyUiBinder extends UiBinder<Widget, ShowWorkPanel> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


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
        initializeWhiteboard(getWidget().getElement());
        _whiteboardOutCallback.windowResized();
    }
    
    @UiHandler("showProblem")
    protected void handleShowProblem(ClickEvent ce) {
        setProblemStatement();
    }
    

    private void setProblemStatement() {
        String problemStatement = "PROBLEM STATEMENT";
        
        if(showProblem.getValue()) {
            canvasBackground.setInnerHTML("<div>" + problemStatement + "</div>");
            canvasBackground.setAttribute("style", "display: block");
            
            initializeWidgets();
        }
        else {
            canvasBackground.setAttribute("style", "display: none");
            canvasBackground.setInnerHTML("");
        }        
    }
    
    native private void initializeWidgets() /*-{
        AuthorApi.initializeWidgets();
    }-*/; 
    
    
    
    @UiField
    CheckBox showProblem;
    
    @UiField
    Element canvasBackground;
    
    
    
    /**
     * send an array of commands. Each element in array is a command and an
     * array of data. For example, one draw', but a bunch of draw requests.
     */
    static public native void updateWhiteboard(String flashId, String command, String commandData) /*-{
         var cmdArray = [];
         if(command == 'draw') {
             cmdArray = [['draw',[commandData]]];
         }
         else if(command == 'clear') {
             cmdArray = [['clear',[]]];
         }
                                                                                                   
         var realArray = [];
         for (var i = 0, t = cmdArray.length; i < t; i++) {
             var ele = [];
             ele[0] = cmdArray[i][0];
             ele[1] = cmdArray[i][1];
             realArray[i] = ele;
         }
         
         //alert('updateWhiteboard: ' + realArray);
         
         $wnd.Whiteboard.updateWhiteboard(realArray);
    }-*/;

    
    protected void whiteboardSave_Gwt() {
        saveWhiteboardToServer();
    }

    MultiActionRequestAction whiteboardActions = new MultiActionRequestAction();
    boolean eatNextWhiteboardOut=false;
    protected void whiteboardOut_Gwt(String json, boolean boo) {
        
        if (eatNextWhiteboardOut) {
            eatNextWhiteboardOut = false;
            return;
        }
        /**
         * If json is simple string 'clear', then force a full clear and
         * remove all chart data for this user/pid. Otherwise, it is a
         * normal draw command.
         */
        CommandType commandType = json.equals("clear") ? CommandType.CLEAR : CommandType.DRAW;
        if(commandType == CommandType.CLEAR) {
            whiteboardActions.getActions().clear();
        }
        
        Action<? extends Response> action = _whiteboardOutCallback.createWhiteboardSaveAction(pid,commandType, json);
        if(action != null) {
            whiteboardActions.getActions().add(action);
        }
        

        CmRpc.EVENT_BUS.fireEvent(new ShowWorkModifiedEvent(this));

        // do every time...
        saveWhiteboardToServer();
    }
    
    
    public interface ShowWorkProxy {
        
        /** Create the appropriate action added to list of commands saved for this whiteboard
         * 
         * Return null for no save operation.
         * 
         * @param commandType
         * @param data
         */
       Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data);
       
       /** Indicate the whiteboard is ready for operation
        * 
        */
       void  showWorkIsReady();
       
       
       /** Fired when the window has been resized
        * 
        */
       void windowResized();
       
    }
    

    /** Provide generate way to load data externally
     * 
     * @param commands
     */
    public void loadWhiteboard(List<WhiteboardCommand> commands) {
        final String flashId = "";
        
        try {
            updateWhiteboard(flashId, "clear",null);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        for (int i = 0, t = commands.size(); i < t; i++) {
            try {
                updateWhiteboard(flashId, commands.get(i).getCommand(), commands.get(i).getData());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error processing whiteboard command: " + e.getMessage());
            }
        }
    }
    

    
    private void saveWhiteboardToServer() {
        
        if(whiteboardActions.getActions().size() == 0)
            return;
        
        CmTutor.getCmService().execute(whiteboardActions, new AsyncCallback<CmList<Response>>() {
            @Override
            public void onSuccess(CmList<Response> result) {
                whiteboardActions.getActions().clear();
            }
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                Window.alert(caught.getMessage());
            }
        });         
    }
    
    private native void setExternalJsniHooks(ShowWorkPanel x) /*-{
                                                                    // overide methods in the Whiteboard instance
                                                                    $wnd.Whiteboard.whiteboardOut = function(data, boo) {
                                                                        x.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel::whiteboardOut_Gwt(Ljava/lang/String;Z)(data, boo);

                                                                    }
                                                                    $wnd.Whiteboard.saveWhiteboard = function() {
                                                                            x.@hotmath.gwt.cm_tutor.client.view.ShowWorkPanel::whiteboardSave_Gwt()();
                                                                    }
                                                                }-*/;

    protected native void initializeWhiteboard(Element ele)/*-{
                                                  if(typeof $wnd.Whiteboard == 'undefined') {
                                                      alert('Whiteboard JS is not loaded');
                                                      return;
                                                  }
                         
                                                  var height = Number($wnd.grabComputedHeight(ele))+15;
                                                  var width = Number($wnd.grabComputedWidth(ele))+15;
                                                  $wnd.Whiteboard.setWhiteboardViewPort(width,height);
                                                  $wnd.Whiteboard.initWhiteboard($doc);
                                              }-*/;

    static public native void disconnectWhiteboard()/*-{
                                                    $wnd.Whiteboard.disconnectWhiteboard($doc);
                                                    }-*/;
    
    
    
    
    static {
        CmRpc.EVENT_BUS.addHandler(WindowHasBeenResizedEvent.TYPE, new WindowHasBeenResizedHandler() {
            @Override
            public void onWindowResized(WindowHasBeenResizedEvent windowHasBeenResizedEvent) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if(__lastInstance != null) {
                            __lastInstance.resizeWhiteboard();
                        }
                    }
                });
            }
        });
    }
}